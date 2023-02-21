package com.grego.chatserver.Controller;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.grego.chatserver.Model.Message;
import com.grego.chatserver.Model.MessageType;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate msgTemplate;

    HashSet<String> users = new HashSet<>();

    @MessageMapping("/message")
    @SendTo("chatroom/public")
    public Message newUser(@Payload Message msg, SimpMessageHeaderAccessor header) {
        System.out.println(msg);
        System.out.println(header);
        header.getSessionAttributes().put("usernames", msg.getSender());
        return msg;
    }

    // @MessageMapping("/message")
    // @SendTo("/chatroom/public")
    public Message sendMessage(@Payload final Message msg) {
        System.out.println(msg);
        if (msg.getType() == MessageType.CONNECT && users.contains(msg.getSender())) {
            return null;
        }
        return msg;
    }

    @MessageMapping("/private-message")
    public Message privMessage(@Payload Message msg) {
        msgTemplate.convertAndSendToUser(msg.getRecipient(), "/private", msg);
        return msg;
    }
}
