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
    .master("local[1]")
    .getOrCreate()


  val path = "data/2011-02-12-0.json"
  val github = spark.read.json(path)

  github.createTempView("github")
  github.persist()

  val logins = spark.sql("select distinct actor.login from github")
  logins.persist()
  val loginCount = logins.count()
  println(s"Number of logins $loginCount")

  // See https://stackoverflow.com/questions/33030726/how-to-iterate-records-spark-scala for the motivation behind
  // using collect().
  // TODO ML Examine reason for collect() deeper.
  logins.collect().foreach(row => {
    val username = row(0)
    println(s"Writing $username")
    val userData = spark.sql(s"select * from github where actor.login = '$username'")
    userData.write.mode(SaveMode.Overwrite).json(s"data/out/$username")
  })
}

