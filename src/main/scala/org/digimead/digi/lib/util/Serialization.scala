/**
 * Digi-Lib-Util - utility module of all Digi applications and libraries, containing various common routines
 *
 * Copyright (c) 2012-2014 Alexey Aksenov ezh@ezh.msk.ru
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

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream }
import org.digimead.digi.lib.log.api.XLoggable
import scala.collection.JavaConversions.{ asScalaBuffer, seqAsJavaList }

object Serialization extends XLoggable {
  def serializeToList(o: java.io.Serializable): java.util.List[Byte] =
    serializeToArray(o).toList
  def serializeToArray(o: java.io.Serializable): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(o)
    oos.close()
    baos.toByteArray()
  }
  def deserializeFromList[T <: java.io.Serializable](s: java.util.List[Byte])(implicit m: scala.reflect.Manifest[T]): Option[T] =
    if (s == null) None else deserializeFromArray[T](s.toList.toArray)
  def deserializeFromArray[T <: java.io.Serializable](s: Array[Byte])(implicit m: scala.reflect.Manifest[T]): Option[T] = try {
    if (s == null) return None
    val ois = new ObjectInputStream(new ByteArrayInputStream(s.toList.toArray))
    val o = ois.readObject()
    ois.close()
    Some(o.asInstanceOf[T])
  } catch {
    case e: Throwable ⇒
      log.error("deserialization error", e)
      None
  }
}
