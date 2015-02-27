package com.docuprep.attach_pdf

import swing._
import BorderPanel.Position.{West, East}
import event.ButtonClicked

import AddPDF_Util._

/**
 * Graphic User Interface for the settings menu of the AddPDF application.
 * 
 * @author James Watts
 * Last Updated: February 13th, 2015
 */
private[attach_pdf] object SettingsGUI extends SimpleSwingApplication {
  
  def top = new MainFrame{
    title = "Settings"											// Set title
    
    // Labels for West Panel
    val inbound1Label = new Label("Inbound Folder: ")			// Inbound Folder (1) Label
    val PDF1Label = new Label("PDF Folder:         ")			// PDF Folder (1) Label
    val inbound2Label = new Label("Inbound Folder: ")			// Inbound Folder (2) Label
    val PDF2Label = new Label("PDF Folder:         ")			// PDF Folder (2) Label
    val inbound3Label = new Label("Inbound Folder: ")			// Inbound Folder (3) Label
    val PDF3Label = new Label("PDF Folder:         ")			// PDF Folder (3) Label
    val inbound4Label = new Label("Inbound Folder: ")			// Inbound Folder (4) Label
    val PDF4Label = new Label("PDF Folder:         ")			// PDF Folder (4) Label
    val location2Label = new Label(								// Label to denote Second Location
        "Second Location (if applicable)")
    val location3Label = new Label( 							// Label to denote Third Location
        "Third Location (if applicable)")
    val location4Label = new Label(								// Label to denote Fourth Location
        "Fourth Location (if applicable)")
    
    // Labels for East Panel
    val checkNewFilesLabel = new Label("Check For New Files Every:")// Check for new files label
    val reportStatusLabel = new Label("Report Status Every:")	// Status report label
    val secondsLabel1 = new Label(" Seconds.")					// Seconds Label (1)
    val secondsLabel2 = new Label(" Seconds.")					// Seconds Label (2)
    val databaseLabel = new Label("Database: ")					// Database Label
    
    // Text Boxes for West Panel
    val inbound1Text = new TextField {							// Text field to input primary inbound folder to find
      columns = 30												// .txt file
      text = currentInboundFolders(0)							// Initialize Inbound Folder 1
    }
    val PDF1Text = new TextField {								// Text field to input primary PDF folder to copy the new
      columns = 30												// PDF file into
      text = currentOutboundFolders(0) 							// Initialize PDF Folder 1
    }
    val inbound2Text = new TextField {							// Text field to input secondary inbound folder to find
      columns = 30												// .txt file
      if(currentInboundFolders(1) != null)
    	text = currentInboundFolders(1)							// Initialize Inbound Folder 2
    }
    val PDF2Text = new TextField {								// Text field to input secondary PDF folder to copy the
      columns = 30												// new PDF file into
      text = currentOutboundFolders(1) 							// Initialize PDF Folder 2
    }
    val inbound3Text = new TextField {							// Text field to input third inbound folder to find .txt
      columns = 30												// file
      if(currentInboundFolders(2) != null)
    	text = currentInboundFolders(2)							// Initialize Inbound Folder 3
    }
    val PDF3Text = new TextField {								// Text field to input third PDF folder to copy the new
      columns = 30												// PDF file into
      text = currentOutboundFolders(2)							// Initialize PDF Folder 3
    }
    val inbound4Text = new TextField {							// Text field to input fourth inbound folder to find .txt
      columns = 30												// file
      if(currentInboundFolders(3) != null)
    	text = currentInboundFolders(3)							// Initialize Inbound Folder 4
    }
    val PDF4Text = new TextField {								// Text field to input fourth PDF folder to copy the new
      columns = 30												// PDF file into
      text = currentOutboundFolders(3)							// Initialize PDF Folder 4
    }
    
    // Text Boxes for East Panel
    val checkNewFilesText = new TextField {						// Text field to input time lapse between checking for
      columns = 5												// new files
      text = checkFilesTime.toString							// Initialize to checkFilesTime from AddPDF_Util
    }
    val reportStatusText = new TextField {						// Text field to input time lapse for reporting the
      columns = 5												// current status
      text = reportStatusTime.toString							// Initialize to reportStatusText from AddPDF_Util
    }
    val databaseText = new TextField {							// Text field to input database to extract and place the
      columns = 10												// PDF files
      text = databaseName										// Initialize to databaseName from AddPDF_Util
    }
    
    // Check Boxes
    val inbound2CheckBox = new CheckBox{selected = box2checked}	// Initialize all check boxes to their most recent state
    val inbound3CheckBox = new CheckBox{selected = box3checked}
    val inbound4CheckBox = new CheckBox{selected = box4checked}
    
    // Buttons
    val closeButton = new Button{								// Button to exit the window
      text = "Close"											// set its title
      tooltip = "Click to close this window"					// add a helpful message
    }
    val applyButton = new Button{								// Button to apply changes to text boxes
      text = "Apply"											// set its title
      tooltip = "Click to apply changes"						// add a helpful message
    }
    
    // Panels:
    // West Panel:
    val westPanel = new BoxPanel(Orientation.Vertical){			// Panel for the West side of application
      
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for inbound folder 1
        contents+=inbound1Label									// add inbound folder 1 label
        contents+=inbound1Text									// add inbound folder 1 text box
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for PDF folder 1
        contents+=PDF1Label										// add PDF folder 1 label
        contents+=PDF1Text										// add PDF folder 1 text box
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
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for PDF folder 2
    	contents+=PDF2Label										// add PDF folder 2 label
    	contents+=PDF2Text										// add PDF folder 2 text box
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
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for PDF folder 3
    	contents+=PDF3Label										// add PDF folder 3 label
    	contents+=PDF3Text										// add PDF folder 3 text box
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
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for PDF folder 4
    	contents+=PDF4Label										// add PDF folder 4 label
    	contents+=PDF4Text										// add PDF folder 4 text box
      }
      border = Swing.EmptyBorder(0,20,0,20)						// Set the border size (add space to left and right sides)
    }
    
    // East panel:
    val eastPanel = new BoxPanel(Orientation.Vertical){			// Panel for the East side of application
      
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for Check New Files label
    	contents+=checkNewFilesLabel							// add Check New Files label
    	contents+=Swing.HGlue									// add horizontal glue to left justify
      }									
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for Check New Files text
    	contents+=checkNewFilesText								// add Check New Files text box
    	contents+=secondsLabel1									// add " Seconds."
      }
      contents+=Swing.VStrut(15)								// add a vertical space
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for Report Status label
    	contents+=reportStatusLabel								// add Report Status label
    	contents+=Swing.HGlue									// add horizontal glue to left justify
      }
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for Report Status text
    	contents+=reportStatusText								// add Report Status text box
    	contents+=secondsLabel2									// add " Seconds."
      }
      contents+=Swing.VStrut(30)								// add a vertical space
      contents+=new BoxPanel(Orientation.Horizontal){			// Panel for Database input
        contents+=databaseLabel									// add "Database: "
        contents+=databaseText									// add Database text box
      }
      contents+=Swing.VStrut(130)								// add a large vertical space
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
      border = Swing.EmptyBorder(20,0,20,0);					// Add space on top and bottom
    }
    
    size = new Dimension(750, 375)								// Set the size of the GUI
    
    // Listen to both buttons and all three CheckBoxes
    listenTo(closeButton, applyButton, inbound2CheckBox, inbound3CheckBox, inbound4CheckBox)
    
    reactions+={
      case ButtonClicked(`closeButton`) => 
          pauseTimer = false									// Un-Pause the timer,
          guiUpdater ! SettingsRunning(false)					// set the SettingsIsRunning flag to false,
          close()												// and close the window
        
      case ButtonClicked(`applyButton`) => 						// Run the applyChanges method, which updates fields
        		    											// according to new text box inputs
        applyChanges(	inbound1Text.text, inbound2Text.text, inbound3Text.text, inbound4Text.text,
        				PDF1Text.text, PDF2Text.text, PDF3Text.text, PDF4Text.text,
        				inbound2CheckBox.selected, inbound3CheckBox.selected, inbound4CheckBox.selected,
        				checkNewFilesText.text, reportStatusText.text, databaseText.text)
    }
  }
  
  /**
   * Displays an error Dialog when an inbound or outbound folder pathname is invalid.
   * 
   * @param folder						Invalid String pathname of a file folder
   * 
   * @param isInbound					Boolean denoting if the pathname is from the inbound or outbound folder list
   * 
   * @author James Watts
   * Last Updated: January 5th, 2015
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
   * @param folder						Invalid String pathname of a file folder
   * 
   * @param isInbound					Boolean denoting if the pathname is from the inbound or outbound folder list
   * 
   * @author James Watts
   * Last Updated: January 5th, 2015
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
   * @param isCheckFilesTime			Boolean denoting if the Check Files Time or Report Status Time is invalid
   * 
   * @author James Watts
   * Last Updated: January 5th, 2015
   */
  protected[attach_pdf] def invalidTimeDialog(isCheckFilesTime: Boolean)
  {
    val timeType = if(isCheckFilesTime) "Check Files Time" else "Report Status Time"
    
    Dialog.showMessage(top.contents.head,
        s"$timeType must be a valid positive integer!",
        title = s"$timeType formatted incorrectly",
        messageType = Dialog.Message.Error)
  }
}