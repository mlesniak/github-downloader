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
  val conf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("Spark")
  val sc = new SparkContext(conf)
  val sql = new SQLContext(sc)


  def partitionByLogin() = {
    val path = "data/2011-02-12-0.json"
    val github = sql.read.json(path)
    github.registerTempTable("github")
    github.persist()
    val logins = sql.sql("select distinct actor.login from github").collect()
    logins.foreach(row => {
      val login = row(0)
      val userData = github.filter(s"actor.login = '$login'")

      // For debugging purposes we use JSON. Later we'll use parquet.
      // userData.write.mode(SaveMode.Overwrite).parquet("data/out/" + login)
      userData.write.mode(SaveMode.Overwrite).json("data/out/" + login)
    })
  }

  def createEvents(): Unit = {
    val userPath = "data/out/*/*"
    val userData = sql.read.json(userPath)
    val events = userData.select("actor.login", "type", "created_at")
    events.show()
  }


  def convertToParquet(): Unit = {
    val rawPath = "hdfs:///user/mlesniak/github/"
    val parquetPath = "hdfs:///user/mlesniak/github-parquet/"
    //    val rawPath = "data/*"
    //    val parquetPath = "data-parquet/"
    val rawData = sql.read.json(rawPath)
    rawData.write.mode(SaveMode.Overwrite).parquet(parquetPath)
  }

  //  scala> sqlContext.sql("select created_at, actor.login, cat[2] from github lateral view explode(payload.shas) v1 as cat").show
  // sqlContext.sql("select created_at, actor.login, cat[2] from github lateral view explode(payload.shas) v1 as cat where type='PushEvent' and actor.login = 'jameswilson'").show(100)

  def commits(username: String): Unit = {
    val rawPath = "hdfs:///user/mlesniak/commits-"
    val commits = sql.sql("" +
      s"select created_at, actor.login, cat[2] from github lateral view explode(payload.shas) v1 " +
      s"as cat " + s"where type='PushEvent' and actor.login = '$username'")
    commits.write.mode(SaveMode.Overwrite).parquet(rawPath + username)
  }

  println("working")
  //   convertToParquet()
}

