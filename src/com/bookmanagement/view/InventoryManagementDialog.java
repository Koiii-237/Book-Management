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
import com.bookmanagement.Dao.InventoryDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Inventory;
import java.awt.Frame;
import java.awt.Window;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class InventoryManagementDialog extends javax.swing.JDialog {

    /**
     * Creates new form BookManagementDialog
     */
    private static final Logger LOGGER = Logger.getLogger(InventoryManagementDialog.class.getName());
    private InventoryDAO inventoryDAO;
    private BookManagementDAO bookManagementDAO;
    private Inventory currentInventory; // Đối tượng Inventory hiện tại (nếu đang sửa)
    private boolean isEditMode; // true nếu đang ở chế độ sửa, false nếu thêm mới
    private boolean isSucceeded = false; // Biến để kiểm tra thao tác có thành công không

    /**
     * Creates new form InventoryDialog
     *
     * @param parent Frame cha của dialog.
     * @param inventory Đối tượng Inventory để chỉnh sửa (null nếu thêm mới).
     * @param isEditMode true nếu là chế độ chỉnh sửa, false nếu thêm mới.
     */
    public InventoryManagementDialog(java.awt.Frame parent, Inventory inventory, boolean isEditMode) {
        super(parent, true); // Luôn là modal
        initComponents();
        this.inventoryDAO = new InventoryDAO();
        this.bookManagementDAO = new BookManagementDAO();
        this.currentInventory = inventory;
        this.isEditMode = isEditMode;
        initComboBox(); // Khởi tạo ComboBox sách
        initDialogState(); // Thiết lập trạng thái ban đầu của dialog

        this.setLocationRelativeTo(parent); // Canh giữa dialog
    }

    /**
     * Trả về true nếu thao tác trong dialog (thêm/sửa) thành công.
     * @return true nếu thành công, false nếu bị hủy hoặc thất bại.
     */
    public boolean isSucceeded() {
        return isSucceeded;
    }

    /**
     * Khởi tạo ComboBox chọn sách với dữ liệu từ cơ sở dữ liệu.
     */
    private void initComboBox() {
        try {
            List<Book> books = bookManagementDAO.getAllBooks();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (Book book : books) {
                model.addElement(book.getBookID() + " - " + book.getBookName());
            }
            cbBook.setModel(model);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải danh sách sách vào ComboBox", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách sách: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Thiết lập trạng thái ban đầu của dialog dựa trên chế độ (thêm mới/chỉnh sửa).
     */
    private void initDialogState() {
        if (isEditMode && currentInventory != null) {
            setTitle("SỬA THÔNG TIN TỒN KHO");
            txtInventoryID.setText(currentInventory.getInventoryId());
            txtInventoryID.setEditable(false); // Không cho phép sửa ID
            txtQuantity.setText(String.valueOf(currentInventory.getQunatity()));
            txtAddress.setText(currentInventory.getAddress());
            // Chọn sách tương ứng trong ComboBox
            for (int i = 0; i < cbBook.getItemCount(); i++) {
                String item = cbBook.getItemAt(i);
                if (item.startsWith(currentInventory.getBookId() + " -")) {
                    cbBook.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            setTitle("THÊM MỚI THÔNG TIN TỒN KHO");
            txtInventoryID.setText("(AUTO GEN / Không sửa)");
            txtInventoryID.setEditable(false); // Luôn không cho phép sửa ID
            txtQuantity.setText("");
            txtAddress.setText("");
            cbBook.setSelectedIndex(-1); // Không chọn gì ban đầu
        }
    }

    /**
     * Thu thập dữ liệu từ các trường UI để tạo hoặc cập nhật đối tượng Inventory.
     * @return Đối tượng Inventory với dữ liệu từ UI, hoặc null nếu có lỗi.
     */
    private Inventory collectFormData() {
        String inventoryId = isEditMode ? currentInventory.getInventoryId() : null; // ID sẽ được tạo tự động nếu thêm mới
        String selectedBookString = (String) cbBook.getSelectedItem();
        if (selectedBookString == null || selectedBookString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        String bookId = selectedBookString.split(" - ")[0];

        int quantity;
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String address = txtAddress.getText().trim();
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập vị trí kho.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new Inventory(inventoryId, quantity, address, bookId);
    }

    /**
     * Xử lý logic lưu (thêm mới hoặc cập nhật) bản ghi tồn kho.
     */
    private void handleSaveLogic() {
        Inventory inv = collectFormData();
        if (inv == null) {
            return; // Dữ liệu không hợp lệ
        }

        try {
            boolean success;
            if (isEditMode) {
                success = inventoryDAO.update(inv);
            } else {
                success = inventoryDAO.insert(inv);
            }

            if (success) {
                isSucceeded = true; // Đặt trạng thái thành công
                JOptionPane.showMessageDialog(this, "Lưu thông tin tồn kho thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Đóng dialog
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lưu thông tin tồn kho.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lưu tồn kho: ", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu tồn kho: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý logic hủy bỏ thao tác và đóng dialog.
     */
    private void handleCancelLogic() {
        isSucceeded = false; // Đặt trạng thái không thành công
        dispose(); // Đóng dialog
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
        txtAddress = new javax.swing.JTextField();
        cbBook = new javax.swing.JComboBox<>();
        txtQuantity = new javax.swing.JTextField();
        pnButton = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add and delete book");
        setModal(true);
        setResizable(false);

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
        txtInventoryID.setText("AUTO GENERATE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtInventoryID, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtAddress, gridBagConstraints);

        cbBook.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(cbBook, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtQuantity, gridBagConstraints);

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
                InventoryManagementDialog dialog = new InventoryManagementDialog(new javax.swing.JFrame(), null, false);
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
    private javax.swing.JComboBox<String> cbBook;
    private javax.swing.JPanel formPanel;
    private javax.swing.JLabel lblAuthor;
    private javax.swing.JLabel lblBookID;
    private javax.swing.JLabel lblBookName;
    private javax.swing.JLabel lblKind;
    private javax.swing.JPanel pnButton;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtInventoryID;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables
}
