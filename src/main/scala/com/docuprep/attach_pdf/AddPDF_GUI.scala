package com.docuprep.attach_pdf

import swing._
import BorderPanel.Position.{West, East}
import event.ButtonClicked

import AddPDF_Util._

/**
 * Graphic User Interface for the AddPDF application.
 * 
 * @author James Watts
 * Last Updated: March 13th, 2015
 */
object AddPDF_GUI extends SimpleSwingApplication {
  
  // Labels
  private val filesWaitingLabel = new Label("Files Waiting to be Processed:")
  private [attach_pdf] val inboundFolderLabel = new Label(s"on: ${currentInboundFolders(0)}, etc.")
  private [attach_pdf] val filesWaitingCountLabel = new Label("0 files waiting")
  private [attach_pdf] val timerLabel = new Label(s"next check in ${generateCountString(timeToNextCheck)}")
  private [attach_pdf] val reportTimerLabel = new Label(s"next report in ${generateCountString(timeToNextReport)}")
  
  // Text Field
  val filesWaitingListBox = new TextArea{						// textArea for the files waiting list
    columns = 10
    rows = 10
  }
  
  def top = new MainFrame{										// Create the MainFrame
    title = "Attach PDF Document"								// add a title
    
    guiUpdater ! GuiRunning(true)
    
    // Buttons
    private val closeButton = new Button{						// Close button	- quits the application
      text = "Close"											// add title
      tooltip = "Click to exit"									// add helpful hint
    }
    
    private val pauseButton = new Button{
      text = "Pause"
      tooltip = "Click to pause and reset the timer"
    }
    
    /* Panel for the West (left) side of application */
    val westPanel = new BoxPanel(Orientation.Vertical){ 		// Add the following contents into a BoxPanel:
      contents+=new BoxPanel(Orientation.Horizontal){
        contents+=filesWaitingLabel								// Files Waiting label
        contents+=Swing.HGlue									// Horizontal Glue to keep the label left-aligned
      }
      contents+=new BoxPanel(Orientation.Horizontal){
        contents+=inboundFolderLabel							// Inbound Folder label
        contents+=Swing.HGlue									// Horizontal Glue to keep the label left-aligned
      }
      contents+=new ScrollPane(filesWaitingListBox)				// List of text files waiting to be processed
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
    val eastPanel = new BoxPanel(Orientation.Vertical){
      contents+=Swing.VStrut(375)								// Add a large vertical space to position the buttons
      contents+=new BoxPanel(Orientation.Horizontal){			// at the bottom of the GUI
        contents+=pauseButton
        contents+=Swing.HStrut(5)
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
      case ButtonClicked(`closeButton`) => 
        if(!SettingsIsRunning && !UsersGuideIsRunning) 
          closeSafely()											// If the close button was clicked, safely quit the application
        
      case ButtonClicked(`pauseButton`) =>						// If the pause button was clicked...
        if(!SettingsIsRunning && !UsersGuideIsRunning)			// And the Settings and UsersGuide GUIs are not currently running...
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
    
    val inputStringArray = new Array[String](2)					// A String Array whose only purpose is to be an input to
    inputStringArray(0) = "no"									// run the SettingsGUI application
    inputStringArray(1) = "yes"
    
    menuBar = new MenuBar {										// Add a menu bar 
      contents += new Menu("File") 								// Add a menu entitled "File"
      {
        contents += new MenuItem(Action("Exit")					// Add an "Exit" menu option that exits the application
          {if(!SettingsIsRunning && !UsersGuideIsRunning) 
            closeSafely()})				
        contents += new MenuItem(Action("Settings"){			// Add a "Settings" menu option
          if(!SettingsIsRunning)								// If there's not already a SettingsGUI application running,
          {
            guiUpdater ! PauseTimer(true)						// When "Settings" is clicked, pause the timer,
            guiUpdater ! SettingsRunning(true)					// set the SettingsIsRunning flag to true,
        	SettingsGUI.startup(inputStringArray)				// and run the SettingsGUI application
          }
        })
      }
      contents += new Menu("Help")								// Add a second menu entitled "Help"
      {
        contents += new MenuItem(Action("User's Guide"){
          if(!UsersGuideIsRunning)								// If there's not already a UsersGuide application running,
          {
            guiUpdater ! PauseTimer(true)						// When "User's Guide" is clicked, pause the timer,
            guiUpdater ! UsersGuideRunning(true)				// set the UsersGuideIsRunning flag to true,
            UsersGuide.startup(inputStringArray)				// and run the UsersGuide application
          }
        })
      }
    }
  }
  
  /**
   * Private helper method used to close the program safely.
   * 
   * @author James Watts
   * Last Updated: January 6th, 2015
   */
  private def closeSafely() {
    guiUpdater ! GuiRunning(false)								// Set the GuiIsRunning field to false
    safelyClose()												// Call AddPDF_Util's safelyClose method
    quit														// quit the GUI
  }
  
}