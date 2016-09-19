package com.mlesniak.github

import java.io.{File, FileOutputStream}
import java.net.URL
import java.nio.file.{Files, Paths}
import java.text.SimpleDateFormat
import java.util.Properties

import com.github.nscala_time.time.Imports._

/**
  * Application entry point.
  *
  * @author Michael Lesniak (mlesniak@micromata.de)
  */
object Main extends App {
  val props = new Properties()
  props.load(getClass.getResourceAsStream("/date-ranges.properties"))

  val sdf = new SimpleDateFormat("dd.MM.yyyy")
  val startDate = sdf.parse(props.getProperty("start"))

  println(startDate)

  var jodaDate = new DateTime(startDate)
  val endDate = DateTime.now

  //  http://data.githubarchive.org/2015-01-01-15.json.gz
  val sdf2 = new SimpleDateFormat("yyyy-MM-dd")

  while (jodaDate < endDate) {
    jodaDate = jodaDate + 1.hour
    new File("data/").mkdir()
    val datePart = sdf2.format(jodaDate.toDate)
    for (hour <- 0 to 23) {
      val path = f"$datePart%s-$hour%d.json.gz"
      if (!Files.exists(Paths.get("data/" + path))) {
        val url = s"http://data.githubarchive.org/$path"
        println(url)
        val src = new URL(url)
        val con = src.openConnection()
        val input = con.getInputStream
        val buffer = new Array[Byte](4096)
        var n: Int = -1

        val output = new FileOutputStream(new File("data/" + path))
        do {
          n = input.read(buffer)
          if (n != -1) {
            output.write(buffer, 0, n)
          }
        } while (n != -1)
        output.close()
      }
      println(path)
      System.exit(1)
    }
  }
}
