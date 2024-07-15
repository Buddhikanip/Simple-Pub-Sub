# Simple Publisher and Subscriber Application using java

## Prerequisites

Before running the server, ensure you have the following:

- Java Development Kit ([JDK](https://www.oracle.com/java/technologies/downloads/)) installed.

## How to run
- You have to clone this project
- Goto `src` folder location
  ```bash
  cd ./Simple-Pub-Sub/src
  ```
- You have to compile `Server.java` file
  ```bash
  javac Server.java
  ```
- Run Server
  ```bash
  java Server 5000
  ```
- Using another terminal compile `Client.java` file
  ```bash
  javac Clinet.java
  ```
- Run Client (Using several terminals you can up several clients with this command)
  ```bash
  java Clinet 127.0.0.1 5000
  ```
