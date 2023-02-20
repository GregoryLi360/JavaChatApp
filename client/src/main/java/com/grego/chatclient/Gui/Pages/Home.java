package com.grego.chatclient.Gui.Pages;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import com.grego.chatclient.Gui.Gui;

public class Home extends JPanel {
    private JTextArea chatLog;
    private JScrollPane scrollableChatLog;

    private JTextArea entry;
    private JScrollPane scrollableEntry;
    private KeyListener entrySend;
    private KeyListener focusEntry;

    private ActionListener sendAction;
    private JButton send;

    private List<Component> onscreenComponents;

    public Home() {
        initActionListeners();
        initComponents();

        onscreenComponents = new ArrayList<Component>() {{
            add(scrollableChatLog);
            add(scrollableEntry);
            add(send);
        }};
        onscreenComponents.forEach(c -> add(c));
        
        setLayout(null);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setFocusable(true);
        setVisible(true);
    }

    private void initActionListeners() {
        focusEntry = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    entry.requestFocus();
                }
            }
            
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        };

        addKeyListener(focusEntry);

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent e) {
                entry.requestFocus();
            }
            
            public void ancestorMoved(AncestorEvent arg0) {}
            public void ancestorRemoved(AncestorEvent arg0) {}
        });

        sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = "grego";
                
            }
        };

        entrySend = new KeyListener() {
            private boolean shiftUp = true;

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftUp = false;
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (shiftUp) {
                        e.consume();
                        sendAction.actionPerformed(null);
                    } else {
                        entry.setText(entry.getText() + System.lineSeparator());
                    }
				}
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftUp = true;
                }
            }

            public void keyTyped(KeyEvent e) {}
        };
    }

    private void initComponents() {
        chatLog = new JTextArea();
		chatLog.setEditable(false);
        chatLog.addKeyListener(focusEntry);
        chatLog.setBorder(BorderFactory.createCompoundBorder(chatLog.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
		scrollableChatLog = new JScrollPane(chatLog);
        scrollableChatLog.setBounds(new Rectangle(
            0,
            0,
            (int) (Gui.STARTINGWINDOW.getWidth()), 
            (int) (Gui.STARTINGWINDOW.getHeight() * 8/10)
        ));


        entry = new JTextArea();
        entry.addKeyListener(entrySend);
        entry.setBorder(BorderFactory.createCompoundBorder(entry.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        scrollableEntry = new JScrollPane(entry);
        scrollableEntry.setBounds(new Rectangle(
            0,
            (int) (Gui.STARTINGWINDOW.getHeight() * 8/10),
            (int) (Gui.STARTINGWINDOW.getWidth() * 6/7), 
            (int) (Gui.STARTINGWINDOW.getHeight() * 2/10)
        ));

		send = new JButton("Send");
		send.addActionListener(sendAction);
        send.setBounds(new Rectangle(
            (int) (Gui.STARTINGWINDOW.getWidth() * 6/7), 
            (int) (Gui.STARTINGWINDOW.getHeight() * 8/10), 
            (int) (Gui.STARTINGWINDOW.getWidth() * 1/7), 
            (int) (Gui.STARTINGWINDOW.getHeight() * 2/10)
        ));
    }    
}
