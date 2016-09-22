package com.mlesniak.github

import org.apache.spark.sql.{SQLContext, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}

//import org.apache.spark.sql.{SaveMode, SparkSession}

//import org.apache.spark.sql.SparkSession

/**
  * Processing playground, might be a separate project later on...
  *
  * @author Michael Lesniak (mlesniak@micromata.de)
  */
object Processing extends App {
  val path = "data/2011-02-12-0.json"

  val conf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("Spark")
  val sc = new SparkContext(conf)
  val sql = new SQLContext(sc)

  val github = sql.read.json(path)
  github.registerTempTable("github")
  github.persist()


  def partitionByLogin() = {
    val logins = sql.sql("select distinct actor.login from github").collect()
    logins.foreach(row => {
      val login = row(0)
      val userData = github.filter(s"actor.login = '$login'")

      // For debugging purposes we use JSON. Later we'll use parquet.
      // userData.write.mode(SaveMode.Overwrite).parquet("data/out/" + login)
      userData.write.mode(SaveMode.Overwrite).json("data/out/" + login)
    })
  }

  partitionByLogin()
}

