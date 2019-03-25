# Amazon Crawler

## Introduction

This is a project that use the key words in rawQuery file to crawler some goods from Amazon and generates some "Ads". This is built for faking ads so that we can get data for building ad engine, which returns some ads by matching key words.

## Format of input files

File Path Settings:   
args[0] is file path for rawQuery.txt,    
args[1] is file path for adsData,   
ards[2] is file path for proxylist.csv,   
args[3] is file path for logfile.

proxylist:
```java
[ip],[port],[port],[user],[password]
127.0.0.1,60099,61336,user,password
```
Currently, the username and password are hard coded in the AmazonCrawler class. Please modify it when you need.

rawQueryData:
```java
[key words to search], [bid price], [campaignId], [queryGroupId]
Prenatal DHA, 3.4, 8040,10
```

## Deploy

Just run 

```bash
mvn clean install
```

in the project folder. Then you can use java to run the jar file generated in the target folder.

## Dependencies

All are listed in pom.xml. 

1. jsoup

2. jackson

3. slf4j

4. lucene

5. junit
