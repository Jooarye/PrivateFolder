package de.jooarye.security.gui;

import de.jooarye.security.util.CryptUtils;
import de.jooarye.security.util.FolderUtils;
import org.apache.commons.io.FileUtils;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

public class FolderEncryptionDialog extends JFrame {

    public FolderEncryptionDialog() {
        initComponents();
    }

    public void initComponents() {
        setSize(375, 120);
        setTitle("Folder | PrivateFolder");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel fileLabel = new JLabel("Folder");
        fileLabel.setBounds(5, 7, 200, 15);
        getContentPane().add(fileLabel);

        JTextField pathField = new JTextField();
        pathField.setBounds(40, 5, 220, 20);
        pathField.setText(System.getProperty("user.home"));
        pathField.setDropTarget(new DropTarget() {
            public synchronized  void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    pathField.setText(droppedFiles.get(0).getPath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        getContentPane().add(pathField);

        JButton chooseButton = new JButton("Choose");
        chooseButton.setBounds(265, 4, 100, 22);
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(new JFrame());
                if(option == JFileChooser.APPROVE_OPTION) {
                    pathField.setText(fileChooser.getSelectedFile().getPath());
                }
            }
        });
        getContentPane().add(chooseButton);

        JLabel keyLabel = new JLabel("Key");
        keyLabel.setBounds(5, 31, 200, 15);
        getContentPane().add(keyLabel);

        JTextField keyField = new JTextField();
        keyField.setBounds(40, 30, 220, 20);
        keyField.setDropTarget(new DropTarget() {
            public synchronized  void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    String key = FileUtils.readFileToString(droppedFiles.get(0));
                    keyField.setText(key);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        getContentPane().add(keyField);

        JButton randKeyButton = new JButton("Random");
        randKeyButton.setBounds(265, 29, 100, 22);
        randKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678990";
                String output = "";
                Random random = new Random();
                for (int i = 0; i < 256; i++) {
                    output += alphabet.charAt(random.nextInt(alphabet.length()));
                }
                keyField.setText(output);
            }
        });
        getContentPane().add(randKeyButton);

        JButton encryptButton = new JButton("Encrypt");
        encryptButton.setBounds(4, 55, 179, 30);
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File folder = new File(pathField.getText());
                if (folder.isDirectory()) {
                    if (CryptUtils.isKeyValid(keyField.getText())) {
                        try {
                            Thread pwd = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    JOptionPane.showMessageDialog(new JFrame(), "Please wait while I'm encrypting your files! This might take a while!", "Please wait!", JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                            pwd.run();
                            SecretKey key = CryptUtils.generateKey(keyField.getText());
                            for (File file : FolderUtils.listFiles(folder)) {
                                CryptUtils.encryptFile(file, key);
                            }
                            for (File file : FolderUtils.listFolders(folder)) {
                                File newFolder = new File(file.getPath().replace(file.getName(), CryptUtils.encryptStr(CryptUtils.generateKey(keyField.getText()), file.getName())));
                                file.renameTo(newFolder);
                            }
                            pwd.stop();
                            JOptionPane.showMessageDialog(new JFrame(), "Finished encrypting your folder!");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(new JFrame(), "Ooops! An error occurred! I don't know what it is but feel free to contact me!", "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Invalid key length only 16, 32, 64, 128 and 256 are allowed!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "Path doesn't resemble a folder!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        getContentPane().add(encryptButton);

        JButton decryptButton = new JButton("Decrypt");
        decryptButton.setBounds(186, 55, 179, 30);
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File folder = new File(pathField.getText());
                if (folder.isDirectory()) {
                    if (CryptUtils.isKeyValid(keyField.getText())) {
                        try {
                            Thread pwd = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    JOptionPane.showMessageDialog(new JFrame(), "Please wait while I'm decrypting your files! This might take a while!", "Please wait!", JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                            pwd.run();
                            SecretKey key = CryptUtils.generateKey(keyField.getText());
                            for (File file : FolderUtils.listFiles(folder)) {
                                CryptUtils.decryptFile(file, key);
                            }
                            for (File file : FolderUtils.listFolders(folder)) {
                                File newFolder = new File(file.getPath().replace(file.getName(), CryptUtils.decryptStr(CryptUtils.generateKey(keyField.getText()), file.getName())));
                                file.renameTo(newFolder);
                            }
                            pwd.stop();
                            JOptionPane.showMessageDialog(new JFrame(), "Finished decrypting your folder!");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(new JFrame(), "Ooops! An error occurred! I don't know what it is but feel free to contact me!", "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Invalid key length only 16, 32, 128 and 256 are allowed!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "Path doesn't resemble a folder!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        getContentPane().add(decryptButton);

        setVisible(true);
    }

}
