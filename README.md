# Simple Publisher and Subscriber Application using java

## Prerequisites

Before running the server, ensure you have the following:

- Java Development Kit ([JDK](https://www.oracle.com/java/technologies/downloads/)) installed.

## How to run
- You have to clone this project
- Goto `src` folder location
  ```shell
  cd ./Simple-Pub-Sub/src
  ```
- You have to compile `Server.java` file
  ```shell
  javac Server.java
  ```
- Run Server
  ```shell
  java Server 5000
  ```
- Using another terminal compile `Client.java` file
  ```shell
  javac Client.java
  ```
  
- Run Client (Using several terminals you can up several clients with topics below commands)
  * Run `PUBLISHER`
    ```shell
    java Client 127.0.0.1 5000 PUBLISHER TOPIC
    ```
  * Run `SUBSCRIBER`
    ```shell
    java Client 127.0.0.1 5000 SUBSCRIBER TOPIC
    ```
    ***Messages from publishers are sent to subscribers who are listening to the publisher's topic.***
