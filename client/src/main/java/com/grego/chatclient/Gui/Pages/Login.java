package com.grego.chatclient.Gui.Pages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

public class Login extends Page {
    private JTextArea username;
    private JButton submit;

    public Login() {
        username = new JTextArea();
        submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = username.getText();
                if (name.length() == 0) return;

                /* check name availability */
                /* enter home with username */
            }
        });
    }
}
