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
import com.bookmanagement.Dao.OrderDetailDAO;
import com.bookmanagement.model.Order;
import com.bookmanagement.model.OrderDetail;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JPopupMenu;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;

public class OrderManagementPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(OrderManagementPanel.class.getName());
    private OrderDAO orderDAO;
    private DefaultTableModel tableModel;

    // Các thành phần cho tính năng gợi ý tìm kiếm
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private DefaultListModel<String> suggestionListModel;
    private Timer suggestionTimer; // Dùng để trì hoãn tìm kiếm gợi ý

    public OrderManagementPanel() {
        initComponents();
        setSize(800, 500); // Kích thước mặc định của panel
        this.orderDAO = new OrderDAO();
        initTable();
        initSuggestionFeature(); // Khởi tạo tính năng gợi ý
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
     * Khởi tạo bảng hiển thị đơn hàng.
     */
    private void initTable() {
        String[] columnNames = {"Mã đơn hàng", "Ngày đặt hàng", "Tổng tiền", "Mã khách hàng", "Trạng thái", "Phương thức TT"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Các ô không thể chỉnh sửa trực tiếp
            }
        };
        tblOrder.setModel(tableModel);
    }

    /**
     * Khởi tạo tính năng gợi ý tìm kiếm.
     */
    private void initSuggestionFeature() {
        suggestionListModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionListModel);
        suggestionPopup = new JPopupMenu();
        suggestionPopup.add(new JScrollPane(suggestionList));

        // Listener cho JList khi người dùng chọn một gợi ý
        suggestionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && suggestionList.getSelectedIndex() != -1) {
                    txtSearch.setText(suggestionList.getSelectedValue());
                    suggestionPopup.setVisible(false);
                    search(); // Thực hiện tìm kiếm ngay sau khi chọn gợi ý
                }
            }
        });
        
        // Listener cho JList khi người dùng click chuột vào một gợi ý
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    txtSearch.setText(suggestionList.getSelectedValue());
                    suggestionPopup.setVisible(false);
                    search();
                }
            }
        });

        // Listener cho JTextField để lấy gợi ý khi người dùng nhập
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                startSuggestionTimer();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                startSuggestionTimer();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                startSuggestionTimer();
            }
        });

        // Listener cho JTextField để xử lý phím mũi tên và Enter
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (suggestionPopup.isVisible()) {
                    int selectedIndex = suggestionList.getSelectedIndex();
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        if (selectedIndex < suggestionListModel.size() - 1) {
                            suggestionList.setSelectedIndex(selectedIndex + 1);
                            suggestionList.ensureIndexIsVisible(selectedIndex + 1);
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        if (selectedIndex > 0) {
                            suggestionList.setSelectedIndex(selectedIndex - 1);
                            suggestionList.ensureIndexIsVisible(selectedIndex - 1);
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (selectedIndex != -1) {
                            txtSearch.setText(suggestionList.getSelectedValue());
                            suggestionPopup.setVisible(false);
                            search();
                        } else {
                            // Nếu không có gợi ý nào được chọn, thực hiện tìm kiếm bình thường
                            search();
                            suggestionPopup.setVisible(false);
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        suggestionPopup.setVisible(false);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    search(); // Nếu popup không hiển thị, Enter sẽ tìm kiếm
                }
            }
        });

        // Timer để trì hoãn việc lấy gợi ý, tránh gọi DB quá nhiều
        suggestionTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSuggestions();
            }
        });
        suggestionTimer.setRepeats(false); // Chỉ chạy một lần sau mỗi lần trì hoãn
    }
    
    private void startSuggestionTimer() {
        if (suggestionTimer.isRunning()) {
            suggestionTimer.restart();
        } else {
            suggestionTimer.start();
        }
    }

    /**
     * Cập nhật danh sách gợi ý dựa trên nội dung của txtSearch.
     */
    private void updateSuggestions() {
        String keyword = txtSearch.getText().trim();
        suggestionListModel.clear();

        if (keyword.isEmpty()) {
            suggestionPopup.setVisible(false);
            return;
        }

        List<String> suggestions = orderDAO.getSuggestions(keyword);
        for (String s : suggestions) {
            suggestionListModel.addElement(s);
        }

        if (!suggestionListModel.isEmpty()) {
            // Đặt kích thước popup bằng kích thước của JList
            suggestionList.setVisibleRowCount(Math.min(suggestionListModel.size(), 10)); // Giới hạn số lượng hiển thị
            suggestionPopup.pack();
            
            // Hiển thị popup ngay dưới JTextField
            suggestionPopup.show(txtSearch, 0, txtSearch.getHeight());
        } else {
            suggestionPopup.setVisible(false);
        }
    }

    /**
     * Điền dữ liệu đơn hàng vào bảng.
     */
    public void fillToTable() {
        DefaultTableModel currentTableModel = (DefaultTableModel) tblOrder.getModel();
        currentTableModel.setRowCount(0); // Xóa các hàng hiện có

        try {
            List<Order> allOrders = orderDAO.getAllOrders();
            for (Order order : allOrders) {
                currentTableModel.addRow(new Object[]{
                    order.getOrderID(),
                    order.getOrderDate(),
                    formatCurrency(order.getTotalAmount()),
                    order.getCustomerID(),
                    order.getStatus(),
                    order.getPaymentMethod() // Hiển thị phương thức thanh toán
                });
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu đơn hàng: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cập nhật trạng thái các nút dựa trên lựa chọn bảng.
     */
    private void updateButtonStates() {
        int selectedRow = tblOrder.getSelectedRow();
        boolean isRowSelected = selectedRow != -1;
        
        btnCancel.setEnabled(isRowSelected);
        btnDetail.setEnabled(isRowSelected);
        btnPrint.setEnabled(isRowSelected);
    }

    /**
     * Thực hiện tìm kiếm đơn hàng dựa trên từ khóa trong txtSearch.
     */
    public void search() {
        String searchTerm = txtSearch.getText().trim();
        List<Order> searchResults;
        suggestionPopup.setVisible(false); // Ẩn popup khi thực hiện tìm kiếm chính

        try {
            if (searchTerm.isEmpty()) {
                searchResults = orderDAO.getAllOrders(); // Nếu trống, hiển thị tất cả
            } else {
                searchResults = orderDAO.searchOrder(searchTerm); 
            }

            DefaultTableModel currentTableModel = (DefaultTableModel) tblOrder.getModel();
            currentTableModel.setRowCount(0); // Xóa dữ liệu cũ

            if (searchResults.isEmpty() && !searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng với từ khóa: '" + searchTerm + "'.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

            for (Order order : searchResults) {
                currentTableModel.addRow(new Object[]{
                    order.getOrderID(),
                    order.getOrderDate(),
                    formatCurrency(order.getTotalAmount()),
                    order.getCustomerID(),
                    order.getStatus(),
                    order.getPaymentMethod() // Hiển thị phương thức thanh toán
                });
            }
            updateButtonStates(); // Cập nhật trạng thái nút sau khi tìm kiếm
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm đơn hàng: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mở OrderDialog để tạo đơn hàng mới.
     */
    private void createNewOrder() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        OrderDialog dialog = new OrderDialog(parentFrame, true);
        dialog.setVisible(true);
        // Giả định OrderDialog có phương thức isSucceeded() để kiểm tra xem thao tác có thành công không
        // Bạn cần thêm phương thức này vào OrderDialog nếu chưa có
         if (dialog.isOrderCreatedSuccessfully()) {
            fillToTable(); // Làm mới bảng sau khi tạo thành công
            JOptionPane.showMessageDialog(this, "Đơn hàng đã được tạo thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
         } else {
              JOptionPane.showMessageDialog(this, "Tạo đơn hàng bị hủy hoặc thất bại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
         }
        updateButtonStates(); // Cập nhật trạng thái nút sau khi dialog đóng
    }
    
    /**
     * Hiển thị dialog chi tiết hóa đơn cho đơn hàng đã chọn.
     */
    private void showInvoiceDetail() {
        int selectedRow = tblOrder.getSelectedRow();
        if (selectedRow != -1) {
            String orderId = (String) tableModel.getValueAt(selectedRow, 0); // Lấy ID đơn hàng từ cột đầu tiên
            try {
                Order order = orderDAO.getOrderByOrderId(orderId); // Lấy đối tượng Order từ DAO
                // Cần thêm OrderDetailDAO và phương thức getOrderDetailsByOrderId vào project của bạn
                 List<OrderDetail> orderDetails = (List<OrderDetail>) new OrderDetailDAO().getOrderDetailByOrderId(orderId); 
                
                if (order != null  && orderDetails != null ) { // Bỏ kiểm tra orderDetails tạm thời nếu chưa có
                    // Tạo OrderDetailDialog (cần tạo class này)
                    OrderDialog detailDialog = new OrderDialog(
                        (Frame) SwingUtilities.getWindowAncestor(this), true, order, orderDetails
                     );
                     detailDialog.setVisible(true);
                    JOptionPane.showMessageDialog(this, "Chức năng xem chi tiết đơn hàng cho ID: " + orderId + " đang được phát triển.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
     * In hóa đơn cho đơn hàng đã chọn.
     */
    private void handlePrintOrder() {
        int selectedRow = tblOrder.getSelectedRow();
        if (selectedRow != -1) {
            String orderId = (String) tableModel.getValueAt(selectedRow, 0); // Lấy ID đơn hàng từ cột đầu tiên
            try {
                Order order = orderDAO.getOrderByOrderId(orderId);
                if (order == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng để in.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if ("Đã hủy".equals(order.getStatus())) {
                    JOptionPane.showMessageDialog(this, "Không thể in hóa đơn cho đơn hàng đã hủy.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Giả định bạn có lớp InvoicePrinter với phương thức printInvoice
                // boolean printed = InvoicePrinter.printInvoice(orderId);
                // if (printed) {
                    JOptionPane.showMessageDialog(this, "Đã gửi lệnh in hóa đơn cho đơn hàng " + orderId, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // } else {
                //    JOptionPane.showMessageDialog(this, "Không thể in hóa đơn. Vui lòng kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                // }
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
        DefaultTableModel currentTableModel = (DefaultTableModel) tblOrder.getModel();
        int selectedRow = tblOrder.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để hủy.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String orderId = (String) currentTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) currentTableModel.getValueAt(selectedRow, 4); 

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
        JOptionPane.showMessageDialog(this, "Dữ liệu đã được làm mới", "NOTIFICATION!", JOptionPane.INFORMATION_MESSAGE);
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
            btnPrint.setEnabled(false);
        }
    }//GEN-LAST:event_spBookTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        handlePrintOrder();
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
