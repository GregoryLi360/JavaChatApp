package com.grego.chatclient;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.grego.chatclient.Gui.Gui;
import com.grego.chatclient.Websocket.WebsocketClient;
import com.grego.chatclient.Websocket.Model.Message;

public final class ChatClientApplication {
    /* websocket no trailing slash, sockjs trailing slash */
    private static final String wsURL = "ws://localhost:8080/chat", sockjsURL = "http://localhost:8080/chat/";

    private ChatClientApplication() throws InterruptedException, ExecutionException {
        new WebsocketClient(wsURL, sockjsURL);
        // SwingUtilities.invokeLater(() -> new Gui());
    }

    public static void main(String[] args) throws Exception {
        new ChatClientApplication();
        new Scanner(System.in).nextLine(); // wait for response
    }

}
