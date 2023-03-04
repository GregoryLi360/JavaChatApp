package com.grego.chatclient;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;


import com.grego.chatclient.Gui.Gui;
import com.grego.chatclient.Websocket.WebsocketClient;
import com.grego.chatclient.Websocket.Model.Message;

public final class ChatClientApplication {
    /* websocket no trailing slash, sockjs trailing slash */
    private static final String wsURL = "ws://localhost:8080/chat", sockjsURL = "http://localhost:8080/chat/";

    private ChatClientApplication() throws InterruptedException, ExecutionException {
        var sc = new Scanner(System.in);
        System.out.print("Username: ");
        var client = new WebsocketClient(sc.nextLine(), wsURL, sockjsURL);
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

}
