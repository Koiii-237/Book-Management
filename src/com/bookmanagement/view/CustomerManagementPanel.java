/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.CustomerDAO;
import com.bookmanagement.model.Customer;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class CustomerManagementPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(CustomerManagementPanel.class.getName());
    private CustomerDAO customerDAO;
    private DefaultTableModel tableModel;

    /**
     * Creates new form CustomerManagementPanel
     */
    public CustomerManagementPanel() {
        initComponents();
        setSize(800, 500); // Default panel size
        this.customerDAO = new CustomerDAO();
        initTable();
        fillToTable();

        // Add ListSelectionListener to the table to enable/disable buttons
        tblCustomer.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensure event fires only once when selection is final
                    updateButtonStates();
                }
            }
        });
        updateButtonStates(); // Update button states initially
    }

    /**
     * Initializes the table structure for customers.
     */
    private void initTable() {
        tableModel = new DefaultTableModel(
                new Object[]{"Mã KH", "Tên Khách Hàng", "Địa Chỉ", "Số Điện Thoại"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Cells are not editable directly
            }
        };
        tblCustomer.setModel(tableModel);
    }

    /**
     * Fills the customer table with data from the database.
     */
    private void fillToTable() {
        tableModel.setRowCount(0); // Clear existing rows

        try {
            ArrayList<Customer> allCustomers = customerDAO.getAllCustomers();
            for (Customer customer : allCustomers) {
                tableModel.addRow(new Object[]{
                    customer.getId(),
                    customer.getName(),
                    customer.getAddress(),
                    customer.getPhoneNumber()
                });
            }
        } catch (Exception e) { // Catch generic Exception to log all possible issues
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu khách hàng: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the enabled state of "SỬA" and "XÓA" buttons.
     */
    private void updateButtonStates() {
        int selectedRow = tblCustomer.getSelectedRow();
        boolean isRowSelected = selectedRow != -1;

        btnUpdate.setEnabled(isRowSelected);
        btnDelete.setEnabled(isRowSelected);
    }

    /**
     * Performs a search for customers based on the search term.
     */
    private void performSearch() {
        String searchTerm = txtSearch.getText().trim();
        ArrayList<Customer> searchResults;

        try {
            if (searchTerm.isEmpty()) {
                searchResults = customerDAO.getAllCustomers(); // If empty, display all
            } else {
                // Assuming searchCustomers can search by Name, Phone, or Address
                searchResults = customerDAO.searchCustomers(searchTerm);
            }

            tableModel.setRowCount(0); // Clear old data

            if (searchResults.isEmpty() && !searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với từ khóa: '" + searchTerm + "'.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

            for (Customer customer : searchResults) {
                tableModel.addRow(new Object[]{
                    customer.getId(),
                    customer.getName(),
                    customer.getAddress(),
                    customer.getPhoneNumber()
                });
            }
            updateButtonStates(); // Update button states after search
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm khách hàng: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens CustomerDialog to add a new customer record.
     */
    private void handleAddNewCustomer() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        // Open dialog in add new mode (pass null for customer and false for isEditMode)
        CustomerManagementDialog dialog = new CustomerManagementDialog(parentFrame, null, false);
        dialog.setVisible(true);

        if (dialog.isSucceeded()) { // Check if the operation was successful
            fillToTable(); // Refresh table after successful addition
            JOptionPane.showMessageDialog(this, "Thêm mới khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Thao tác thêm mới bị hủy hoặc thất bại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
        updateButtonStates(); // Update button states after dialog closes
    }

    /**
     * Opens CustomerDialog to edit the selected customer record.
     */
    private void handleEditCustomer() {
        int selectedRow = tblCustomer.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng để sửa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String customerId = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer != null) {
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                // Open dialog in edit mode (pass the customer object and true for isEditMode)
                CustomerManagementDialog dialog = new CustomerManagementDialog(parentFrame, customer, true);
                dialog.setVisible(true);

                if (dialog.isSucceeded()) { // Check if the operation was successful
                    fillToTable(); // Refresh table after successful update
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Thao tác cập nhật bị hủy hoặc thất bại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải khách hàng để sửa: " + customerId, ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        updateButtonStates(); // Update button states after dialog closes
    }

    /**
     * Deletes the selected customer record.
     */
    private void handleDeleteCustomer() {
        int selectedRow = tblCustomer.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String customerId = (String) tableModel.getValueAt(selectedRow, 0);
        String customerName = (String) tableModel.getValueAt(selectedRow, 1); // Customer name for confirmation message

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khách hàng '" + customerName + "' (Mã KH: " + customerId + ") không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = customerDAO.deleteCustomer(customerId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    fillToTable(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) { // Catch generic Exception to log all possible issues
                LOGGER.log(Level.SEVERE, "Lỗi khi xóa khách hàng: " + customerId, ex);
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateButtonStates(); // Update button states after deletion
    }

    /**
     * Refreshes all data in the table.
     */
    private void handleRefreshTable() {
        fillToTable();
        txtSearch.setText(""); // Clear search field
        updateButtonStates();
        JOptionPane.showMessageDialog(this, "Dữ liệu đã được làm mới.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
        handleAddNewCustomer();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        handleDeleteCustomer();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        handleEditCustomer();   
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        handleRefreshTable();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void spCustomerTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spCustomerTableMouseClicked
        
    }//GEN-LAST:event_spCustomerTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        performSearch();
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

