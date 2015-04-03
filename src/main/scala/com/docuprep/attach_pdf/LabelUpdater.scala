package com.docuprep.attach_pdf

import akka.actor.Actor

/* Case Classes to pass to the LabelUpdater as Requests */
private[attach_pdf] case class FilesWaiting(fileList: Set[String], count: Int)
private[attach_pdf] case class Inbound(inboundFolder: String)
private[attach_pdf] case class Count(countString: String)
private[attach_pdf] case class ReportCount(countString: String)
private[attach_pdf] case class SettingsRunning(settingsIsRunning: Boolean)
private[attach_pdf] case class GuiRunning(guiIsRunning: Boolean)
private[attach_pdf] case class UsersGuideRunning(usersGuideRunning:Boolean)
private[attach_pdf] case class PauseTimer(pause: Boolean)

/**An Actor object whose purpose is to update the AddPDF_GUI labels and text box, as well as the SettingsIsRunning,
 * GuiIsRunning, and UsersGuideIsRunning boolean fields.  This is to prevent race conditions encountered by 
 * multi-threading and to make sure the GUI is updated quickly and by only one thread.
 * 
 * If a FilesWaiting case class is sent, the files waiting count label and files waiting text box are updated.
 * If an Inbound case class is sent, the inbound folder label is updated.
 * If a Count case class is sent, the timer label is updated.
 * If a ReportCount case class is sent, the report timer label is updated.
 * If a SettingsRunning case class is sent, the AddPDF_Util.SettingIsRunning field is updated.
 * If a GUIRunning case class is sent, the AddPDF_Util.GuiIsRunning field is updated.
 * If a UsersGuideRunning case class is sent, the AddPDF_Util.UsersGuideIsRunning is updated.
 * If a PauseTimer case class is sent, the AddPDF_Util.pauseTimer field is updated.
 * If the string "exit" is sent, the LabelUpdater will shut down.
 * All other messages are ignored.
 * 
 * @author James Watts
 * Last updated: April 3rd, 2015
 */
private[attach_pdf] class LabelUpdater extends Actor {
  
  def receive = {
    /* Request to update the Files Waiting text box and Files Waiting Count label */
    case FilesWaiting(fileList, count) => 
      if(AddPDF_Util.GuiIsRunning)										// If the GUI is running...
      {
        AddPDF_GUI.filesWaitingCountLabel.text =						// Update the files waiting count label
          if(count==1) "1 file waiting" 
          else s"$count files waiting"
        AddPDF_GUI.filesWaitingListBox.text = fileList.mkString("\n")	// And Update the list of files waiting
      }
    
    /* Request to update the Inbound Folder label */
    case Inbound(inboundFolder) =>
      if(AddPDF_Util.GuiIsRunning)										// Update the inbound folder label if the GUI is running
        AddPDF_GUI.inboundFolderLabel.text = s"on: $inboundFolder, etc."
    
    /* Request to update the Timer label */
    case Count(countString) =>
      if(AddPDF_Util.GuiIsRunning)										// Update the timer label if the GUI is running
        AddPDF_GUI.timerLabel.text = s"next check in $countString"
    
    /* Request to update the Report Timer label */
    case ReportCount(countString) =>
      if(AddPDF_Util.GuiIsRunning)										// Update the report timer label if the GUI is running
        AddPDF_GUI.reportTimerLabel.text = s"next report in $countString"
    
    /* Request to update the SettingsIsRunning Boolean field */
    case SettingsRunning(running) =>
      AddPDF_Util.SettingsIsRunning = running							// Update the SettingsIsRunning field
    
    /* Request to update the GuiIsRunning Boolean field */
    case GuiRunning(running) =>
      AddPDF_Util.GuiIsRunning = running								// Update the GuiIsRunning field
      
    /* Request to update the UsersGuideIsRunning field */
    case UsersGuideRunning(running) =>
      AddPDF_Util.UsersGuideIsRunning = running							// Update the UsersGuideIsRunning field
    
    /* Request to pause the timer */
    case PauseTimer(pause) =>
      AddPDF_Util.pauseTimerLastValue = AddPDF_Util.pauseTimer			// Update the pauseTimerLastVaue field
      AddPDF_Util.pauseTimer = pause									// Update the pauseTimer field
    
    /* Request to stop the LabelUpdater thread */
    case "exit" =>
      AddPDF_Util.GuiIsRunning = false									// Set the AddPDF_Util Boolean fields to false
      AddPDF_Util.SettingsIsRunning = false
      AddPDF_Util.UsersGuideIsRunning = false
      //context.stop(self)												// Stop the current Actor
      context.system.shutdown()											// Shutdown the Actor System
    
    /* Any other (unknown) request */
    case _ =>
      // Do nothing
  }
}