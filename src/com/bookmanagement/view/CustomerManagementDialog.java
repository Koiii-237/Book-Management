/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.CustomerDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Customer;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class CustomerManagementDialog extends javax.swing.JDialog {

    /**
     * Creates new form BookManagementDialog
     */
    private Customer currentCustomer; // The Customer object being edited (null if adding a new customer)
    private CustomerDAO customerDAO;
    private boolean dataSaved = false; // Flag to check if the operation was successful

    /**
     * Creates new form CustomerDialog
     *
     * @param parent Frame parent of the dialog.
     * @param customer Customer object to edit (null if adding new).
     * @param isEditMode true if in edit mode, false if adding new.
     */
    public CustomerManagementDialog(java.awt.Frame parent, boolean modal, Customer customer) {
        super(parent, modal);
        this.customerDAO = new CustomerDAO();
        this.currentCustomer = customer;
        initComponents();
        setLocationRelativeTo(parent);

        // Set the title and fill data if in update mode
        if (currentCustomer == null) {
            this.setTitle("Add New Customer");
            txtId.setText("ID is auto-generated");
            txtId.setEnabled(false);
        } else {
            this.setTitle("Update Customer");
            fillDataFromCustomer();
            txtId.setEnabled(false);
        }
    }

    /**
     * Returns true if the operation in the dialog (add/edit) was successful.
     *
     * @return true if successful, false if cancelled or failed.
     */
    public boolean isDataSaved() {
        return dataSaved;
    }

    /**
     * Sets the initial state of the dialog based on the mode (add new/edit).
     */
    private void fillDataFromCustomer() {
        if (currentCustomer != null) {
            txtFullName.setText(currentCustomer.getFullName());
            txtEmail.setText(currentCustomer.getEmail());
            txtPhoneNumber.setText(currentCustomer.getPhone());
            txtAddress.setText(currentCustomer.getAddress());
        }
    }

    /**
     * Collects data from UI fields to create or update a Customer object.
     *
     * @return Customer object with data from UI, or null if invalid.
     */
    private Customer getCustomerDataFromForm() {
        // Tự động xác thực dữ liệu đầu vào
        if (!validateInput()) {
            return null;
        }

        String fullName = txtFullName.getText();
        String email = txtEmail.getText();
        String phone = txtPhoneNumber.getText();
        String address = txtAddress.getText();
        
        Customer customer = (currentCustomer == null) ? new Customer() : currentCustomer;
        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
        
        return customer;
    }

    /**
     * Handles the logic for saving (adding new or updating) the customer
     * record.
     */
    
    private boolean validateInput() {
        if (txtFullName.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty() || 
            txtPhoneNumber.getText().trim().isEmpty() || txtAddress.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all schools.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Xác thực định dạng email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        if (!emailPattern.matcher(txtEmail.getText().trim()).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Xác thực định dạng số điện thoại (chỉ chứa số)
        if (!txtPhoneNumber.getText().trim().matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "The phone number is only contained.", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void saveCustomer() {
        Customer customer = getCustomerDataFromForm();
        if (customer == null) {
            return;
        }
        
        boolean success;
        if (currentCustomer == null) {
            // Add new customer
            success = customerDAO.addCustomer(customer);
            if (success) {
                JOptionPane.showMessageDialog(this, "Customer added successfully.", "Notification", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // Update customer
            success = customerDAO.updateCustomer(customer);
            if (success) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully.", "Notification", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        if (success) {
            dataSaved = true;
            this.dispose(); // Close dialog after successful save
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save customer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        formPanel = new javax.swing.JPanel();
        lblFullName = new javax.swing.JLabel();
        lblCustomerId = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblPhoneNumber = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtFullName = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtPhoneNumber = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        pnButton = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CUSTOMER ");
        setModal(true);

        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        formPanel.setLayout(new java.awt.GridBagLayout());

        lblFullName.setText("FULL NAME: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblFullName, gridBagConstraints);

        lblCustomerId.setText("ID CUSTOMER : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblCustomerId, gridBagConstraints);

        lblEmail.setText("EMAIL: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblEmail, gridBagConstraints);

        lblPhoneNumber.setText("PHONE NUMBER: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblPhoneNumber, gridBagConstraints);

        txtId.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtId, gridBagConstraints);

        txtFullName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFullNameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtFullName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtEmail, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtPhoneNumber, gridBagConstraints);

        jLabel1.setText("ADDRESS: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtAddress, gridBagConstraints);

        getContentPane().add(formPanel, java.awt.BorderLayout.CENTER);

        pnButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnSave.setText("SAVE");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        pnButton.add(btnSave);

        btnCancel.setText("CANCEL");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnButton.add(btnCancel);

        getContentPane().add(pnButton, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFullNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFullNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFullNameActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblBookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBookMouseClicked

    }//GEN-LAST:event_tblBookMouseClicked

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveCustomer();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CustomerManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomerManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomerManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CustomerManagementDialog dialog = new CustomerManagementDialog(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel formPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblCustomerId;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblPhoneNumber;
    private javax.swing.JPanel pnButton;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtPhoneNumber;
    // End of variables declaration//GEN-END:variables
}
