package com.docuprep.attach_pdf

import java.io.{File, PrintWriter, FileNotFoundException}
import scala.io.Source
import java.sql.{Connection, DriverManager}
import collection.mutable.MutableList
import akka.actor.{ActorSystem, Props}

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.util.PDFMergerUtility
import org.apache.commons.io.FileUtils

import org.slf4j.LoggerFactory
import ch.qos.logback.classic.{Logger, LoggerContext}

import com.typesafe.config.ConfigFactory

/**
 * Enumeration to distinguish the types of databases allowed for use by the AddPDF_GUI application.
 * 
 * @author James Watts
 * Last Updated: June 10th, 2015
 */
object DatabaseType extends Enumeration {
  type DatabaseType = Value
  val MS_SQL_DATABASE, H2_DATABASE, NO_TYPE = Value
}

/**
 * Utility object that contains a list of fields and methods designed for use by the AddPDF_GUI application.
 * 
 * @author James Watts
 * Last Updated: June 30th, 2015
 */
object AddPDF_Util {
  
  /*******************************************
   *  General Setup of the Utility Object: 	 *
   *******************************************/
  import DatabaseType._
  
  /** Set up the Logger for this program 	**/
  private val logger:Logger = (LoggerFactory.getLogger("com.docuprep.attach_pdf.AddPDF_Util")).asInstanceOf[ch.qos.logback.classic.Logger]
  private val lc:LoggerContext = (LoggerFactory.getILoggerFactory()).asInstanceOf[LoggerContext]
  /* Get rid of warning messages from log4j due to "no appender" on org.apache.pdfbox.pdfparser.PDFObjectStreamParser logger */
  private val L2:org.apache.log4j.Logger = org.apache.log4j.Logger.getLogger("org.apache.pdfbox.pdfparser.PDFObjectStreamParser")
  L2.setLevel(org.apache.log4j.Level.OFF)
  
  /** Get important values from the CONFIG file **/
  /* Load the CONFIG file */
  private val config = ConfigFactory.load()
  
  /* Get the inbound folders from the CONFIG file */
  private val initialInboundFolderList = config.getStringList("attachPDF.InboundFolders")
  private[attach_pdf] var currentInboundFolders = new MutableList[String]
  for(index <- 0 until initialInboundFolderList.size)
  {
    val temp = initialInboundFolderList.get(index)
    if(temp.isEmpty)
      currentInboundFolders += null
    else
      currentInboundFolders += temp
  }
  
  /* Get the outbound folders from the CONFIG file */
  private val initialOutboundFolderList = config.getStringList("attachPDF.OutboundFolders")
  private[attach_pdf] var currentOutboundFolders = List[String]()
  for(index <- 0 until initialOutboundFolderList.size())
  { currentOutboundFolders = currentOutboundFolders ::: initialOutboundFolderList.get(index) :: List() }
  
  /* Get the checkFilesTime and reportStatusTime from the CONFIG file */
  private[attach_pdf] var checkFilesTime 	= config.getInt("attachPDF.checkFilesTime")
  private[attach_pdf] var reportStatusTime	= config.getInt("attachPDF.reportStatusTime")
  
  /* Get the database info from the CONFIG file */
  private[attach_pdf] var databaseName 	= config.getString("attachPDF.database.name")
  private[attach_pdf] var dbPath 		= config.getString("attachPDF.database.pathname")
  private val dbUser  =	config.getString("attachPDF.database.username")
  private val dbPswd  =	config.getString("attachPDF.database.password")
  private val dbTable = config.getString("attachPDF.database.table")
  private var dbType  =	config.getString("attachPDF.database.type") match {
    					case "H2_DATABASE"		=> H2_DATABASE
    					case "MS_SQL_DATABASE"	=> MS_SQL_DATABASE
    					case _					=> NO_TYPE
  }
  private var tempApp = config.getString("attachPDF.database.application")
		  	  tempApp = if(tempApp.length > 25) tempApp.substring(0,25) else tempApp
  private val app 	  = s"$tempApp${" " * (25-tempApp.length())}"	// Make sure the application's name is exactly 25 characters long
  
  /** Initialize other important fields **/
  // GUI Label Updater (Actor)
  private val system = ActorSystem("System")
  private [attach_pdf] val guiUpdater = system.actorOf(Props(new LabelUpdater()), name = "LabelUpdater")
  
  // Timer
  private val timer = new Timer(1000)(count())								// A timer that runs the 'count' method once per second.
  
  // Timer variables
  private[attach_pdf] var timeToNextCheck = checkFilesTime					// Initialize to checkFilesTime from CONFIG file
  private[attach_pdf] var timeToNextReport = reportStatusTime				// Initialize to reportStatusTime from CONFIG file
  private[attach_pdf] var pauseTimer = false								// Start with the timer running
  private[attach_pdf] var pauseTimerLastValue = false
  
  /* Flag for whether or not the GUI (AddPDF_GUI) is currently running.  This is for testing purposes so that the application can 
   * be run/tested without running the GUI. */
  private[attach_pdf] var GuiIsRunning = false
  
  /* Flag for whether or not a Settings GUI Application is currently running.  This is to ensure that only one Settings
   * Application can run at a time and that the main AddPDF GUI Application cannot be closed while that Settings Application
   * is running. */
  private[attach_pdf] var SettingsIsRunning = false
  
  /* Flag for whether or not a User's Guide GUI Application is currently running.  This is to ensure that only one User's Guide
   * Application can run at a time and that the main AddPDF GUI Application cannot be closed while that User's Guide Application
   * is running. */
  private[attach_pdf] var UsersGuideIsRunning = false
  
  // Boolean values for initializing check boxes
  private[attach_pdf] var box2checked = (currentInboundFolders(1) != null)	// Initialize all check boxes to their most recent values
  private[attach_pdf] var box3checked = (currentInboundFolders(2) != null)
  private[attach_pdf] var box4checked = (currentInboundFolders(3) != null)
  
  // Set to hold the names of all text files within the inbound folders
  private var filesWaitingSet = Set[String]()
  
  /*******************************************
   *			   Methods 	 		 		 *
   *******************************************/
  
  /**
   * Main method only used for debugging and testing purposes
   * 
   * @author James Watts
   * Last Updated: February 20th, 2015
   */
  private def main(args: scala.Array[String]): Unit = {
    // Inbound Folders (for testing purposes on my Mac)
    val inboundFoldersMyMac = List("""Users/jameswatts/Pictures/TESTING""",
        """Users/jameswatts/Pictures/TESTING2""",
        """Users/jameswatts/Pictures/TESTING3""",
        """Users/jameswatts/Pictures/TESTING4""")
    // Inbound Folders (for testing purposes on my PC)
    val inboundFoldersMyPC = List("""C:\TestPictureFiles\TESTING""",
        """C:\TestPictureFiles\TESTING2""",
        """C:\TestPictureFiles\TESTING3""",
        """C:\TestPictureFiles\TESTING4""")
    
    // Outbound Folders (for testing purposes on my Mac)
    val PDFFoldersMyMac = List("""Users/jameswatts/Pictures/TESTING""", 
        """Users/jameswatts/Pictures/TESTING2""",
        """Users/jameswatts/Pictures/TESTING3""", 
        """Users/jameswatts/Pictures/TESTING4""")
    // Outbound Folders (for testing purposes on my PC)
    val PDFFoldersMyPC = List("""C:\TestPictureFiles\TESTING""",
        """C:\TestPictureFiles\TESTING2""",
        """C:\TestPictureFiles\TESTING3""",
        """C:\TestPictureFiles\TESTING4""")
    
    // Test merge for my PC
//    for(index <- 0 until inboundFoldersMyPC.length if inboundFoldersMyPC(index)!=null){
//      if(new File(inboundFoldersMyPC(index)) isDirectory)
//    	  merge(inboundFoldersMyPC(index), PDFFoldersMyPC)
//    }
    
  }
  
  /**
   * Merges two PDF files as instructed by a .txt file located in the inboundFolder and copies the newly created PDF file 
   * into all of the folders in destinationPathNames.
   * 
   * Searches for a .txt file in the input inboundFolder and reads the file to get 3 fields of information (in order):
   * 	1. filename for a PDF to be added,
   *  	2. filename for the original PDF that is added to,
   *   	3. either "Begin" or "End", denoting whether to attach the first file to the beginning or end of the original.
   * The two PDF files (if they exist) are combined in the desired order to create a new PDF file, which replaces the original 
   * (or "to be added to") file.  The new combined file is then copied into the locations provided in the destinationPathNames 
   * input.  Both the "to be added" and the original "to be added to" files are deleted, as well as the .txt file, leaving only 
   * the newly combined PDF file.
   * 
   * @param inboundFolder:					a String pathname of the folder to search for the .txt file
   * 
   * @param destinationPathNames:			a list of String pathnames of the folders to copy the newly combined file into
   * 
   * @author James Watts
   * Last Updated: June 23rd, 2015
   */
  def merge(inboundFolder:String, destinationPathNames:List[String]):Unit = {    
    // If a Unix-based computer is being used (i.e. Linux or Macintosh), the folder separator String is "/"
    // Otherwise, if a Windows computer is being used, "\" is used as the folder separator
    val folderSeparator = File.separator
    
    var filesList = Array[File]()
    
    try{
      filesList = (new File(inboundFolder)).listFiles				// Obtain all the contents of the inbound folder
    }
    catch{
      case ex:FileNotFoundException => 								// If inbound folder could not be opened,
        logger.error(s"${ex.getMessage()}\n")						// Log the error, pause the timer, and return
        guiUpdater ! PauseTimer(true)
        return
      case ex:Exception =>											// If any other error,
        logger.error(s"${ex.getMessage()}\n")						// Log the error, pause the timer, and return
        guiUpdater ! PauseTimer(true)
        return
    }
    
    for(txtfile<-filesList if txtfile.getName.endsWith(".txt"))		// Find all .txt files and iterate through each
    {
      try{
	    // Read from the .txt file:
    	val txtSrc = Source.fromFile(txtfile)
	    val infoList = separateString(deleteQuotes(txtSrc.mkString))// The three items of the text file in a List
	    logger.info(s"Source Text File: $txtfile")
	    
	    // Find FileToAttach = infoList(0)
	    val FileToAttach:File = new File(infoList(0).trim())
    	if (!FileToAttach.isFile()){								// If the File to Attach is not a valid file
    	  txtSrc.close()											// Close the txtSrc and throw an exception
    	  throw new FileNotFoundException(s"File not found: $FileToAttach")
    	}
    	if (!infoList(0).toLowerCase.endsWith(".pdf")){				// If the File to Attach is not a .pdf file
    	  txtSrc.close()											// Close the txtSrc and throw an exception
    	  throw new IllegalArgumentException("""File To Attach is not in the form ".pdf"""")
    	}
    	logger.debug(s"File to Attach: $FileToAttach")
    	
	    // OriginalFile = infoList(1)
	    val OriginalFile:File = new File(infoList(1).trim())
    	if (!OriginalFile.isFile()){								// If the Original File is not a valid file
    	  txtSrc.close()											// Close the txtSrc and throw an exception
    	  throw new FileNotFoundException(s"File not found: $OriginalFile")
    	}
    	if (!infoList(1).toLowerCase.endsWith(".pdf")){				// If the Original File is not a .pdf file
    	  txtSrc.close()											// Close the txtSrc and throw an exception
    	  throw new IllegalArgumentException("""Original File is not in the form ".pdf"""")
    	}
    	logger.debug(s"Original File: $OriginalFile")
    	
	    // Begin/End = infoList(2)
	    val BeginEnd:String = infoList(2).toLowerCase.trim()
	    if(!(BeginEnd.equals("begin") || BeginEnd.equals("end"))){	// The third string must be "Begin" or "End" (case does not matter)
	      txtSrc.close()											// Otherwise, close the txtSrc and throw an exception
	      throw new IllegalArgumentException("""Third String must by "Begin" or "End"""")
	    }
	    logger.debug(BeginEnd)
	    
	    val beginTrue = (BeginEnd.equals("begin"))					// Boolean true if "Begin", false if "End"
	    
	    // Close the .txt file
	    txtSrc.close()
	    
	    // Parse the filename of original (to be used later)
	    val ParsedOriginalName = OriginalFile.getName				// Parsed Original Name
	    logger.debug(s"Parsed File Name: $ParsedOriginalName")
	    
	    // Determine the "Master" and "Append" files using the "BeginEnd" field
	    val Master = if(beginTrue) FileToAttach else OriginalFile
	    val Append = if(beginTrue) OriginalFile else FileToAttach
	    logger.info(s"Master File: $Master")
	    logger.info(s"Append File: $Append")
	    
	    // Open Master PDF file
	    val MasterSrc = Source.fromFile(Master)
	    
	    // Open Append PDF file
	    val AppendSrc = Source.fromFile(Append)
	    
	    // Count # of pages of Master
	    val MasterPDF = PDDocument.load(Master)
	    val MasterPageLength = MasterPDF.getNumberOfPages()
	    logger.info(s"Master PDF has $MasterPageLength pages")
	    
	    // Count # of pages of Append
	    val AppendPDF = PDDocument.load(Append)
	    val AppendPageLength = AppendPDF.getNumberOfPages()
	    logger.info(s"Append PDF has $AppendPageLength pages")
	    
	    // Add # of pages to get new total page #
	    logger.info(s"Combined PDF has ${MasterPageLength + AppendPageLength} pages")
	    
	    // Append "Append" to end of "Master"
	    // Create a PDFMergerUtility object and set the destination path name
	    val merger = new PDFMergerUtility()
	    merger.setDestinationFileName(s"${destinationPathNames(0)}$folderSeparator$ParsedOriginalName")
	    
	    merger.addSource(Master)
	    merger.addSource(Append)
	    
	    // Close and Delete Original file
	    if(beginTrue)
	    {
	      AppendSrc.close
	      AppendPDF.close
	      Append.delete
	    }
	    else
	    {
	      MasterSrc.close
	      MasterPDF.close
	      Master.delete
	    }
	    
    	// Merge the files (Here's where the real magic happens)
	    merger.mergeDocuments()
	    
	    val combinedFile = new File(s"${destinationPathNames(0)}$folderSeparator$ParsedOriginalName")
	    
	    /* Copy new Combined File into the other 3 destination locations: */
	    for(i <- 1 until destinationPathNames.size)
	      FileUtils.copyFile(combinedFile, new File(s"${destinationPathNames(i)}$folderSeparator$ParsedOriginalName"))
	    
    	// Close and Delete File to Attach
	    if(beginTrue)
	    {
	      MasterSrc.close
	      MasterPDF.close
	      Master.delete
	    }
	    else
	    {
	      AppendSrc.close
	      AppendPDF.close
	      Append.delete
	    }
	    
	    // Finally, delete the .txt file
	    txtfile.delete
	    
	    logger.info("Merge Successful")
      }
      catch{
        // For any thrown exception, print the error message and pause the timer.  The timer will remain paused until 
        // either the Settings window is closed or the AddPDF window is closed and reopened.
        case ex:FileNotFoundException => 							// If one of the PDF files listed in the text file
          logger.error(s"${ex.getMessage()}\n")						// is not found
          guiUpdater ! PauseTimer(true)
          return
        case ex:IllegalArgumentException => 						// If "Begin"/"End" was not found in the text file
          logger.error(s"${ex.getMessage()}\n")						// or if either of the file names was not a .pdf
          guiUpdater ! PauseTimer(true)
          return
        case ex:Exception => 										// In case of any other Exceptions
          logger.error(s"${ex.getMessage()}\n")
          guiUpdater ! PauseTimer(true)
          return
      }
    }
  }
  
  /**
   * Private helper method to separate the items in a long String into a list of separate Strings that are "," delimited.
   * 
   * @param line							A String that contains items separated by commas
   * 
   * @return								A list of separated Strings that were previously "," delimited
   * 
   * @author James Watts
   * Last Updated: May 27th, 2015
   */
  private def separateString(line: String) = line.split(",").toList
  
  /**
   * Private helper method to remove quotation marks from inside of a String.
   * 
   * @param line							A raw String containing quotation marks
   * 
   * @return								The original raw String without any quotation marks
   * 
   * @author James Watts
   * Last Updated: November 13th, 2014
   */
  private def deleteQuotes(line: String) = line.replaceAll(""""""", "")
  
  /**
   * If the timer is running, this method loops through the folders in currentInboundFolders and runs the countTextFiles 
   * method on each inbound folder in order to obtain a set and count of all .txt files contained within the inbound folders.
   * 
   * This information is then passed in a message to the LabelUpdator so it can update the filesWaitingCountLabel 
   * and filesWaitingListBox.
   * 
   * @author James Watts
   * Last Updated: March 30th, 2015
   */
  private def updateFilesWaiting() {
    if(!pauseTimer)	{											// If the pauseTimer flag is false (the timer is running)
      filesWaitingSet = Set[String]()							// Reset the filesWaitingSet
      for(index <- 0 until currentInboundFolders.length; 		// Loop through the Inbound folders
    	if currentInboundFolders(index) != null) 
      	{
    	  val (s, c) = countTextFiles(new File(					// Use the countTextFiles method to obtain a set
    	  	currentInboundFolders(index)).listFiles)			// and count of text files to be processed
    	  guiUpdater ! FilesWaiting(s, c)						// Send a message to the LabelUpdater to update the
      	}														// labels accordingly
    }
  }
  
  /**
   * Searches through an input array of File objects, counts the number of files with the .txt extension, and returns a
   * tuple containing a set of the .txt Files and its size.  Updates the filesWaitingSet field in the process.
   * 
   * @param filesList						An array of File objects
   * 
   * @return 								A (Set[String], Int) tuple representing a Set of the .txt file names found in the
   * 										input array and a count of those .txt files.
   * 
   * @author James Watts
   * Last Updated: January 16th, 2015
   */
  private def countTextFiles(filesList: scala.Array[File]) = 
  {
    for(txtfile <- filesList if txtfile.getName.endsWith(".txt")) {			// Loop through the array of files
      filesWaitingSet += (txtfile.getName())								// Update the set of text files waiting
    }
    (filesWaitingSet,filesWaitingSet.size)									// Return the set with its size
  }
  
  /**
   * Method that reports the status of the PDF Merger by sending information to the 'dbTable' table in the database
   * having the pathname of 'dbPath' (both of these parameters - as well as database username and password - are set
   * in the application.CONF file).
   * 
   * @author James Watts
   * Last Updated: June 30th, 2015
   */
  def reportStatus() {
    var conn:Connection = null
    try{
    	val separatorIfNeeded = if(dbPath.endsWith("/") || dbPath.endsWith("\\")) "" else File.separator
    	
    	if (dbType == H2_DATABASE)
    	{
    	  val driverClass = "org.h2.Driver"
          val jdbcPrefix = """jdbc:h2:file:"""
          
          // Get the Driver class and establish a connection to the database
          Class.forName(driverClass)			// DB pathname 									// username // password
          conn = DriverManager.getConnection(s"$jdbcPrefix$dbPath$separatorIfNeeded$databaseName", dbUser, 		dbPswd)
    	}
    	else if(dbType == MS_SQL_DATABASE)
    	{
    	  val driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    	  val jdbcPrefix = """jdbc:sqlserver://"""
          val databaseNamePrefix = ";databaseName="
          val ipaddress = parseOutIP(dbPath)
          
          Class.forName(driverClass)			// DB pathname 										// username // password
	      conn = DriverManager.getConnection(s"$jdbcPrefix$ipaddress$databaseNamePrefix$databaseName;", dbUser, 	dbPswd)
    	}
    	else if(dbType == NO_TYPE)
    	{
    	  logger.warn("No database to report status to")
    	  return
    	}
    	
	    // 'trans_id' will be generated automatically, so no need to worry about that
	    // 'last_reported' will be the current date and time
    	// H2 databases use NOW() to receive the current date and time while MS SQL Server databases use GETDATE()
    	val currentDateFunction = dbType match{
    	  case MS_SQL_DATABASE	=> "GETDATE()"
    	  case H2_DATABASE		=> "NOW()"
    	}
	    
	    // 'machine_name' will be "PDF(" + the machine name + ")"
	    val machineName = getMachineName()
	    
	    val queryStatement = conn.prepareStatement(
	        s"SELECT * FROM $dbTable WHERE Application = '$app' AND Machine_Name = '$machineName'")
	    val updateStatement = conn.prepareStatement(
	        s"UPDATE $dbTable SET Last_Reported = $currentDateFunction WHERE Application = '$app' AND Machine_Name = '$machineName'")
	    val insertStatement = conn.prepareStatement(
	        s"INSERT INTO $dbTable (application, last_reported, machine_name, comments, status) VALUES ('$app', $currentDateFunction, '$machineName', '${" "*255}', '${" "*25}')")
	    
	    // Query to see if the machine name (with the given application (PACKAGE CREATOR)) is in the table already
	    val results = queryStatement.executeQuery()
	    
	    if(results.next)
	    {
	      /* If the row already exists, just update its last_reported column */
	      updateStatement.executeUpdate()
	    }
	    else
	    {
	      /* If the row hasn't been created yet, insert a new row into the 'dbTable' table with the gathered information */
	      insertStatement.executeUpdate()
	    }
	    
	    logger.debug("Reported Status to Database")
//	    println("I'm working :)")	// Helpful console message
    }
    catch
    {
      case e:Exception => logger.error(s"Problem connecting to the database: ${e.getMessage}")
    }
    finally
    {
      // Make sure to close the database connection whether the queries worked or not
      if(conn != null)
    	  conn.close()
    }  
  }
  
  /**
   * When given a String folder pathname that begins with a an IP Address, this method parses out the IP Address and returns it
   * as a String.
   * 
   * @param pathWithIP						A String folder pathname that begins with an IP Address (or "C:")
   * 
   * @return								The IP Address contained in the input String as a String
   * 
   * @author James Watts
   * Last Updated: June 23rd, 2015
   */
  def parseOutIP(pathWithIP:String):String = 
  {
    var ipAddress:String = ""
    if(pathWithIP.startsWith("""\\""") || pathWithIP.startsWith("""//"""))
    {
      val endpoint = (pathWithIP.substring(2)).indexOf(File.separator) + 2		// Index of the end of the IP Address
      ipAddress = pathWithIP.substring(2, endpoint)								// Get the IP Address
    }
    else if(pathWithIP.startsWith("""\""") || pathWithIP.startsWith("""/"""))
    {
      val endpoint = (pathWithIP.substring(1)).indexOf(File.separator) + 1		// Index of the end of the IP Address
      ipAddress = pathWithIP.substring(1, endpoint)								// Get the IP Address
    }
    else
    {
      val endpoint = pathWithIP.indexOf(File.separator)							// Index of the end of the IP Address
      ipAddress = pathWithIP.substring(0, endpoint)								// Get the IP Address
    }
    
    /* If the 'IP Address' obtained is actually just 'C:', then return 'localhost' */
    if(ipAddress == "C:") "localhost" else ipAddress
  }
  
  /**
   * Method to retrieve the name of the user's machine and parse it to a string in the form of 'PDF (machineName)'.
   * Makes sure that there are exactly 25 characters in the string by adding whitespace to pad the end of the string.
   * 
   * @author James Watts
   * Last Updated: May 19th, 2014
   */
  def getMachineName():String = 
  {
    try{
      val compName = java.net.InetAddress.getLocalHost().getHostName()	// Get the machine name
      val machineName = s"PDF ($compName)"								// Surround it with "PDF (" and ")"
      
      // Account for a machine name that's too long
      val returnName = if (machineName.length > 25) (s"${machineName.substring(0, 24)})") else machineName
      val paddingAmount = (25 - returnName.length())					// Calculate the amount of needed padding
      s"$returnName${" " * paddingAmount}"								// Return the machine name with the padding
    }
    catch{
      case (ex:Exception) => "PDF (Could not get name) "				// Hopefully, this line should never be reached
    }
  }
  
  /**
   * Depending on whether the pauseTimer is set to false or true, count() either counts down and runs the merge method 
   * (or reportStatus method) or resets and pauses the GUI counter.
   * 
   * When the pauseTimer is set to false, it counts down from SettingsGUI.checkFilesTime to zero, decrementing by 1
   * every time it is run (once per second in this application).  Once the count reaches zero, the merge method is
   * run using the lists currentInboundFolders and currentOutboundFolders. The counter is then restarted.  The 
   * updateFilesWaiting method is called right before and after the merge method is run.
   * 
   * The timeToNextReport counter is also decremented from reportStatusTime to zero, decrementing by 1 every time it 
   * is run.  Once this count reaches zero, the reportStatus method is called and the timeToNextReport counter is reset 
   * to reportStatusTime.
   * 
   * When the pauseTimer is set to true, it resets the timer and report status timer to checkFilesTime and reportStatusTime, 
   * respectively, and pauses these timers until pauseTimer is set back to false.
   * 
   * The timerLabel and reportTimerLabel are updated every time this method runs.
   * 
   * @author James Watts
   * Last Updated: May 27th, 2015
   */
  def count() {
    if(!pauseTimer)														// If the pauseTimer flag is false (counter not paused)
    {
      if(timeToNextCheck<=0)
      {																	// If the timeToNextCheck counter reached zero (or below)
        for(index <- 0 until currentInboundFolders.length; 				// Loop through the inbound folders
          if currentInboundFolders(index) != null)
           merge(currentInboundFolders(index),currentOutboundFolders)	// Start the merging process
        
        timeToNextCheck=checkFilesTime									// Restart the timer
        guiUpdater ! Count(generateCountString(timeToNextCheck))		// Update the timer label
        updateFilesWaiting()											// Update the list and count of files waiting
      }
      else 
      {																	// If the timeToNextCheck counter has not reached zero yet
        if(timeToNextCheck == 1) updateFilesWaiting()					// Update the files waiting if we're at 1 second left
        timeToNextCheck-=1												// Decrement the timer by 1 second
        guiUpdater ! Count(generateCountString(timeToNextCheck))		// Update the timer label
        
      }
      if(timeToNextReport<=0)
      {																	// If the timeToNextReport counter reached zero (or below)
        reportStatus()													// Report the status 
        timeToNextReport = reportStatusTime								// Restart the counter
        guiUpdater ! ReportCount(generateCountString(timeToNextReport))	// Update the report timer label
      }
      else
      {																	// If the timeToNextReport counter has not reached zero yet
        timeToNextReport-=1												// Decrement the counter by 1 second
        guiUpdater ! ReportCount(generateCountString(timeToNextReport))	// Update the report timer label
      }
    }
    else
    {																	// If the pauseTimer is true (counter is paused)
      timeToNextCheck = checkFilesTime 									// Restart both timers and keep them at their max values
      timeToNextReport = reportStatusTime
      guiUpdater ! Count(generateCountString(timeToNextCheck))			// Update the two timer labels
      guiUpdater ! ReportCount(generateCountString(timeToNextReport))
    }
  }
  
  /**
   * Converts an input amount of seconds to an easy-to-read string displaying the minutes and seconds (formatted as MM:SS).
   * 
   * @param time				Int representing the number of seconds to convert to minutes and seconds
   * 
   * @return					A String representation of minutes and seconds for a timer label
   * 
   * @author James Watts
   * Last Updated: April 10th, 2015
   */
  def generateCountString(time:Int):String = {
    val minutes = time/60										// Get the minutes and seconds based off of the input time
    val seconds = time%60
    
    val minutesString:String = if(minutes>9) minutes.toString else s"0$minutes"
    val secondsString:String = if(seconds>9) seconds.toString else s"0$seconds"
    
    s"$minutesString:$secondsString"							// Return the count string formatted as MM:SS
//    s"${if(minutes>9) minutes.toString else s"0$minutes"}:${if(seconds>9) seconds.toString else s"0$seconds"}" // Alternate method
  }
  
  /**
   * Method used to update fields according to text box inputs in the Settings GUI.  The fields that are updated 
   * are as follows:
   * 		1. currentInboundFolders
   * 		2. currentOutboundFolders
   *   		3. box2Checked
   *    	4. box3Checked
   *     	5. box4Checked
   *     	6. checkFilesTime
   *    	7. reportStatusTime
   *     	8. dbPath / databaseName
   * This method also checks for the validity of all changes before the actual fields are updated.  If any of the
   * inbound or outbound folders are invalid or duplicates, the first five fields listed will not be updated.
   * Fields 6-8 are considered individually and each will be updated if its respective change results in a valid
   * value for that field (for example, a positive integer for checkFilesTime or reportStatusTime).
   * 
   * @param inbound1 - inbound4			String pathnames to the four inbound folders.  Empty Strings are 
   * 									allowed for all but inbound1.  Must be non-null.
   * @param PDF1 - PDF4					String pathnames to the four outbound (PDF) folders.  Empty Strings 
   * 									are not allowed.  Must be non-null.
   * @param inbound2Checked				Boolean denoting if the 2nd inbound folder is to be included in the 
   * 									currentInboundFolders list.
   * @param inbound3Checked				Boolean denoting if the 3rd inbound folder is to be included in the 
   * 									currentInboundFolders list.
   * @param inbound4Checked				Boolean denoting if the 4th inbound folder is to be included in the 
   * 									currentInboundFolders list.
   * @param checkFilesTimeString		String representation of the check files time amount.  Must be a 
   * 									positive integer to be valid.
   * @param reportStatusTimeString		String representation of the report status time amount.  Must be a 
   * 									positive integer to be valid.
   * @param dbPathName					String pathname of the folder the Database is found in.
   * @param dbName						String name of Database to report to.
   * 
   * @author James Watts
   * Last Updated: June 23rd, 2015
   */
  def applyChanges(	inbound1:String, inbound2:String, inbound3:String, inbound4:String, 
		  			PDF1:String, PDF2:String, PDF3:String, PDF4:String, 
		  			inbound2Checked:Boolean, inbound3Checked:Boolean, inbound4Checked:Boolean,
		  			checkFilesTimeString:String, reportStatusTimeString:String, dbPathName:String, dbName:String)
  {
    val tempInboundList = MutableList(inbound1)				// Create a new list for inbound folders,
         													// starting with the primary folder.
    
    if(inbound2Checked && !inbound2.isEmpty)				// If check box for second inbound folder is checked,	
      tempInboundList += inbound2							// and it is not an empty string,
    else													// add the second inbound folder to the inbound list.
      tempInboundList += null								// Otherwise, add a null.
    
    if(inbound3Checked && !inbound3.isEmpty)				// If check box for third inbound folder is checked,
      tempInboundList += inbound3							// and it is not an empty string,
    else													// add the third inbound folder to the inbound list.
      tempInboundList += null								// Otherwise, add a null.
    
    if(inbound4Checked && !inbound4.isEmpty)				// If check box for fourth inbound folder is checked,
      tempInboundList += inbound4							// and it is not an empty string,
    else													// add the fourth inbound folder to the inbound list.
      tempInboundList += null								// Otherwise, add a null.
    
    val tempOutboundList = List(PDF1, PDF2, PDF3, PDF4)		// Create a new list for outbound folders with all 4
       														// of the updated PDF folder names.
    
    /* Check folders for validity and for no duplicates.  If all is good, set the currentInboundFolders and 
     * currentOutboundFolders lists to be the new inputs.  Otherwise, they will remain unchanged. */
    if(checkFolderValidity(tempInboundList, tempOutboundList) && checkFolderDuplicates(tempInboundList, tempOutboundList))
    {
      currentInboundFolders = tempInboundList
      currentOutboundFolders = tempOutboundList
      
      /* Update status of checked/unchecked boxes:									*
       * 	Any box is considered unchecked if the inbound textbox below is empty.	*
       * 	Otherwise, the boxXchecked field's value is changed to inboundXChecked.	*/
      box2checked = (currentInboundFolders(1) != null) && inbound2Checked
      box3checked = (currentInboundFolders(2) != null) && inbound3Checked
      box4checked = (currentInboundFolders(3) != null) && inbound4Checked
    }
    
    try{
      val temp = checkFilesTime
      checkFilesTime = checkFilesTimeString.toInt			// Update data from 'check new files' text box only if a 
      if(checkFilesTime <= 0)								// positive integer is typed into the box.  Otherwise, 
      {														// don't update and instead show an error message.
        checkFilesTime = temp
        throw new NumberFormatException()
      }
    }														
    catch{													
      case ex: NumberFormatException => 
        if(SettingsIsRunning) SettingsGUI.invalidTimeDialog(true)
    }
    
    try{
      val temp = reportStatusTime
      reportStatusTime = reportStatusTimeString.toInt		// Update data from 'report status' text box only if a
      if(reportStatusTime <= 0)								// positive integer is typed into the box.  Otherwise,
      {														// don't update and instead show an error message.
        reportStatusTime = temp
        throw new NumberFormatException()
      }
    }
    catch{
      case ex: NumberFormatException => 
        if(SettingsIsRunning) SettingsGUI.invalidTimeDialog(false)
    }
    
    /* Test the validity of the database */
    val tempDbType = checkDatabaseValidity(dbPathName, dbName)
    if(tempDbType == MS_SQL_DATABASE || tempDbType == H2_DATABASE) // Update data from database text box only if the database is valid.
    {
      databaseName = dbName
	  dbPath = dbPathName
	  dbType = tempDbType
    }
    
    guiUpdater ! Inbound(currentInboundFolders(0))			// Send a message to the LabelUpdater to update the inboundFolderLabel
    														// to include the first currentInboundFolder.
    
    saveSettingsToConfigFile()								// Save the current settings to the CONFIG file.
  }
  
  /** 
   * Checks each member of the Inbound and Outbound folders lists and makes sure all folders listed exist on their respective 
   * servers.  Displays an error Dialog if any folder listed does not exist.
   * 
   * @param inboundList				MutableList of inbound folders (may contain nulls, but not empty strings)
   * @param outboundList			List of outbound folders (must not contain nulls or empty strings)
   * 
   * @return						Boolean true if all folders in the inboundList and outboundList lists are valid folders.
   * 								False if any inbound or outbound folder listed is not valid.
   * 
   * @author James Watts
   * Last Updated: April 3rd, 2015
   */
  def checkFolderValidity(inboundList:MutableList[String], outboundList:List[String]):Boolean = {	
    var allFoldersAreValid = true								// Check if all of the inbound folders are valid folders.
    for(folder <- inboundList if folder!=null)
      if( !(new File(folder).isDirectory) )						// Search through all non-null inbound folders.  If any
      {															// inbound folder is not a valid directory, display an
        if(SettingsIsRunning)									// error and return a false.
          SettingsGUI.invalidFolderDialog(folder, true)
        allFoldersAreValid = false
      }
    
    for(folder <- outboundList)
      if( !(new File(folder).isDirectory) )						// Search through all outbound folders.  If any outbound
      {															// folder is not a valid directory, display an error
        if(SettingsIsRunning)									// and return a false.
          SettingsGUI.invalidFolderDialog(folder, false)
        allFoldersAreValid = false
      }
    
    allFoldersAreValid											// Return the Boolean flag
  }
  
  /**
   * Checks for duplicates in both the Inbound and Outbound folders lists.  Displays an error Dialog if any duplicates
   * appear in either list.
   * 
   * @param inboundList				MutableList of inbound folders (may contain nulls, but not empty strings)
   * @param outboundList			List of outbound folders (must not contain nulls or empty strings)
   * 
   * @return						Boolean true if there are no duplicates, false if otherwise.
   * 
   * @author James Watts
   * Last Updated: February 9th, 2015
   */
  def checkFolderDuplicates(inboundList:MutableList[String], outboundList:List[String]):Boolean = {
    var allFoldersAreUnique = true								// Boolean flag, initialized at true.
    
    /* Sort each list first.  Then compare each entry with the immediate next entry to check for duplicates. */
    // Filter out all nulls from the inbound list and sort it
    var inbnd = List[String]()
    for(index <- 0 until inboundList.size if inboundList(index) != null)
      inbnd = inboundList(index) :: inbnd
    
    val inbound = inbnd.sorted
    
    // Traverse through sorted inbound list to find duplicates
    for(i <-1 until inbound.size)
    {
      if(inbound(i-1) == inbound(i))
      {
        if(SettingsIsRunning)
          SettingsGUI.duplicateFolderDialog(inbound(i), true)
        allFoldersAreUnique = false
      }
    }
    
    // Sort the outbound list
    val outbound = outboundList.sorted
    // Traverse through sorted outbound list to find duplicates
    for(i <- 1 until outbound.size)
    {
      if(outbound(i-1) == outbound(i))
      {
        if(SettingsIsRunning)
          SettingsGUI.duplicateFolderDialog(outbound(i), false)
        allFoldersAreUnique = false
      }
    }
    allFoldersAreUnique											// Return the Boolean flag
  }
  
  /**
   * Checks the validity of the database on the input file path.  Displays an error Dialog if the database is invalid.
   * 
   * @param dbPathName				String pathname of the folder the Database is found in.
   * @param dbName					String name of Database to report to.
   * 
   * @return						Boolean true if the Database exists and is valid, false if otherwise. (TODO)
   * 
   * @author James Watts
   * Last Updated: June 19th, 2015
   */
  private[attach_pdf] def checkDatabaseValidity(dbPathName:String, dbName:String):DatabaseType = 
  {
    val separatorIfNeeded = if(dbPathName.endsWith("/") || dbPathName.endsWith("\\")) "" else File.separator
    
    /* First check to see if the database file exists as a file and is of the right file type */
    if((new File(s"$dbPathName$separatorIfNeeded$dbName.mv.db")).isFile())	// If an H2 database...
    {
      // (type = H2_DATABASE)
      var driverClass = "org.h2.Driver"
      var jdbcPrefix = """jdbc:h2:file:"""
      var conn:Connection = null
      try{
    	/* Get the Driver class and establish a connection to the database */
	    Class.forName(driverClass)			// DB pathname 									// username // password
	    conn = DriverManager.getConnection(s"$jdbcPrefix$dbPathName$separatorIfNeeded$dbName", 	dbUser, 	dbPswd)
	    
	    val query = conn.prepareStatement(s"SELECT * FROM $dbTable WHERE Application = '$app' AND Machine_Name = '${getMachineName}'")
	    query.executeQuery()				// If this query works, then the database is a valid H2 database. Otherwise, invalid.
	    H2_DATABASE
      }
      catch
      {
        case e:Exception => 				// If connection cannot be made or the database does not contain the correct table,
          if(SettingsIsRunning) SettingsGUI.invalidDatabaseDialog(s"$dbPathName$separatorIfNeeded$dbName", true)
          NO_TYPE							// then display a message and return a NO_TYPE.
      }
      finally
      {
        if(conn != null)					// Make sure to close the database connection, if applicable.
          conn.close()
      }
    }
    else if((new File(s"$dbPathName$separatorIfNeeded$dbName.mdf")).isFile())		// If a Microsoft SQL Server database...
    {
      // (type = MS_SQL_DATABASE)
      val driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
      val jdbcPrefix = """jdbc:sqlserver://"""
      val databaseNamePrefix = ";databaseName="
      val ipaddress = parseOutIP(dbPath)

      var conn:Connection = null
      try{
      
    	/* Get the Driver class and establish a connection to the database */
	    Class.forName(driverClass)			// DB pathname 											// username // password
	    conn = DriverManager.getConnection(s"$jdbcPrefix$ipaddress$databaseNamePrefix$databaseName;", 	dbUser, 	dbPswd)
	    
	    val query = conn.prepareStatement(s"SELECT * FROM $dbTable WHERE Application = '$app' AND Machine_Name = '${getMachineName}'")
	    query.executeQuery()				// If this query works, then the database is a valid MS SQL database. Otherwise, invalid.
	    MS_SQL_DATABASE
      }
      catch
      {
        case e:Exception => 				// If connection cannot be made or the database does not contain the correct table,
          if(SettingsIsRunning) SettingsGUI.invalidDatabaseDialog(s"$dbPathName$separatorIfNeeded$dbName", true)
          NO_TYPE							// then display a message and return a NO_TYPE.
      }
      finally
      {
        if(conn != null)					// Make sure to close the database connection, if applicable.
          conn.close()
      }
    }
    else									// Non existent file or not of the right database file type
    {
      if(SettingsIsRunning) SettingsGUI.invalidDatabaseDialog(s"$dbPathName$separatorIfNeeded$dbName", false)
      NO_TYPE								// If it doesn't exist, return a NO_TYPE instead of trying to connect
    }
  }
  
  /**
   * Saves the current settings to the configuration file so that they remain unchanged the next time the application is run.
   * In order to do this, this method rewrites the CONFIG file from scratch.
   * 
   * @author James Watts
   * Last Updated: June 3rd, 2015
   */
  private def saveSettingsToConfigFile()
  {
    var pw:PrintWriter = null
    try{
      pw = new PrintWriter(new File("src/main/resources/application.conf"))				// Create a PrintWriter for the CONF file
      
      /* InboundFolders */
      pw.write("attachPDF {\r\n\tInboundFolders = [\r\n\t\t\"\"\"")
      pw.append(currentInboundFolders(0))
      pw.append("\"\"\"\r\n\t\t\"\"\"")
      if(currentInboundFolders(1) != null) pw.append(currentInboundFolders(1))			// Account for unchecked boxes
      pw.append("\"\"\"\r\n\t\t\"\"\"")
      if(currentInboundFolders(2) != null) pw.append(currentInboundFolders(2))
      pw.append("\"\"\"\r\n\t\t\"\"\"")
      if(currentInboundFolders(3) != null) pw.append(currentInboundFolders(3))
      
      /* OutboundFolders */
      pw.append("\"\"\"\r\n\t]\r\n\tOutboundFolders = [\r\n\t\t\"\"\"")
      pw.append(currentOutboundFolders(0))
      pw.append("\"\"\"\r\n\t\t\"\"\"")
      pw.append(currentOutboundFolders(1))
      pw.append("\"\"\"\r\n\t\t\"\"\"")
      pw.append(currentOutboundFolders(2))
      pw.append("\"\"\"\r\n\t\t\"\"\"")
      pw.append(currentOutboundFolders(3))
      pw.append("\"\"\"\r\n\t]\r\n")
      
      /* checkFilesTime and reportStatusTime */
      pw.append(s"\tcheckFilesTime = $checkFilesTime\r\n\treportStatusTime = $reportStatusTime\r\n")
      
      /* database */
      pw.append("\tdatabase {\r\n\t\ttype = \"")										// type
      pw.append(dbType.toString)
      pw.append("\"\r\n\t\tpathname = \"\"\"")											// pathname
      pw.append(dbPath)
      pw.append("\"\"\"\r\n\t\tname = \"")												// name
      pw.append(databaseName)
      pw.append("\"\r\n\t\ttable = \"")													// table
      pw.append(dbTable)
      pw.append("\"\r\n\t\tapplication = \"")											// application
      pw.append(app)
      pw.append("\"\r\n\t\tusername = \"")												// username
      pw.append(dbUser)
      pw.append("\"\r\n\t\tpassword = \"")												// password
      pw.append(dbPswd)
      pw.append("\"\r\n\t}\r\n}\r\n")
    }
    catch{
      case e:Exception => logger.error("Error in Saving the settings to the configuration file")
    }
    finally{
      // Make sure to flush and close the PrintWriter whether the write worked or not
      if(pw != null)
      {
        pw.flush()
        pw.close()
      }
    }
  }
  
  /**
   * Method to safely close the Utility object by shutting down the GUI label updater and stopping the timer.
   * 
   * @author James Watts
   * Last Updated: March 20th, 2015
   */
  def closeUtility()
  {
    guiUpdater ! "exit"										// Send an exit message to the Label Updater
    timer.stopTimer											// Stop the timer
  }
  
}