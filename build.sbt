// Name of the project
name := "NeuralNetwork"

// Project version
version := "0.0.1"

// Version of Scala used by the project
scalaVersion := "2.11.4"

// Necessary for ScalaFXML, which allows using JavaFX Scene builder with ScalaFX
addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)

// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.2.2"
//libraryDependencies ++= List(
//  "org.scalafx" % "scalafx_2.11" % "8.0.0-R4"
//)

// Add dependency on JavaFX library (only for Java 7)
unmanagedJars in Compile += Attributed.blank(file(scala.util.Properties.javaHome) / "/lib/jfxrt.jar")

// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true

// Include /src/resources in classpath
unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "resources") }
