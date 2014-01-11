//
// Copyright (c) 2012-2014 Alexey Aksenov ezh@ezh.msk.ru
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

OSGiManager // ++ sbt.scct.ScctPlugin.instrumentSettings

name := "digi-lib-util"

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
    osgiBndBundleCopyright := "Copyright © 2012-2013 Alexey B. Aksenov/Ezh. All rights reserved.",
    osgiBndExportPackage := List("org.digimead.*"),
    osgiBndImportPackage := List("!org.aspectj.*", "*"),
    osgiBndBundleLicense := "http://www.apache.org/licenses/LICENSE-2.0.txt;description=The Apache Software License, Version 2.0"
  )
})

crossScalaVersions := Seq("2.10.3")

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-Xcheckinit", "-feature") ++
  (if (true || (System getProperty "java.runtime.version" startsWith "1.7")) Seq() else Seq("-optimize")) // -optimize fails with jdk7

// http://vanillajava.blogspot.ru/2012/02/using-java-7-to-target-much-older-jvms.html
javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.6", "-target", "1.6")

javacOptions in doc := Seq("-source", "1.6")

if (sys.env.contains("XBOOTCLASSPATH")) Seq(javacOptions += "-Xbootclasspath:" + sys.env("XBOOTCLASSPATH")) else Seq()

//
// Custom local options
//

resolvers += "digimead-maven" at "http://storage.googleapis.com/maven.repository.digimead.org/"

libraryDependencies ++= Seq(
    "com.google.guava" % "guava" % "15.0",
    "org.digimead" %% "digi-lib" % "0.2.3.4-SNAPSHOT",
    "org.digimead" %% "digi-lib-test" % "0.2.2.4-SNAPSHOT" % "test"
  )

//
// Testing
//

parallelExecution in Test := false

//logLevel := Level.Debug
