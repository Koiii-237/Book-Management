/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.OrderDAO;
import com.bookmanagement.model.Order;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class OrderManagementPanel extends javax.swing.JPanel {

    /**
     * Creates new form BookPanel
     */
    private static final Logger LOGGER = Logger.getLogger(OrderManagementPanel.class.getName());
    private OrderDAO orderDAO;

    public OrderManagementPanel() {
        initComponents();
        setSize(800, 500); // Kích thước mặc định của panel
        this.orderDAO = new OrderDAO();
        initTable();
        fillToTable();
        
        // Thêm ListSelectionListener cho bảng để kích hoạt/vô hiệu hóa các nút
        tblOrder.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
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
     * Initializes the table model for orders.
     */
    private void initTable() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Mã đơn hàng", "Ngày đặt hàng", "Tổng tiền", "Mã khách hàng", "Trạng thái"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Cells are not editable directly
            }
        };
        tblOrder.setModel(model);
    }

    /**
     * Fills the order table with data from the database.
     */
    private void fillToTable() {
        DefaultTableModel tableModel = (DefaultTableModel) tblOrder.getModel();
        tableModel.setRowCount(0); // Clear existing rows

        try {
            ArrayList<Order> allOrders = (ArrayList<Order>) orderDAO.getAllOrders();
            for (Order order : allOrders) {
                tableModel.addRow(new Object[]{
                    order.getOrderID(), // Sử dụng getOrderID()
                    order.getOrderDate(), // Sử dụng getOrderDate()
                    formatCurrency(order.getTotalAmount()), // Sử dụng getTotalAmount() và định dạng tiền tệ
                    order.getCustomerID(), // Sử dụng getCustomerID()
                    order.getStatus() // Sử dụng getStatus()
                });
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu đơn hàng: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Updates the enabled state of buttons based on table selection.
     */
    private void updateButtonStates() {
        int selectedRow = tblOrder.getSelectedRow();
        boolean isRowSelected = selectedRow != -1;
        
        btnCancel.setEnabled(isRowSelected);
        btnDetail.setEnabled(isRowSelected);
        btnPrint.setEnabled(isRowSelected);
    }

    /**
     * Performs a search for orders based on the search term.
     */
    public void search() {
        String searchTerm = txtSearch.getText().trim();
        ArrayList<Order> searchResults;

        try {
            if (searchTerm.isEmpty()) {
                searchResults = (ArrayList<Order>) orderDAO.getAllOrders(); // If empty, display all
            } else {
                // Assuming searchOrder can search by Order ID or Customer ID
                searchResults = orderDAO.searchOrder(searchTerm); 
            }

            DefaultTableModel model = (DefaultTableModel) tblOrder.getModel();
            model.setRowCount(0); // Clear old data

            if (searchResults.isEmpty() && !searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng với từ khóa: '" + searchTerm + "'.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

            for (Order order : searchResults) {
                model.addRow(new Object[]{
                    order.getOrderID(), // Sử dụng getOrderID()
                    order.getOrderDate(), // Sử dụng getOrderDate()
                    formatCurrency(order.getTotalAmount()), // Sử dụng getTotalAmount() và định dạng tiền tệ
                    order.getCustomerID(), // Sử dụng getCustomerID()
                    order.getStatus() // Sử dụng getStatus()
                });
            }
            updateButtonStates(); // Update button states after search
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm đơn hàng: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Opens the OrderDialog to create a new order.
     */
    private void createNewOrder() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        // Đảm bảo OrderDialog là modal và không truyền Order cũ (tạo mới)
        OrderDialog dialog = new OrderDialog(parentFrame, true); 
        dialog.setVisible(true);
        // Giả định OrderDialog có phương thức isSucceeded() để kiểm tra xem thao tác có thành công không
        if (dialog.isSucceeded()) { 
            fillToTable(); // Refresh table after successful creation
            JOptionPane.showMessageDialog(this, "Đơn hàng đã được tạo thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
             JOptionPane.showMessageDialog(this, "Tạo đơn hàng bị hủy hoặc thất bại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
        updateButtonStates(); // Cập nhật trạng thái nút sau khi dialog đóng
    }
    
    /**
     * Displays the invoice detail dialog for the selected order.
     */
    private void showInvoiceDetail() {
        int selectedRow = tblOrder.getSelectedRow();
        if (selectedRow != -1) {
            String orderId = (String) tblOrder.getValueAt(selectedRow, 0); // Lấy ID đơn hàng từ cột đầu tiên
            try {
                Order order = orderDAO.getOrderByOrderId(orderId); // Lấy đối tượng Order từ DAO
                if (order != null) {
                    // Tạo OrderDialog ở chế độ xem chi tiết
                    OrderDialog orderDialog = new OrderDialog((Frame) SwingUtilities.getWindowAncestor(this), order, true); // Truyền đối tượng Order và đặt chế độ xem
                    orderDialog.setVisible(true);
                    // Không cần fillToTable() ở đây vì chế độ xem không thay đổi dữ liệu bảng chính
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng với mã: " + orderId + ". Dữ liệu có thể đã bị xóa hoặc không đồng bộ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi tải chi tiết đơn hàng: " + orderId, ex);
                JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết đơn hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để xem chi tiết.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Prints the invoice for the selected order.
     */
    private void printOrder() {
        int selectedRow = tblOrder.getSelectedRow();
        if (selectedRow != -1) {
            String orderId = (String) tblOrder.getValueAt(selectedRow, 0); // Lấy ID đơn hàng từ cột đầu tiên
            try {
                // Kiểm tra xem đơn hàng có tồn tại và không phải là "Đã hủy" không
                Order order = orderDAO.getOrderByOrderId(orderId);
                if (order == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng để in.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if ("Đã hủy".equals(order.getStatus())) {
                    JOptionPane.showMessageDialog(this, "Không thể in hóa đơn cho đơn hàng đã hủy.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Gọi lớp InvoicePrinter để in hóa đơn
                boolean printed = InvoicePrinter.printInvoice(orderId);
                if (printed) {
                    JOptionPane.showMessageDialog(this, "Đã gửi lệnh in hóa đơn cho đơn hàng " + orderId, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Thông báo đã được xử lý trong InvoicePrinter.printInvoice
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi chuẩn bị in đơn hàng: " + orderId, ex);
                JOptionPane.showMessageDialog(this, "Lỗi khi chuẩn bị in đơn hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để in.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Xử lý chức năng hủy đơn hàng được chọn.
     */
    private void cancelOrder() {
        DefaultTableModel tableModel = (DefaultTableModel) tblOrder.getModel();
        int selectedRow = tblOrder.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để hủy.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String orderId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4); // Giả sử trạng thái ở cột thứ 4

        if ("Đã hủy".equals(currentStatus) || "Đã hoàn thành".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Không thể hủy đơn hàng đã " + currentStatus + ".", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy đơn hàng " + orderId + " không?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = orderDAO.updateOrderStatus(orderId, "Đã hủy"); // Cập nhật trạng thái thành "Đã hủy"
                if (success) {
                    JOptionPane.showMessageDialog(this, "Đơn hàng " + orderId + " đã được hủy thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    fillToTable(); // Làm mới bảng
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể hủy đơn hàng " + orderId + ".", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi hủy đơn hàng: " + orderId, ex);
                JOptionPane.showMessageDialog(this, "Lỗi khi hủy đơn hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Phương thức trợ giúp để định dạng tiền tệ
    private String formatCurrency(java.math.BigDecimal amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(amount);
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
        btnCreateOrder = new javax.swing.JButton();
        btnDetail = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        spBookTable = new javax.swing.JScrollPane();
        tblOrder = new javax.swing.JTable();

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

        btnCreateOrder.setText("CREATE ORDER");
        btnCreateOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateOrderActionPerformed(evt);
            }
        });
        pnToolbar.add(btnCreateOrder);

        btnDetail.setText("DETAIL");
        btnDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailActionPerformed(evt);
            }
        });
        pnToolbar.add(btnDetail);

        btnCancel.setText("CANCEL");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnToolbar.add(btnCancel);

        btnPrint.setText("PRINT");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        pnToolbar.add(btnPrint);

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

        tblOrder.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOrder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrderMouseClicked(evt);
            }
        });
        spBookTable.setViewportView(tblOrder);

        add(spBookTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblOrderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrderMouseClicked
        updateButtonStates();
    }//GEN-LAST:event_tblOrderMouseClicked

    private void btnCreateOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateOrderActionPerformed
        // TODO add your handling code here:
        createNewOrder();
    }//GEN-LAST:event_btnCreateOrderActionPerformed

    private void btnDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailActionPerformed
        showInvoiceDetail();
    }//GEN-LAST:event_btnDetailActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        cancelOrder(); 
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        fillToTable();
        txtSearch.setText(""); // Xóa nội dung tìm kiếm
        updateButtonStates(); 
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void spBookTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spBookTableMouseClicked
        // TODO add your handling code here:
        if (tblOrder.getSelectedRow() != -1) {
            btnCancel.setEnabled(true);
            btnDetail.setEnabled(true);
            btnPrint.setEnabled(true); // Kích hoạt nút in khi có hàng được chọn
        } else {
            btnCancel.setEnabled(false);
            btnDetail.setEnabled(false);
            btnPrint.setEnabled(false); // Vô hiệu hóa nút in khi không có hàng được chọn
        }
    }//GEN-LAST:event_spBookTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        printOrder();
    }//GEN-LAST:event_btnPrintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCreateOrder;
    private javax.swing.JButton btnDetail;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JPanel pnToolbar;
    private javax.swing.JScrollPane spBookTable;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

}
