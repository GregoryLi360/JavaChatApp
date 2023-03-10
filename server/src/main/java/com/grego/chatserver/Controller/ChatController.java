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
    public static Map<String, String> usernameMap = new ConcurrentHashMap<>() {{
        put(SERVER_NAME, "");
    }};

    public static Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @MessageMapping("/connection")
    @SendTo("/chatroom/public")
    public Message newUser(@Payload Message msg, SimpMessageHeaderAccessor header) throws IOException {
        String username = msg.getSender();

        boolean isDuplicateUsername = usernameMap.containsKey(username);
        boolean atMaxCapacity = usernameMap.size() > 3;

        if (!(isDuplicateUsername || atMaxCapacity)) {
            System.out.println("Unique username connection: " + username);
            usernameMap.put(username, header.getSessionId());
            return msg;
        }

        String errorMessage = "";
        if (isDuplicateUsername) {
            System.out.println("Duplicate username connection: " + username);
            errorMessage = "Username " + username + " is already in use. Please choose a different username.";
        } else if (atMaxCapacity) {
            System.out.println("Max capacity reached, rejected: " + username);
            errorMessage = "Server is at max capacity, please connect at a later time.";
        }

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
