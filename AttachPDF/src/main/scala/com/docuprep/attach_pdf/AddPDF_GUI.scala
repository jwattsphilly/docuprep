package com.docuprep.attach_pdf

import swing._									// For GUI creation/editing
import BorderPanel.Position.{West, East}
import event.ButtonClicked

import AddPDF_Util._

/**
 * Graphic User Interface for the AddPDF application.
 * 
 * @author James Watts
 * Last Updated: August 17th, 2015
 */
object AddPDF_GUI extends SimpleSwingApplication {
  
  // Labels
  private 			   val filesWaitingLabel		= new Label("Files Waiting to be Processed:")
  private [attach_pdf] val inboundFolderLabel		= new Label(s"on ${currentInboundFolders(0)}, etc.")
  private [attach_pdf] val filesWaitingCountLabel	= new Label("0 files waiting")
  private [attach_pdf] val timerLabel				= new Label(s"next check in ${generateCountString(timeToNextCheck)}")
  private [attach_pdf] val reportTimerLabel			= new Label(s"next report in ${generateCountString(timeToNextReport)}")
  
  // Text Field
  private [attach_pdf] val filesWaitingListBox = new TextArea{	// TextArea for the files waiting list
    columns = 10
    rows = 10
  }
  
  def top = new MainFrame{										// Create the MainFrame
    title = "Attach PDF Document"								// Add a title
    
    peer.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
    override def closeOperation()								// Safely quit the whole application only if neither the Settings
    { 															// nor User's Guide GUIs are running
      if(!SettingsIsRunning && !UsersGuideIsRunning) closeGUISafely()
    }
    
    guiUpdater ! GuiRunning(true)								// Set the GuiIsRunning flag to true
    
    // Buttons
    private val closeButton = new Button{						// Close button	- quits the application
      text = "Close"											// Add text
      tooltip = "Click to exit"									// Add helpful hint
    }
    private val pauseButton = new Button{						// Pause button - pauses/unpauses the timer
      text = "Pause"											// Add text
      tooltip = "Click to pause and reset the timer"			// Add helpful hint
    }
    
    /* Panel for the West (left) side of application */
    private val westPanel = new BoxPanel(Orientation.Vertical){ // Add the following contents into a BoxPanel:
      contents+=new BoxPanel(Orientation.Horizontal){
        contents+=filesWaitingLabel								// Files Waiting label
        contents+=Swing.HGlue									// Horizontal Glue to keep the label left-aligned
      }
      contents+=new BoxPanel(Orientation.Horizontal){
        contents+=inboundFolderLabel							// Inbound Folder label
        contents+=Swing.HGlue									// Horizontal Glue to keep the label left-aligned
      }
      contents+=new ScrollPane(filesWaitingListBox)				// List of text files waiting to be processed, scrollable just in case
      contents+=new BoxPanel(Orientation.Horizontal){
        contents+=filesWaitingCountLabel						// Files Waiting Count label
        contents+=Swing.HGlue									// Horizontal Glue to keep the label left-aligned
      }
      contents+=new BoxPanel(Orientation.Horizontal){
        contents+=timerLabel									// Timer Label
        contents+=Swing.HGlue									// Horizontal Glue to keep the label left-aligned
      }
      contents+=new BoxPanel(Orientation.Horizontal){
        contents+=reportTimerLabel								// Report Timer Label
        contents+=Swing.HGlue									// Horizontal Glue to keep the label left-aligned
      }
    }
    
    /* Panel for the East (right) side of application */
    private val eastPanel = new BoxPanel(Orientation.Vertical){
      contents+=Swing.VStrut(375)								// Add a large vertical space to position the buttons at the bottom
      contents+=new BoxPanel(Orientation.Horizontal){			// of the GUI
        contents+=pauseButton
        contents+=Swing.HStrut(5)								// A little horizontal space between the two buttons
        contents+=closeButton
      }
    }
    
    /* Add the East and West Panels to the MainFrame */
    contents = new BorderPanel {								// Create a BorderPanel for the MainFrame
      layout(westPanel) = West
      layout(eastPanel) = East
      border = Swing.EmptyBorder(10,15,10,15)					// Set the border size
    }
    
    listenTo(closeButton, pauseButton)							// Listen to the close button and the pause button
    
    // Reactions
    reactions+={
      case ButtonClicked(`closeButton`) => 						// If the close button was clicked,
        if(!SettingsIsRunning && !UsersGuideIsRunning) 			// and the Settings and UsersGuide GUIs are not currently running,
          closeGUISafely()										// safely quit the application
        
      case ButtonClicked(`pauseButton`) =>						// If the pause button was clicked,
        if(!SettingsIsRunning && !UsersGuideIsRunning)			// and the Settings and UsersGuide GUIs are not currently running...
        {
          if(pauseTimer)										// If the timer is already paused,
          {														// Unpause it and update the button's text and tooltip
            guiUpdater ! PauseTimer(false)
            pauseButton.text = "Pause"
            pauseButton.tooltip = "Click to pause and reset the timer"
          }
          else													// Otherwise, if the timer is not already paused,
          {														// then pause it and update the button's text and tooltip
            guiUpdater ! PauseTimer(true)
            pauseButton.text = "UnPause"
            pauseButton.tooltip = "Click to unpause the timer"
          }
        }
    }
    
    size = new Dimension(500, 500)								// Set the size of the GUI window
    
    private val inputStringArray = new Array[String](2)			// A String Array whose only purpose is to be an input to
    inputStringArray(0) = "no"									// run the SettingsGUI and UsersGuide applications
    inputStringArray(1) = "yes"
    
    menuBar = new MenuBar {										// Add a menu bar 
      contents += new Menu("File") 								// Add a menu entitled "File"
      {
        contents += new MenuItem(Action("Settings"){			// Add a "Settings" menu option
          if(!SettingsIsRunning)								// If there's not already a SettingsGUI application running...
          {
            if(!UsersGuideIsRunning) guiUpdater!PauseTimer(true)// When "Settings" is clicked, pause the timer,
            guiUpdater ! SettingsRunning(true)					// set the SettingsIsRunning flag to true,
        	SettingsGUI.startup(inputStringArray)				// and run the SettingsGUI application
          }
        })
        contents += new MenuItem(Action("Exit")					// Add an "Exit" menu option that exits the application
          {if(!SettingsIsRunning && !UsersGuideIsRunning)
            closeGUISafely()})
      }
      contents += new Menu("Help")								// Add a second menu entitled "Help"
      {
        contents += new MenuItem(Action("User's Guide"){
          if(!UsersGuideIsRunning)								// If there's not already a UsersGuide application running...
          {
            if(!SettingsIsRunning) guiUpdater!PauseTimer(true)	// When "User's Guide" is clicked, pause the timer,
            guiUpdater ! UsersGuideRunning(true)				// set the UsersGuideIsRunning flag to true,
            UsersGuide.startup(inputStringArray)				// and run the UsersGuide application
          }
        })
      }
    }
    
    if(!initialValidityCheck())									// Check fields from configuration file for validity
      openFieldsInvalidDialog()
  }
  
  /**
   * Displays an error Dialog when the AddPDF application is started with invalid fields set in the configuration file.
   * 
   * @author James Watts
   * Last Updated: July 21st, 2015
   */
  protected[attach_pdf] def openFieldsInvalidDialog()
  {
    Dialog.showMessage(null,
        "There is a problem with one or more of the fields set in the configuration file. \nPlease open settings to fix these fields.",
        title = "Please open Settings",
        messageType = Dialog.Message.Error)
  }
  
  /**
   * Private helper method used to close the program safely.
   * 
   * This method is called when either the close button or the red X in the corner is pressed or when the Exit 
   * menu item is selected.
   * 
   * @author James Watts
   * Last Updated: May 8th, 2015
   */
  private def closeGUISafely() {
    guiUpdater ! GuiRunning(false)								// Set the GuiIsRunning field to false
    closeUtility()												// Call AddPDF_Util's closeUtility method
    quit														// Quit the GUI
  }
  
}