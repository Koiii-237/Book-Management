/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.InventoryDAO;
import com.bookmanagement.Dao.StockTransactionDAO;
import com.bookmanagement.Dao.WarehouseDAO;
import com.bookmanagement.dao.BookDAO;
import com.bookmanagement.model.Inventory;
import com.bookmanagement.model.StockTransaction;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Lớp JPanel để quản lý thông tin tồn kho sách. Bao gồm các chức năng: hiển thị
 * danh sách, tìm kiếm, thêm mới, sửa, xóa.
 */
public class InventoryManagementPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(InventoryManagementPanel.class.getName());
    private InventoryDAO inventoryDAO;
    private BookDAO bookDAO;
    private WarehouseDAO warehouseDAO;
    private StockTransactionDAO stockTransactionDAO;
    private DefaultTableModel tableModel;
    private List<Inventory> allInventories;

    public InventoryManagementPanel() {
        initComponents();
        inventoryDAO = new InventoryDAO();
        bookDAO = new BookDAO(); // Initialize the new DAO
        warehouseDAO = new WarehouseDAO(); // Initialize the new DAO
        stockTransactionDAO = new StockTransactionDAO();
        initTable();
        fillToTable();

        tblInventory.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean rowSelected = tblInventory.getSelectedRow() != -1;
                    btnUpdate.setEnabled(rowSelected);
                    btnDelete.setEnabled(rowSelected);
                    btnImport.setEnabled(rowSelected);
                    btnExport.setEnabled(rowSelected);
                }
            }
        });
        // Disable buttons initially
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnImport.setEnabled(false);
        btnExport.setEnabled(false);
    }

    public void initTable() {
        tableModel = new DefaultTableModel(
                new Object[]{"Inventory ID", "Book ID", "Book Name", "Warehouse ID", "Warehouse Name", "Quantity"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct editing on the table
            }
        };
        tblInventory.setModel(tableModel);
    }

    public void fillToTable() {
        try {
            allInventories = inventoryDAO.getAllInventories();
            refreshTableWithData(allInventories);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error loading inventory list", ex);
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTableWithData(List<Inventory> inventories) {
        tableModel.setRowCount(0);
        if (inventories != null) {
            for (Inventory inventory : inventories) {
                String bookName = "Not Found";
                String warehouseName = "Not Found";
                try {
                    bookName = bookDAO.getBookById(inventory.getBookId()).getTitle();
                    warehouseName = warehouseDAO.getWarehouseById(inventory.getWarehouseId()).getWarehouseName();
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, "Could not retrieve book or warehouse name for inventory: " + inventory.getInventoryId(), ex);
                }

                tableModel.addRow(new Object[]{
                    inventory.getInventoryId(),
                    inventory.getBookId(),
                    bookName,
                    inventory.getWarehouseId(),
                    warehouseName,
                    inventory.getQuantity()
                });
            }
        }
    }

    private void handleAddNewInventory() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        InventoryManagementDialog dialog = new InventoryManagementDialog(parentFrame, true, null);
        dialog.setVisible(true);
        if (dialog.isDataSaved()) {
            fillToTable();
        }
    }

    private void handleEditInventory() {
        int selectedRow = tblInventory.getSelectedRow();
        if (selectedRow != -1) {
            int inventoryId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Inventory inventory = inventoryDAO.getInventoryById(inventoryId);
                if (inventory != null) {
                    Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                    InventoryManagementDialog dialog = new InventoryManagementDialog(parentFrame, true, inventory);
                    dialog.setVisible(true);
                    if (dialog.isDataSaved()) {
                        fillToTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Inventory record not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error fetching inventory data for editing", ex);
                JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteInventory() {
        int selectedRow = tblInventory.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this inventory record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int inventoryId = (int) tableModel.getValueAt(selectedRow, 0);
                try {
                    Inventory inventoryToDelete = inventoryDAO.getInventoryById(inventoryId);
                    if (inventoryDAO.deleteInventory(inventoryId)) {
                        stockTransactionDAO.addStockTransaction(new StockTransaction(
                                0,
                                inventoryToDelete.getBookId(),
                                inventoryToDelete.getWarehouseId(),
                                null,
                                inventoryToDelete.getQuantity(),
                                "DELETE",
                                LocalDateTime.now()
                        ));
                        JOptionPane.showMessageDialog(this, "Inventory deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        fillToTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete inventory.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(InventoryManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, "Error when delete inventory: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleRefreshTable() {
        fillToTable();
    }

    private void search() {
        String searchText = txtSearch.getText().toLowerCase().trim();
        List<Inventory> filteredList = new ArrayList<>();
        if (allInventories != null) {
            for (Inventory inventory : allInventories) {
                String bookName = "";
                String warehouseName = "";
                bookName = bookDAO.getBookById(inventory.getBookId()).getTitle();
                try {
                    warehouseName = warehouseDAO.getWarehouseById(inventory.getWarehouseId()).getWarehouseName();
                } catch (SQLException ex) {
                    Logger.getLogger(InventoryManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (String.valueOf(inventory.getInventoryId()).contains(searchText)
                        || String.valueOf(inventory.getBookId()).contains(searchText)
                        || String.valueOf(inventory.getWarehouseId()).contains(searchText)
                        || (bookName != null && bookName.toLowerCase().contains(searchText))
                        || (warehouseName != null && warehouseName.toLowerCase().contains(searchText))) {
                    filteredList.add(inventory);
                }
            }
        }
        refreshTableWithData(filteredList);
    }

    private void handleImportInventory() {
        int selectedRow = tblInventory.getSelectedRow();
        if (selectedRow != -1) {
            int bookId = (int) tableModel.getValueAt(selectedRow, 1);
            int warehouseId = (int) tableModel.getValueAt(selectedRow, 3);
            int currentQuantity = (int) tableModel.getValueAt(selectedRow, 5);

            String input = JOptionPane.showInputDialog(this, "Enter the quantity to add:", "Import Stock", JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.isEmpty()) {
                try {
                    int quantityToAdd = Integer.parseInt(input);
                    if (quantityToAdd <= 0) {
                        JOptionPane.showMessageDialog(this, "Quantity must be a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (inventoryDAO.updateInventoryQuantity(bookId, warehouseId, quantityToAdd)) {
                        JOptionPane.showMessageDialog(this, "Stock imported successfully. New quantity is: " + (currentQuantity + quantityToAdd), "Success", JOptionPane.INFORMATION_MESSAGE);
                        fillToTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to import stock. The inventory record may not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Handles the stock export function.
     */
    private void handleExportInventory() {
        int selectedRow = tblInventory.getSelectedRow();
        if (selectedRow != -1) {
            int bookId = (int) tableModel.getValueAt(selectedRow, 1);
            int warehouseId = (int) tableModel.getValueAt(selectedRow, 3);
            int currentQuantity = (int) tableModel.getValueAt(selectedRow, 5);

            String input = JOptionPane.showInputDialog(this, "Enter the quantity to export:", "Export Stock", JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.isEmpty()) {
                try {
                    int quantityToExport = Integer.parseInt(input);
                    if (quantityToExport <= 0) {
                        JOptionPane.showMessageDialog(this, "Quantity must be a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (quantityToExport > currentQuantity) {
                        JOptionPane.showMessageDialog(this, "Export quantity cannot be greater than the current stock (" + currentQuantity + ").", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (inventoryDAO.updateInventoryQuantity(bookId, warehouseId, -quantityToExport)) {
                        JOptionPane.showMessageDialog(this, "Stock exported successfully. New quantity is: " + (currentQuantity - quantityToExport), "Success", JOptionPane.INFORMATION_MESSAGE);
                        fillToTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to export stock. Insufficient stock or a database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
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
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        spBookTable = new javax.swing.JScrollPane();
        tblInventory = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        lblSearch.setText("Search: ");
        pnToolbar.add(lblSearch);

        txtSearch.setColumns(20);
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
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

        btnUpdate.setText("UPDATE");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        pnToolbar.add(btnUpdate);

        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        pnToolbar.add(btnDelete);

        btnRefresh.setText("REFRESH");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        pnToolbar.add(btnRefresh);

        btnImport.setText("IMPORT");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });
        pnToolbar.add(btnImport);

        btnExport.setText("EXPORT");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        pnToolbar.add(btnExport);

        add(pnToolbar, java.awt.BorderLayout.PAGE_START);

        spBookTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spBookTableMouseClicked(evt);
            }
        });

        tblInventory.setModel(new javax.swing.table.DefaultTableModel(
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
        tblInventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblInventoryMouseClicked(evt);
            }
        });
        spBookTable.setViewportView(tblInventory);

        add(spBookTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblInventoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblInventoryMouseClicked
        if (tblInventory.getSelectedRow() != -1) {
            btnDelete.setEnabled(true);
            btnUpdate.setEnabled(true);
        } else {
            btnDelete.setEnabled(false);
            btnUpdate.setEnabled(false);
        }
    }//GEN-LAST:event_tblInventoryMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        handleAddNewInventory();
    }//GEN-LAST:event_btnAddActionPerformed

    private void spBookTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spBookTableMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_spBookTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        handleEditInventory();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        handleDeleteInventory();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        handleRefreshTable();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        // TODO add your handling code here:
        handleImportInventory();
    }//GEN-LAST:event_btnImportActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
        handleExportInventory();
    }//GEN-LAST:event_btnExportActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_txtSearchKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JPanel pnToolbar;
    private javax.swing.JScrollPane spBookTable;
    private javax.swing.JTable tblInventory;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
