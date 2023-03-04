package com.grego.chatclient.Gui.Pages;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;

public class Login extends Page {
    private JTextField username;
    private JButton submit;
    private ActionListener submitAction;
    private List<Component> onScreenComponents;

    public Login(ActionListener switchPage) {
        initActionListeners(switchPage);
        initComponents();

        onScreenComponents = new ArrayList<Component>() {{
            add(username);
            add(submit);
        }};
        onScreenComponents.forEach(c -> add(c));

        setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setLayout(null);
        setVisible(true);
    }

    private void initActionListeners(ActionListener switchPage) {
        submitAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = username.getText();
                if (name.length() == 0) return;

                switchPage.actionPerformed(e);

                /* check name availability */
                /* enter home with username */
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
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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
    }
}
