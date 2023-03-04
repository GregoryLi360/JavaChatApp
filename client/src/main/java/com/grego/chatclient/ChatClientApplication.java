package com.grego.chatclient;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;

import org.springframework.messaging.simp.stomp.ConnectionLostException;

import com.grego.chatclient.Gui.Gui;
import com.grego.chatclient.Websocket.WebsocketClient;
import com.grego.chatclient.Websocket.Model.Message;

public final class ChatClientApplication {
    /* websocket no trailing slash, sockjs trailing slash */
    public static final String wsURL = "ws://localhost:8080/chat", sockjsURL = "http://localhost:8080/chat/";
    private WebsocketClient client;

    private ChatClientApplication() throws RuntimeException, InterruptedException, ExecutionException {
        var sc = new Scanner(System.in);
        System.out.print("Username: ");
        String username = sc.nextLine();
        client = new WebsocketClient(username);
        
        while (true) {
            var msg = sc.nextLine(); 
            if (msg.length() == 0 || msg.equals("exit")) {
                break;
            }

            client.send(msg);
        }
        sc.close(); 
        // wait before disconnecting
        client.disconnect();
        
        // SwingUtilities.invokeLater(() -> new Gui());
    }

    public static void main(String[] args) throws Exception {
        new ChatClientApplication();
    }


    // void temp () {
    //     if (getRootCause(exception).getClass() == ConnectionLostException.class) {
    //         System.out.println("reconnecting with new username");
    //         String username = "noob";
    //         System.out.print("Username: " + username);
    //         WebsocketClient.this.username = username;

    //         StompSessionHandler sessionHandler = new CustomStompSessionHandler(username);

    //         try { /* to connect by default websocket, then sockjs fallback option */
    //             getClientAndSession(new StandardWebSocketClient(), ChatClientApplication.wsURL, sessionHandler);
    //         } catch (ExecutionException | InterruptedException e) { 
    //             checkSockJSFallbackAvailability(e);
    //             try {
    //                 getClientAndSession(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))), ChatClientApplication.sockjsURL, sessionHandler);
    //             } catch (InterruptedException | ExecutionException e1) {
    //                 e1.printStackTrace();
    //             }
    //         }
    //     }
    // }

}
