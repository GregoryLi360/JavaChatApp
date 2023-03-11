# JavaChatApp
> **Frontend and Backend Java websocket chat application**


## Table of contents
- [Install](#install)
- [Additional Info](#additional-info)
  - [Features](#features)
  - [Client](#client)
  - [Server](#server)
  - [Known Issues](#known-issues)


## Install
1. [Install Java 17+](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)
2. [Install jar file](https://github.com/GregoryLi360/JavaChatApp/blob/master/client/target/grego-chat-client.jar)

## Additional Info

### Features
* Timestamped messages with connect and disconnect messages
* Max users capacity set on the server-side
* Force disconnects duplicate usernames in the same session

### Client
GUI built with Javax Swing\
Uses Spring stomp messaging framework to communicate with server

### Server
Java Spring Boot server\
Uses stomp messaging framework 

### Known Issues

