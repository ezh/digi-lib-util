package org.digimead.digi.lib.util

import java.text.SimpleDateFormat
import java.util.Date

object Util {
  private lazy val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")

  def dateString(date: Date) = df.format(date)
  def dateFile(date: Date) = dateString(date).replaceAll("""[:\.]""", "_").replaceAll("""\+""", "x")
}
