package com.grego.chatclient;

import java.time.format.DateTimeFormatter;
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
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm:ss.SSS a");
    public static final String SERVER_NAME = "* SERVER MESSAGE *";
    
    private final Scanner sc = new Scanner(System.in);
    private WebsocketClient client;

    private ChatClientApplication() throws RuntimeException, InterruptedException, ExecutionException {
        getNewClient();        
        // SwingUtilities.invokeLater(() -> new Gui());

        while (true) {
            var msg = sc.nextLine(); 
            if (client.client == null || !client.client.isRunning()) {
                getNewClient();
                continue;
            }

            if (msg.length() == 0 || msg.equals("exit") || !client.client.isRunning()) {
                break;
            }

            client.send(msg);
        }
        sc.close();
        client.disconnect();
    }

    public synchronized void getNewClient() throws RuntimeException, InterruptedException, ExecutionException {
        System.out.print("Username: ");
        String username = sc.nextLine();

        client = new WebsocketClient(username, this);
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
