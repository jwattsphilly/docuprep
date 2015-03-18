package com.docuprep.attach_pdf

import swing._
import BorderPanel.Position.{North, South}
import event.ButtonClicked

import AddPDF_Util._

/**
 * Graphic User Interface for the User's Guide for the AddPDF application
 * 
 * @author James Watts
 * Last Updated: March 18th, 2014
 */
object UsersGuide extends SimpleSwingApplication {
  
  private val arraySize = 38
  
  def top = new MainFrame{										// Create a new MainFrame for the User's Guide
    title = "User's Guide"										// Entitle it
    
    /* STRINGS */
    val generalSelection = "General Help"
    val mainPageSelection = "Main Page"
    val settingsSelection = "Settings"
    
    /* Help Text for General Help */
    val generalText = new Array[String](arraySize)
    generalText(0) = "General Help..."	// TODO: Write up general help text
    generalText(1) = " "
    generalText(2) = " "
    generalText(3) = " "
    generalText(4) = " "
    generalText(5) = " "
    generalText(6) = " "
    generalText(7) = " "
    generalText(8) = " "
    generalText(9) = " "
    generalText(10) = " "
    generalText(11) = " "
    generalText(12) = " "
    generalText(13) = " "
    generalText(14) = " "
    generalText(15) = " "
    generalText(16) = " "
    generalText(17) = " "
    generalText(18) = " "
    generalText(19) = " "
    generalText(20) = " "
    generalText(21) = " "
    generalText(22) = " "
    generalText(23) = " "
    generalText(24) = " "
    generalText(25) = " "
    generalText(26) = " "
    generalText(27) = " "
    generalText(28) = " "
    generalText(29) = " "
    generalText(30) = " "
    generalText(31) = " "
    generalText(32) = " "
    generalText(33) = " "
    generalText(34) = " "
    generalText(35) = " "
    generalText(36) = " "
    generalText(37) = ""
    
    /* Help Text for Main Page Help */
    val mainPageText = new Array[String](arraySize)
    mainPageText(0) = "Main Page Help:"
    mainPageText(1) = " "
    mainPageText(2) = "Menu Bars:"
    mainPageText(3) = "    File -> Exit:"
    mainPageText(4) = "        Safely closes the Attach PDF application."
    mainPageText(5) = "    File -> Settings:"
    mainPageText(6) = "        Opens the Settings Graphic User Interface.  When Settings is running, the timers are paused."
    mainPageText(7) = "    Help -> User's Guide:"
    mainPageText(8) = "        Opens the interactive User's Guide for the Attach PDF application.  When the User's Guide"
    mainPageText(9) = "        is running, the timers are paused."
    mainPageText(10) = " "
    mainPageText(11) = "Buttons:"
    mainPageText(12) = "    Pause/UnPause:"
    mainPageText(13) = "        Pauses/Unpauses the countdown timers.  When the timers are paused, they are reset to"
    mainPageText(14) = "        their highest values."
    mainPageText(15) = "    Close:"
    mainPageText(16) = "        Safely closes the Attach PDF application."
    mainPageText(17) = " "
    mainPageText(18) = "Timers:"
    mainPageText(19) = "    On the bottom lefthand corner of the window, you will see two timers."
    mainPageText(20) = "    When the first timer, labeled \"next check in,\" reaches 0, the inbound folders set in Settings"
    mainPageText(21) = "    are searched for text files containing the necessary information to merge two PDF files.  The"
    mainPageText(22) = "    names of these text files are displayed in the white box underneath \"Files Waiting to be"
    mainPageText(23) = "    Processed.\"  The PDF files are then automatically merged and copied to the four outbound"
    mainPageText(24) = "    folders."
    mainPageText(25) = "    When the second timer, labeled \"next report in,\" reaches 0, the application reports its status"
    mainPageText(26) = "    to the database set in Settings."
    mainPageText(27) = " "
    mainPageText(28) = " "
    mainPageText(29) = " "
    mainPageText(30) = " "
    mainPageText(31) = " "
    mainPageText(32) = " "
    mainPageText(33) = " "
    mainPageText(34) = " "
    mainPageText(35) = " "
    mainPageText(36) = " "
    mainPageText(37) = ""
    
    /* Help Text for Settings Help */
    val settingsText = new Array[String](arraySize)
    settingsText(0) = "Settings Help:"
    settingsText(1) = " "
    settingsText(2) = "Text Fields:"
    settingsText(3) = "    There are a total of 11 text fields included in the Settings application.  The types and"
    settingsText(4) = "    descriptions of these fields are as follows:"
    settingsText(5) = " "
    settingsText(6) = "    Inbound Folders:"
    settingsText(7) = "        Pathnames to the folders to search for the .txt files containing the merge information.  Any"
    settingsText(8) = "        Inbound Folder text field (apart from the first one) that is left blank is treated as if the"
    settingsText(9) = "        checkbox above is unchecked, and that folder will not be included in the list of inbound"
    settingsText(10) = "        folders to check.  Duplicate folder names and nonexistant folder names are not accepted."
    settingsText(11) = """        If the desired folder pathname is on a remote server, prefix the pathname with a "\\""""
    settingsText(12) = "        followed by by the server's IP address."
    settingsText(13) = "    PDF Folders:"
    settingsText(14) = "        Pathnames to the folders to copy merged PDF files into.  All four of these fields must be"
    settingsText(15) = "        filled in.  Duplicate folder names are not accepted.  If the desired folder pathname is on a"
    settingsText(16) = """        remote server, prefix the pathname with a "\\" followed by by the server's IP address."""
    settingsText(17) = "    Check For New Files Every:"
    settingsText(18) = "        An amount of seconds for the timer to countdown from before a merge is run.  This field must"
    settingsText(19) = "        contain a positive whole number to be accepted."
    settingsText(20) = "    Report Status Every:"
    settingsText(21) = "        An amount of seconds for the timer to countdown from before reporting the application's"
    settingsText(22) = "        status to the database.  This field must contain a positive whole number to be accepted."
    settingsText(23) = "    Database:"
    settingsText(24) = "        The name of the database the Attach PDF application reports to.  The database must be valid"
    settingsText(25) = "        to be accepted."
    settingsText(26) = " "
    settingsText(27) = "Check Boxes:"
    settingsText(28) = "    When checked, the \"Inbound Folder\" fields directly beneath will be included in the list of"
    settingsText(29) = "    folders to be checked for .txt files.  When unchecked, the \"Inbound Folder\" fields directly"
    settingsText(30) = "    beneath will be excluded from that list."
    settingsText(31) = " "
    settingsText(32) = "Buttons:"
    settingsText(33) = "    Close:"
    settingsText(34) = "        Safely closes the Settings application."
    settingsText(35) = "    Apply:";
    settingsText(36) = "        Applies any and all valid text field changes made to the to the Attach PDF application."
    settingsText(37) = "";
    
    /* Buttons */
    val closeButton = new Button{								// Button to exit the window
      text = "Close"											// set its title
      tooltip = "Click to close this window"					// add a helpful message
    }
    val searchButton = new Button{
      text = "Okay"
    }
    
    /* ComboBox */
    val pullDownMenu = new ComboBox(List(generalSelection, mainPageSelection, settingsSelection))
    
    /* Labels */
    val helpText = new Array[Label](arraySize)
    for(i <- 0 until arraySize) helpText(i) = new Label(generalText(i))
    
    /* Vertical Box Panel to hold all help text */
    val HelpBox = new BoxPanel(Orientation.Vertical){
      for(i <- 0 until arraySize)
      {
        contents += new BoxPanel(Orientation.Horizontal){
          contents += helpText(i)
          contents += Swing.HGlue
        }
      }
    }
    
    /* Combine the lines of help text into one BoxPanel */
    val helpPanel = new BoxPanel(Orientation.Vertical){
      contents+=new BoxPanel(Orientation.Horizontal)
      {
        contents+=new Label("Welcome to the User's Guide.  What would you like help with?")
        contents+=Swing.HGlue
      }
      contents+=new BoxPanel(Orientation.Horizontal)
      {
        contents+=pullDownMenu
        contents+=Swing.HStrut(5)
        contents+=searchButton
      }
      contents+=Swing.VStrut(10)
      contents+=HelpBox
      
      border = Swing.EmptyBorder(0, 0, 20 ,0)					// Add some space at the bottom
    }
    
    /* Combine all panels and the close button and add a border */
    contents = new BorderPanel{
      layout(helpPanel) = North
      layout(closeButton) = South
      border = Swing.EmptyBorder(20,20,20,20)
    }
    
    size = new Dimension(610, 800)								// Set the size of the GUI window
    
    listenTo(closeButton, searchButton)
    
    reactions+={
      case ButtonClicked(`searchButton`) =>						// When the search button is pressed...
        val selection = pullDownMenu.selection.item				// Obtain the selection from the pull-down menu
        
        if(selection == generalSelection)						// If General Help is wanted, display general help text
        {
          for(i <- 0 until arraySize) helpText(i).text = generalText(i)
        }
        else if(selection == mainPageSelection)					// If Main Page Help is wanted, display main page help text
        {
          for(i <- 0 until arraySize) helpText(i).text = mainPageText(i)
        }
        else if(selection == settingsSelection)					// If Settings Help is wanted, display settings help text
        {
          for(i <- 0 until arraySize) helpText(i).text = settingsText(i)
        }
        
      
      case ButtonClicked(`closeButton`) => 						// When the close button is pressed...
        if(!SettingsIsRunning)
        {
          guiUpdater ! PauseTimer(false)						// Only unpause the timer if the Settings GUI is not running
        }
        guiUpdater ! UsersGuideRunning(false)					// Update the UsersGuideIsRunning flag to false
        close() 												// Close the window
    }
    
  }
}