package com.grego.chatserver.Controller;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grego.chatserver.Model.Message;
import com.grego.chatserver.Model.MessageType;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
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

        /* allow unique username to connect */
        if (!usernameMap.containsKey(username)) {
            System.out.println("Unique username connection: " + username);
            usernameMap.put(username, header.getSessionId());
            return msg;
        }

        /* forcefully disconnect duplicate usernames */
        System.out.println("Duplicate username connection: " + username);
        String errorMessage = "Username " + username + " is already in use. Please choose a different username.";
        Message error = Message.builder()
            .sender(SERVER_NAME)
            .recipient(header.getSessionId())
            .content(errorMessage)
            .type(MessageType.DISCONNECT)
            .build();
        
        /* send error message to private channel */
        CompletableFuture<Void> future = new CompletableFuture<>();
        msgTemplate.convertAndSendToUser(username, "/private", error, new MessagePostProcessor() {
            @Override
            public org.springframework.messaging.Message<?> postProcessMessage(org.springframework.messaging.Message<?> message) {
                // MappingJackson2MessageConverter mapper = new MappingJackson2MessageConverter();
                // var msg = mapper.fromMessage(message, Message.class);
                future.complete(null);
                return message;
            }
        });

        /* disconnect user after message is send (and supposedly received) */
        future.thenRun(() -> {
            try {
                var session = sessionMap.remove(header.getSessionId());
                System.out.println("Force disconnecting: " + session);
                Thread.sleep(500);
                session.close(CloseStatus.NORMAL);
                System.out.println("Force disconnection successful");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        return null;
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message sendMessage(@Payload final Message msg, SimpMessageHeaderAccessor header) {
        System.out.println("message: " + msg);
        if (msg.getType() != MessageType.MESSAGE || !header.getSessionId().equals(usernameMap.get(msg.getSender()))) {
            System.out.println("Impersonator!");
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
