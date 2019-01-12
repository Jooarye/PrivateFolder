package de.jooarye.security.gui;

import de.jooarye.security.util.CryptUtils;
import org.apache.commons.io.FileUtils;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Random;

public class FileEncryptionDialog extends JFrame {

    public FileEncryptionDialog() {
        initComponents();
    }

    public void initComponents() {
        setSize(365, 120);
        setTitle("File | PrivateFolder");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel fileLabel = new JLabel("File");
        fileLabel.setBounds(5, 7, 200, 15);
        getContentPane().add(fileLabel);

        JTextField pathField = new JTextField();
        pathField.setBounds(30, 5, 220, 20);
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
        chooseButton.setBounds(255, 4, 100, 22);
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
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
        keyField.setBounds(30, 30, 220, 20);
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
        randKeyButton.setBounds(255, 29, 100, 22);
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
        encryptButton.setBounds(4, 55, 174, 30);
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File(pathField.getText());
                if (file.isFile()) {
                    if (CryptUtils.isKeyValid(keyField.getText())) {
                        try {
                            SecretKey key = CryptUtils.generateKey(keyField.getText());
                            if (file.exists()) {
                                Thread pwd = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JOptionPane.showMessageDialog(new JFrame(), "Please wait while I'm encrypting your file! This might take a while!", "Please wait!", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                });
                                pwd.run();
                                CryptUtils.encryptFile(file, key);
                                pwd.stop();
                                JOptionPane.showMessageDialog(new JFrame(), "Finished encrypting your file!");
                            } else {
                                JOptionPane.showMessageDialog(new JFrame(), "The selected file doesn't exist!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(new JFrame(), "Ooops! An error occurred! I don't know what it is but feel free to contact me!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Invalid key length only 16, 32, 64, 128 and 256 are allowed!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "Path doesn't resemble a file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        getContentPane().add(encryptButton);

        JButton decryptButton = new JButton("Decrypt");
        decryptButton.setBounds(181, 55, 174, 30);
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File(pathField.getText());
                if (file.isFile()) {
                    if (CryptUtils.isKeyValid(keyField.getText())) {
                        try {
                            SecretKey key = CryptUtils.generateKey(keyField.getText());
                            if (file.exists()) {
                                Thread pwd = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JOptionPane.showMessageDialog(new JFrame(), "Please wait while I'm decrypting your file! This might take a while!", "Please wait!", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                });
                                pwd.run();
                                CryptUtils.decryptFile(file, key);
                                pwd.stop();
                                JOptionPane.showMessageDialog(new JFrame(), "Finished decrypting your file!");
                            } else {
                                JOptionPane.showMessageDialog(new JFrame(), "The selected file doesn't exist!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(new JFrame(), "Ooops! An error occurred! I don't know what it is but feel free to contact me!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Invalid key length only 16, 32, 128 and 256 are allowed!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "Path doesn't resemble a file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        getContentPane().add(decryptButton);

        setVisible(true);
    }

}
