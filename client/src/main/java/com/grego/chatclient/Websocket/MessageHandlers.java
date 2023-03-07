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
import com.grego.chatclient.Gui.Gui;
import com.grego.chatclient.Gui.Pages.Pages;
import com.grego.chatclient.Websocket.Model.Message;
import com.grego.chatclient.Websocket.Model.MessageType;

public class MessageHandlers {
    public static class PublicStompFrameHandler implements StompFrameHandler {
        private Gui gui;
        public PublicStompFrameHandler(Gui gui) {
            this.gui = gui;
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
                case CONNECT:
                    if (message.getSender().equals(gui.getUsername()))    
                        gui.switchPage(Pages.HOME);
                case DISCONNECT:
                    gui.addTextToChatLog(time + "  " + sender + " has " + message.getType().toString().toLowerCase() + "ed" + "\n");
                    break;
                case MESSAGE:
                    gui.addTextToChatLog(time + "  " + sender + ": " + message.getContent() + "\n");
                    break;
            }
        }
    }

    public static class PrivateStompFrameHandler implements StompFrameHandler {
        private Gui gui;
        public PrivateStompFrameHandler(Gui gui) {
            this.gui = gui;
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
                        gui.switchPage(Pages.LOGIN);
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
