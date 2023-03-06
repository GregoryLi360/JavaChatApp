package com.grego.chatserver.Model;

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
