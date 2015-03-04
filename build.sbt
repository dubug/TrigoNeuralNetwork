// Name of the project
name := "NeuralNetwork"

// Project version
version := "0.0.1"

// Version of Scala used by the project
scalaVersion := "2.11.4"

// Include /src/resources in classpath
unmanagedClasspath in Runtime <+= baseDirectory map { bd => Attributed.blank(bd / "resources") }

// Necessary for ScalaFXML, which allows using JavaFX Scene builder with ScalaFX
addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)

// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.2.2"

// Logging
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.clapper" %% "grizzled-slf4j" % "1.0.2")

// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true
