package com.mlesniak.github

import org.apache.spark.sql.{SaveMode, SparkSession}

//import org.apache.spark.sql.SparkSession

/**
  * Processing playground, might be a separate project later on...
  *
  * @author Michael Lesniak (mlesniak@micromata.de)
  */
object Processing extends App {
  val spark = SparkSession
    .builder()
    .appName("Spark SQL Example")
    .master("local[*]")
    .getOrCreate()


  val path = "data/2011-02-12-0.json"
  val github = spark.read.json(path)

  github.createOrReplaceTempView("github")
  github.cache()

  val logins = spark.sql("select distinct actor.login from github")
  logins.show()

  logins.foreach(row => {
    // Store all data for a given user.
    val username = row(0)
    println(s"Processing $username")
    val userData = github.filter(s"actor.login = '$username'")
    userData.write.mode(SaveMode.Overwrite).json(s"data/out/$username")
  })
}
