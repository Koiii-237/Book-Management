/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */

import com.bookmanagement.Dao.UserDAO;
import com.bookmanagement.model.User;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;

public class UserPermissionDialog extends javax.swing.JDialog {

    /**
     * Creates new form UserPermission
     */
    
    private static final Logger LOGGER = Logger.getLogger(UserPermissionDialog.class.getName());
    private UserDAO userDAO;
    private User user;
    private Map<String, JCheckBox> permissionCheckBoxes;
    private boolean dataSaved = false;
    
    public UserPermissionDialog(java.awt.Frame parent, boolean modal, User user) {
        super(parent, modal);
        this.user = user;
        this.userDAO = new UserDAO();
        this.permissionCheckBoxes = new HashMap<>();
        initComponents();
        setLocationRelativeTo(parent);
        setTitle("Phân quyền cho người dùng: " + user.getUsername());
        
        initPermissionsPanel();
        loadPermissions();
    }
    
    
    private void initPermissionsPanel() {
        // Create a new JPanel for the checkboxes with a BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Add a scroll pane to the panel
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách quyền"));
        
        // Add the scroll pane to the main form panel
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        // Create a button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        
        btnSave.addActionListener(this::btnSaveActionPerformed);
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadPermissions() {
        try {
            // Get all available permissions from the database
            List<String> allPermissions = userDAO.getAllSystemPermissions();
            // Get the user's current permissions
            List<String> userPermissions = userDAO.getPermissionsForUser(user.getUserId());
            
            JPanel permissionsPanel = (JPanel) ((JScrollPane) this.getContentPane().getComponent(0)).getViewport().getView();
            
            for (String permissionName : allPermissions) {
                JCheckBox checkBox = new JCheckBox(permissionName);
                if (userPermissions.contains(permissionName)) {
                    checkBox.setSelected(true);
                }
                permissionsPanel.add(checkBox);
                permissionCheckBoxes.put(permissionName, checkBox);
            }
            revalidate();
            repaint();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải danh sách quyền.", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách quyền: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles the save button action.
     * @param evt ActionEvent
     */
    private void btnSaveActionPerformed(ActionEvent evt) {
        // Collect all selected permissions from the checkboxes
        List<String> selectedPermissions = new ArrayList<>();
        for (Map.Entry<String, JCheckBox> entry : permissionCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedPermissions.add(entry.getKey());
            }
        }
        
        // Update the user object with the new permissions
        user.setPermissions(selectedPermissions);
        
        try {
            // Call the DAO to update permissions in the database
            userDAO.updatePermissions(user);
            JOptionPane.showMessageDialog(this, "Cập nhật quyền thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dataSaved = true;
            dispose();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật quyền.", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật quyền: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Checks if data was saved successfully.
     * @return boolean true if data was saved, false otherwise.
     */
    public boolean isDataSaved() {
        return dataSaved;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnPermissions = new javax.swing.JPanel();
        pnButtons = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnPermissions.setBorder(javax.swing.BorderFactory.createTitledBorder("Permissions"));
        pnPermissions.setLayout(new java.awt.GridLayout(0, 1));
        getContentPane().add(pnPermissions, java.awt.BorderLayout.CENTER);

        btnCancel.setText("CANCEL");
        pnButtons.add(btnCancel);

        btnSave.setText("SAVE");
        pnButtons.add(btnSave);

        getContentPane().add(pnButtons, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel pnButtons;
    private javax.swing.JPanel pnPermissions;
    // End of variables declaration//GEN-END:variables
}
