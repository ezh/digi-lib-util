/**
 * Digi-Lib-Util - utility module of all Digi applications and libraries, containing various common routines
 *
 * Copyright (c) 2012-2013 Alexey Aksenov ezh@ezh.msk.ru
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.digimead.digi.lib.util

import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.jar.JarFile

import scala.collection.JavaConversions._

object Util {
  private lazy val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")

  def dateString(date: Date) = df.format(date)
  def dateFile(date: Date) = dateString(date).replaceAll("""[:\.]""", "_").replaceAll("""\+""", "x")

  /** Get bundles list from the specific location. */
  def getBundles(pathToBundles: File, recursive: Boolean = true): Array[String] = {
    val root = pathToBundles.toString()
    val rootLength = root.length()
    val jars = FileUtil.recursiveListFiles(pathToBundles, """.*\.jar""".r)
    jars.map { jar =>
      val relative = jar.toString.substring(rootLength) match {
        case str if str.startsWith(File.separator) => str.substring(1)
        case str => str
      }
      var archive: JarFile = null
      try {
        archive = new JarFile(jar)
        val manifest = archive.getManifest()
        val attributes = manifest.getMainAttributes()
        if (attributes.keySet().exists(_.toString() == "Bundle-SymbolicName"))
          Some("reference:file:" + relative)
        else
          None
      } catch {
        case e: Throwable => // Skip
          None
      } finally {
        if (archive != null)
          try { archive.close } catch { case e: Throwable => }
      }
    }.flatten
  }
  /** Get path to the directory with application data. */
  def getPath(env: String, clazz: Class[_]): File = {
    val path = Option(System.getProperty(env)).map(new URL(_)).orElse(jarLocation(clazz))
    // try to get jar location or get current directory
    val result = (path match {
      case Some(url) =>
        val jar = new File(url.toURI())
        if (jar.isDirectory() && jar.exists() && jar.canWrite())
          Some(jar) // return exists
        else {
          val jarDirectory = if (jar.isFile()) jar.getParentFile() else jar
          if (jarDirectory.exists() && jarDirectory.canWrite())
            Some(jarDirectory) // return exists
          else {
            if (jarDirectory.mkdirs()) // create
              Some(jarDirectory)
            else
              None
          }
        }
      case None =>
        None
    }) getOrElse {
      new File(".")
    }
    if (!result.isAbsolute())
      throw new IllegalStateException(s"Unable to get path for '$env', invalid relative path '$result'")
    result
  }
  /** Returns the jar location as URL if any. */
  def jarLocation(clazz: Class[_]): Option[URL] = try {
    val source = clazz.getProtectionDomain.getCodeSource
    if (source != null)
      Option(source.getLocation)
    else
      None
  } catch {
    // catch all possible throwables
    case e: Throwable =>
      None
  }
}
