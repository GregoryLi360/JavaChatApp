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
import java.util.Scanner;
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
import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.glassfish.tyrus.core.HandshakeException;

import com.grego.chatclient.ChatClientApplication;
import com.grego.chatclient.Websocket.MessageHandlers.PublicStompFrameHandler;
import com.grego.chatclient.Websocket.MessageHandlers.PrivateStompFrameHandler;
import com.grego.chatclient.Websocket.Model.Message;
import com.grego.chatclient.Websocket.Model.MessageType;

@ClientEndpoint
public class WebsocketClient extends Endpoint {
    private WebSocketStompClient client;
    private StompSession session;
    private String username;
    private PublicStompFrameHandler publicMessageHandler;
    private PrivateStompFrameHandler privateMessageHandler;

    public WebsocketClient(String username, PublicStompFrameHandler publicMessageHandler, PrivateStompFrameHandler privateMessageHandler) throws RuntimeException, InterruptedException, ExecutionException {
        this.username = username;
        this.publicMessageHandler = publicMessageHandler;
        this.privateMessageHandler = privateMessageHandler;
        StompSessionHandler sessionHandler = new CustomStompSessionHandler(username);

        try { /* to connect by default websocket, then sockjs fallback option */
            getClientAndSession(new StandardWebSocketClient(), ChatClientApplication.wsURL, sessionHandler);
        } catch (ExecutionException | InterruptedException e) { 
            checkSockJSFallbackAvailability(e);
            getClientAndSession(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))), ChatClientApplication.sockjsURL, sessionHandler);
        }
    }

    public void disconnect() {
        if (session.isConnected()) 
            session.disconnect();

        if (client.isRunning()) 
            client.stop();
    }

    public boolean sessionConnected() {
        return session.isConnected();
    }

    public void send(String content) {
        Message msg = Message.builder()
            .sender(username)
            .type(MessageType.MESSAGE)
            .content(content)
            .timestamp(ZonedDateTime.now(ZoneOffset.UTC).format(ChatClientApplication.DATE_TIME_FORMATTER))
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

    private class CustomStompSessionHandler extends StompSessionHandlerAdapter {
        private String username;

        public CustomStompSessionHandler(String username) {
            this.username = username;
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("Connected to WebSocket server");
            
            /* subscribe to public chatroom and private messages */
            String publicChat = "/chatroom/public", privateChat = "/user/" + username + "/private";
            String sessionID = session.getSessionId();
            var publicSub = session.subscribe(publicChat, publicMessageHandler);
            var privateSub = session.subscribe(privateChat, privateMessageHandler);

            /* send a connection message */
            Message msg = Message.builder()
                .sender(username)
                .type(MessageType.CONNECT)
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC).format(ChatClientApplication.DATE_TIME_FORMATTER))
                .build();

            session.send("/app/connection", msg);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            super.handleTransportError(session, exception);
            System.out.println("Exception: " + exception);
        }

        @Override
        public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            super.handleException(session, command, headers, payload, exception);
            System.out.println("Exception: " + exception);
        }
    }

    @Override
    public void onOpen(Session arg0, EndpointConfig arg1) {}
}