/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import javax.swing.JOptionPane;


public class BookManagementDialog extends javax.swing.JDialog {

    /**
     * Creates new form BookManagementDialog
     */
    private boolean dataSaved = false;
    private String bookIdToEdit = null; 
    
    /**
     * Constructor 
     * @param parent
     * @param modal       
     * @param bookId
     */
    
    public BookManagementDialog(java.awt.Frame parent, boolean modal, String bookId) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        
        txtBookID.setEditable(false); // ID sách không thể chỉnh sửa thủ công
        if (bookIdToEdit == null) {
            // Chế độ Thêm mới: Đặt tiêu đề và xóa trắng các trường
            setTitle("Thêm Sách Mới");
            clearForm(); 
        } else {
            // Chế độ Chỉnh sửa: Đặt tiêu đề và tải dữ liệu sách vào form
            setTitle("Chỉnh Sửa Thông tin Sách");
            loadBookDetails(bookIdToEdit);
        }
    }
    
    private void loadBookDetails(String bookId) {
        // ** TẠI ĐÂY, BẠN SẼ GỌI PHƯƠNG THỨC TỪ LỚP DAO/SERVICE CỦA MÌNH
        // ĐỂ LẤY CHI TIẾT SÁCH TỪ CƠ SỞ DỮ LIỆU DỰA TRÊN bookId. **
        // Ví dụ: Book book = yourBookDAO.getBookById(bookId);

        // Sau khi có đối tượng Book, bạn sẽ điền dữ liệu vào các trường:
        // if (book != null) {
        //     txtBookID.setText(book.getId());
        //     txtBookTitle.setText(book.getTitle());
        //     txtBookAuthor.setText(book.getAuthor());
        //     txtBookCategory.setText(book.getCategory());
        //     txtBookDescription.setText(book.getDescription());
        //     txtBookPrice.setText(String.valueOf(book.getPrice())); // Chuyển số sang String
        //     txtBookQuantity.setText(String.valueOf(book.getQuantity())); // Chuyển số sang String
        // } else {
        //     JOptionPane.showMessageDialog(this, "Không tìm thấy sách với ID: " + bookId, "Lỗi", JOptionPane.ERROR_MESSAGE);
        //     dispose(); // Đóng dialog nếu không tìm thấy sách
        // }
    }
    
    private void clearForm() {
        txtBookID.setText("");
        txtBookName.setText("");
        txtAuthor.setText("");
        txtKind.setText("");
        txtDescribe.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
    }
    
    public void addBook(String bookID){
        
    }
    
    public void save(){
        // 1. Lấy dữ liệu từ các trường nhập liệu
        String id = txtBookID.getText().trim(); 
        String name = txtBookName.getText().trim();
        String author = txtAuthor.getText().trim();
        String kind = txtKind.getText().trim();
        String description = txtDescribe.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String quantityStr = txtQuantity.getText().trim();

        // 2. Validate dữ liệu nhập vào (kiểm tra rỗng, định dạng số, ...)
        if (name.isEmpty() || author.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các trường bắt buộc.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            if (price < 0 || quantity < 0) {
                JOptionPane.showMessageDialog(this, "Giá và Số lượng không được là số âm.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Gọi phương thức thêm/cập nhật từ lớp DAO/Service của bạn
            if (bookIdToEdit == null) {
                // Đây là trường hợp THÊM MỚI
                // Ví dụ: yourBookDAO.addBook(new Book(null, title, author, category, description, price, quantity));
                JOptionPane.showMessageDialog(this, "Thêm sách mới thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Đây là trường hợp CẬP NHẬT
                // Ví dụ: yourBookDAO.updateBook(new Book(id, title, author, category, description, price, quantity));
                JOptionPane.showMessageDialog(this, "Cập nhật sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            
            dataSaved = true; // Đánh dấu là dữ liệu đã được lưu thành công
            dispose(); // Đóng dialog
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá và Số lượng phải là số hợp lệ.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Xử lý các lỗi khác từ DAO/Service
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    public void cancel(){
        dataSaved = false;
        dispose();
    }
    
    public boolean isDataSaved(){
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
        lblQuantity = new javax.swing.JLabel();
        txtBookID = new javax.swing.JTextField();
        txtBookName = new javax.swing.JTextField();
        txtAuthor = new javax.swing.JTextField();
        txtKind = new javax.swing.JTextField();
        txtPrice = new javax.swing.JTextField();
        txtQuantity = new javax.swing.JTextField();
        lblDescribe = new javax.swing.JLabel();
        txtDescribe = new javax.swing.JTextField();
        pnButton = new javax.swing.JPanel();
        btnSave = new javax.swing.JToggleButton();
        btnCancel = new javax.swing.JToggleButton();

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

        lblQuantity.setText("Quantity: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(lblQuantity, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        formPanel.add(txtQuantity, gridBagConstraints);

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

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        cancel();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtDescribeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescribeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescribeActionPerformed

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
            java.util.logging.Logger.getLogger(BookManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BookManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BookManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BookManagementDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BookManagementDialog dialog = new BookManagementDialog(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JToggleButton btnCancel;
    private javax.swing.JToggleButton btnSave;
    private javax.swing.JPanel formPanel;
    private javax.swing.JLabel lblAuthor;
    private javax.swing.JLabel lblBookID;
    private javax.swing.JLabel lblBookName;
    private javax.swing.JLabel lblDescribe;
    private javax.swing.JLabel lblKind;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JPanel pnButton;
    private javax.swing.JTextField txtAuthor;
    private javax.swing.JTextField txtBookID;
    private javax.swing.JTextField txtBookName;
    private javax.swing.JTextField txtDescribe;
    private javax.swing.JTextField txtKind;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables
}
