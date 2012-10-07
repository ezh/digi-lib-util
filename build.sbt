//
// Copyright (c) 2012 Alexey Aksenov ezh@ezh.msk.ru
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

import com.jsuereth.sbtsite.SiteKeys

com.typesafe.sbtaspectj.AspectjPlugin.settings

sbt.source.align.SSA.ssaSettings

site.settings

ghpages.settings

name := "Digi-Lib-Util"

description := "Utility library for digi components"

organization := "org.digimead"

version := "0.2"

crossScalaVersions := Seq("2.8.2", "2.9.0", "2.9.0-1", "2.9.1", "2.9.2")

scalaVersion := "2.9.2"

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-Xcheckinit") ++
  (if (true || (System getProperty "java.runtime.version" startsWith "1.7")) Seq() else Seq("-optimize")) // -optimize fails with jdk7

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

git.remoteRepo := "git@github.com:ezh/digi-lib-util.git"

site.addMappingsToSiteDir(mappings in packageDoc in Compile, "api")

SiteKeys.siteMappings <<=
  (SiteKeys.siteMappings, PamfletKeys.write, PamfletKeys.output, baseDirectory) map {
    (mappings, _, pamfletDir, baseDirectory) =>
      val publishDir = baseDirectory / "publish"
      val releasesDir = baseDirectory / "publish/releases"
      mappings ++ (pamfletDir ** "*.*" x relativeTo(pamfletDir)) ++ (releasesDir ** "*.*" x relativeTo(publishDir))
  }

PamfletKeys.docs <<= baseDirectory / "publish/docs"

TaskKey[Unit]("publish-github") <<= (streams, com.jsuereth.ghpages.GhPages.ghpages.pushSite) map { (s, push) =>
  s.log.info("publishing project to github")
}

TaskKey[Unit]("publish-github") <<= TaskKey[Unit]("publish-github").dependsOn(PamfletKeys.write)

publishTo  <<= baseDirectory  { (base) => Some(Resolver.file("file",  base / "publish/releases" )) }

resolvers += "digi-lib" at "http://ezh.github.com/digi-lib/releases"

resolvers += "digi-lib-slf4j" at "http://ezh.github.com/digi-lib-slf4j/releases"

libraryDependencies ++= {
  Seq(
    "org.slf4j" % "slf4j-api" % "1.7.1",
    "org.digimead" %% "digi-lib" % "0.2"
  )
}

if (sys.env.contains("LOCAL_BUILD")) {
  Seq[Project.Setting[_]](
    unmanagedResourceDirectories in Compile <+= baseDirectory { _ / "src" / "main" / "scala" },
    libraryDependencies ++= {
      Seq(
        "org.scalatest" %% "scalatest" % "1.8" % "test",
        "org.digimead" %% "digi-lib-slf4j" % "0.1" % "test"
      )
    }
  )
} else {
  Seq[Project.Setting[_]]()
}
