package com.grego.chatserver.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.grego.chatserver.model.Message;

@Controller
public class ChatController {

    @Autowired
    SimpMessagingTemplate msgTemplate;

    @MessageMapping("/message")
    @SendTo("chatroom/public")
    public Message newUser(@Payload Message msg, SimpMessageHeaderAccessor header) {
        header.getSessionAttributes().put("usernames", msg.getSender());
        return msg;
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message sendMessage(@Payload final Message msg) {
        return msg;
    }

    @MessageMapping("/private-message")
    public Message privMessage(@Payload Message msg) {
        msgTemplate.convertAndSendToUser(msg.getRecipient(), "/private", msg);
        return msg;
    }
}
