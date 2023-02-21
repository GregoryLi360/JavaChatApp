package com.grego.chatclient.Gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.grego.chatclient.Gui.Pages.Home;
import com.grego.chatclient.Gui.Pages.Page;

public class Gui extends JFrame {
    private static final Dimension FULLSCREEN = Toolkit.getDefaultToolkit().getScreenSize();
    private static final Dimension SMALLESTWINDOW = new Dimension(FULLSCREEN.width < 200 ? FULLSCREEN.width : 200, FULLSCREEN.width < 200 ? FULLSCREEN.width : 200 * FULLSCREEN.height / FULLSCREEN.width);
    public static final Dimension STARTINGWINDOW = new Dimension(FULLSCREEN.width / 2, FULLSCREEN.height / 2 + 50);
    private Dimension originalWindow = STARTINGWINDOW;
    
    private Home home;
    private Page page;
    
    public Gui() {
        page = home = new Home();

        initActionListeners();

        page.setBounds(5, 5, STARTINGWINDOW.width - 10, STARTINGWINDOW.height - 40);
        add(page);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(STARTINGWINDOW.width + 10, STARTINGWINDOW.height);
        setMinimumSize(new Dimension(SMALLESTWINDOW.width + 10, SMALLESTWINDOW.height));
		setTitle("GregoChatClient");
        setLayout(null);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void switchPage(Page newPage) {
        page = newPage;
        getContentPane().removeAll();
        add(page);
        repaint();
    }

    private void initActionListeners() {
        addComponentListener(new ComponentAdapter() {
            @Override
			public void componentResized(ComponentEvent evt) {
                Dimension newWindow = new Dimension(getSize().width - 10, getSize().height - 40);
                page.setBounds(new Rectangle(5, 5, newWindow.width, newWindow.height));

                for (Component component: page.getComponents()) 
                    component.setBounds(calculateBounds(component.getBounds(), page.previousWindowDimension, newWindow));
                
                page.previousWindowDimension = newWindow;
                originalWindow = newWindow;
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                page.requestFocus();
            }

            public void mouseReleased(MouseEvent arg0) {}
            public void mouseClicked(MouseEvent arg0) {}
            public void mouseEntered(MouseEvent arg0) {}
            public void mouseExited(MouseEvent arg0) {}
        });
    }

    private static final Rectangle calculateBounds(Rectangle component, Dimension originalWindow, Dimension newWindow) {
        double ratioX = newWindow.getWidth() / originalWindow.getWidth();
        double ratioY = newWindow.getHeight() / originalWindow.getHeight();
        int x = (int) Math.round(component.getX() * ratioX);
        int y = (int) Math.round(component.getY() * ratioY);
        int width = (int) Math.round(component.getWidth() * ratioX);
        int height = (int) Math.round(component.getHeight() * ratioY);
        return new Rectangle(x, y, width, height);
    }
}
