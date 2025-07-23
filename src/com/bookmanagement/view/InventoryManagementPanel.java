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
import com.bookmanagement.Dao.InventoryDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Inventory;
import com.bookmanagement.model.User;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.SwingUtilities;


public class InventoryManagementPanel extends javax.swing.JPanel {

    /**
     * Creates new form BookPanel
     */
    InventoryDAO inventoryDAO;
    private DefaultTableModel tableModel;
    private List<Inventory> currentInventoryList;
    
    public InventoryManagementPanel() {
        initComponents();
        this.inventoryDAO = new InventoryDAO();
        initTable();
        refreshTable();
        
        btnDelete.setEnabled(false);
        btnUpdate.setEnabled(false);
    }
    
    public void initTable(){
        tableModel = new DefaultTableModel(
            new Object[]{"INVENTORY ID", "QUANTITY", "ADDRESS", "BOOK ID"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInventory.setModel(tableModel);
    }
    
    private void displayInventoryInTable(List<Inventory> inventoryToDisplay) {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có
        for (Inventory inventory : inventoryToDisplay) {
            Vector<Object> row = new Vector<>();
            row.add(inventory.getInventoryId());
            row.add(inventory.getQunatity());
            row.add(inventory.getAddress());
            row.add(inventory.getBookId());
            tableModel.addRow(row);
        }
    }
    
    private void addInventory() {
        InventoryManagementDialog dialog = new InventoryManagementDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Inventory newStock = dialog.getStockItem();
            if (newStock != null) {
                try {
                    // Thêm mục tồn kho mới vào cơ sở dữ liệu thông qua DatabaseManager
                    boolean success = inventoryDAO.insert(newStock);
                    if (success) {
                        refreshTable(); // Cập nhật lại bảng (để hiển thị mục mới và đảm bảo currentStockList được cập nhật)
                        JOptionPane.showMessageDialog(this, "Thêm mới tồn kho thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Đây có thể là do trùng lặp ID nếu DatabaseManager có kiểm tra
                        JOptionPane.showMessageDialog(this, "Không thể thêm tồn kho. Có thể mã tồn kho đã tồn tại.", "Lỗi thêm", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi thêm tồn kho vào DB: " + e.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có trong tableModel
        try {
            // Lấy danh sách tồn kho từ DatabaseManager (tức là từ DB)
            currentInventoryList = inventoryDAO.getAll();
            // Hiển thị toàn bộ dữ liệu lên bảng
            displayInventoryInTable(currentInventoryList);
        } catch (Exception e) {
            // Xử lý lỗi nếu có vấn đề khi tải dữ liệu từ DB
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu tồn kho: " + e.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void update() {
        int selectedRow = tblInventory.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một mục tồn kho để sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy Stock ID từ bảng để tìm đối tượng Stock gốc
        String stockIdToEdit = (String) tableModel.getValueAt(selectedRow, 0);
        Inventory selectedStock = null;
        // Tìm đối tượng Stock trong danh sách currentStockList (đã được tải từ DB)
        for (Inventory i : currentInventoryList) {
            if (i.getInventoryId().equals(stockIdToEdit)) {
                selectedStock = i;
                break;
            }
        }

        if (selectedStock == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy mục tồn kho để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo một bản sao của đối tượng Stock để truyền vào dialog,
        // tránh việc dialog sửa đổi trực tiếp đối tượng trong currentStockList trước khi lưu vào DB.
        // Dialog sẽ trả về đối tượng đã sửa đổi.
        Inventory stockToPassToDialog = new Inventory(selectedStock.getInventoryId(), selectedStock.getQunatity(), selectedStock.getAddress(), selectedStock.getBookId());
        InventoryManagementDialog dialog = new InventoryManagementDialog((Frame) SwingUtilities.getWindowAncestor(this), stockToPassToDialog);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                // Cập nhật mục tồn kho vào cơ sở dữ liệu thông qua DatabaseManager
                // Sử dụng đối tượng stockToPassToDialog đã được cập nhật bởi dialog
                boolean success = inventoryDAO.update(stockToPassToDialog);
                if (success) {
                    refreshTable(); // Cập nhật lại bảng (để hiển thị thay đổi và cập nhật currentStockList)
                    JOptionPane.showMessageDialog(this, "Chỉnh sửa tồn kho thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật tồn kho.", "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật tồn kho vào DB: " + e.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void delete() {
        int selectedRow = tblInventory.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một mục tồn kho để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String stockIdToDelete = (String) tableModel.getValueAt(selectedRow, 0); // Lấy Stock ID từ cột đầu tiên

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa mục tồn kho có mã '" + stockIdToDelete + "' này?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Xóa mục tồn kho khỏi cơ sở dữ liệu thông qua DatabaseManager
                boolean success = inventoryDAO.delete(stockIdToDelete);
                if (success) {
                    refreshTable(); // Cập nhật lại bảng (để loại bỏ mục đã xóa và cập nhật currentStockList)
                    JOptionPane.showMessageDialog(this, "Xóa tồn kho thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa tồn kho.", "Lỗi xóa", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa tồn kho khỏi DB: " + e.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    
    public void search(){
       String searchTerm = txtSearch.getText().trim();
        ArrayList<Inventory> searchResults = new ArrayList<>();

        if (searchTerm.isEmpty()) {
           try {
               searchResults = inventoryDAO.getAll();
           } catch (SQLException ex) {
               Logger.getLogger(InventoryManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
           }
        } else {
            searchResults = inventoryDAO.searchInventory(searchTerm);
        }
        
        DefaultTableModel model = (DefaultTableModel) tblInventory.getModel();
        model.setRowCount(0);
        
        if (searchResults.isEmpty() && !searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NO FOUND BOOK WITH: '" + searchTerm + "'.", "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        }

        for (Inventory inventory : searchResults) {
            model.addRow(new Object[]{
                inventory.getInventoryId(),
                inventory.getQunatity(),
                inventory.getAddress(),
                inventory.getBookId(),
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
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnCheck = new javax.swing.JButton();
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

        btnCheck.setText("CHECK");
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });
        pnToolbar.add(btnCheck);

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
        if(tblInventory.getSelectedRow() != -1){
            btnDelete.setEnabled(true);
            btnUpdate.setEnabled(true);
        }
        else{
            btnDelete.setEnabled(false);
            btnUpdate.setEnabled(false);
        }
    }//GEN-LAST:event_tblInventoryMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        addInventory();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_btnCheckActionPerformed

    private void spBookTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spBookTableMouseClicked
        // TODO add your handling code here:
       
    }//GEN-LAST:event_spBookTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        update();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        delete();
    }//GEN-LAST:event_btnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnDelete;
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

