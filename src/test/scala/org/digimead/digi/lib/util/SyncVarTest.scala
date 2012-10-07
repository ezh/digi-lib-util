/**
 * Digi-Lib-Util - utility module of all Digi applications and libraries, containing various common routines
 *
 * Copyright (c) 2012 Alexey Aksenov ezh@ezh.msk.ru
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.digimead.digi.lib.util

import java.util.Date
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers._
import java.util.concurrent.atomic.AtomicInteger
import org.digimead.digi.lib.log.Logging
import org.digimead.digi.lib.log.Record
import scala.concurrent.{ SyncVar => ScalaSyncVar }

class LoggingTestSimpleInit extends FunSuite with BeforeAndAfter {

  before {
    Record.init(new Record.DefaultInit)
    Logging.init(new Logging.DefaultInit)
  }

  /**
   * VAR total: 95ms
   * @volatile VAR total: 143ms
   * atomic total: 137ms
   * synchronized total: 245ms
   * scala sync total: 920ms
   * sync total: 614ms
   */
  test("benchmark access speed") {
    var time = 0L

    // var
    var a = 1
    time = System.currentTimeMillis
    var result = for (i <- 0 to 10000000) {
      a = a + i
      a
    }
    System.err.println("VAR total: " + (System.currentTimeMillis - time) + "ms")

    // volatile var
    @volatile var b = 1
    time = System.currentTimeMillis
    result = for (i <- 0 to 10000000) {
      b = b + i
      b
    }
    System.err.println("@volatile VAR total: " + (System.currentTimeMillis - time) + "ms")

    // atomic var
    val c = new AtomicInteger(0)
    time = System.currentTimeMillis
    result = for (i <- 0 to 10000000) {
      c.set(c.get + i)
      c.get
    }
    System.err.println("atomic total: " + (System.currentTimeMillis - time) + "ms")

    // atomic var
    var d = 0
    val lock = new Object
    time = System.currentTimeMillis
    result = for (i <- 0 to 10000000) lock.synchronized {
      d = d + i
      d
    }
    System.err.println("synchronized total: " + (System.currentTimeMillis - time) + "ms")

    // scala sync var
    val e = new ScalaSyncVar[Int]()
    e.set(0)
    time = System.currentTimeMillis
    result = for (i <- 0 to 10000000) {
      e.set(e.get + i)
      e.get
    }
    System.err.println("scala sync total: " + (System.currentTimeMillis - time) + "ms")
    assert(true)

    // sync var
    val f = SyncVar[Int](0)
    time = System.currentTimeMillis
    result = for (i <- 0 to 10000000) {
      f.set(f.get + i)
      f.get
    }
    System.err.println("sync total: " + (System.currentTimeMillis - time) + "ms")
    assert(true)
  }
}
