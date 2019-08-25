name := "car-adverts"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += "DynamoDB Local Release Repository" at "https://s3-us-west-2.amazonaws.com/dynamodb-local/release"

val akkaHTTP = "10.1.9"
val akkaVersion = "2.5.23"
val localDynamoDB = "1.11.0.1"
libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    
    "com.typesafe.akka" %% "akka-http" % akkaHTTP,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHTTP,
    
    "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.535",
    "com.gu" %% "scanamo-alpakka" % "1.0.0-M8" exclude ("com.amazonaws", "aws-java-sdk-dynamodb"),
    "com.amazonaws" % "DynamoDBLocal" % localDynamoDB exclude ("com.amazonaws", "aws-java-sdk-dynamodb"),
    "com.almworks.sqlite4java" % "sqlite4java" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "sqlite4java-win32-x86" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "sqlite4java-win32-x64" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "libsqlite4java-osx" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "libsqlite4java-linux-i386" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "libsqlite4java-linux-amd64" % "latest.integration" % "test",
    
    "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    "org.mockito" %% "mockito-scala-scalatest" % "1.5.14",
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHTTP,
    
    "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
)

lazy val copyJars = taskKey[Unit]("copyJars")
copyJars := {
    import java.nio.file.Files
    import java.io.File
    val artifactTypes = Set("dylib", "so", "dll")
    val files = Classpaths.managedJars(Test, artifactTypes, update.value).files
    Files.createDirectories(new File(baseDirectory.value, "native-libs").toPath)
    files.foreach { f =>
        val fileToCopy = new File("native-libs", f.name)
        if (!fileToCopy.exists()) {
            Files.copy(f.toPath, fileToCopy.toPath)
        }
    }
}

(compile in Compile) := (compile in Compile).dependsOn(copyJars).value

coverageEnabled := true

coverageExcludedPackages := "<empty>;dev.harmeetsingh.caradverts.configuration\\..*;dev.harmeetsingh.caradverts.Application"

scapegoatVersion in ThisBuild := "1.3.8"

scapegoatIgnoredFiles := Seq(".*/Application.scala")

scapegoatConsoleOutput := false

parallelExecution in Test := false