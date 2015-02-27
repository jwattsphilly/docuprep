package com.docuprep.attach_pdf

import swing._
import BorderPanel.Position.{West, East}
import event.ButtonClicked

import AddPDF_Util._

/**
 * Graphic User Interface for the AddPDF application.
 * 
 * @author James Watts
 * Last Updated: February 16th, 2015
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
    
    /* Panel for the West (left) side of application */
    val westPanel = new BoxPanel(Orientation.Vertical){ // Add the following contents into a BoxPanel:
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
      contents+=Swing.VGlue										// Add Vertical Glue to position the close button
      contents+=closeButton										// at the bottom of the GUI
    }
    
    /* Add the East and West Panels to the MainFrame */
    contents = new BorderPanel {								// Create a BorderPanel for the MainFrame
      layout(westPanel) = West
      layout(eastPanel) = East
      border = Swing.EmptyBorder(10,15,10,15)					// Set the border size
    }
    
    listenTo(closeButton)										// Listen to the close button
    
    // Reactions
    reactions+={
      case ButtonClicked(`closeButton`) => 
        if(!SettingsIsRunning) closeSafely()					// if the close button was clicked, safely quit the application
    }
    
    size = new Dimension(500, 500)								// Set the size of the GUI window
    
    val inputStringArray = new Array[String](2)					// A String Array whose only purpose is to be an input to
    inputStringArray(0) = "no"									// run the SettingsGUI application
    inputStringArray(1) = "yes"
    
    menuBar = new MenuBar {										// Add a menu bar 
      contents += new Menu("File") 								// Entitle it "File"
      {
        contents += new MenuItem(Action("Exit")
            {if(!SettingsIsRunning) closeSafely()})				// Add an "Exit" menu option that exits the application
        contents += new MenuItem(Action("Settings"){			// Add a "Settings" menu option
          if(!SettingsIsRunning)								// If there's not already a SettingsGUI application running,
          {
            pauseTimer = true									// When "Settings" is clicked, Pause the timer
            guiUpdater ! SettingsRunning(true)					// set the SettingsIsRunning flag to true
        	SettingsGUI.startup(inputStringArray)				// and run the SettingsGUI application
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