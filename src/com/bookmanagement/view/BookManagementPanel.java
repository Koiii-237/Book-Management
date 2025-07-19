/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;


public class BookManagementPanel extends javax.swing.JPanel {

    /**
     * Creates new form BookPanel
     */
    
    public BookManagementPanel() {
        initComponents();
        initTable();
    }
    
    public void initTable(){
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID BOOK", "AUTHOR", "BOOK NAME", "KIND", "DESCRIPT", "PRICE"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBook.setModel(tableModel);
    }
    
    public void fillToTable(){
        DefaultTableModel tableModel = (DefaultTableModel) tblBook.getModel();
        tableModel.setRowCount(0);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        System.out.println("Dữ liệu sách đã được tải.");
    }
    
    public void addBook(){
        BookManagementDialog dialog = new BookManagementDialog(null, true, null); // true: modal, null: không có dữ liệu để chỉnh sửa
        dialog.setVisible(true); // Hiển thị dialog

        // Kiểm tra kết quả sau khi dialog đóng
        if (dialog.isDataSaved()) {
            JOptionPane.showMessageDialog(this, "Thêm sách mới thành công!");
            fillToTable();
             // Tải lại dữ liệu vào bảng để hiển thị sách mới
        }
    }
    
    public void deleteBook(){
        int selectedRow = tblBook.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sách để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sách này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) tblBook.getModel();
            String bookIdToDelete = model.getValueAt(selectedRow, 0).toString(); // Lấy ID sách để xóa

            try {
                // --- KẾT NỐI VỚI LỚP DAO/SERVICE CỦA BẠN ĐỂ XÓA DỮ LIỆU TỪ CSDL ---
                // Ví dụ: boolean success = yourBookDAO.deleteBook(bookIdToDelete);
                // if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    fillToTable(); // Tải lại dữ liệu để cập nhật bảng
                // } else {
                //    JOptionPane.showMessageDialog(this, "Không thể xóa sách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                // }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa sách: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    public void updateBook(){
        int selectedRow = tblBook.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sách để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy Mã Sách từ hàng được chọn trên bảng
        DefaultTableModel model = (DefaultTableModel) tblBook.getModel();
        String bookIdToEdit = model.getValueAt(selectedRow, 0).toString(); // Cột 0 là Mã Sách

        // Tạo BookEntryDialog ở chế độ chỉnh sửa, truyền Mã Sách
        BookManagementDialog dialog = new BookManagementDialog(null, true, bookIdToEdit);
        dialog.setVisible(true);

        if (dialog.isDataSaved()) {
            JOptionPane.showMessageDialog(this, "Cập nhật sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            fillToTable(); // Tải lại dữ liệu để hiển thị sách đã cập nhật
        }
    }
    
    public void search(){
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            fillToTable(); // Nếu ô tìm kiếm trống, tải lại tất cả sách
            return;
        }

        // --- KẾT NỐI VỚI LỚP DAO/SERVICE CỦA BẠN ĐỂ TÌM KIẾM DỮ LIỆU TỪ CSDL ---
        // Ví dụ: List<Book> searchResults = yourBookDAO.searchBooks(searchTerm);
        // Sau đó cập nhật DefaultTableModel của tblBooks với searchResults
        DefaultTableModel model = (DefaultTableModel) tblBook.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ trên bảng
        
        // Thêm dữ liệu tìm kiếm vào bảng (sẽ trống nếu chưa có kết nối CSDL)
        // for (Book book : searchResults) {
        //     model.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(),
        //                               book.getCategory(), book.getPrice(), book.getQuantity()});
        // }
        JOptionPane.showMessageDialog(this, "Chức năng tìm kiếm đã được gọi với từ khóa: '" + searchTerm + "'.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnToolbar = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnAdd = new javax.swing.JToggleButton();
        btnDelete = new javax.swing.JToggleButton();
        btnUpdate = new javax.swing.JToggleButton();
        btnRefresh = new javax.swing.JToggleButton();
        spBookTable = new javax.swing.JScrollPane();
        tblBook = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        lblSearch.setText("Search: ");
        pnToolbar.add(lblSearch);

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        pnToolbar.add(txtSearch);

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        pnToolbar.add(btnSearch);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        pnToolbar.add(jSeparator1);

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        pnToolbar.add(btnAdd);

        btnDelete.setText("Delete");
        pnToolbar.add(btnDelete);

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        pnToolbar.add(btnUpdate);

        btnRefresh.setText("Refresh");
        pnToolbar.add(btnRefresh);

        add(pnToolbar, java.awt.BorderLayout.PAGE_START);

        tblBook.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblBook.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBookMouseClicked(evt);
            }
        });
        spBookTable.setViewportView(tblBook);

        add(spBookTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblBookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBookMouseClicked
        // TODO add your handling code here:
        if(tblBook.getSelectedRow() != -1){
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        }
        else{
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }//GEN-LAST:event_tblBookMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        addBook();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        updateBook();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_btnSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnAdd;
    private javax.swing.JToggleButton btnDelete;
    private javax.swing.JToggleButton btnRefresh;
    private javax.swing.JToggleButton btnSearch;
    private javax.swing.JToggleButton btnUpdate;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JPanel pnToolbar;
    private javax.swing.JScrollPane spBookTable;
    private javax.swing.JTable tblBook;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
