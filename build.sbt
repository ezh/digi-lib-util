//
// Copyright (c) 2012-2013 Alexey Aksenov ezh@ezh.msk.ru
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// DEVELOPMENT CONFIGURATION

import sbt.osgi.manager._

activateOSGiManager ++ sbt.scct.ScctPlugin.instrumentSettings

name := "Digi-Lib-Util"

description := "Utility library for Digi components"

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

organization := "org.digimead"

organizationHomepage := Some(url("http://digimead.org"))

homepage := Some(url("https://github.com/ezh/digi-lib-util"))

version <<= (baseDirectory) { (b) => scala.io.Source.fromFile(b / "version").mkString.trim }

inConfig(OSGiConf)({
  import OSGiKey._
  Seq[Project.Setting[_]](
    osgiBndBundleSymbolicName := "org.digimead.digi.lib.util",
    osgiBndBundleCopyright := "Copyright © 2011-2013 Alexey B. Aksenov/Ezh. All rights reserved.",
    osgiBndExportPackage := List("org.digimead.*"),
    osgiBndImportPackage := List("!org.aspectj.lang", "*"),
    osgiBndBundleLicense := "http://www.apache.org/licenses/LICENSE-2.0.txt;description=The Apache Software License, Version 2.0"
  )
})

crossScalaVersions := Seq("2.10.1")

scalaVersion := "2.10.1"

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-Xcheckinit", "-feature") ++
  (if (true || (System getProperty "java.runtime.version" startsWith "1.7")) Seq() else Seq("-optimize")) // -optimize fails with jdk7

// http://vanillajava.blogspot.ru/2012/02/using-java-7-to-target-much-older-jvms.html
javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.6", "-target", "1.6")

if (sys.env.contains("XBOOTCLASSPATH")) Seq(javacOptions += "-Xbootclasspath:" + sys.env("XBOOTCLASSPATH")) else Seq()

resolvers += "digimead-maven" at "http://storage.googleapis.com/maven.repository.digimead.org/"

moduleConfigurations := {
  val digi = "digimead" at "http://storage.googleapis.com/maven.repository.digimead.org/"
  Seq(
    ModuleConfiguration("org.digimead", "digi-lib", digi),
    ModuleConfiguration("org.digimead", "digi-lib-slf4j", digi)
  )
}

libraryDependencies ++= Seq(
    "org.digimead" %% "digi-lib" % "0.2.3",
    "org.scalatest" %% "scalatest" % "1.9.1" % "test"
      excludeAll(ExclusionRule("org.scala-lang", "scala-reflect"), ExclusionRule("org.scala-lang", "scala-actors")),
    "org.slf4j" % "slf4j-log4j12" % "1.7.1" % "test"
  )

parallelExecution in Test := false

parallelExecution in sbt.scct.ScctPlugin.ScctTest := false

//sourceDirectory in Test <<= baseDirectory / "Testing Infrastructure Is Absent"

//logLevel := Level.Debug
