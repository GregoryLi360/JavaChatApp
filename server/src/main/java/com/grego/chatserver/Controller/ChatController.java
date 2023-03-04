package com.grego.chatserver.Controller;

import java.util.HashMap;
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
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate msgTemplate;

    // @Autowired
    // private WebSocketEventListener listener;

    private static final String SERVER_NAME = "* SERVER MESSAGE *";
    private HashMap<String, String> usernameMap = new HashMap<>() {{
        put(SERVER_NAME, "");
    }};

    public static volatile HashMap<String, String> sessionMap = new HashMap<>();

    @MessageMapping("/connection")
    @SendTo("/chatroom/public")
    public Message newUser(@Payload Message msg, SimpMessageHeaderAccessor header) {
        String username = msg.getSender();
        System.out.println("Connection: " + msg);
        if (usernameMap.containsKey(username)) {
            String errorMessage = "Username " + username + " is already in use. Please choose a different username.";
            Message error = Message.builder()
                .sender(SERVER_NAME)    
                .recipient(username)
                .content(errorMessage)
                .type(MessageType.MESSAGE)
                .build();
            System.out.println("Duplicate");
            return error;
        } else {
            usernameMap.put(username, header.getSessionId());
            System.out.println("Success");
            return msg;
        }
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message sendMessage(@Payload final Message msg, SimpMessageHeaderAccessor header) {
        System.out.println("message: " + msg);
        if (msg.getType() != MessageType.MESSAGE || !usernameMap.get(msg.getSender()).equals(header.getSessionId())) return null;
        System.out.println("valid message");
        return msg;
    }

    @MessageMapping("/private-message")
    public Message privMessage(@Payload Message msg) {
        msgTemplate.convertAndSendToUser(msg.getRecipient(), "/private", msg);
        return msg;
    }
}
