package com.grego.chatclient.Gui.Pages;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.grego.chatclient.ChatClientApplication;

public class Login extends Page {
    private JTextField username;
    private JButton submit;
    private ActionListener submitAction;
    private JTextArea warning;
    private List<Component> onScreenComponents;
    private ChatClientApplication controller;
    private String submittedUsername = "";

    public Login(ActionListener switchPage, ChatClientApplication controller) {
        this.controller = controller;

        initActionListeners(switchPage);
        initComponents();

        onScreenComponents = new ArrayList<Component>() {{
            add(username);
            add(submit);
            add(warning);
        }};
        onScreenComponents.forEach(c -> add(c));

        setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setLayout(null);
        setVisible(true);
    }

    public String getUsername() {
        return submittedUsername;
    }

    public void setWarning(String warningText) {
        warning.setText(warningText);
    }

    public void setSubmitEnabled(boolean enabled) {
        submit.setEnabled(enabled);
    }

    private void initActionListeners(ActionListener switchPage) {
        submitAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submit.setEnabled(false);

                String name = submittedUsername = username.getText();
                try {
                    controller.getNewClient(name);
                } catch (RuntimeException | InterruptedException | ExecutionException e1) {
                    e1.printStackTrace();
                }
            }
        };
    }

    private void initComponents() {
        username = new JTextField();
        username.setBounds(new Rectangle(
            (int) (previousWindowDimension.getWidth() / 2 - previousWindowDimension.getWidth() * 1/8),
            (int) (previousWindowDimension.getHeight() / 2 - previousWindowDimension.getHeight() * 1/16),
            (int) (previousWindowDimension.getWidth() * 2/8),
            (int) (previousWindowDimension.getHeight() * 2/16)
        ));
        username.setBorder(BorderFactory.createCompoundBorder(username.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        username.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && submit.isEnabled()) {
                    submitAction.actionPerformed(null);
                }
            }
            
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        });

        submit = new JButton("Submit");
        submit.addActionListener(submitAction);
        submit.setBounds(new Rectangle(
            (int) (previousWindowDimension.getWidth() / 2 + previousWindowDimension.getWidth() * 1/8),
            (int) (previousWindowDimension.getHeight() / 2 - previousWindowDimension.getHeight() * 1/20),
            (int) (previousWindowDimension.getWidth() * 2/16),
            (int) (previousWindowDimension.getHeight() * 2/20)
        ));

        warning = new JTextArea("");
        warning.setBackground(getBackground());
        warning.setEditable(false);
        warning.setLineWrap(true);
        warning.setForeground(Color.red);
        warning.setBounds(new Rectangle(
            (int) (previousWindowDimension.getWidth() / 2 - previousWindowDimension.getWidth() * 1/5),
            (int) (previousWindowDimension.getHeight() / 2 + previousWindowDimension.getHeight() * 2/20),
            (int) (previousWindowDimension.getWidth() * 2/5),
            (int) (previousWindowDimension.getHeight() * 2/20)
        ));
    }
}
