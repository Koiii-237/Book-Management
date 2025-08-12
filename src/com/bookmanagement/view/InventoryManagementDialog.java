/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.InventoryDAO;
import com.bookmanagement.Dao.WarehouseDAO;
import com.bookmanagement.dao.BookDAO;
import com.bookmanagement.model.Inventory;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.bookmanagement.Dao.StockTransactionDAO;
import com.bookmanagement.model.StockTransaction;
import java.time.LocalDateTime;

public class InventoryManagementDialog extends javax.swing.JDialog {

    /**
     * Creates new form BookManagementDialog
     */
    private static final Logger LOGGER = Logger.getLogger(InventoryManagementDialog.class.getName());
    private Inventory currentInventory;
    private InventoryDAO inventoryDAO;
    private BookDAO bookDAO;
    private WarehouseDAO warehousesDAO;
    private StockTransactionDAO stockTransactionDAO; // Khai báo StockTransactionDAO
    private boolean dataSaved = false;

    /**
     * Tạo dialog mới.
     *
     * @param parent Frame cha của dialog.
     * @param modal Chỉ định dialog có phải là modal hay không.
     * @param inventory Đối tượng Inventory để chỉnh sửa (null nếu thêm mới).
     */
    public InventoryManagementDialog(java.awt.Frame parent, boolean modal, Inventory inventory) {
        super(parent, modal);
        this.inventoryDAO = new InventoryDAO();
        this.currentInventory = inventory;
        this.bookDAO = new BookDAO();
        this.warehousesDAO = new WarehouseDAO();
        this.stockTransactionDAO = new StockTransactionDAO();
        initComponents();
        setLocationRelativeTo(parent);

        if (currentInventory == null) {
            this.setTitle("Add New Inventory");
            lblBookNameDisplay.setText("");
            lblWarehouseNameDisplay.setText("");
            txtInventoryId.setText("AUTO GENERATE");
            txtInventoryId.setEnabled(false);
        } else {
            this.setTitle("Edit Inventory");
            txtInventoryId.setEnabled(false);
            loadInventoryData();

        }
        addTextFieldListeners();
    }

    private void addTextFieldListeners() {
        txtBookId.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateBookName();
            }

            public void removeUpdate(DocumentEvent e) {
                updateBookName();
            }

            public void insertUpdate(DocumentEvent e) {
                updateBookName();
            }
        });

        txtWarehouseId.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateWarehouseName();
            }

            public void removeUpdate(DocumentEvent e) {
                updateWarehouseName();
            }

            public void insertUpdate(DocumentEvent e) {
                updateWarehouseName();
            }
        });
    }

    private void updateBookName() {
        String bookIdText = txtBookId.getText();
        if (bookIdText.matches("\\d+")) {
            try {
                int bookId = Integer.parseInt(bookIdText);
                String bookName = bookDAO.getBookById(bookId).getTitle();
                lblBookNameDisplay.setText(bookName != null ? "Book: " + bookName : "Not found book");
            } catch (NumberFormatException ex) {
                lblBookNameDisplay.setText("Book code is invalid");
            }
        } else {
            lblBookNameDisplay.setText("");
        }
    }

    /**
     * Cập nhật tên kho dựa trên Warehouse ID được nhập.
     */
    private void updateWarehouseName() {
        String warehouseIdText = txtWarehouseId.getText();
        if (warehouseIdText.matches("\\d+")) {
            try {
                int warehouseId = Integer.parseInt(warehouseIdText);
                String warehouseName = warehousesDAO.getWarehouseById(warehouseId).getWarehouseName();
                lblWarehouseNameDisplay.setText(warehouseName != null ? "Kho: " + warehouseName : "Không tìm thấy kho");
            } catch (NumberFormatException ex) {
                lblWarehouseNameDisplay.setText("Warehouse is invalid");
            } catch (SQLException ex) {
                Logger.getLogger(InventoryManagementDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            lblWarehouseNameDisplay.setText("");
        }
    }

    private Inventory getInventoryFromForm() {
        Inventory inventory = new Inventory();
        if (currentInventory != null) {
            inventory.setInventoryId(Integer.parseInt(txtInventoryId.getText()));
        }
        try {
            inventory.setBookId(Integer.parseInt(txtBookId.getText()));
            inventory.setWarehouseId(Integer.parseInt(txtWarehouseId.getText()));
            inventory.setQuantity(Integer.parseInt(txtQuantity.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Book code, warehouse code and quantity must be valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return inventory;
    }

    public boolean isDataSaved() {
        return dataSaved;
    }

    private void loadInventoryData() {
        if (currentInventory != null) {
            txtInventoryId.setText(String.valueOf(currentInventory.getInventoryId()));
            txtBookId.setText(String.valueOf(currentInventory.getBookId()));
            txtWarehouseId.setText(String.valueOf(currentInventory.getWarehouseId()));
            txtQuantity.setText(String.valueOf(currentInventory.getQuantity()));
            updateBookName();
            updateWarehouseName();
        }
    }

    private void handleSave() {
        try {
            Inventory inventory = getInventoryFromForm();
            if (inventory == null || !validateForm(inventory)) {
                return;
            }

            boolean success = false;
            if (currentInventory == null) {
                // Thêm tồn kho mới
                success = inventoryDAO.addInventory(inventory);
                if (success) {
                    stockTransactionDAO.addStockTransaction(new StockTransaction(
                            0, // transactionId sẽ được tự động tạo
                            inventory.getBookId(),
                            null,
                            inventory.getWarehouseId(),
                            inventory.getQuantity(),
                            "ADD",
                            LocalDateTime.now()
                    ));
                    dataSaved = true;
                    JOptionPane.showMessageDialog(this, "Add inventory complete!", "Complete", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Add new inventory fail!", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                    // Cập nhật tồn kho
                    int oldQuantity = inventoryDAO.getQuantityForBookInWarehouse(inventory.getBookId(), inventory.getWarehouseId());
                    currentInventory.setBookId(inventory.getBookId());
                    currentInventory.setWarehouseId(inventory.getWarehouseId());
                    currentInventory.setQuantity(inventory.getQuantity());
                    success = inventoryDAO.updateInventory(inventory);
                    if (success) {
                        int quantityChange = inventory.getQuantity() - oldQuantity;
                        if (quantityChange != 0) {
                            String transactionType = (quantityChange > 0) ? "UPDATE_INCREASE" : "UPDATE_DECREASE";
                            stockTransactionDAO.addStockTransaction(new StockTransaction(
                                    0,
                                    inventory.getBookId(),
                                    null,
                                    inventory.getWarehouseId(),
                                    Math.abs(quantityChange),
                                    transactionType,
                                    LocalDateTime.now()
                            ));
                        }
                        JOptionPane.showMessageDialog(this, "Update inventory complete!", "Complete", JOptionPane.INFORMATION_MESSAGE);
                        this.dispose();
                    }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter the valid number for book ID, warehouse and quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch(SQLException e){
            LOGGER.log(Level.SEVERE, "Error when working with the database", e);
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateForm(Inventory inventory) {
        if (inventory.getBookId() <= 0 || inventory.getWarehouseId() <= 0 || inventory.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "Book code, warehouse code and quantity must be greater than 0!", "Eroor", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void handleCancel() {
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        formPanel = new javax.swing.JPanel();
        lblBookId = new javax.swing.JLabel();
        lblInventoryId = new javax.swing.JLabel();
        lblWarehouseId = new javax.swing.JLabel();
        txtInventoryId = new javax.swing.JTextField();
        txtWarehouseId = new javax.swing.JTextField();
        lblQuantity = new javax.swing.JLabel();
        txtBookId = new javax.swing.JTextField();
        txtQuantity = new javax.swing.JTextField();
        lblBookNameDisplay = new javax.swing.JLabel();
        lblWarehouseNameDisplay = new javax.swing.JLabel();
        pnButton = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ADD/UPDATE INVENTORY\n");
        setModal(true);
        setResizable(false);

        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        formPanel.setLayout(new java.awt.GridBagLayout());

        lblBookId.setText("BOOK ID: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblBookId, gridBagConstraints);

        lblInventoryId.setText("INVENTORY ID: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblInventoryId, gridBagConstraints);

        lblWarehouseId.setText("WAREHOUSE ID: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblWarehouseId, gridBagConstraints);

        txtInventoryId.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtInventoryId, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtWarehouseId, gridBagConstraints);

        lblQuantity.setText("QUANTITY: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblQuantity, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtBookId, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtQuantity, gridBagConstraints);

        lblBookNameDisplay.setText("BOOK NAME: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblBookNameDisplay, gridBagConstraints);

        lblWarehouseNameDisplay.setText("WAREHOUSE NAME: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblWarehouseNameDisplay, gridBagConstraints);

        getContentPane().add(formPanel, java.awt.BorderLayout.CENTER);

        pnButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnButton.setMinimumSize(new java.awt.Dimension(500, 300));
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
        handleSave();

    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here
        handleCancel();
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
            java.util.logging.Logger.getLogger(InventoryManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InventoryManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InventoryManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InventoryManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InventoryManagementDialog dialog = new InventoryManagementDialog(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JLabel lblBookId;
    private javax.swing.JLabel lblBookNameDisplay;
    private javax.swing.JLabel lblInventoryId;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblWarehouseId;
    private javax.swing.JLabel lblWarehouseNameDisplay;
    private javax.swing.JPanel pnButton;
    private javax.swing.JTextField txtBookId;
    private javax.swing.JTextField txtInventoryId;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtWarehouseId;
    // End of variables declaration//GEN-END:variables
}
