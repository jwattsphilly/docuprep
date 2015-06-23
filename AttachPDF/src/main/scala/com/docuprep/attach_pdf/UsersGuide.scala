package com.docuprep.attach_pdf

import swing._
import BorderPanel.Position.{North, South}
import event.ButtonClicked

import AddPDF_Util.{SettingsIsRunning,guiUpdater,pauseTimerLastValue}

/**
 * Graphic User Interface for the User's Guide for the AddPDF application
 * 
 * @author James Watts
 * Last Updated: June 12th, 2014
 */
object UsersGuide extends SimpleSwingApplication {
  
  private val arraySize = 42
  
  def top = new MainFrame{										// Create a new MainFrame for the User's Guide
    title = "User's Guide"										// Entitle it
    
    peer.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
    override def closeOperation() = { closeUsersGuide() }		// Close the window instead of quitting the whole application
    
    /* STRINGS */
    private val mainPageSelection = "Main Page"
    private val settingsSelection = "Settings"
    
    /* Help Text for Main Page Help */
    private val mainPageText = new Array[String](arraySize)
    mainPageText(0)  = "Main Page Help:"
    mainPageText(1)  = " "
    mainPageText(2)  = "Menu Bars:"
    mainPageText(3)  = "    File -> Exit:"
    mainPageText(4)  = "        Safely closes the Attach PDF application."
    mainPageText(5)  = "    File -> Settings:"
    mainPageText(6)  = "        Opens the Settings Graphic User Interface.  When Settings is running, the timers are paused."
    mainPageText(7)  = "    Help -> User's Guide:"
    mainPageText(8)  = "        Opens the interactive User's Guide for the Attach PDF application.  When the User's Guide"
    mainPageText(9)  = "        is running, the timers are paused."
    mainPageText(10) = " "
    mainPageText(11) = "Buttons:"
    mainPageText(12) = "    Pause/UnPause:"
    mainPageText(13) = "        Pauses/Unpauses the countdown timers.  When the timers are paused, they are reset to"
    mainPageText(14) = "        their maximum values."
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
    mainPageText(37) = " "
    mainPageText(38) = " "
    mainPageText(39) = " "
    mainPageText(40) = " "
    mainPageText(41) = ""
    
    /* Help Text for Settings Help */
    private val settingsText = new Array[String](arraySize)
    settingsText(0)  = "Settings Help:"
    settingsText(1)  = " "
    settingsText(2)  = "Text Fields:"
    settingsText(3)  = "    There are a total of 11 text fields included in the Settings application.  The types and"
    settingsText(4)  = "    descriptions of these fields are as follows:"
    settingsText(5)  = " "
    settingsText(6)  = "    Inbound Folders:"
    settingsText(7)  = "        Pathnames to the folders to search for the .txt files containing the merge information.  Any"
    settingsText(8)  = "        Inbound Folder text field (apart from the first one) that is left blank is treated as if the"
    settingsText(9)  = "        checkbox above is unchecked, and that folder will not be included in the list of inbound"
    settingsText(10) = "        folders to check.  Duplicate folder names and nonexistant folder names are not accepted."
    settingsText(11) = "        You may use the button on the right-hand side to select a folder from a file chooser dialogue"
    settingsText(12) = "        or simply type in the pathname of the desired folder."
    settingsText(13) = "    PDF Folders:"
    settingsText(14) = "        Pathnames to the folders to copy merged PDF files into.  All four of these fields must be"
    settingsText(15) = "        filled in.   Duplicate folder names and non-existent folder names are not accepted.  You may"
    settingsText(16) = "        use the button on the right-hand side to select a folder from a file chooser dialogue or"
    settingsText(17) = "        simply type in the pathname of the desired folder."
    settingsText(18) = "    Check For New Files Every:"
    settingsText(19) = "        An amount of seconds for the timer to countdown from before a merge is run.  This field must"
    settingsText(20) = "        contain a positive whole number to be accepted."
    settingsText(21) = "    Report Status Every:"
    settingsText(22) = "        An amount of seconds for the timer to countdown from before reporting the application's"
    settingsText(23) = "        status to the database.  This field must contain a positive whole number to be accepted."
    settingsText(24) = "    Database:"
    settingsText(25) = "        The name of the database the Attach PDF application reports to.  In order for a database to"
    settingsText(26) = "        be considered valid it must be either a Microsoft SQL Server database (*.mdf file extension)"
    settingsText(27) = "        or an H2 database (*.mv.db file extension). The database must also contain the correct table"
    settingsText(28) = "        for the application to report to.  You may use the button on the right-hand side to select a"
    settingsText(29) = "        database file from a file chooser dialogue."
    settingsText(30) = " "
    settingsText(31) = "Check Boxes:"
    settingsText(32) = "    When checked, the \"Inbound Folder\" fields directly beneath will be included in the list of"
    settingsText(33) = "    folders to be checked for .txt files.  When unchecked, the \"Inbound Folder\" fields directly"
    settingsText(34) = "    beneath will be excluded from that list."
    settingsText(35) = " "
    settingsText(36) = "Buttons:"
    settingsText(37) = "    Close:"
    settingsText(38) = "        Safely closes the Settings application."
    settingsText(39) = "    Apply:"
    settingsText(40) = "        Applies any and all valid text field changes made to the to the Attach PDF application."
    settingsText(41) = ""
    
    /* Buttons */
    private val closeButton = new Button{						// Button to exit the window
      text = "Close"											// Set its text
      tooltip = "Click to close this window"					// Add a helpful message
    }
    private val searchButton = new Button{						// Button for help selection (Main Page help / Settings help)
      text = "Okay"												// Set its text
    }
    
    /* ComboBox */
    private val pullDownMenu = new ComboBox(List(mainPageSelection, settingsSelection))
    
    /* Labels */
    private val helpText = new Array[Label](arraySize)			// Create labels for each line of help text
    for(i <- 0 until arraySize) helpText(i) = new Label(mainPageText(i))
    
    /* Vertical BoxPanel to hold all help text */
    private val HelpBox = new BoxPanel(Orientation.Vertical){
      for(i <- 0 until arraySize)
      {
        contents += new BoxPanel(Orientation.Horizontal){
          contents += helpText(i)								// Add each label to the HelpBox BoxPanel
          contents += Swing.HGlue								// And use HGlue to left-align
        }
      }
    }
    
    /* Combine all elements (apart from closeButton) into one BoxPanel */
    private val helpPanel = new BoxPanel(Orientation.Vertical){
      contents+=new BoxPanel(Orientation.Horizontal)
      {
        contents+=new Label("Welcome to the User's Guide.  What would you like help with?")
        contents+=Swing.HGlue
      }
      contents+=Swing.VStrut(2)									// Small vertical space between 'Welcome to User's Guide' and combo box
      contents+=new BoxPanel(Orientation.Horizontal)
      {
        contents+=pullDownMenu									// Add the pullDownMenu (the combo box)
        contents+=Swing.HStrut(5)								// Horizontal space between the combo box and the search button
        contents+=searchButton									// Add the search button to the right of the pullDownMenu
      }
      contents+=Swing.VStrut(10)								// Vertical space between the combo box and the help text
      contents+=HelpBox											// Add all of the help text
      
      border = Swing.EmptyBorder(0, 0, 20 ,0)					// Add some space at the bottom
    }
    
    /* Combine all panels and the close button and add a border */
    contents = new BorderPanel{
      layout(helpPanel) = North
      layout(closeButton) = South
      border = Swing.EmptyBorder(20,20,20,20)
    }
    
    size = new Dimension(610, 835)								// Set the size of the GUI window
    
    listenTo(closeButton, searchButton)							// Listen to the two buttons
    
    reactions+={
      case ButtonClicked(`searchButton`) =>						// When the search button is pressed...
        val selection = pullDownMenu.selection.item				// Obtain the selection from the pull-down menu
        
        if(selection == mainPageSelection)						// If Main Page Help is wanted, display main page help text
          for(i <- 0 until arraySize) helpText(i).text = mainPageText(i)
          
        else if(selection == settingsSelection)					// If Settings Help is wanted, display settings help text
          for(i <- 0 until arraySize) helpText(i).text = settingsText(i)
      
      case ButtonClicked(`closeButton`) => 						// When the close button is pressed, call the closeUsersGuide method
        closeUsersGuide()
    }
    
    /**
     * Unpauses the timer (if Settings GUI is not also running) and closes the User's Guide window.
     * This method is called when either the close button or the red X in the corner is pressed.
     * 
     * @author James Watts
     * Last Updated: May 8th, 2014
     */
    private def closeUsersGuide()
    {
      if(!SettingsIsRunning)									// If the Settings GUI is not also running,
          guiUpdater ! PauseTimer(pauseTimerLastValue)			// set pauseTimer to its most recent value.
      guiUpdater ! UsersGuideRunning(false)						// Update the UsersGuideIsRunning flag to false
      close() 													// Close the window
    }
  }
}