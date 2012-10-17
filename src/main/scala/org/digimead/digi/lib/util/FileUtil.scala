/**
 * Digi-Lib-Util - utility module of all Digi applications and libraries, containing various common routines
 *
 * Copyright (c) 2012 Alexey Aksenov ezh@ezh.msk.ru
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

import java.io.{ File => JFile }
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.FileChannel

import scala.annotation.tailrec

import org.digimead.digi.lib.aop.Loggable
import org.digimead.digi.lib.log.Logging

object FileUtil extends Logging {
  @Loggable
  def copyFile(sourceFile: JFile, destFile: JFile): Boolean = {
    if (!destFile.exists())
      destFile.createNewFile()
    var source: FileChannel = null
    var destination: FileChannel = null
    try {
      source = new FileInputStream(sourceFile).getChannel()
      destination = new FileOutputStream(destFile).getChannel()
      destination.transferFrom(source, 0, source.size())
    } finally {
      if (source != null) {
        source.close()
      }
      if (destination != null) {
        destination.close()
      }
    }
    sourceFile.length == destFile.length
  }
  @Loggable
  def deleteFile(dfile: JFile): Boolean =
    if (dfile.isDirectory) deleteFileRecursive(dfile) else dfile.delete
  private def deleteFileRecursive(dfile: JFile): Boolean = {
    if (dfile.isDirectory)
      dfile.listFiles.foreach { f => deleteFileRecursive(f) }
    dfile.delete match {
      case true => true
      case false =>
        log.error("unable to delete \"" + dfile + "\"")
        false
    }
  }
  @Loggable
  def writeToFile(file: JFile, text: String) {
    val fw = new FileWriter(file)
    try { fw.write(text) }
    finally { fw.close }
  }
  /**
   * Write to a stream
   *
   * @param in
   * @param out
   */
  @Loggable
  def writeToStream(in: InputStream, out: OutputStream) {
    val buffer = new Array[Byte](8192)
    @tailrec
    def next(exit: Boolean = false) {
      if (exit) {
        in.close()
        out.close()
        return
      }
      val read = in.read(buffer)
      if (read > 0)
        out.write(buffer, 0, read)
      next(read == -1)
    }
    next()
  }
}
