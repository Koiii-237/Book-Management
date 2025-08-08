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

/**
 * Lớp JPanel để quản lý thông tin tồn kho sách.
 * Bao gồm các chức năng: hiển thị danh sách, tìm kiếm, thêm mới, sửa, xóa.
 */
public class InventoryManagementPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(InventoryManagementPanel.class.getName());
    private InventoryDAO inventoryDAO;
    private BookManagementDAO bookManagementDAO; // Cần để lấy tên sách
    private DefaultTableModel tableModel;

    /**
     * Creates new form InventoryManagementPanel
     */
    public InventoryManagementPanel() {
        initComponents();
        setSize(800, 500); // Kích thước mặc định của panel
        this.inventoryDAO = new InventoryDAO();
        this.bookManagementDAO = new BookManagementDAO(); // Khởi tạo BookManagementDAO
        initTable();
        fillToTable();

        // Thêm ListSelectionListener cho bảng để kích hoạt/vô hiệu hóa các nút
        tblInventory.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Đảm bảo sự kiện chỉ kích hoạt một lần khi chọn xong
                    updateButtonStates();
                }
            }
        });
        updateButtonStates(); // Cập nhật trạng thái nút ban đầu
    }

    /**
     * Khởi tạo cấu trúc bảng tồn kho.
     */
    private void initTable() {
        tableModel = new DefaultTableModel(
                new Object[]{"Mã Tồn Kho", "Mã Sách", "Tên Sách", "Số Lượng", "Vị Trí Kho"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        tblInventory.setModel(tableModel);
    }

    /**
     * Đổ dữ liệu từ cơ sở dữ liệu vào bảng tồn kho.
     */
    private void fillToTable() {
        tableModel.setRowCount(0); // Xóa các hàng hiện có

        try {
            ArrayList<Inventory> allInventories = inventoryDAO.getAll();
            for (Inventory inv : allInventories) {
                String bookName = "N/A";
                try {
                    Book book = bookManagementDAO.getBookById(inv.getBookId());
                    if (book != null) {
                        bookName = book.getBookName();
                    }
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, "Không thể lấy tên sách cho mã sách: " + inv.getBookId(), ex);
                }

                tableModel.addRow(new Object[]{
                    inv.getInventoryId(),
                    inv.getBookId(),
                    bookName,
                    inv.getQunatity(), // Lưu ý: getQunatity() có thể là lỗi chính tả, nên là getQuantity()
                    inv.getAddress()
                });
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu tồn kho: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu tồn kho: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật trạng thái kích hoạt/vô hiệu hóa của các nút "SỬA" và "XÓA".
     */
    private void updateButtonStates() {
        int selectedRow = tblInventory.getSelectedRow();
        boolean isRowSelected = selectedRow != -1;

        btnUpdate.setEnabled(isRowSelected);
        btnDelete.setEnabled(isRowSelected);
    }

    /**
     * Thực hiện chức năng tìm kiếm tồn kho dựa trên từ khóa.
     */
    private void performSearch() {
        String searchTerm = txtSearch.getText().trim();
        ArrayList<Inventory> searchResults;

        try {
            if (searchTerm.isEmpty()) {
                searchResults = inventoryDAO.getAll(); // Nếu trống, hiển thị tất cả
            } else {
                // Giả sử searchInventory có thể tìm kiếm theo Mã Sách hoặc Vị Trí Kho
                searchResults = inventoryDAO.searchInventory(searchTerm);
            }

            tableModel.setRowCount(0); // Xóa dữ liệu cũ

            if (searchResults.isEmpty() && !searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy tồn kho với từ khóa: '" + searchTerm + "'.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

            for (Inventory inv : searchResults) {
                String bookName = "N/A";
                try {
                    Book book = bookManagementDAO.getBookById(inv.getBookId());
                    if (book != null) {
                        bookName = book.getBookName();
                    }
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, "Không thể lấy tên sách cho mã sách: " + inv.getBookId(), ex);
                }
                tableModel.addRow(new Object[]{
                    inv.getInventoryId(),
                    inv.getBookId(),
                    bookName,
                    inv.getQunatity(),
                    inv.getAddress()
                });
            }
            updateButtonStates(); // Cập nhật trạng thái nút sau khi tìm kiếm
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm tồn kho: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm tồn kho: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mở InventoryDialog để thêm mới một bản ghi tồn kho.
     */
    private void handleAddNewInventory() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        // Mở dialog ở chế độ thêm mới (truyền null cho inventory và false cho isEditMode)
        InventoryManagementDialog dialog = new InventoryManagementDialog(parentFrame, null, false);
        dialog.setVisible(true);

        if (dialog.isSucceeded()) { // Kiểm tra xem thao tác có thành công không
            fillToTable(); // Làm mới bảng sau khi thêm mới thành công
            JOptionPane.showMessageDialog(this, "Thêm mới tồn kho thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Thao tác thêm mới bị hủy hoặc thất bại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
        updateButtonStates(); // Cập nhật trạng thái nút sau khi dialog đóng
    }

    /**
     * Mở InventoryDialog để chỉnh sửa bản ghi tồn kho được chọn.
     */
    private void handleEditInventory() {
        int selectedRow = tblInventory.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bản ghi tồn kho để sửa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String inventoryId = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            Inventory inventory = inventoryDAO.findById(inventoryId);
            if (inventory != null) {
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                // Mở dialog ở chế độ chỉnh sửa (truyền đối tượng inventory và true cho isEditMode)
                InventoryManagementDialog dialog = new InventoryManagementDialog(parentFrame, inventory, true);
                dialog.setVisible(true);

                if (dialog.isSucceeded()) { // Kiểm tra xem thao tác có thành công không
                    fillToTable(); // Làm mới bảng sau khi sửa thành công
                    JOptionPane.showMessageDialog(this, "Cập nhật tồn kho thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Thao tác cập nhật bị hủy hoặc thất bại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi tồn kho để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải bản ghi tồn kho để sửa: " + inventoryId, ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải bản ghi tồn kho: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        updateButtonStates(); // Cập nhật trạng thái nút sau khi dialog đóng
    }

    /**
     * Xóa bản ghi tồn kho được chọn.
     */
    private void handleDeleteInventory() {
        int selectedRow = tblInventory.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bản ghi tồn kho để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String inventoryId = (String) tableModel.getValueAt(selectedRow, 0);
        String bookName = (String) tableModel.getValueAt(selectedRow, 2); // Tên sách để hiển thị trong thông báo

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa bản ghi tồn kho của sách '" + bookName + "' (Mã Tồn Kho: " + inventoryId + ") không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = inventoryDAO.delete(inventoryId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa tồn kho thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    fillToTable(); // Làm mới bảng
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa tồn kho.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi xóa tồn kho: " + inventoryId, ex);
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa tồn kho: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateButtonStates(); // Cập nhật trạng thái nút sau khi xóa
    }

    /**
     * Làm mới toàn bộ dữ liệu trong bảng.
     */
    private void handleRefreshTable() {
        fillToTable();
        txtSearch.setText(""); // Xóa nội dung tìm kiếm
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
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
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
    }//GEN-LAST:event_btnAddActionPerformed

    private void spBookTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spBookTableMouseClicked
        // TODO add your handling code here:
       
    }//GEN-LAST:event_spBookTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        handleAddNewInventory();
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
    private javax.swing.JTable tblInventory;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}

