name := """nanicolle"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
//  jdbc,
//  cache,
//  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test,

  evolutions,
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "com.typesafe.slick" % "slick-codegen_2.11" % "3.1.1",
  "com.typesafe" % "config" % "1.3.0",

  "com.jsuereth" %% "scala-arm" % "1.4",

  "org.postgresql"  %  "postgresql"  %  "9.4-1201-jdbc41"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

EclipseKeys.preTasks := Seq(compile in Compile)

// Slick code generator
import com.typesafe.config.ConfigFactory

slickCodeGen <<= slickCodeGenTask // register sbt command
//(compile in Compile) <<= (compile in Compile) dependsOn (slickCodeGenTask) // register automatic code generation on compile
lazy val config = ConfigFactory.parseFile(new File("./conf/application.conf"))
lazy val slickCodeGen = taskKey[Seq[File]]("slick-codegen")
lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val slickDriver = config.getString("slick.dbs.default.driver").init
  val jdbcDriver = config.getString("slick.dbs.default.db.driver")
  val url = config.getString("slick.dbs.default.db.url")
  val outputDir = "app/"
  val pkg = "models"
  val username = config.getString("slick.dbs.default.db.user")
  val password = config.getString("slick.dbs.default.db.password")

  toError(
    r.run(
      "slick.codegen.SourceCodeGenerator",
      cp.files,
      Array(slickDriver, jdbcDriver, url, outputDir, pkg, username, password),
      s.log
    )
  )
  val fname = outputDir + "/Tables.scala"
  Seq(file(fname))
}