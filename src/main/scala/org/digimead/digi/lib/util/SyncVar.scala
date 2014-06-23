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

import java.util.concurrent.atomic.AtomicReference
import org.digimead.digi.lib.log.api.XLoggable

/**
 * variant of scala.concurrent.SyncVar
 * remove few possible deadlocks from isSet, get, get(timeout)
 * set weaker access permissions protected vs private
 * faster ??? return values immediately if any
 * benchmark:
 * VAR total: 95ms
 * \@volatile VAR total: 143ms
 * atomic total: 137ms
 * synchronized total: 245ms
 * scala SyncVar total: 920ms
 * this SyncVar total: 614ms
 */

class SyncVar[A] extends XLoggable {
  protected val value = new AtomicReference[Option[A]](None)

  def get(): A = value.get match {
    case Some(result) ⇒ result
    case None ⇒
      while (value.get match {
        case Some(result) ⇒ return result
        case None ⇒ true
      }) value.synchronized {
        log.traceWhere(this + " get() waiting", XLoggable.Where.BEFORE)
        value.wait
        log.traceWhere(this + " get() running", XLoggable.Where.BEFORE)
      }
      // unreachable point
      value.get.getOrElse(null.asInstanceOf[A])
  }

  /**
   * Waits `timeout` millis. If `timeout <= 0` just returns 0. If the system clock
   *  went backward, it will return 0, so it never returns negative results.
   */
  protected def waitMeasuringElapsed(fName: String, timeout: Long): Long = if (timeout <= 0) 0 else {
    val start = System.currentTimeMillis
    value.synchronized {
      log.traceWhere(this + " " + fName + "(" + timeout + ") waiting", -4)
      value.wait(timeout)
    }
    val elapsed = System.currentTimeMillis - start
    val result = if (elapsed < 0) 0 else elapsed
    log.traceWhere(this + " " + fName + "(" + timeout + ") running, reserve " + (timeout - result), -4)
    result
  }

  /**
   * Waits for this SyncVar to become defined at least for
   *  `timeout` milliseconds (possibly more), and gets its
   *  value (if guard return true).
   *
   *  @param timeout     the amount of milliseconds to wait, 0 means forever
   *  @return            `None` if variable is undefined after `timeout`, `Some(value)` otherwise
   */
  def get(timeout: Long, guard: (A) ⇒ Boolean): Option[A] = value.get match {
    case Some(result) if (guard(result)) ⇒ Some(result)
    case _ ⇒
      var rest = timeout
      while ((value.get match {
        case Some(result) if (guard(result)) ⇒
          return Some(result)
        case _ ⇒ true
      }) && rest > 0) {
        /**
         * Defending against the system clock going backward
         *  by counting time elapsed directly.  Loop required
         *  to deal with spurious wakeups.
         */
        val elapsed = waitMeasuringElapsed("get", rest)
        rest -= elapsed
      }
      value.get
  }

  /**
   * Waits for this SyncVar to become defined at least for
   *  `timeout` milliseconds (possibly more), and gets its
   *  value.
   *
   *  @param timeout     the amount of milliseconds to wait, 0 means forever
   *  @return            `None` if variable is undefined after `timeout`, `Some(value)` otherwise
   */
  def get(timeout: Long): Option[A] = value.get match {
    case Some(result) ⇒ Some(result)
    case _ ⇒
      var rest = timeout
      while ((value.get match {
        case Some(result) ⇒
          return Some(result)
        case _ ⇒ true
      }) && rest > 0) {
        /**
         * Defending against the system clock going backward
         *  by counting time elapsed directly.  Loop required
         *  to deal with spurious wakeups.
         */
        val elapsed = waitMeasuringElapsed("get", rest)
        rest -= elapsed
      }
      value.get
  }

  def take(): A = value.getAndSet(None) match {
    case Some(result) ⇒ result
    case None ⇒
      while (value.getAndSet(None) match {
        case Some(result) ⇒ return result
        case None ⇒ true
      }) value.synchronized {
        log.traceWhere(this + " take() waiting", XLoggable.Where.BEFORE)
        value.wait
        log.traceWhere(this + " take() running", XLoggable.Where.BEFORE)
      }
      // unreachable point
      value.get.getOrElse(null.asInstanceOf[A])
  }

  def set(x: A): Unit = {
    value.set(Some(x))
    value.synchronized { value.notifyAll }
  }

  def put(x: A): Unit = {
    while (!value.compareAndSet(None, Some(x)))
      value.synchronized {
        log.traceWhere(this + " put(...) waiting, current value is " + value, XLoggable.Where.BEFORE)
        value.wait
        log.traceWhere(this + " put(...) running", XLoggable.Where.BEFORE)
      }
    value.synchronized { value.notifyAll }
  }

  def put(x: A, timeout: Long): Unit = {
    if (timeout > 0) {
      var rest = timeout
      while ((if (value.compareAndSet(None, Some(x)))
        value.synchronized {
        value.notifyAll
        false
      }
      else
        true) && rest > 0)
        value.synchronized {
          log.traceWhere(this + " put(...) waiting, current value is " + value, XLoggable.Where.BEFORE)
          /**
           * Defending against the system clock going backward
           *  by counting time elapsed directly.  Loop required
           *  to deal with spurious wakeups.
           */
          rest -= waitMeasuringElapsed("put", rest)
        }
    } else if (value.compareAndSet(None, Some(x)))
      value.synchronized { value.notifyAll }
  }

  def isSet: Boolean = value.get != None

  def unset(): Unit = {
    value.set(None)
    value.synchronized { value.notifyAll }
  }

  def waitUnset(timeout: Long): Boolean = {
    if (timeout > 0) {
      var rest = timeout
      while ((if (value.get == None) return true else true) && rest > 0)
        value.synchronized {
          log.traceWhere(this + " waitUnset(...) waiting, current value is " + value, XLoggable.Where.BEFORE)
          /**
           * Defending against the system clock going backward
           *  by counting time elapsed directly.  Loop required
           *  to deal with spurious wakeups.
           */
          rest -= waitMeasuringElapsed("waitUnset", rest)
          if (value.get == None) return true
        }
      false
    } else isSet
  }
}

object SyncVar {
  def apply[T](arg: T): SyncVar[T] = synchronized {
    val result = new SyncVar[T]()
    result.set(arg)
    result
  }
  def apply[T](): SyncVar[T] = synchronized {
    new SyncVar[T]()
  }
}
