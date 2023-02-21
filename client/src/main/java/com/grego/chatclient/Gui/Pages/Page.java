package com.grego.chatclient.Gui.Pages;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.grego.chatclient.Gui.Gui;

public abstract class Page extends JPanel {
    public Dimension previousWindowDimension = Gui.STARTINGWINDOW;
}
