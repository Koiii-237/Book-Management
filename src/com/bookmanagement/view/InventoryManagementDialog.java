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
import com.bookmanagement.model.Inventory;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class InventoryManagementDialog extends javax.swing.JDialog {

    /**
     * Creates new form BookManagementDialog
     */
    private Inventory currentInventory; 
    private InventoryDAO inventoryDAO;
    private boolean dataSaved = false;

    /**
     * Constructor
     *
     * @param parent
     * @param modal
     * @param bookId
     */
    public InventoryManagementDialog(Frame owner) {
        super(owner, "ADD NEW INVENTORY", true);
        initComponents();
        setSize(500, 200);
        setupListeners();
        setLocationRelativeTo(owner);
    }
    
    public InventoryManagementDialog(Frame owner, Inventory inventory) {
        super(owner, "UPDATE INVENTORY", true);
        this.currentInventory = inventory;
        setSize(500, 200);
        setupListeners();
        setLocationRelativeTo(owner);
        populateFields(inventory); // Điền dữ liệu của item vào các trường
        // Không cho phép chỉnh sửa MaKho khi sửa
        txtBookID.setEditable(false);
    }
    
    private void populateFields(Inventory inventory) {
        txtInventoryID.setText(inventory.getInventoryId());
        txtBookID.setText(inventory.getBookId());
        txtQuantityInventory.setText(String.valueOf(inventory.getQunatity()));
        txtAddress.setText(inventory.getAddress());
        txtBookID.setEnabled(false);
    }
    
    private void setupListeners() {
       btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Khi nút "Lưu" được nhấn
                if (validateInput()) { // Kiểm tra tính hợp lệ của dữ liệu nhập vào
                    saveStock(); // Lưu hoặc cập nhật đối tượng Stock
                    dataSaved = true; // Đặt cờ saved thành true
                    dispose(); // Đóng dialog (giải phóng tài nguyên)
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Khi nút "Hủy" được nhấn
                dataSaved = false; // Đặt cờ saved thành false
                dispose(); // Đóng dialog
            }
        });
    }
    
    private void saveStock() {
        try {
            String stockId = txtInventoryID.getText().trim();
            int quantity = Integer.parseInt(txtQuantityInventory.getText().trim());
            String location = txtAddress.getText().trim();
            String bookId = txtBookID.getText().trim();

            if (stockId.isEmpty() || location.isEmpty() || bookId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (currentInventory == null) {
                // Nếu currentStock là null, đây là trường hợp thêm mới
                currentInventory = new Inventory(stockId, quantity, location, bookId);
            } else {
                // Nếu currentStock đã tồn tại, đây là trường hợp chỉnh sửa
                currentInventory.setQunatity(quantity);
                currentInventory.setAddress(location);
                currentInventory.setBookId(bookId);
                // Stock ID không thay đổi khi chỉnh sửa
            }
            dataSaved = true; // Đặt cờ là true nếu lưu thành công
            dispose(); // Đóng dialog
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là một số nguyên hợp lệ.", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public Inventory getStockItem() {
        return currentInventory;
    }
    
    public boolean isSaved() {
        return dataSaved;
    }
    
    private boolean validateInput() {
        String maKho = txtInventoryID.getText().trim();
        String maSach = txtBookID.getText().trim();
        String soLuongStr = txtQuantityInventory.getText().trim();
        String viTri = txtAddress.getText().trim();

        if (maKho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã Kho không được để trống.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (maSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã Sách không được để trống.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (viTri.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vị Trí không được để trống.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(soLuongStr);
            if (soLuong < 0) {
                JOptionPane.showMessageDialog(this, "Số Lượng Tồn phải là số không âm.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số Lượng Tồn phải là một số nguyên hợp lệ.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true; // Tất cả dữ liệu hợp lệ
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
        lblBookName = new javax.swing.JLabel();
        lblBookID = new javax.swing.JLabel();
        lblAuthor = new javax.swing.JLabel();
        lblKind = new javax.swing.JLabel();
        txtInventoryID = new javax.swing.JTextField();
        txtQuantityInventory = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        txtBookID = new javax.swing.JTextField();
        pnButton = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add and delete book");
        setModal(true);

        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        formPanel.setLayout(new java.awt.GridBagLayout());

        lblBookName.setText("QUANTITY INVENTORY: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblBookName, gridBagConstraints);

        lblBookID.setText("INVENTORY ID");
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

        lblKind.setText("BOOK ID: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblKind, gridBagConstraints);

        txtInventoryID.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtInventoryID, gridBagConstraints);

        txtQuantityInventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantityInventoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtQuantityInventory, gridBagConstraints);
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
        formPanel.add(txtBookID, gridBagConstraints);

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

        formPanel.add(pnButton, new java.awt.GridBagConstraints());

        getContentPane().add(formPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtQuantityInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantityInventoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantityInventoryActionPerformed

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
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
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
                InventoryManagementDialog dialog = new InventoryManagementDialog(new javax.swing.JFrame());
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
    private javax.swing.JTextField txtBookID;
    private javax.swing.JTextField txtInventoryID;
    private javax.swing.JTextField txtQuantityInventory;
    // End of variables declaration//GEN-END:variables
}
