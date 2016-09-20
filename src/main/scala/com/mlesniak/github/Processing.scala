package com.mlesniak.github

import org.apache.spark.sql.SparkSession

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
    // .config("spark.some.config.option", "some-value")
    .getOrCreate()


  val path = "data/2011-02-12-0.json"
  val github = spark.read.json(path)

  github.printSchema()
  github.createOrReplaceTempView("github")
  val repos = spark.sql("SELECT repo.name, count(repo.name) FROM github group by repo.name order by count(repo.name) " +
    "desc")
  repos.show()

  val rows = repos.rdd
  println(rows)

  val grdd = github.rdd
  println(grdd)
}
