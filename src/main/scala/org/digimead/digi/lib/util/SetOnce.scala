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

import annotation.implicitNotFound

class SetOnce[T] {
  private[this] var value: Option[T] = None
  def isSet = value.isDefined
  def ensureSet { if (value.isEmpty) throwISE("uninitialized value") }
  def apply() = { ensureSet; value.get }
  def :=(finalValue: T)(implicit credential: SetOnceCredential) {
    value = Some(finalValue)
  }
  def allowAssignment = {
    if (value.isDefined) throwISE("final value already set")
    else new SetOnceCredential
  }
  private def throwISE(msg: String) = throw new IllegalStateException(msg)

  @implicitNotFound(msg = "This value cannot be assigned without the proper credential token.")
  class SetOnceCredential private[SetOnce]
}

object SetOnce {
  implicit def unwrap[A](wrapped: SetOnce[A]): A = wrapped()
}
