package com.dicarlomtz.views;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dicarlomtz.controllers.WatcherController;

public class FolderChooser extends JPanel implements ActionListener {

    private JButton go;
    private JFileChooser chooser;
    private String chooserTitle;

    public FolderChooser() {
        this.go = new JButton("Choose your folder");
        go.addActionListener(this);
        add(go);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(chooserTitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = chooser.getSelectedFile().getAbsolutePath() + "\\";
                WatcherController.getController().createExcelFolderWatcher(path);
            } catch (IOException | InterruptedException e1) {
                Logger.getLogger(FolderChooser.class.getName()).log(Level.SEVERE, null, e1);
                e1.printStackTrace();
            }
        }
    }
}