/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.BookManagementDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.User;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.SwingUtilities;


public class BookManagementPanel extends javax.swing.JPanel {

    /**
     * Creates new form BookPanel
     */
    BookManagementDAO bookDAO;
    
    public BookManagementPanel() {
        initComponents();
        bookDAO = new BookManagementDAO();
        initTable();
        fillToTable();
    }
    
    public void initTable(){
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID BOOK", "AUTHOR", "BOOK NAME", "CATEGORY", "DESCRIBLE", "PRICE"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBook.setModel(model);
        
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
    
    public void fillToTable(){
        try {
            DefaultTableModel tableModel = (DefaultTableModel) tblBook.getModel();
            tableModel.setRowCount(0);
            for(Book book : bookDAO.getAllBooks()){
                tableModel.addRow(new Object[]{
                    book.getBookID(),
                    book.getBookName(),
                    book.getAuthor(),
                    book.getGenre(),
                    book.getDescription(),
                    book.getPrice(),
                });
            }
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            
            
        } catch (SQLException ex) {
            Logger.getLogger(BookManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addBook(){
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        BookManagementDialog dialog = new BookManagementDialog(parent, true, null);
        dialog.setVisible(true); // Hiển thị dialog

        // Kiểm tra kết quả sau khi dialog đóng
        if (dialog.isDataSaved()) {
            JOptionPane.showMessageDialog(this, "COMPLETE!");
            fillToTable();
             // Tải lại dữ liệu vào bảng để hiển thị sách mới
        }
    }
    
    public void deleteBook(){
        int selectedRow = tblBook.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please choose book id to delete", "NOTIFICATION!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "NOTIFICATION!", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) tblBook.getModel();
            String maSachToDelete = model.getValueAt(selectedRow, 0).toString();

            try {
                boolean success = bookDAO.deleteBook(maSachToDelete);
                if (success) {
                    JOptionPane.showMessageDialog(this, "COMPLETE!", "NOTIFICATION!", JOptionPane.INFORMATION_MESSAGE);
                    fillToTable();
                } else {
                    JOptionPane.showMessageDialog(this, "FAIL.", "ERROR!", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ERROR WHEN DELETE BOOK: " + ex.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void updateBook(){
        try {
            int selectedRow = tblBook.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please choose book to delete", "NOTIFICATION!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            DefaultTableModel model = (DefaultTableModel) tblBook.getModel();
            String maSachToEdit = model.getValueAt(selectedRow, 0).toString();
            
            Book bookToEdit = bookDAO.getBookById(maSachToEdit);
            
            if (bookToEdit != null) {
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                BookManagementDialog dialog = new BookManagementDialog(parentFrame, true, bookToEdit);
                dialog.setVisible(true);
                
                if (dialog.isDataSaved()) {
                    fillToTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "NO FOUND BOOK TO UPDATE.", "ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void search() throws SQLException{
       String searchTerm = txtSearch.getText().trim();
        ArrayList<Book> searchResults;

        if (searchTerm.isEmpty()) {
            searchResults = (ArrayList<Book>) bookDAO.getAllBooks();
        } else {
            searchResults = (ArrayList<Book>) bookDAO.searchBooks(searchTerm);
        }
        
        DefaultTableModel model = (DefaultTableModel) tblBook.getModel();
        model.setRowCount(0);
        
        if (searchResults.isEmpty() && !searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NOT FOUND BOOK WITH: '" + searchTerm + "'.", "NOTIFICATION!", JOptionPane.INFORMATION_MESSAGE);
        }

        for (Book book : searchResults) {
            model.addRow(new Object[]{
                book.getBookID(),
                book.getAuthor(),
                book.getBookName(),
                book.getGenre(),
                book.getDescription(),
                book.getPrice()
            });
        }
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
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        spBookTable = new javax.swing.JScrollPane();
        tblBook = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        lblSearch.setText("Search: ");
        pnToolbar.add(lblSearch);

        txtSearch.setColumns(20);
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        pnToolbar.add(txtSearch);

        btnSearch.setText("SEARCH");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        pnToolbar.add(btnSearch);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        pnToolbar.add(jSeparator1);

        btnAdd.setText("ADD");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        pnToolbar.add(btnAdd);

        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        pnToolbar.add(btnDelete);

        btnUpdate.setText("UPDATE");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        pnToolbar.add(btnUpdate);

        btnRefresh.setText("REFRESH");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        pnToolbar.add(btnRefresh);

        add(pnToolbar, java.awt.BorderLayout.PAGE_START);

        spBookTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spBookTableMouseClicked(evt);
            }
        });

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
        if(tblBook.getSelectedRow() != -1){
            btnDelete.setEnabled(true);
            btnUpdate.setEnabled(true);
        }
        else{
            btnDelete.setEnabled(false);
            btnUpdate.setEnabled(false);
        }
    }//GEN-LAST:event_tblBookMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        addBook();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteBook();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        updateBook();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        fillToTable();
        txtSearch.setText("");
        JOptionPane.showMessageDialog(this, "Dữ liệu đã được làm mới.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void spBookTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spBookTableMouseClicked
        // TODO add your handling code here:
        if (tblBook.getSelectedRow() != -1) {
                    btnUpdate.setEnabled(true);
                    btnDelete.setEnabled(true);
                } else {
                    btnUpdate.setEnabled(false);
                    btnDelete.setEnabled(false);
                }
    }//GEN-LAST:event_spBookTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        try {
            // TODO add your handling code here:
            search();
        } catch (SQLException ex) {
            Logger.getLogger(BookManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JPanel pnToolbar;
    private javax.swing.JScrollPane spBookTable;
    private javax.swing.JTable tblBook;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}

