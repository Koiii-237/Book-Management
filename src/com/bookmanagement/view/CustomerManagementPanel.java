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
import com.bookmanagement.Dao.CustomerDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Customer;
import com.bookmanagement.model.User;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.SwingUtilities;


public class CustomerManagementPanel extends javax.swing.JPanel {

    /**
     * Creates new form BookPanel
     */
    CustomerDAO csDAO;
    
    
    public CustomerManagementPanel() {
        initComponents();
        csDAO = new CustomerDAO();
        initTable();
        fillToTable();
    }
    
    public void initTable(){
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID CUSTOMER", "NAME", "ADDRESS", "PHONE NUMBER"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCustomer.setModel(model);
        
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
    
    public void fillToTable(){
        DefaultTableModel tableModel = (DefaultTableModel) tblCustomer.getModel();
        tableModel.setRowCount(0);
        for(Customer cs : csDAO.getAllCustomers()){
            tableModel.addRow(new Object[]{
                    cs.getId(),
                    cs.getName(),
                    cs.getAddress(),
                    cs.getPhoneNumber()
            });
        }
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
    
    public void addCustomer(){
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        CustomerManagementDialog dialog = new CustomerManagementDialog(parent, true, null); 
        dialog.setVisible(true); // Hiển thị dialog

        // Kiểm tra kết quả sau khi dialog đóng
        if (dialog.isDataSaved()) {
            fillToTable();
             // Tải lại dữ liệu vào bảng để hiển thị sách mới
        }
    }
    
    public void deleteCustomer(){
        int selectedRow = tblCustomer.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please choose book id to delete", "NOTIFICATION!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "NOTIFICATION!", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) tblCustomer.getModel();
            String idToDelete = model.getValueAt(selectedRow, 0).toString();

            try {
                boolean success = csDAO.deleteCustomer(idToDelete);
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
    
    public void updateCustomer(){
        int selectedRow = tblCustomer.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please choose book to delete", "NOTIFICATION!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblCustomer.getModel();
        String idToEdit = model.getValueAt(selectedRow, 0).toString();

        Customer csToEdit = csDAO.getCustomerById(idToEdit);

        if (csToEdit != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            CustomerManagementDialog dialog = new CustomerManagementDialog(parentFrame, true, csToEdit);
            dialog.setVisible(true);

            if (dialog.isDataSaved()) {
                fillToTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "NO FOUND BOOK TO UPDATE.", "ERROR!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void search(){
       String searchTerm = txtSearch.getText().trim();
        ArrayList<Customer> searchResults;

        if (searchTerm.isEmpty()) {
            searchResults = csDAO.getAllCustomers();
        } else {
            searchResults = csDAO.searchCustomers(searchTerm);
        }
        
        DefaultTableModel model = (DefaultTableModel) tblCustomer.getModel();
        model.setRowCount(0);
        
        if (searchResults.isEmpty() && !searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NO FOUND BOOK WITH: '" + searchTerm + "'.", "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        }

        for (Customer cs : searchResults) {
            model.addRow(new Object[]{
                cs.getId(), 
                cs.getName(), 
                cs.getAddress(),
                cs.getPhoneNumber()
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
        spCustomerTable = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();

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

        spCustomerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spCustomerTableMouseClicked(evt);
            }
        });

        tblCustomer.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomerMouseClicked(evt);
            }
        });
        spCustomerTable.setViewportView(tblCustomer);

        add(spCustomerTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblCustomerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomerMouseClicked
        if(tblCustomer.getSelectedRow() != -1){
            btnDelete.setEnabled(true);
            btnUpdate.setEnabled(true);
        }
        else{
            btnDelete.setEnabled(false);
            btnUpdate.setEnabled(false);
        }
    }//GEN-LAST:event_tblCustomerMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        addCustomer();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteCustomer();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        updateCustomer();   
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        fillToTable();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void spCustomerTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spCustomerTableMouseClicked

    }//GEN-LAST:event_spCustomerTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        search();
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
    private javax.swing.JScrollPane spCustomerTable;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}

