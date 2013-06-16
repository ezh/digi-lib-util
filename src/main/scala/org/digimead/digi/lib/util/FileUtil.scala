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

import java.io.{ File => JFile }
import java.io.FileWriter
import java.io.InputStream
import java.io.OutputStream

import org.digimead.digi.lib.aop.log
import org.digimead.digi.lib.log.api.Loggable

import com.google.common.io.ByteStreams
import com.google.common.io.Files

object FileUtil extends Loggable {
  @log
  def copyFile(sourceFile: JFile, destFile: JFile) =
    Files.copy(sourceFile, destFile)
  @log
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
  @log
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
  @log
  def writeToStream(in: InputStream, out: OutputStream) =
    ByteStreams.copy(in, out)
}
