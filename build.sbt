// Written by James Watts
lazy val root = (project in file(".")).
	settings(
		name := "Attach PDF", 
		version := "1.0", 
		organization := "com.docuprep",
		scalaVersion := "2.11.2",
		libraryDependencies ++= Seq(
			// For PDF Merger:
			"org.apache.pdfbox" % "pdfbox" % "1.8.6",
			// For GUI:
			"org.scala-lang" % "scala-swing" % "latest.integration",
			// For Logging:
			"ch.qos.logback" % "logback-classic" % "1.1.2",
			"org.slf4j" % "slf4j-api" % "1.7.7",
			// For the Database: // TODO: Change to Microsoft SQL Server Database for actual thing
			"com.h2database" % "h2" % "1.3.148",
			// For multi-threading:
			"com.typesafe.akka" % "akka-actor_2.11" % "2.3.3",
			// For working with a configuration file:
			"com.typesafe" % "config" % "1.2.1"
		).map(_.exclude("commons-logging", "commons-logging")),
		mainClass in (Compile, run) := Some("com.docuprep.attach_pdf.AddPDF_GUI")
	)
