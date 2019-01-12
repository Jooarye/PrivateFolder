package de.jooarye.security;

import de.jooarye.security.gui.SelectionDialog;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        SelectionDialog selectionDialog = new SelectionDialog();
    }
}
