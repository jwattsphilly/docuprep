package com.docuprep.attach_pdf

import swing._									// For GUI creation/editing
import BorderPanel.Position.{West, East}
import event.ButtonClicked

import AddPDF_Util._

/**
 * Graphic User Interface for the settings menu of the AddPDF application.
 * 
 * @author James Watts
 * Last Updated: September 11th, 2015
 */
private[attach_pdf] object SettingsGUI extends SimpleSwingApplication {
  
  def top = new MainFrame{
    title = "Settings"											// Set title
    
    private var tempDatabasePath = dbPath
    
    peer.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
    override def closeOperation() = { closeSettings() }			// Close the window instead of quitting the whole application
    
    // Labels for West Panel
    private val inbound1Label	= new Label("Inbound Folder: ")					// Inbound Folder (1) Label
    private val PDF1Label 		= new Label("PDF Folder:         ")				// PDF Folder (1) Label
    private val inbound2Label 	= new Label("Inbound Folder: ")					// Inbound Folder (2) Label
    private val PDF2Label 		= new Label("PDF Folder:         ")				// PDF Folder (2) Label
    private val inbound3Label 	= new Label("Inbound Folder: ")					// Inbound Folder (3) Label
    private val PDF3Label 		= new Label("PDF Folder:         ")				// PDF Folder (3) Label
    private val inbound4Label 	= new Label("Inbound Folder: ")					// Inbound Folder (4) Label
    private val PDF4Label 		= new Label("PDF Folder:         ")				// PDF Folder (4) Label
    private val location2Label 	= new Label("Second Location (if applicable)")	// Label to denote Second Location
    private val location3Label 	= new Label("Third Location (if applicable)")	// Label to denote Third Location
    private val location4Label 	= new Label("Fourth Location (if applicable)")	// Label to denote Fourth Location
    
    // Labels for East Panel
    private val checkNewFilesLabel	= new Label("Check For New Files Every:")	// Check for new files label
    private val reportStatusLabel	= new Label("Report Status Every:")			// Status report label
    private val secondsLabel1		= new Label(" Seconds.")					// Seconds Label (1)
    private val secondsLabel2		= new Label(" Seconds.")					// Seconds Label (2)
    private val databaseLabel		= new Label("Database: ")					// Database Label
    
    // Text Boxes for West Panel
    private val inbound1Text		= new TextField {			// Text field to input primary inbound folder to find .txt file
      text = currentInboundFolders(0)							// Initialize Inbound Folder 1
      columns = 30
    }
    private val inbound2Text		= new TextField {			// Text field to input secondary inbound folder to find .txt file
      if(currentInboundFolders(1) != null)						// Initialize Inbound Folder 2 if applicable
    	text = currentInboundFolders(1)
      columns = 30
    }
    private val inbound3Text		= new TextField {			// Text field to input third inbound folder to find .txt file
      if(currentInboundFolders(2) != null)						// Initialize Inbound Folder 3 if applicable
    	text = currentInboundFolders(2)
      columns = 30
    }
    private val inbound4Text		= new TextField {			// Text field to input fourth inbound folder to find .txt file
      if(currentInboundFolders(3) != null)						// Initialize Inbound Folder 4 if applicable
    	text = currentInboundFolders(3)
      columns = 30
    }
    private val PDF1Text			= new TextField {			// Text field to input primary PDF folder to copy the new PDF file into
      text = currentOutboundFolders(0) 							// Initialize PDF Folder 1
      columns = 30
    }
    private val PDF2Text			= new TextField {			// Text field to input secondary PDF folder to copy the new PDF file into
      text = currentOutboundFolders(1) 							// Initialize PDF Folder 2
      columns = 30
    }
    private val PDF3Text	 		= new TextField {			// Text field to input third PDF folder to copy the new PDF file into
      text = currentOutboundFolders(2)							// Initialize PDF Folder 3
      columns = 30
    }
    private val PDF4Text	 		= new TextField {			// Text field to input fourth PDF folder to copy the new PDF file into
      text = currentOutboundFolders(3)							// Initialize PDF Folder 4
      columns = 30
    }
    
    // Text Boxes for East Panel
    private val checkNewFilesText	= new TextField {			// Text field to input time lapse between checking for new files
      text = checkFilesTime.toString							// Initialize to checkFilesTime from AddPDF_Util
      columns = 5
    }
    private val reportStatusText 	= new TextField {			// Text field to input time lapse for reporting the current status
      text = reportStatusTime.toString							// Initialize to reportStatusText from AddPDF_Util
      columns = 5
    }
    private val databaseText	  	= new TextField {			// Text field to input database to report to
      text = databaseName										// Initialize to databaseName from AddPDF_Util
      columns = 10
    }
    
    // Check Boxes (initialize all to their most recent state)
    private val inbound2CheckBox	= new CheckBox{selected = box2checked}
    private val inbound3CheckBox	= new CheckBox{selected = box3checked}
    private val inbound4CheckBox	= new CheckBox{selected = box4checked}
    
    // Buttons
    private val closeButton 		 = new Button{				// Button to exit the window
      text = "Close"											// Set its title
      tooltip = "Click to close this window"					// Add a helpful message
    }
    private val applyButton 		 = new Button{				// Button to apply changes to text boxes
      text = "Apply"											// Set its title
      tooltip = "Click to apply changes"						// Add a helpful message
    }
    private val chooseFolderInbound1 = new Button{				// Button for folder selection for Inbound Folder 1
      text = "..."												// Set its title
      tooltip = "Choose a File Folder for Inbound Folder 1"		// Add a helpful message
    }
    private val chooseFolderInbound2 = new Button{				// Button for folder selection for Inbound Folder 2
      text = "..."												// Set its title
      tooltip = "Choose a File Folder for Inbound Folder 2"		// Add a helpful message
    }
    private val chooseFolderInbound3 = new Button{				// Button for folder selection for Inbound Folder 3
      text = "..."												// Set its title
      tooltip = "Choose a File Folder for Inbound Folder 3"		// Add a helpful message
    }
    private val chooseFolderInbound4 = new Button{				// Button for folder selection for Inbound Folder 4
      text = "..."												// Set its title
      tooltip = "Choose a File Folder for Inbound Folder 4"		// Add a helpful message
    }
    private val chooseFolderPDF1 	 = new Button{				// Button for folder selection for PDF Folder 1
      text = "..."												// Set its title
      tooltip = "Choose a File Folder for PDF Folder 1"			// Add a helpful message
    }
    private val chooseFolderPDF2 	 = new Button{				// Button for folder selection for PDF Folder 1
      text = "..."												// Set its title
      tooltip = "Choose a File Folder for PDF Folder 2"			// Add a helpful message
    }
    private val chooseFolderPDF3 	 = new Button{				// Button for folder selection for PDF Folder 1
      text = "..."												// Set its title
      tooltip = "Choose a File Folder for PDF Folder 3"			// Add a helpful message
    }
    private val chooseFolderPDF4 	 = new Button{				// Button for folder selection for PDF Folder 1
      text = "..."												// Set its title
      tooltip = "Choose a File Folder for PDF Folder 4"			// Add a helpful message
    }
    private val chooseDatabaseButton = new Button{				// Button for database selection
      text = "..."												// Set its title
      tooltip = "Choose a Database to report to"				// Add a helpful message
    }
    
    // Panels:
    // West Panel:
    private val westPanel = new BoxPanel(Orientation.Vertical){	// Panel for the West side of application
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for inbound folder 1
        contents+=inbound1Label									// add inbound folder 1 label
        contents+=inbound1Text									// add inbound folder 1 text box
        contents+=chooseFolderInbound1							// add inbound folder 1 file select button
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for PDF folder 1
        contents+=PDF1Label										// add PDF folder 1 label
        contents+=PDF1Text										// add PDF folder 1 text box
        contents+=chooseFolderPDF1								// add PDF folder 1 file select button
      }
      contents+=Swing.VStrut(15)								// Add a vertical space
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for location 2 label
        contents+=location2Label								// add location 2 label
        contents+=inbound2CheckBox								// add inbound 2 checkbox
        contents+=Swing.HGlue									// add horizontal glue to left justify
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for inbound folder 2
    	contents+=inbound2Label									// add inbound folder 2 label
    	contents+=inbound2Text									// add inbound folder 2 text box
    	contents+=chooseFolderInbound2							// add inbound folder 2 file select button
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for PDF folder 2
    	contents+=PDF2Label										// add PDF folder 2 label
    	contents+=PDF2Text										// add PDF folder 2 text box
    	contents+=chooseFolderPDF2								// add PDF folder 2 file select button
      }
      contents+=Swing.VStrut(15)								// Add a vertical space
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for location 3 label
    	contents+=location3Label								// add location 3 label
    	contents+=inbound3CheckBox								// add inbound 3 checkbox
    	contents+=Swing.HGlue									// add horizontal glue to left justify
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for inbound folder 3
    	contents+=inbound3Label									// add inbound folder 3 label
    	contents+=inbound3Text									// add inbound folder 3 text box
    	contents+=chooseFolderInbound3							// add inbound folder 3 file select button
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for PDF folder 3
    	contents+=PDF3Label										// add PDF folder 3 label
    	contents+=PDF3Text										// add PDF folder 3 text box
    	contents+=chooseFolderPDF3								// add PDF folder 3 file select button
      }
      contents+=Swing.VStrut(15)								// Add a vertical space
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for location 4 label
    	contents+=location4Label								// add location 4 label
    	contents+=inbound4CheckBox								// add inbound 4 checkbox
    	contents+=Swing.HGlue									// add horizontal glue to left justify
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for inbound folder 4
    	contents+=inbound4Label									// add inbound folder 4 label
    	contents+=inbound4Text									// add inbound folder 4 text box
    	contents+=chooseFolderInbound4							// add inbound folder 4 file select button
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for PDF folder 4
    	contents+=PDF4Label										// add PDF folder 4 label
    	contents+=PDF4Text										// add PDF folder 4 text box
    	contents+=chooseFolderPDF4								// add PDF folder 4 file select button
      }
      border = Swing.EmptyBorder(0,20,0,20)						// Set the border size (add space to left and right sides)
    }
    
    // East panel:
    private val eastPanel = new BoxPanel(Orientation.Vertical){	// Panel for the East side of application
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for Check New Files label
    	contents+=checkNewFilesLabel							// add Check New Files label
    	contents+=Swing.HGlue									// add horizontal glue to left justify
      }									
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for Check New Files text
    	contents+=checkNewFilesText								// add Check New Files text box
    	contents+=secondsLabel1									// add " Seconds."
      }
      contents+=Swing.VStrut(15)								// Add a vertical space
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for Report Status label
    	contents+=reportStatusLabel								// add Report Status label
    	contents+=Swing.HGlue									// add horizontal glue to left justify
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for Report Status text
    	contents+=reportStatusText								// add Report Status text box
    	contents+=secondsLabel2									// add " Seconds."
      }
      contents+=Swing.VStrut(30)								// Add a vertical space
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for Database input
        contents+=databaseLabel									// add "Database: "
        contents+=databaseText									// add Database text box
        contents+=chooseDatabaseButton							// add Database button
      }
      contents+=Swing.VStrut(130)								// Add a large vertical space
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for the two buttons
        contents+=closeButton									// add the Close Button
        contents+=Swing.HStrut(5)
        contents+=applyButton									// add the Apply Button
      }
      border = Swing.EmptyBorder(0,20,0,20)						// Set the border size (add space to left and right sides)
    }
    
    // Combination of all panels
    contents = new BorderPanel{
      layout(westPanel) = West									// West panel on West side
      layout(eastPanel) = East									// East panel on East side
      border = Swing.EmptyBorder(20,0,20,0)						// Add space on top and bottom
    }
    
    size = new Dimension(775, 400)								// Set the size of the GUI
    
    // Listen to all buttons and CheckBoxes
    listenTo(closeButton, applyButton, inbound2CheckBox, inbound3CheckBox, inbound4CheckBox,
        chooseFolderInbound1, chooseFolderInbound2, chooseFolderInbound3, chooseFolderInbound4,
        chooseFolderPDF1, chooseFolderPDF2, chooseFolderPDF3, chooseFolderPDF4, chooseDatabaseButton)
    
    reactions+={
      /* If the Close button is pressed, safely close the Settings Window. */
      case ButtonClicked(`closeButton`) => closeSettings
      
      /* If any of the 'chooseFolder' buttons are pressed, run the folderSelectionDialog method. */
      case ButtonClicked(`chooseFolderInbound1`) => folderSelectionDialog(inbound1Text)
      case ButtonClicked(`chooseFolderInbound2`) => folderSelectionDialog(inbound2Text)
      case ButtonClicked(`chooseFolderInbound3`) => folderSelectionDialog(inbound3Text)
      case ButtonClicked(`chooseFolderInbound4`) => folderSelectionDialog(inbound4Text)
      case ButtonClicked(`chooseFolderPDF1`) 	 => folderSelectionDialog(PDF1Text)
      case ButtonClicked(`chooseFolderPDF2`)	 => folderSelectionDialog(PDF2Text)
      case ButtonClicked(`chooseFolderPDF3`)	 => folderSelectionDialog(PDF3Text)
      case ButtonClicked(`chooseFolderPDF4`)	 => folderSelectionDialog(PDF4Text)
      
      /* If the chooseDatabaseButton is pressed, run the databaseSelectionDialog method */
      case ButtonClicked(`chooseDatabaseButton`) => databaseSelectionDialog()
      
      /* If the Apply button is pressed, run the applyChanges method, which updates fields according to new text box inputs. */
      case ButtonClicked(`applyButton`) =>
        applyChanges(	inbound1Text.text, inbound2Text.text, inbound3Text.text, inbound4Text.text,
        				PDF1Text.text, PDF2Text.text, PDF3Text.text, PDF4Text.text,
        				inbound2CheckBox.selected, inbound3CheckBox.selected, inbound4CheckBox.selected,
        				checkNewFilesText.text, reportStatusText.text, tempDatabasePath, databaseText.text	)
    }
    
    /**
     * Opens a File Chooser dialog in the path specified in tempDatabasePath.  Once a database file
     * is chosen from the File Chooser dialog, this method sets the text in databaseText to be the name
     * of the newly chosen database file and resets tempDatabasePath to be the pathname of the folder
     * the database file is found in.
     * 
     * @author James Watts
     * Last Updated September 11th, 2015
     */
    private def databaseSelectionDialog()
    {
      /* Create a FileChooser for the user to select the database from. */
      val filechooser = new FileChooser(new java.io.File(tempDatabasePath))
      filechooser.fileSelectionMode = FileChooser.SelectionMode.FilesOnly
      filechooser.fileFilter = new javax.swing.filechooser.FileNameExtensionFilter("Database", "db", "mdf") // Valid database files only
      
      if(filechooser.showDialog(null, "Select") == FileChooser.Result.Approve)
      {
        val nameOfDB = filechooser.selectedFile.getName()
        
        /* Account for different database types */
        var nameWithoutExtension = nameOfDB.stripSuffix(".db")
        nameWithoutExtension = nameWithoutExtension.stripSuffix(".mv")	// If an H2 database, the extension is .mv.db
        nameWithoutExtension = nameWithoutExtension.stripSuffix(".mdf")	// If an MS SQL Express database, the extension is .mdf (or .MDF)
        nameWithoutExtension = nameWithoutExtension.stripSuffix(".MDF")
        
        /* Set the database TextField's text to be the name of the newly chosen database and update the tempDatabasePath field */
        databaseText.text = nameWithoutExtension
        tempDatabasePath = filechooser.selectedFile.toString.stripSuffix(nameOfDB)
      }
    }
    
    /**
     * Opens a File Chooser dialog one level up from the file pathname specified in the input TextField
     * object.  Once a folder is chosen from the File Chooser dialog, this method resets the input 
     * TextField's text to be the pathname of the newly chosen folder.
     * 
     * @param tf							A TextField object
     * 
     * @author James Watts
     * Last Updated September 11th, 2015
     */
    private def folderSelectionDialog(tf:TextField)
    {
      /* Create a FileChooser for the user to select the folder from. */
      val filechooser = new FileChooser(new java.io.File(s"${tf.text}${java.io.File.separator}.."))
      filechooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
      
      /* Set the TextField's text to be the pathname of the newly chosen directory */
      if(filechooser.showDialog(null, "Select") == FileChooser.Result.Approve)
        tf.text = filechooser.selectedFile.toString
    }
    
    /**
     * Sets the timer to its most recent value (if User's Guide GUI is not also running) and closes the 
     * Settings window.  This method is called when either the close button or the red X in the corner is 
     * pressed.
     * 
     * @author James Watts
     * Last Updated: May 11th, 2015
     */
    private def closeSettings()
    {
      if(!UsersGuideIsRunning)									// If a User's Guide application is not also running,
        guiUpdater ! PauseTimer(pauseTimerLastValue)			// set pauseTimer to its most recent value.
      guiUpdater ! SettingsRunning(false)						// Set the SettingsIsRunning flag to false
      close()													// Close the window.
    }
  }
  
  /**
   * Displays an error Dialog when an inbound or outbound folder pathname is invalid.
   * 
   * @param folder							Invalid String pathname of a file folder
   * 
   * @param isInbound						Boolean denoting if the pathname is from the inbound or outbound folder list.
   * 										True if inbound, false if outbound.
   * 
   * @author James Watts
   * Last Updated: August 11th, 2015
   */
  protected[attach_pdf] def invalidFolderDialog(folder: String, isInbound: Boolean)
  {
    val inOut = if(isInbound) "Inbound" else "Outbound"
    
    Dialog.showMessage(top.contents.head,
        s"$folder is not a valid directory!",
        title = s"Error: Invalid $inOut Folder",
        messageType = Dialog.Message.Error)
  }
  
  /**
   * Displays an error Dialog when an inbound or outbound folder pathname is repeated.
   * 
   * @param folder							Invalid String pathname of a file folder
   * 
   * @param isInbound						Boolean denoting if the pathname is from the inbound or outbound folder list.
   * 										True if inbound, false if outbound.
   * 
   * @author James Watts
   * Last Updated: August 11th, 2015
   */
  protected[attach_pdf] def duplicateFolderDialog(folder: String, isInbound: Boolean)
  {
    val inOut = if(isInbound) "Inbound" else "Outbound"
    
    Dialog.showMessage(top.contents.head,
        s"$folder is repeated as an $inOut folder!",
        title = s"Error: $inOut Folder Repetition",
        messageType = Dialog.Message.Error)
  }
  
  /**
   * Displays an error Dialog when the Check Files Time or Report Status Time is invalid.
   * 
   * @param isCheckFilesTime				Boolean denoting if the Check Files Time or Report Status Time is invalid.
   * 										True if 'Check Files Time,' false if 'Report Status Time.'
   * 
   * @author James Watts
   * Last Updated: August 11th, 2015
   */
  protected[attach_pdf] def invalidTimeDialog(isCheckFilesTime: Boolean)
  {
    val timeType = if(isCheckFilesTime) "Check Files Time" else "Report Status Time"
    
    Dialog.showMessage(top.contents.head,
        s"$timeType must be a valid positive integer!",
        title = s"$timeType formatted incorrectly",
        messageType = Dialog.Message.Error)
  }
  
  /**
   * Displays an error Dialog when the database name is invalid.
   * 
   * @param dbPathWithName					Full pathname of the invalid database as a String.
   * @param dbExists						Boolean denoting if the database file exists in the file system or not.
   * 
   * @author James Watts
   * Last Updated: August 11th, 2015
   */
  protected[attach_pdf] def invalidDatabaseDialog(dbPathWithName:String, dbExists:Boolean)
  {
    val messagePart2 = 
      if(dbExists) "The database file does not contain the correct table or a connection could not be made." 
      else "The database file does not exist."
    
    Dialog.showMessage(top.contents.head,
        s"$dbPathWithName is not a valid database!  $messagePart2",
        title = "Database name invalid",
        messageType = Dialog.Message.Error)
  }
}