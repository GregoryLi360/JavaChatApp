package com.grego.chatclient.Websocket;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.WebSocket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.ClientEndpoint;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.Session;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.glassfish.tyrus.core.HandshakeException;

import com.grego.chatclient.Websocket.Model.Message;

@ClientEndpoint
public class WebsocketClient extends Endpoint {
    private WebSocketStompClient client;
    private StompSession session;

    public WebsocketClient(String wsURL, String sockjsURL) {
        StompSessionHandler sessionHandler = new CustomStompSessionHandler();

        try {
            client = new WebSocketStompClient(new StandardWebSocketClient());
            client.setMessageConverter(new MappingJackson2MessageConverter());
            session = client.connect(wsURL, sessionHandler).get();
        } catch (ExecutionException | InterruptedException e) { 
            /* check for sockjs fallback availability */
            Throwable root = getRootCause(e);
            if (!(root.getClass() == HandshakeException.class && ((HandshakeException) root).getHttpStatusCode() == 400)) 
                throw new RuntimeException(e);

            /* use sockjs fallback */
            client = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
            client.setMessageConverter(new MappingJackson2MessageConverter());
            try {
                session = client.connect(sockjsURL, sessionHandler).get();
            } catch (InterruptedException | ExecutionException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    private static Throwable getRootCause(Throwable t) {
        for (Throwable cause = null; (cause = t.getCause()) != null  && (t != cause);)
            t = cause;
        return t;
    }


    @OnMessage
    public void onMessage(String message) {
        System.out.println("[Greetings] " + message + "\n");
    }

    private static class CustomStompSessionHandler extends StompSessionHandlerAdapter {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("Connected to WebSocket server");

            String destination = "/chatroom/public"; // Replace with the destination you want to subscribe to
            session.subscribe(destination, new CustomStompFrameHandler());
        }
    }

    private static class CustomStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            Message message = (Message) payload;
            System.out.println("Received message: " + message.getContent());
        }
    }

    @Override
    public void onOpen(Session arg0, EndpointConfig arg1) {
        // TODO Auto-generated method stub
        
    }

}