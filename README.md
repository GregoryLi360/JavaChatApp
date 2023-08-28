# JavaChatApp
> **Frontend and Backend Java websocket chat application**


## Table of contents
- [Demo](#demo)
- [Installation](#installation)
- [Additional Info](#additional-info)
  - [Features](#features)
  - [Client](#client)
  - [Server](#server)
  - [Known Issues](#known-issues)

## Demo
https://user-images.githubusercontent.com/110444522/224509809-dd2f0bfa-3c83-4cd3-87e7-81aa2248553f.mp4


## Installation
1. [Install Java 17+](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)
2. [Install jar file and run](https://github.com/GregoryLi360/JavaChatApp/blob/master/client/target/grego-chat-client.jar)

## Additional Info

### Features
* Timestamped messages with connect and disconnect messages
* Max users capacity set on the server-side
* Force disconnects duplicate usernames in the same session

### Client
Runs on local application\
GUI built with Javax Swing\
Uses Spring stomp messaging framework to communicate with server

### Server
Runs on Render.com free server\
Java Spring Boot server\
Uses stomp messaging framework 

### Known Issues
* Render.com free tier has a slow cold start, so you need to wait and relaunch the client after attempting to connect
* After 5 minutes, Render.com will close a websocket connection forcefully because of free tier limitations
* Client textbox can become unfocused/unselectable
