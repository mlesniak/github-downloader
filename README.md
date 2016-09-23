# Introduction

This simple program will download all files from [githubarchive.org](http://githubarchive.org). This website contains

- Activity archives starting 2/12/2011
- Activity archives for dates between 2/12/2011-12/31/2014, recorded from the (now deprecated) Timeline API
- Activity archives for dates starting 1/1/2015, recorded from the Events API

## Build

    mvn clean install

## Start 

    java -jar target/github-downloader-1.0-SNAPSHOT.jar
    
The application will download all files to the directory `data/`. 
On each run, it will start anew, although already existing files are not downloaded.

## Source code quality

Note that the source code is hacky and just a simple script to download the necessary files.

## Spark SQL statements

    scala> sqlContext.sql("select created_at, actor.login, cat[2] from github lateral view explode(payload.shas) v1 as cat").show

## TODO

- Group files by user for playing around (as json first)
- Convert JSON to Parquet for further processing

## License
        
Copyright (c) 2016 Michael Lesniak, licensed under the Apache License.        
