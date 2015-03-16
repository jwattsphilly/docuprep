package com.docuprep.attach_pdf

import swing._
import BorderPanel.Position._
import event._

import AddPDF_Util._

/**
 * Graphic User Interface for User's Guide for AddPDF application
 * 
 * @author James Watts
 * Last Updated: March 16th, 2014
 */
object UsersGuide extends SimpleSwingApplication {
  
  private val arraySize = 26
  
  def top = new MainFrame{										// Create a new MainFrame for the User's Guide
    title = "User's Guide"										// Entitle it
    
    /* STRINGS */
    val generalSelection = "General Help"
    val mainPageSelection = "Main Page"
    val settingsSelection = "Settings"
    
    /* Help Text for General Help */
    val generalText = new Array[String](arraySize)
    generalText(0) = "General Help..."	// TODO: Write up general help text
    generalText(1) = "a"
    generalText(2) = "b"
    generalText(3) = "c"
    generalText(4) = "d"
    generalText(5) = "e"
    generalText(6) = "f"
    generalText(7) = "g"
    generalText(8) = "h"
    generalText(9) = "i"
    generalText(10) = "j"
    generalText(11) = "k"
    generalText(12) = "l"
    generalText(13) = "m"
    generalText(14) = "n"
    generalText(15) = "o"
    generalText(16) = "p"
    generalText(17) = "q"
    generalText(18) = "r"
    generalText(19) = "s"
    generalText(20) = "t"
    generalText(21) = "u"
    generalText(22) = "v"
    generalText(23) = "w"
    generalText(24) = "x"
    generalText(25) = "y"
    
    /* Help Text for Main Page Help */
    val mainPageText = new Array[String](arraySize)
    mainPageText(0) = "Main Page Help..."
    mainPageText(1) = " "
    mainPageText(2) = "Menu Bars:"
    mainPageText(3) = "    File -> Exit:"
    mainPageText(4) = "        Safely closes the Attach PDF application."
    mainPageText(5) = "    File -> Settings:"
    mainPageText(6) = "        Opens the Settings Graphic User Interface.  When Settings is running, the timers are paused."
    mainPageText(7) = "    Help -> User's Guide:"
    mainPageText(8) = "        Opens the interactive User's Guide for the Attach PDF application.  When the User's Guide"
    mainPageText(9) = "    is running, the timers are paused."
    mainPageText(10) = " "
    mainPageText(11) = "Buttons:"
    mainPageText(12) = "    Pause/UnPause:"
    mainPageText(13) = "        Pauses/Unpauses the countdown timers."
    mainPageText(14) = "    Close:"
    mainPageText(15) = "        Safely closes the Attach PDF application."
    mainPageText(16) = " "
    mainPageText(17) = "Timers:"
    mainPageText(18) = "    On the bottom lefthand corner of the window, you will see two timers."
    mainPageText(19) = "    When the first timer, labeled \"next check in,\" reaches 0, the inbound folders set in Settings"
    mainPageText(20) = "are searched for text files containing the necessary information to merge two PDF files.  The"
    mainPageText(21) = "names of these text files are displayed in the white box underneath \"Files Waiting to be"
    mainPageText(22) = "Processed.\"  The PDF files are then automatically merged and copied to the four outbound"
    mainPageText(23) = "folders."
    mainPageText(24) = "    When the second timer, labeled \"next report in,\" reaches 0, the application reports its status"
    mainPageText(25) = "to the database set in Settings."
    
    /* Help Text for Settings Help */
    val settingsText = new Array[String](arraySize)
    settingsText(0) = "Settings Help..."	// TODO: Write up help text for SettingsGUI
    settingsText(1) = "z"
    settingsText(2) = "y"
    settingsText(3) = "x"
    settingsText(4) = "w"
    settingsText(5) = "v"
    settingsText(6) = "u"
    settingsText(7) = "t"
    settingsText(8) = "s"
    settingsText(9) = "r"
    settingsText(10) = "q"
    settingsText(11) = "p"
    settingsText(12) = "o"
    settingsText(13) = "n"
    settingsText(14) = "m"
    settingsText(15) = "l"
    settingsText(16) = "k"
    settingsText(17) = "j"
    settingsText(18) = "i"
    settingsText(19) = "h"
    settingsText(20) = "g"
    settingsText(21) = "f"
    settingsText(22) = "e"
    settingsText(23) = "d"
    settingsText(24) = "c"
    settingsText(25) = "b"
    
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
    val helpText = new Array[Label](arraySize)
    for(i <- 0 until arraySize)
    {
      helpText(i) = new Label(generalText(i))
    }
    
    /* */
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
    
    size = new Dimension(610, 600)								// Set the size of the GUI window
    
    listenTo(closeButton, searchButton)
    
    reactions+={
      case ButtonClicked(`searchButton`) =>
        // TODO:
        val selection = pullDownMenu.selection.item
        
        if(selection == generalSelection)
        {
          for(i <- 0 until arraySize)
          {
            helpText(i).text = generalText(i)
          }
        }
        else if(selection == mainPageSelection)
        {
          for(i <- 0 until arraySize)
          {
            helpText(i).text = mainPageText(i)
          }
        }
        else if(selection == settingsSelection)
        {
          for(i <- 0 until arraySize)
          {
            helpText(i).text = settingsText(i)
          }
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