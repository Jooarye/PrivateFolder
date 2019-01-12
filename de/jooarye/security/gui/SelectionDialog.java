package de.jooarye.security.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectionDialog extends JFrame {

    public SelectionDialog() {
        initComponents();
    }

    public void initComponents() {
        setLayout(null);
        setSize(340, 115);
        setTitle("Select | PrivateFolder");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel prompt = new JLabel("Please select if you want to en- / decrypt a folder or a file!");
        prompt.setBounds(5, 5, 400, 12);
        getContentPane().add(prompt);

        JButton folder = new JButton("Folder");
        folder.setBounds(5, 20, 160, 60);
        folder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FolderEncryptionDialog fed = new FolderEncryptionDialog();
                dispose();
            }
        });
        getContentPane().add(folder);

        JButton file = new JButton("File");
        file.setBounds(170, 20, 160, 60);
        file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileEncryptionDialog fed = new FileEncryptionDialog();
                dispose();
            }
        });
        getContentPane().add(file);

        setVisible(true);
    }
}
