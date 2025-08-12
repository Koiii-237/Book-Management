/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;


import com.bookmanagement.Dao.CustomerDAO;
import com.bookmanagement.Dao.InventoryDAO;
import com.bookmanagement.Dao.OrderDAO;
import com.bookmanagement.Dao.OrderItemDAO;
import com.bookmanagement.Dao.PaymentDAO;
import com.bookmanagement.Dao.PromotionDAO;
import com.bookmanagement.dao.BookDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Customer;
import com.bookmanagement.model.Order;
import com.bookmanagement.model.OrderItem;
import com.bookmanagement.model.Promotion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.ComboBoxModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class OrderDialog extends javax.swing.JDialog {
    private static final Logger LOGGER = Logger.getLogger(OrderDialog.class.getName());
    private Order currentOrder;
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final BookDAO bookDAO;
    private final CustomerDAO customerDAO;
    private final PromotionDAO promotionDAO;
    private final InventoryDAO inventoryDAO;
    private final PaymentDAO paymentDAO;

    private final DefaultTableModel orderItemTableModel;
    private final Map<Integer, Book> bookMap; // Lưu trữ sách theo ID để truy cập nhanh
    private final Map<Integer, Promotion> promotionMap; // Lưu trữ khuyến mãi theo ID
    private final Map<Integer, Customer> customerMap; // Lưu trữ khách hàng theo ID
    private final List<OrderItem> orderItems;
    

    public OrderDialog(java.awt.Frame parent, boolean modal, Order order) {
        super(parent, modal);
        initComponents();
        
        this.currentOrder = order;
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.bookDAO = new BookDAO();
        this.customerDAO = new CustomerDAO();
        this.promotionDAO = new PromotionDAO();
        this.inventoryDAO = new InventoryDAO();
        this.paymentDAO = new PaymentDAO();

        this.orderItems = new ArrayList<>();
        this.bookMap = new HashMap<>();
        this.promotionMap = new HashMap<>();
        this.customerMap = new HashMap<>();
        
        // Khởi tạo bảng mặt hàng đơn hàng
        orderItemTableModel = (DefaultTableModel) tblProductsInOrder.getModel();
        orderItemTableModel.setColumnIdentifiers(new String[]{"ID Sách", "Tiêu đề", "Giá", "Số lượng", "Tổng"});

        loadDataForComboBoxes();
        
        if (this.currentOrder != null) {
            // Chế độ chỉnh sửa
            fillFormWithOrderData();
        } else {
            // Chế độ thêm mới
            this.currentOrder = new Order();
            // Khởi tạo một đối tượng đơn hàng mới với các giá trị mặc định
            lblStatus.setText("Trạng thái: Đang xử lý");
            txtOrderDate.setText(LocalDateTime.now().toString());
            btnConfirm.setEnabled(false);
            btnCancel.setEnabled(false);
        }
        
        setupListeners();
        updateTotalAmounts();
    }

    
    private void setupListeners() {
        // Thêm listener cho JComboBox khách hàng để cập nhật thông tin
        cbxCustomer.addActionListener(e -> {
            if (cbxCustomer.getSelectedItem() instanceof Customer) {
                Customer selectedCustomer = (Customer) cbxCustomer.getSelectedItem();
                txtCustomerID.setText(String.valueOf(selectedCustomer.getCustomerId()));
            } else {
                 txtCustomerID.setText("");
            }
        });
        
        // Thêm DocumentListener cho trường số lượng
        txtQuantity.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validateQuantity();
            }
            public void removeUpdate(DocumentEvent e) {
                validateQuantity();
            }
            public void insertUpdate(DocumentEvent e) {
                validateQuantity();
            }
        });
    }

    private void loadDataForComboBoxes() {
        try {
            // Tải khách hàng
            List<Customer> customers = customerDAO.searchCustomers("");
            // DefaultComboBoxModel là model của JComboBox, dùng để quản lý dữ liệu trong ComboBox.
            DefaultComboBoxModel<Customer> customerModel = new DefaultComboBoxModel<>();
            customers.forEach(customer -> {
                customerModel.addElement(customer);
                customerMap.put(customer.getCustomerId(), customer);
            });
            cbxCustomer.setModel( (ComboBoxModel) customerModel);
            // Tải sách
            List<Book> books = bookDAO.getAllBooks();
            // Khai báo bookModel là DefaultComboBoxModel
            DefaultComboBoxModel<Book> bookModel = new DefaultComboBoxModel<>();
            books.forEach(book -> {
                bookModel.addElement(book);
                bookMap.put(book.getBookId(), book);
            });
            // Gán bookModel (là ComboBoxModel) vào JComboBox cbxBook
            cbxBook.setModel( (ComboBoxModel) bookModel);
            // Tải khuyến mãi
            List<Promotion> promotions = promotionDAO.getAllActivePromotions();
            DefaultComboBoxModel<Promotion> promoModel = new DefaultComboBoxModel<>();
            promoModel.addElement(null);
            promotions.forEach(promo -> {
                promoModel.addElement(promo);
                promotionMap.put(promo.getPromotionId(), promo);
            });
            cbxPromotion.setModel((ComboBoxModel) promoModel);
        } catch (SQLException ex) {
            Logger.getLogger(OrderDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void fillFormWithOrderData() {
        try {
            lblOrderID.setText("Mã đơn hàng: " + currentOrder.getOrderId());
            lblStatus.setText("Trạng thái: " + currentOrder.getStatus());
            txtOrderDate.setText(currentOrder.getOrderDate().toString());
            Customer customer = customerDAO.getCustomerById(currentOrder.getCustomerId());
            if (customer != null) {
                cbxCustomer.setSelectedItem(customer);
            }
            if (currentOrder.getPromotionId() != 0) {
                Promotion promo = promotionDAO.getPromotionById(currentOrder.getPromotionId());
                if (promo != null) {
                    cbxPromotion.setSelectedItem(promo);
                }
            }
            List<OrderItem> items = orderItemDAO.getOrderItemsByOrderId(currentOrder.getOrderId());
            orderItems.addAll(items);
            items.forEach(this::addRowToTable);
            updateTotalAmounts();
        } catch (SQLException ex) {
            Logger.getLogger(OrderDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addRowToTable(OrderItem item) {
        Book book = bookMap.get(item.getBookId());
        orderItemTableModel.addRow(new Object[]{
            item.getBookId(),
            book != null ? book.getTitle() : "Không tìm thấy",
            formatCurrency(item.getUnitPrice()),
            item.getQuantity(),
            formatCurrency(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
        });
    }
    
    private void clearForm() {
        currentOrder = new Order();
        txtCustomerID.setText("");
        lblOrderID.setText("Mã đơn hàng: (Tự động)");
        txtOrderDate.setText(LocalDateTime.now().toString());
        orderItems.clear();
        orderItemTableModel.setRowCount(0);
        updateTotalAmounts();
        cbxCustomer.setSelectedIndex(0);
        cbxBook.setSelectedIndex(0);
        cbxPromotion.setSelectedIndex(0);
        txtQuantity.setText("");
        lblTotal.setText("Tổng tiền: 0 VND");
        lblTotalDiscount.setText("Giảm giá: 0 VND");
        lblActualTotal.setText("Thanh toán: 0 VND");
        
        btnSave.setEnabled(true);
        btnConfirm.setEnabled(false);
        btnCancel.setEnabled(false);
    }
    
    private void validateQuantity() {
        try {
            int quantity = Integer.parseInt(txtQuantity.getText());
            Book selectedBook = (Book) cbxBook.getSelectedItem();
            if (selectedBook != null) {
                int availableStock = inventoryDAO.getStockQuantity(selectedBook.getBookId());
                if (quantity <= 0 || quantity > availableStock) {
                    btnAdd.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ hoặc vượt quá tồn kho. Tồn kho hiện tại: " + availableStock, "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else {
                    btnAdd.setEnabled(true);
                }
            }
        } catch (NumberFormatException e) {
            btnAdd.setEnabled(false);
        }
    }
    
    private void updateTotalAmounts() {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            totalAmount = totalAmount.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        Promotion selectedPromotion = (Promotion) cbxPromotion.getSelectedItem();
        if (selectedPromotion != null && selectedPromotion.isActive() && promotionDAO.isPromotionActive(selectedPromotion.getPromotionId(), LocalDate.now())) {
            BigDecimal discountPercentage = selectedPromotion.getDiscountPercentage();
            discountAmount = totalAmount.multiply(discountPercentage.divide(new BigDecimal(100)));
        }

        BigDecimal actualAmount = totalAmount.subtract(discountAmount);

        lblTotal.setText("Tổng tiền: " + formatCurrency(totalAmount));
        lblTotalDiscount.setText("Giảm giá: " + formatCurrency(discountAmount));
        lblActualTotal.setText("Thanh toán: " + formatCurrency(actualAmount));
    }
    
    private String formatCurrency(BigDecimal amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(amount);
    }
    
    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {                                     
        try {
            Book selectedBook = (Book) cbxBook.getSelectedItem();
            int quantity = Integer.parseInt(txtQuantity.getText());

            if (selectedBook == null || quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sách và nhập số lượng hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (quantity > inventoryDAO.getStockQuantity(selectedBook.getBookId())) {
                 JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho.", "Lỗi tồn kho", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            
            OrderItem existingItem = orderItems.stream()
                    .filter(item -> item.getBookId() == selectedBook.getBookId())
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                for (int i = 0; i < orderItemTableModel.getRowCount(); i++) {
                    if (orderItemTableModel.getValueAt(i, 0).equals(selectedBook.getBookId())) {
                        orderItemTableModel.setValueAt(existingItem.getQuantity(), i, 3);
                        orderItemTableModel.setValueAt(formatCurrency(existingItem.getUnitPrice().multiply(new BigDecimal(existingItem.getQuantity()))), i, 4);
                        break;
                    }
                }
            } else {
                OrderItem newItem = new OrderItem();
                newItem.setBookId(selectedBook.getBookId());
                newItem.setQuantity(quantity);
                newItem.setUnitPrice(selectedBook.getPrice());
                orderItems.add(newItem);
                addRowToTable(newItem);
            }
            
            updateTotalAmounts();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ hoặc lỗi cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(OrderDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }                                    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cbxBook = new javax.swing.JComboBox<>();
        txtQuantity = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        pnBookSelection = new javax.swing.JPanel();
        lblBook = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtOrderDate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        cbxCustomer = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cbxPromotion = new javax.swing.JComboBox<>();
        txtCustomerID = new javax.swing.JTextField();
        txtPromotion = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblOrderID = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductsInOrder = new javax.swing.JTable();
        btnDelete = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblTotalPrice = new javax.swing.JLabel();
        lblTotalDiscount = new javax.swing.JLabel();
        btnConfirm = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        lblActualTotal = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CREATE NEW ORDER");
        setModal(true);
        setPreferredSize(new java.awt.Dimension(800, 600));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jLabel4.setText("ADD PRODUCT TO ORDER: ");

        jLabel5.setText("Book: ");

        jLabel6.setText("Quantity: ");

        cbxBook.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnAdd.setText("ADD");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(35, 35, 35)
                        .addComponent(cbxBook, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(btnAdd)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbxBook, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        pnBookSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pnBookSelection.setPreferredSize(new java.awt.Dimension(500, 250));

        lblBook.setText("Order ID: ");

        jLabel1.setText("Customer ID: ");

        jLabel2.setText("Date Create: ");

        jLabel3.setText("Status: ");

        lblStatus.setText("Waiting...");

        jLabel7.setText("Promotion ID: ");

        jLabel9.setText("Customer: ");

        jLabel10.setText("Promotion: ");

        lblOrderID.setText("AUTO GENERATE");

        javax.swing.GroupLayout pnBookSelectionLayout = new javax.swing.GroupLayout(pnBookSelection);
        pnBookSelection.setLayout(pnBookSelectionLayout);
        pnBookSelectionLayout.setHorizontalGroup(
            pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnBookSelectionLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(pnBookSelectionLayout.createSequentialGroup()
                        .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(lblBook))
                        .addGap(34, 34, 34)
                        .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtOrderDate, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                            .addComponent(txtCustomerID)
                            .addComponent(txtPromotion, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                            .addComponent(lblOrderID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(62, 62, 62)
                        .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnBookSelectionLayout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 836, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnBookSelectionLayout.createSequentialGroup()
                                .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(54, 54, 54)
                                .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbxCustomer, 0, 256, Short.MAX_VALUE)
                                    .addComponent(cbxPromotion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnBookSelectionLayout.setVerticalGroup(
            pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnBookSelectionLayout.createSequentialGroup()
                .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnBookSelectionLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(lblStatus))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnBookSelectionLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblBook)
                            .addComponent(lblOrderID))
                        .addGap(18, 18, 18)))
                .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnBookSelectionLayout.createSequentialGroup()
                        .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtOrderDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(31, 31, 31)
                        .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCustomerID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel1)))
                    .addComponent(cbxCustomer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnBookSelectionLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(pnBookSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtPromotion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addGroup(pnBookSelectionLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(cbxPromotion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        tblProductsInOrder.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblProductsInOrder);

        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jButton2.setText("DELETE ALL");

        jLabel8.setText("Discount: ");

        lblTotal.setText("Total: ");

        lblTotalPrice.setText("0 VND");

        lblTotalDiscount.setText("0 VND");

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

        jLabel11.setText("PAY: ");

        lblActualTotal.setText("0 VND");

        btnSave.setText("SAVE");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnRefresh.setText("REFRESH");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnBookSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 999, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnDelete)
                        .addGap(57, 57, 57)
                        .addComponent(jButton2)
                        .addGap(102, 102, 102))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSave)
                        .addGap(72, 72, 72)
                        .addComponent(btnConfirm)
                        .addGap(46, 46, 46)
                        .addComponent(btnCancel)
                        .addGap(38, 38, 38)
                        .addComponent(btnRefresh)
                        .addGap(86, 86, 86))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(53, 53, 53)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotalPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTotalDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblActualTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 993, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnBookSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(lblTotalPrice))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblTotalDiscount))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lblActualTotal))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirm)
                    .addComponent(btnCancel)
                    .addComponent(btnSave)
                    .addComponent(btnRefresh))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblBookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBookMouseClicked

    }//GEN-LAST:event_tblBookMouseClicked

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        Customer selectedCustomer = (Customer) cbxCustomer.getSelectedItem();
        Promotion selectedPromotion = (Promotion) cbxPromotion.getSelectedItem();
        
        if (selectedCustomer == null || orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng và thêm ít nhất một mặt hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentOrder.setCustomerId(selectedCustomer.getCustomerId());
        currentOrder.setOrderDate(LocalDateTime.now());
        currentOrder.setStatus("Đang chờ xác nhận");
        currentOrder.setTotalAmount(BigDecimal.ZERO);
        currentOrder.setTotalDiscount(BigDecimal.ZERO);
        currentOrder.setActualAmount(BigDecimal.ZERO);
        if (selectedPromotion != null) {
            currentOrder.setPromotionId(selectedPromotion.getPromotionId());
        }

        boolean success = orderDAO.addOrder(currentOrder);
        if (success) {
            for (OrderItem item : orderItems) {
                item.setOrderId(currentOrder.getOrderId());
                orderItemDAO.addOrderItem(item);
            }
            
            JOptionPane.showMessageDialog(this, "Thêm đơn hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm đơn hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        int cancelResult = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy đơn hàng này?", "Hủy đơn hàng", JOptionPane.YES_NO_OPTION);
        if (cancelResult == JOptionPane.YES_OPTION) {
            currentOrder.setStatus("Đã hủy");
            orderDAO.updateOrder(currentOrder);
            JOptionPane.showMessageDialog(this, "Đơn hàng đã được hủy thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        clearForm();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        // TODO add your handling code here:
        if (currentOrder == null || currentOrder.getOrderId() == 0) {
            JOptionPane.showMessageDialog(this, "Không có đơn hàng để xác nhận.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmResult = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xác nhận đơn hàng này? Thao tác này không thể hoàn tác.", "Xác nhận đơn hàng", JOptionPane.YES_NO_OPTION);
        if (confirmResult == JOptionPane.YES_OPTION) {
            currentOrder.setStatus("Hoàn tất");
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderItem item : orderItems) {
                totalAmount = totalAmount.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            }
            BigDecimal discountAmount = BigDecimal.ZERO;
            Promotion selectedPromotion = (Promotion) cbxPromotion.getSelectedItem();
            if (selectedPromotion != null && selectedPromotion.isActive() && promotionDAO.isPromotionActive(selectedPromotion.getPromotionId(), LocalDate.now())) {
                BigDecimal discountPercentage = selectedPromotion.getDiscountPercentage();
                discountAmount = totalAmount.multiply(discountPercentage.divide(new BigDecimal(100)));
            }
            BigDecimal actualAmount = totalAmount.subtract(discountAmount);
            currentOrder.setTotalAmount(totalAmount);
            currentOrder.setTotalDiscount(discountAmount);
            currentOrder.setActualAmount(actualAmount);
            orderDAO.updateOrder(currentOrder);
            paymentDAO.createPayment(currentOrder.getOrderId(), currentOrder.getActualAmount(), "Tiền mặt");
            for (OrderItem item : orderItems) {
                try {
                    inventoryDAO.decreaseInventoryQuantity(item.getBookId(), item.getQuantity());
                } catch (SQLException ex) {
                    Logger.getLogger(OrderDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            JOptionPane.showMessageDialog(this, "Đơn hàng đã được xác nhận thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:zz
    }//GEN-LAST:event_btnDeleteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(OrderDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                OrderDialog dialog = new OrderDialog(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cbxBook;
    private javax.swing.JComboBox<String> cbxCustomer;
    private javax.swing.JComboBox<String> cbxPromotion;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblActualTotal;
    private javax.swing.JLabel lblBook;
    private javax.swing.JLabel lblOrderID;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalDiscount;
    private javax.swing.JLabel lblTotalPrice;
    private javax.swing.JPanel pnBookSelection;
    private javax.swing.JTable tblProductsInOrder;
    private javax.swing.JTextField txtCustomerID;
    private javax.swing.JTextField txtOrderDate;
    private javax.swing.JTextField txtPromotion;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables

}
