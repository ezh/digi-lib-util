package org.digimead.digi.lib.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

import scala.collection.JavaConversions._

import org.digimead.digi.lib.log.Logging

object Serialization extends Logging {
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
    case e =>
      log.error("deserialization error", e)
      None
  }
}
