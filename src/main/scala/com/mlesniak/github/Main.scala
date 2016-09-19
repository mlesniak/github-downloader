package com.mlesniak.github

import java.text.SimpleDateFormat
import java.util.Properties

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
}
