package com.grego.chatclient.Websocket;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.WebSocket;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.websocket.RemoteEndpoint.Basic;
import javax.faces.application.Application;
import javax.json.Json;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.Session;

import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.ContentTypeResolver;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.glassfish.tyrus.core.HandshakeException;

import com.grego.chatclient.Websocket.Model.Message;
import com.grego.chatclient.Websocket.Model.MessageType;

@ClientEndpoint
public class WebsocketClient extends Endpoint {
    private WebSocketStompClient client;
    public StompSession session;
    private String username;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

    public WebsocketClient(String username, String wsURL, String sockjsURL) throws RuntimeException, InterruptedException, ExecutionException {
        this.username = username;
        StompSessionHandler sessionHandler = new CustomStompSessionHandler(username);

        try { /* to connect by default websocket, then sockjs fallback option */
            getClientAndSession(new StandardWebSocketClient(), wsURL, sessionHandler);
        } catch (ExecutionException | InterruptedException e) { 
            checkSockJSFallbackAvailability(e);
            getClientAndSession(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))), sockjsURL, sessionHandler);
        }
    }

    public void disconnect() {
        session.disconnect();
    }

    public void send(String content) {
        Message msg = Message.builder()
            .sender(username)
            .type(MessageType.MESSAGE)
            .content(content)
            .timestamp(ZonedDateTime.now(ZoneOffset.UTC).format(DATE_TIME_FORMATTER))
            .build();

        session.send("/app/message", msg);
    }

    private void getClientAndSession(WebSocketClient client, String url, StompSessionHandler sessionHandler) throws InterruptedException, ExecutionException {
        this.client = new WebSocketStompClient(client);
        this.client.setMessageConverter(new MappingJackson2MessageConverter());
        this.session = this.client.connect(url, sessionHandler).get();
    }

    private boolean checkSockJSFallbackAvailability(Exception e) throws RuntimeException {
        Throwable root = getRootCause(e);
        if (!(root.getClass() == HandshakeException.class && ((HandshakeException) root).getHttpStatusCode() == 400)) 
            throw new RuntimeException(e);

        return true;
    }

    private static Throwable getRootCause(Throwable t) {
        for (Throwable cause = null; (cause = t.getCause()) != null  && (t != cause);)
            t = cause;
        return t;
    }

    private static class CustomStompSessionHandler extends StompSessionHandlerAdapter {
        private String username;

        public CustomStompSessionHandler(String username) {
            this.username = username;
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("Connected to WebSocket server");
            
            String destination = "/chatroom/public";
            session.subscribe(destination, new CustomStompFrameHandler());
            System.out.println("subscribed");
            
            Message msg = Message.builder()
                .sender(username)
                .type(MessageType.CONNECT)
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC).format(DATE_TIME_FORMATTER))
                .build();

            session.send("/app/connection", msg);
        }

        @Override
        public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            super.handleException(session, command, headers, payload, exception);
            exception.printStackTrace();
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            super.handleTransportError(session, exception);
            exception.printStackTrace();
        }
    }

    private static class CustomStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, @Nullable @Payload Object payload) {
            if (payload == null) return;
            
            Message message = (Message) payload;
            String sender = message.getSender();
            String time = (message.getTimestamp() == null ? ZonedDateTime.now() : 
                    LocalDateTime.parse(message.getTimestamp(), DATE_TIME_FORMATTER)
                        .atZone(ZoneOffset.UTC)
                        .withZoneSameInstant(ZoneId.systemDefault())
                ).format(DATE_TIME_FORMATTER);

            switch (message.getType()) {
                case CONNECT:
                case DISCONNECT:
                    System.out.println(time + "  " + sender + " has " + message.getType().toString().toLowerCase() + "ed");
                    break;
                case MESSAGE:
                    System.out.println(time + "  " + sender + ": " + message.getContent());
                    break;
            }
        }
    }

    @Override
    public void onOpen(Session arg0, EndpointConfig arg1) {}
}