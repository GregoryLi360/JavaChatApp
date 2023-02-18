package com.grego.chatserver.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import com.grego.chatserver.model.Message;

@Controller
public class ChatController {

    @GetMapping("/chat.send")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload final Message msg) {
        return msg;
    }

    @MessageMapping("/chat.newUser")
    @SendTo("topic/public")
    public Message newUser(@Payload Message msg, SimpMessageHeaderAccessor header) {
        header.getSessionAttributes().put("usernames", msg.getSender());
        return msg;
    }
}
