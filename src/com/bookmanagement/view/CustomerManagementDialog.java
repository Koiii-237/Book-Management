/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.BookManagementDAO;
import com.bookmanagement.Dao.CustomerDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Customer;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerManagementDialog extends javax.swing.JDialog {

    /**
     * Creates new form BookManagementDialog
     */
    private static final Logger LOGGER = Logger.getLogger(CustomerManagementDialog.class.getName());
    private CustomerDAO customerDAO;
    private Customer currentCustomer; // Current Customer object (if in edit mode)
    private boolean isEditMode; // true if in edit mode, false if adding new
    private boolean isSucceeded = false; // Flag to check if the operation was successful

    /**
     * Creates new form CustomerDialog
     *
     * @param parent Frame parent of the dialog.
     * @param customer Customer object to edit (null if adding new).
     * @param isEditMode true if in edit mode, false if adding new.
     */
    public CustomerManagementDialog(java.awt.Frame parent, Customer customer, boolean isEditMode) {
        super(parent, true); // Always modal
        initComponents();
        this.customerDAO = new CustomerDAO();
        this.currentCustomer = customer;
        this.isEditMode = isEditMode;
        initDialogState(); // Set initial state of the dialog

        this.setLocationRelativeTo(parent); // Center the dialog
    }

    /**
     * Returns true if the operation in the dialog (add/edit) was successful.
     * @return true if successful, false if cancelled or failed.
     */
    public boolean isSucceeded() {
        return isSucceeded;
    }

    /**
     * Sets the initial state of the dialog based on the mode (add new/edit).
     */
    private void initDialogState() {
        if (isEditMode && currentCustomer != null) {
            setSize(800, 250);
            setTitle("SỬA THÔNG TIN KHÁCH HÀNG");
            txtId.setText(currentCustomer.getId());
            txtId.setEditable(false); // Do not allow editing ID
            txtName.setText(currentCustomer.getName());
            txtAddress.setText(currentCustomer.getAddress());
            txtPhoneNumber.setText(currentCustomer.getPhoneNumber());
        } else {
            setSize(800, 250);
            setTitle("THÊM MỚI THÔNG TIN KHÁCH HÀNG");
            txtId.setText("(AUTO GEN / Không sửa)");
            txtId.setEditable(false); // Always not editable for new ID
            txtName.setText("");
            txtAddress.setText("");
            txtPhoneNumber.setText("");
        }
    }

    /**
     * Collects data from UI fields to create or update a Customer object.
     * @return Customer object with data from UI, or null if invalid.
     */
    private Customer collectFormData() {
        String customerId = isEditMode ? currentCustomer.getId() : null; // ID will be auto-generated if adding new
        
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String address = txtAddress.getText().trim();
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String phoneNumber = txtPhoneNumber.getText().trim();
        if (phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        // Basic phone number validation (can be enhanced)
        if (!phoneNumber.matches("^0[0-9]{9}$")) { // Starts with 0, followed by 9 digits
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ. Vui lòng nhập 10 chữ số và bắt đầu bằng 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new Customer(customerId, name, address, phoneNumber);
    }

    /**
     * Handles the logic for saving (adding new or updating) the customer record.
     */
    private void handleSaveLogic() {
        Customer customer = collectFormData();
        if (customer == null) {
            return; // Invalid data
        }

        try {
            boolean success;
            if (isEditMode) {
                success = customerDAO.updateCustomer(customer);
            } else {
                success = customerDAO.addCustomer(customer);
            }

            if (success) {
                isSucceeded = true; // Set success flag
                JOptionPane.showMessageDialog(this, "Lưu thông tin khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close dialog
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lưu thông tin khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) { // Catch generic Exception to log all possible issues
            LOGGER.log(Level.SEVERE, "Lỗi khi lưu khách hàng: ", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the logic for cancelling the operation and closing the dialog.
     */
    private void handleCancelLogic() {
        isSucceeded = false; // Set failure flag
        dispose(); // Close dialog
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        formPanel = new javax.swing.JPanel();
        lblBookName = new javax.swing.JLabel();
        lblBookID = new javax.swing.JLabel();
        lblAuthor = new javax.swing.JLabel();
        lblKind = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        txtPhoneNumber = new javax.swing.JTextField();
        pnButton = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CUSTOMER ");
        setModal(true);

        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        formPanel.setLayout(new java.awt.GridBagLayout());

        lblBookName.setText("NAME: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblBookName, gridBagConstraints);

        lblBookID.setText("ID CUSTOMER : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblBookID, gridBagConstraints);

        lblAuthor.setText("ADDRESS: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblAuthor, gridBagConstraints);

        lblKind.setText("PHONE NUMBER: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblKind, gridBagConstraints);

        txtId.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtId, gridBagConstraints);

        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtAddress, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtPhoneNumber, gridBagConstraints);

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

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

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
        handleSaveLogic();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        handleCancelLogic();
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
                CustomerManagementDialog dialog = new CustomerManagementDialog(new javax.swing.JFrame(), null, false);
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
    private javax.swing.JLabel lblAuthor;
    private javax.swing.JLabel lblBookID;
    private javax.swing.JLabel lblBookName;
    private javax.swing.JLabel lblKind;
    private javax.swing.JPanel pnButton;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhoneNumber;
    // End of variables declaration//GEN-END:variables
}
