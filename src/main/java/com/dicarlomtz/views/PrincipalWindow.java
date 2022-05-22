package com.dicarlomtz.views;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.BorderLayout;

public class PrincipalWindow {

    private JFrame frame;
    private FolderChooser choser;

    public PrincipalWindow() {
        this.frame = new JFrame("Excel Watcher");
        this.choser = new FolderChooser();
    }

    public void init() {
        frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
        frame.add(choser, BorderLayout.CENTER);
        frame.setSize(new Dimension(300, 300));
        frame.setVisible(true);
    }

}
