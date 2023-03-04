package com.grego.chatserver.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import com.grego.chatserver.Model.Message;
import com.grego.chatserver.Model.MessageType;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

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

    public static Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @MessageMapping("/connection")
    @SendTo("/chatroom/public")
    public Message newUser(@Payload Message msg, SimpMessageHeaderAccessor header) throws IOException {
        String username = msg.getSender();
        System.out.println("Connection: " + msg);
        if (usernameMap.containsKey(username)) {
            System.out.println("Duplicate");
            String errorMessage = "Username " + username + " is already in use. Please choose a different username.";
            Message error = Message.builder()
                .sender(SERVER_NAME)    
                .recipient(username)
                .content(errorMessage)
                .type(MessageType.DISCONNECT)
                .build();
                
            CompletableFuture<Void> future = new CompletableFuture<>();
            msgTemplate.convertAndSend("/chatroom/public", error,new MessagePostProcessor() {
                @Override
                public org.springframework.messaging.Message<?> postProcessMessage(org.springframework.messaging.Message<?> message) {
                    future.complete(null);
                    return message;
                }
            });

            /* disconnect user after message is received */
            future.thenRun(() -> {
                try {
                    sessionMap.get(header.getSessionId()).close();
                    sessionMap.remove(header.getSessionId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            return error;
        } else {
            System.out.println("Success");
            usernameMap.put(username, header.getSessionId());
            return msg;
        }
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message sendMessage(@Payload final Message msg, SimpMessageHeaderAccessor header) {
        System.out.println("message: " + msg);
        // if (msg.getType() != MessageType.MESSAGE || !header.getSessionId().equals(usernameMap.get(msg.getSender()))) return null;
        System.out.println("valid message");
        return msg;
    }

    @MessageMapping("/private-message")
    public Message privMessage(@Payload Message msg) {
        msgTemplate.convertAndSendToUser(msg.getRecipient(), "/private", msg);
        return msg;
    }
}
