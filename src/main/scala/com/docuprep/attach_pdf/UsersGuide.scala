package com.docuprep.attach_pdf

import swing._
import BorderPanel.Position._
import event._

import AddPDF_Util._

/**
 * Graphic User Interface for User's Guide for AddPDF application
 * 
 * @author James Watts
 * Last Updated: March 13th, 2014
 */
object UsersGuide extends SimpleSwingApplication {
  
  def top = new MainFrame{										// Create a new MainFrame for the User's Guide
    title = "User's Guide"										// Entitle it
    
    /* STRINGS */
    val generalSelection = "General Help"
    val settingsSelection = "Settings"
    val mainPageSelection = "Main Page"
    val generalText = "General Help..."		// TODO: Write up general help text
    val settingsText = "Settings Help..."	// TODO: Write up help text for SettingsGUI
    val mainPageText = "Main Page Help..."	// TODO: Write up help text for AddPDF_GUI
    
    /* BUTTONS */
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
    val helpText1 = new Label(generalText)
//    val helpText2 = new Label(generalText)
    
    val HelpBox = new BoxPanel(Orientation.Vertical){
      contents+=new BoxPanel(Orientation.Horizontal){
        contents += helpText1
        contents+=Swing.HGlue
      }
    }
    
    /* Combine the lines of help text into one BoxPanel */
    val helpPannel = new BoxPanel(Orientation.Vertical){
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
    
    
    /* TODO: Documentation */
    contents = new BorderPanel{
      layout(helpPannel) = North
      layout(closeButton) = South
      border = Swing.EmptyBorder(20,20,20,20)
    }
    
    listenTo(closeButton, searchButton)
    
    reactions+={
      case ButtonClicked(`searchButton`) =>
        // TODO:
        val selection = pullDownMenu.selection.item
        
        if(selection == generalSelection)
        {
          helpText1.text = generalText
        }
        else if(selection == settingsSelection)
        {
          helpText1.text = settingsText
        }
        else if (selection == mainPageSelection)
        {
          helpText1.text = mainPageText
        }
      
      case ButtonClicked(`closeButton`) => 
        if(!SettingsIsRunning)
        {
          guiUpdater ! PauseTimer(false)						// Only unpause the timer if the Settings GUI is not running
        }
        guiUpdater ! UsersGuideRunning(false)					// Update the UsersGuideIsRunning flag to false
        close() 												// Close the window
    }
    
  }
  
  
}