/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.SwingUtilities;

/**
 *
 * @author ADMIN
 */
public class MainApp {
    public static void main(String args[]){
        try {
            FlatIntelliJLaf.setup();
        } catch (Exception e) { 
            System.err.println("Failed to initialize FlatLaf Look and Feel: " + e.getMessage());
        }

        // Run the application on the Event Dispatch Thread to ensure thread safety.
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
