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
import com.bookmanagement.model.Book;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserManagementDialog extends javax.swing.JDialog {

    /**
     * Creates new form BookManagementDialog
     */
    private Book currentBook; // Đối tượng Book đang được chỉnh sửa (null nếu thêm mới)
    private BookManagementDAO bookDAO;
    private boolean dataSaved = false;

    /**
     * Constructor
     *
     * @param parent
     * @param modal
     * @param bookId
     */
    public UserManagementDialog(java.awt.Frame parent, boolean modal, Book book) {
        super(parent, modal);
        this.bookDAO = new BookManagementDAO();
        this.currentBook = book;
        initComponents();
        setLocationRelativeTo(parent);
        
        
        if (currentBook == null) {
            // Chế độ Thêm mới: Đặt tiêu đề và xóa trắng các trường
            setTitle("ADD NEW BOOK!");
            clearForm();
            txtBookID.setEditable(false);
        } else {
            // Chế độ Chỉnh sửa: Đặt tiêu đề và tải dữ liệu sách vào form
            setTitle("UPDATE INFOR OF BOOK");
            loadBookDetails(currentBook);
            txtBookID.setEditable(false);

        }
    }

    private void loadBookDetails(Book book) {
        if (book != null) {
            txtBookID.setText(book.getBookID());
            txtBookName.setText(book.getBookName());
            txtAuthor.setText(book.getAuthor());
            txtKind.setText(book.getCartegory());
            txtDescribe.setText(book.getDescibe());
            txtPrice.setText(book.getPrice() != null ? book.getPrice().toPlainString() : ""); // Đã điều chỉnh: getDonGia()
        }
    }

    private void clearForm() {
        txtBookID.setText("");
        txtBookName.setText("");
        txtAuthor.setText("");
        txtKind.setText("");
        txtDescribe.setText("");
        txtPrice.setText("");
    }


    public void save() {
        // 1. Lấy dữ liệu từ các trường nhập liệu
        String id = txtBookID.getText().trim();
        String name = txtBookName.getText().trim();
        String author = txtAuthor.getText().trim();
        String kind = txtKind.getText().trim();
        String description = txtDescribe.getText().trim();
        String priceStr = txtPrice.getText().trim();

        // 2. Validate dữ liệu nhập vào (kiểm tra rỗng, định dạng số, ...)
        if (name.isEmpty() || author.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Book Name, Author, Price.", "NOTIFICATION!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);

            if (price.compareTo(BigDecimal.ONE) < 0) {
                JOptionPane.showMessageDialog(this, "Price are not negative.", "NOTIFICATION!", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean result;
            if (currentBook == null) {
                Book book = new Book(author, name, author, description, price);
                result = bookDAO.addBook(book);
                if (result) {
                    JOptionPane.showMessageDialog(this, "Add new Book complete!", "NOTIFICATION!", JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    
                }
            } 
            else {
                currentBook.setBookName(name);
                currentBook.setAuthor(author);
                currentBook.setCartegory(kind);
                currentBook.setDescibe(description);
                currentBook.setPrice(price);
                result = bookDAO.updateBook(currentBook);
                if(result){
                    JOptionPane.showMessageDialog(this, "Update new Book complete!", "NOTIFICATION!", JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(this, "Update new Book fail!", "NOTIFICATION!", JOptionPane.ERROR_MESSAGE);
                }
            }

            dataSaved = true; // Đánh dấu là dữ liệu đã được lưu thành công
            dispose(); // Đóng dialog

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and quantity must be valid numbers.", "NOTIFICATION!", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(UserManagementDialog.class.getName()).log(Level.SEVERE, "Lỗi khi lưu dữ liệu sách", ex);
            JOptionPane.showMessageDialog(this, "ERROR: " + ex.getMessage(), "NOTIFICATION!", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void cancel() {
        dataSaved = false;
        dispose();
    }

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
        java.awt.GridBagConstraints gridBagConstraints;

        formPanel = new javax.swing.JPanel();
        lblBookName = new javax.swing.JLabel();
        lblBookID = new javax.swing.JLabel();
        lblAuthor = new javax.swing.JLabel();
        lblKind = new javax.swing.JLabel();
        lblPrice = new javax.swing.JLabel();
        txtBookID = new javax.swing.JTextField();
        txtBookName = new javax.swing.JTextField();
        txtAuthor = new javax.swing.JTextField();
        txtKind = new javax.swing.JTextField();
        txtPrice = new javax.swing.JTextField();
        lblDescribe = new javax.swing.JLabel();
        txtDescribe = new javax.swing.JTextField();
        pnButton = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add and delete book");
        setModal(true);

        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        formPanel.setLayout(new java.awt.GridBagLayout());

        lblBookName.setText("Book Name: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblBookName, gridBagConstraints);

        lblBookID.setText("ID BOOK: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblBookID, gridBagConstraints);

        lblAuthor.setText("Author: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblAuthor, gridBagConstraints);

        lblKind.setText("Kind: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblKind, gridBagConstraints);

        lblPrice.setText("Price: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblPrice, gridBagConstraints);

        txtBookID.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtBookID, gridBagConstraints);

        txtBookName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBookNameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtBookName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtAuthor, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtKind, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtPrice, gridBagConstraints);

        lblDescribe.setText("Descibe: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblDescribe, gridBagConstraints);

        txtDescribe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescribeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtDescribe, gridBagConstraints);

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

    private void txtBookNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBookNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBookNameActionPerformed

    private void txtDescribeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescribeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescribeActionPerformed

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
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        cancel();
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
            java.util.logging.Logger.getLogger(UserManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UserManagementDialog dialog = new UserManagementDialog(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JLabel lblDescribe;
    private javax.swing.JLabel lblKind;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JPanel pnButton;
    private javax.swing.JTextField txtAuthor;
    private javax.swing.JTextField txtBookID;
    private javax.swing.JTextField txtBookName;
    private javax.swing.JTextField txtDescribe;
    private javax.swing.JTextField txtKind;
    private javax.swing.JTextField txtPrice;
    // End of variables declaration//GEN-END:variables
}
