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
import com.bookmanagement.Dao.OrderDAO;
import com.bookmanagement.Dao.OrderItemDAO;
import com.bookmanagement.Dao.PaymentDAO;
import com.bookmanagement.Dao.PromotionDAO;
import com.bookmanagement.Dao.StockTransactionDAO;
import com.bookmanagement.model.Order;
import com.bookmanagement.model.OrderItem;
import com.bookmanagement.view.OrderDialog; // Giả sử bạn có một OrderDialog để thêm/sửa
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.NumberFormat;
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
import java.time.LocalDate;
import java.util.ArrayList;

public class OrderManagementPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(OrderManagementPanel.class.getName());
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private InventoryDAO inventoryDAO;
    private PaymentDAO paymentDAO;
    private StockTransactionDAO stockTransactionDAO;
    
    private DefaultTableModel tableModel;
    private List<Order> allOrders;

    /**
     * Tạo một panel quản lý đơn hàng mới.
     */
    public OrderManagementPanel() {
        initComponents();
        
        // Khởi tạo các đối tượng DAO
        orderDAO = new OrderDAO();
        orderItemDAO = new OrderItemDAO();
        inventoryDAO = new InventoryDAO();
        paymentDAO = new PaymentDAO();
        stockTransactionDAO = new StockTransactionDAO();
        
        // Khởi tạo danh sách đơn hàng
        allOrders = new ArrayList<>();
        
        // Khởi tạo bảng và nạp dữ liệu ban đầu
        initTable();
        loadOrders();
        
        // Thiết lập các Listener cho các thành phần GUI
        setupListeners();
    }

    /**
     * Khởi tạo các đối tượng DAO và thiết lập mô hình bảng.
     */
    private void initTable() {
        String[] columnNames = {"ID Đơn Hàng", "ID Khách Hàng", "Ngày Đặt", "Tổng Tiền", "Giảm Giá", "Thực Thu", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Không cho phép chỉnh sửa ô trực tiếp trên bảng
                return false;
            }
        };
        tblOrder.setModel(tableModel);
    }
    
    /**
     * Nạp dữ liệu từ database vào bảng
     */
    
    private void loadOrders() {
        allOrders = orderDAO.getAllOrders();
        fillToTable(allOrders);
    }
    
    private void fillToTable(List<Order> orders) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        for (Order order : orders) {
            Object[] row = new Object[]{
                order.getOrderId(),
                order.getCustomerId(),
                order.getOrderDate(),
                currencyFormatter.format(order.getTotalAmount()),
                currencyFormatter.format(order.getTotalDiscount()),
                order.getStatus(),
                order.getPromotionId() != null ? String.valueOf(order.getPromotionId()) : "N/A"
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Cài đặt các listeners cho các thành phần
     */
    private void setupListeners() {

        // Listener để cập nhật các nút khi một hàng trong bảng được chọn
        tblOrder.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tblOrder.getSelectedRow();
                    if (selectedRow != -1) {
                        // Lấy đối tượng Order từ danh sách allOrders
                        Order selectedOrder = allOrders.get(selectedRow);
                        // Cập nhật trạng thái các nút và thông tin chi tiết
                        updateButtonStates(selectedOrder);
                        updateOrderDetails(selectedOrder);
                    } else {
                        // Không có hàng nào được chọn
                        updateButtonStates(null);
                        updateOrderDetails(null);
                    }
                }
            }
        });
        
        // Listener cho ô tìm kiếm để tự động tìm kiếm khi gõ
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // Không cần thiết cho plain text fields
            }
        });
    }

    /**
     * Cập nhật trạng thái các nút dựa trên đơn hàng được chọn.
     * @param order Đơn hàng được chọn, hoặc null nếu không có
     */
    private void updateButtonStates(Order order) {
        boolean isSelected = (order != null);
        btnUpdate.setEnabled(isSelected);
        btnDelete.setEnabled(isSelected);
        
        if (isSelected) {
            String status = order.getStatus();
            // Nút xác nhận chỉ hoạt động khi trạng thái là "Đang xử lý"
            btnConfirm.setEnabled("Đang xử lý".equals(status));
            // Nút hủy hoạt động khi trạng thái là "Đang xử lý" hoặc "Đã xác nhận"
            btnCancel.setEnabled("Đang xử lý".equals(status) || "Đã xác nhận".equals(status));
        } else {
            // Vô hiệu hóa tất cả các nút khi không có đơn hàng nào được chọn
            btnConfirm.setEnabled(false);
            btnCancel.setEnabled(false);
        }
    }
    
    /**
     * Cập nhật thông tin chi tiết đơn hàng
     * @param order Đơn hàng được chọn, hoặc null
     */
    private void updateOrderDetails(Order order) {
        if (order != null) {
            try {
                lblOrderId.setText(String.valueOf(order.getOrderId()));
                lblCustomerId.setText(String.valueOf(order.getCustomerId()));
                lblDate.setText(order.getOrderDate().toString());
                lblStatus.setText(order.getStatus());
                
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                lblTotal.setText(currencyFormatter.format(order.getTotalAmount()));
                lblDiscount.setText(currencyFormatter.format(order.getTotalDiscount()));
                lblActualTotal.setText(currencyFormatter.format(order.getActualAmount()));
                
                // Lấy thông tin chi tiết đơn hàng (các mặt hàng)
                List<OrderItem> items = orderItemDAO.getOrderItemsByOrderId(order.getOrderId());
                // Hiển thị danh sách này (có thể trong một JTable hoặc JList khác)
                // Cập nhật giao diện để hiển thị các chi tiết này
                // Ở đây ta chỉ log ra để đơn giản hóa, trong thực tế sẽ có panel riêng
                LOGGER.log(Level.INFO, "Chi tiết đơn hàng {0}: {1}", new Object[]{order.getOrderId(), items});
            } catch (SQLException ex) {
                Logger.getLogger(OrderManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // Xóa nội dung khi không có đơn hàng nào được chọn
            lblOrderId.setText("");
            lblCustomerId.setText("");
            lblDate.setText("");
            lblStatus.setText("");
            lblTotal.setText("");
            lblDiscount.setText("");
            lblPromotion.setText("");
            lblActualTotal.setText("");
        }
    }

    /**
     * Lọc danh sách đơn hàng dựa trên từ khóa tìm kiếm
     */
    private void search() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0); // Xóa tất cả các hàng

        try {
            List<Order> filteredOrders;
            if (searchText.isEmpty()) {
                filteredOrders = allOrders;
            } else {
                // Lọc danh sách đơn hàng dựa trên ID hoặc tên khách hàng (nếu có)
                filteredOrders = new ArrayList<>();
                for (Order order : allOrders) {
                    if (String.valueOf(order.getOrderId()).toLowerCase().contains(searchText) ||
                        String.valueOf(order.getCustomerId()).toLowerCase().contains(searchText)) {
                        filteredOrders.add(order);
                    }
                }
            }

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            for (Order order : filteredOrders) {
                Object[] rowData = {
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getOrderDate().toString(),
                    currencyFormatter.format(order.getTotalAmount()),
                    currencyFormatter.format(order.getTotalDiscount()),
                    currencyFormatter.format(order.getActualAmount()),
                    order.getStatus()
                };
                tableModel.addRow(rowData);
                loadOrders();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm đơn hàng", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    /**
     * Xác nhận đơn hàng.
     * Cập nhật trạng thái, trừ số lượng tồn kho và tạo giao dịch thanh toán.
     * @param evt 
     */
    
    /**
     * Hủy đơn hàng.
     * Cập nhật trạng thái và hoàn lại số lượng tồn kho nếu cần.
     * @param evt 
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
        spOrderTable = new javax.swing.JScrollPane();
        tblOrder = new javax.swing.JTable();
        pnDetails = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblCustomerId = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        lblOrderId = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblDiscount = new javax.swing.JLabel();
        lblPromotion = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrderDetails = new javax.swing.JTable();
        btnConfirm = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        lblActualTotal = new javax.swing.JLabel();

        lblSearch.setText("Search: ");
        pnToolbar.add(lblSearch);

        txtSearch.setColumns(20);
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
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

        btnAdd.setText("CREATE ORDER");
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

        spOrderTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spOrderTableMouseClicked(evt);
            }
        });

        tblOrder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Order ID", "Customer ID", "Date Create", "Status", "Total", "Discount", "Promotion ID"
            }
        ));
        tblOrder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrderMouseClicked(evt);
            }
        });
        spOrderTable.setViewportView(tblOrder);

        pnDetails.setBackground(new java.awt.Color(102, 102, 102));
        pnDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("ORDER DETAIL"));

        jLabel1.setText("Order ID: ");

        jLabel2.setText("Customer ID: ");

        jLabel3.setText("Date Create: ");

        jLabel4.setText("Status: ");

        jLabel5.setText("Total: ");

        jLabel6.setText("Discount: ");

        jLabel7.setText("Promotion ID: ");

        tblOrderDetails.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblOrderDetails);

        btnConfirm.setText("CONFIRM");
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        btnCancel.setText("CANCEL");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel8.setText("Actual Total: ");

        javax.swing.GroupLayout pnDetailsLayout = new javax.swing.GroupLayout(pnDetails);
        pnDetails.setLayout(pnDetailsLayout);
        pnDetailsLayout.setHorizontalGroup(
            pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 933, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnDetailsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConfirm)
                .addGap(57, 57, 57)
                .addComponent(btnCancel)
                .addGap(135, 135, 135))
            .addGroup(pnDetailsLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(pnDetailsLayout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPromotion))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnDetailsLayout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDiscount))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnDetailsLayout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTotal))
                        .addGroup(pnDetailsLayout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblStatus))
                        .addGroup(pnDetailsLayout.createSequentialGroup()
                            .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(jLabel2)
                                .addComponent(jLabel1))
                            .addGap(18, 18, 18)
                            .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblOrderId)
                                .addComponent(lblCustomerId)
                                .addComponent(lblDate))))
                    .addGroup(pnDetailsLayout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblActualTotal)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnDetailsLayout.setVerticalGroup(
            pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblOrderId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblCustomerId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblDiscount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblPromotion))
                .addGap(7, 7, 7)
                .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblActualTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirm)
                    .addComponent(btnCancel))
                .addContainerGap(52, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnToolbar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 961, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(spOrderTable, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spOrderTable, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(pnDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblOrderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrderMouseClicked

    }//GEN-LAST:event_tblOrderMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        OrderDialog orderDialog = new OrderDialog(parentFrame, true, null);
        orderDialog.setVisible(true);
        loadOrders();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        int selectedRow = tblOrder.getSelectedRow();
        if (selectedRow != -1) {
            Order selectedOrder = allOrders.get(selectedRow);
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            OrderDialog orderDialog = new OrderDialog(parentFrame, true, selectedOrder);
            orderDialog.setVisible(true);
            loadOrders(); // Làm mới bảng sau khi dialog đóng
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblOrder.getSelectedRow();
        if (selectedRow != -1) {
            Order selectedOrder = allOrders.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa đơn hàng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Xóa đơn hàng, các chi tiết đơn hàng liên quan cũng sẽ bị xóa (cascade delete)
                orderDAO.deleteOrder(selectedOrder.getOrderId());
                JOptionPane.showMessageDialog(this, "Đã xóa đơn hàng thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadOrders(); // Làm mới bảng
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        txtSearch.setText("");
        loadOrders();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void spOrderTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spOrderTableMouseClicked

    }//GEN-LAST:event_spOrderTableMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        // TODO add your handling code here:\
        search();
    }//GEN-LAST:event_txtSearchKeyPressed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblOrder.getSelectedRow();
        if (selectedRow != -1) {
            Order selectedOrder = allOrders.get(selectedRow);
            String currentStatus = selectedOrder.getStatus();
            if ("Đang xử lý".equals(currentStatus) || "Đã xác nhận".equals(currentStatus)) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy đơn hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Nếu đơn hàng đã được xác nhận, cần hoàn lại tồn kho
                    if ("Đã xác nhận".equals(currentStatus)) {
                        try {
                            List<OrderItem> orderItems = orderItemDAO.getOrderItemsByOrderId(selectedOrder.getOrderId());
                            for (OrderItem item : orderItems) {
                                try {
                                    // Hoàn lại số lượng sách trong kho
                                    inventoryDAO.increaseInventoryQuantity(item.getBookId(), item.getQuantity());
                                    // Ghi lại giao dịch nhập kho
                                    stockTransactionDAO.addStockTransaction(item.getBookId(), item.getQuantity(), "Hoàn kho cho đơn hàng bị hủy " + selectedOrder.getOrderId());
                                } catch (SQLException ex) {
                                    Logger.getLogger(OrderManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(OrderManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    // Cập nhật trạng thái đơn hàng
                    selectedOrder.setStatus("Đã hủy");
                    orderDAO.updateOrderStatus(selectedOrder.getOrderId(), "Đã hủy");
                    JOptionPane.showMessageDialog(this, "Đã hủy đơn hàng thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadOrders(); // Làm mới bảng
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không thể hủy đơn hàng đã hoàn tất hoặc đã hủy.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để hủy.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblOrder.getSelectedRow();
        if (selectedRow != -1) {
            Order selectedOrder = allOrders.get(selectedRow);
            if ("Đang xử lý".equals(selectedOrder.getStatus())) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xác nhận đơn hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        // Lấy chi tiết đơn hàng để cập nhật tồn kho
                        List<OrderItem> orderItems = orderItemDAO.getOrderItemsByOrderId(selectedOrder.getOrderId());
                        for (OrderItem item : orderItems) {
                            try {
                                // Trừ số lượng sách trong kho
                                inventoryDAO.decreaseInventoryQuantity(item.getBookId(), item.getQuantity());
                                // Ghi lại giao dịch nhập/xuất kho
                                stockTransactionDAO.addStockTransaction(item.getBookId(), -item.getQuantity(), "Xuất kho cho đơn hàng " + selectedOrder.getOrderId());
                            } catch (SQLException ex) {
                                Logger.getLogger(OrderManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        // Cập nhật trạng thái đơn hàng
                        selectedOrder.setStatus("Đã xác nhận");
                        orderDAO.updateOrderStatus(selectedOrder.getOrderId(), "Đã xác nhận");
                        // Tạo một giao dịch thanh toán (payment) mới
                        paymentDAO.createPayment(selectedOrder.getOrderId(), selectedOrder.getActualAmount(), "Tiền mặt"); // Giả sử phương thức thanh toán là "Tiền mặt"
                        JOptionPane.showMessageDialog(this, "Đã xác nhận đơn hàng thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadOrders(); // Làm mới bảng
                    } catch (SQLException ex) {
                        Logger.getLogger(OrderManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Chỉ có thể xác nhận đơn hàng đang xử lý.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để xác nhận.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnConfirmActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblActualTotal;
    private javax.swing.JLabel lblCustomerId;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDiscount;
    private javax.swing.JLabel lblOrderId;
    private javax.swing.JLabel lblPromotion;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnDetails;
    private javax.swing.JPanel pnToolbar;
    private javax.swing.JScrollPane spOrderTable;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTable tblOrderDetails;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

}
