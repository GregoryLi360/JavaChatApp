package com.grego.chatclient.Websocket.Model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private MessageType type;
    private String content;
    private String sender;
    private String recipient;
    private String timestamp;
}
