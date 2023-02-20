package com.grego.chatclient.Websocket.Model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class Message {
    private MessageType type;
    private String content;
    private String sender;
    private String recipient;
    private String timestamp;
}
