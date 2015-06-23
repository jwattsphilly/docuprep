package com.docuprep.attach_pdf

/**A Timer object that uses a Java Swing Timer to perform an input operation (op) every given amount of milliseconds 
 * (millisToCheck).  To be used as new Timer(millisToCheck)(op).
 * 
 * @param millisToCheck				An amount in milliseconds denoting how frequently to run the input operation op
 * 
 * @param op						Any parameterless function that returns a Unit for the Timer to run every millisToCheck 
 * 									milliseconds
 * 
 * @author James Watts
 * Last Updated: March 20th, 2015
 */
protected[attach_pdf] class Timer (millisToCheck: Int)(op: => Unit){
  
  private val action = new javax.swing.AbstractAction 					// Create a new Java AbstractAction for the Java Timer
  { def actionPerformed(ev: java.awt.event.ActionEvent) = op }			// Define its action performed to be op
  
  // Underlying Java Timer for the Timer
  private val timer = new javax.swing.Timer(millisToCheck, action)		// Create a Java Timer ("timer") with op as its action
  
  timer.setRepeats(true)												// Set the repeats to true
  timer.start															// Start the Java Timer
  
  /**
   * Method used to stop the Timer
   * 
   * @author James Watts
   * Last Updated: February 2nd, 2014
   */
  def stopTimer:Unit = {
    timer.setRepeats(false)												// Set repeats to false
    timer.stop															// Stop the Java Timer
  }
}