package com.grego.chatserver.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.grego.chatserver.Model.Message;
import com.grego.chatserver.Model.MessageType;

@Component
public class WebSocketEventListener {
    @Autowired 
    private SimpMessageSendingOperations sendingOperation;
    
    @EventListener
    public void handleWebSocketConnectListener(final SessionConnectEvent event) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        final String sessionID = (String) headerAccessor.getSessionId();
        System.out.println("Connected: " + sessionID);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        final String sessionID = (String) headerAccessor.getSessionId();

        System.out.println("Disconnected: " + sessionID);

        Optional<String> disconnectedUser = ChatController.usernameMap.entrySet().stream().filter(e -> e.getValue().equals(sessionID)).map(e -> e.getKey()).findFirst();
        disconnectedUser.ifPresent(username -> {
            final Message msg = Message.builder()
                .type(MessageType.DISCONNECT)
                .sender(username)
                .build();

            sendingOperation.convertAndSend("/chatroom/public", msg);   
        });
    }
}