package com.grego.chatclient.Websocket;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;

import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import com.grego.chatclient.ChatClientApplication;
import com.grego.chatclient.Websocket.Model.Message;
import com.grego.chatclient.Websocket.Model.MessageType;

public class MessageHandlers {
    public static class PublicStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, @Nullable @Payload Object payload) {
            if (!(payload instanceof Message)) return;
            
            Message message = (Message) payload;
            String sender = message.getSender();
            String time = (message.getTimestamp() == null ? ZonedDateTime.now() : 
                    LocalDateTime.parse(message.getTimestamp(), ChatClientApplication.DATE_TIME_FORMATTER)
                        .atZone(ZoneOffset.UTC)
                        .withZoneSameInstant(ZoneId.systemDefault())
                ).format(ChatClientApplication.DATE_TIME_FORMATTER);

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

    public static class PrivateStompFrameHandler implements StompFrameHandler {
        private ChatClientApplication chatClient;

        public PrivateStompFrameHandler(ChatClientApplication chatClientApplication) {
            chatClient = chatClientApplication;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, @Nullable @Payload Object payload) {
            if (!(payload instanceof Message)) return;
            
            Message message = (Message) payload;
            String sender = message.getSender();
            String time = (message.getTimestamp() == null ? ZonedDateTime.now() : 
                    LocalDateTime.parse(message.getTimestamp(), ChatClientApplication.DATE_TIME_FORMATTER)
                        .atZone(ZoneOffset.UTC)
                        .withZoneSameInstant(ZoneId.systemDefault())
                ).format(ChatClientApplication.DATE_TIME_FORMATTER);

            switch (message.getType()) {
                case DISCONNECT:
                    System.out.println(message.getRecipient());
                    System.out.println(headers.getMessageId().substring(0, headers.getMessageId().lastIndexOf("-")));
                    if (message.getSender().equals(ChatClientApplication.SERVER_NAME) 
                        && message.getRecipient().equals(headers.getMessageId().substring(0, headers.getMessageId().lastIndexOf("-")))) {
                        /* new client needed */
                        System.out.println("new client needed");
                        // try {
                            // chatClient.getNewClient();
                        // } catch (RuntimeException | InterruptedException | ExecutionException e) {
                            // e.printStackTrace();
                        // }
                    }
                    // System.out.println(time + "  " + sender + " has " + message.getType().toString().toLowerCase() + "ed");
                    break;
                case MESSAGE:
                    // System.out.println(time + "  " + sender + ": " + message.getContent());
                    break;

                case CONNECT:
            }
        }
    }
}
