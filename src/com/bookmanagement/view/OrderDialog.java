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


import com.bookmanagement.Dao.BookManagementDAO;
import com.bookmanagement.Dao.CustomerDAO;
import com.bookmanagement.Dao.InvoiceDAO;
import com.bookmanagement.Dao.OrderDAO;
import com.bookmanagement.Dao.OrderItemDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Customer;
import com.bookmanagement.model.Order;
import com.bookmanagement.model.OrderItem;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.CardLayout; // Import CardLayout
import java.awt.Frame;
import java.awt.GridBagConstraints;
import javax.swing.ImageIcon; // Import ImageIcon
import java.awt.Image;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class OrderDialog extends javax.swing.JDialog {
    private static final Logger LOGGER = Logger.getLogger(OrderDialog.class.getName());

    // DAO
    private final BookManagementDAO bookDAO = new BookManagementDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderDetailDAO = new OrderItemDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    // Data Models
    private final DefaultTableModel cartTableModel;
    private final DefaultComboBoxModel<String> bookComboBoxModel;

    // Danh sách sách trong giỏ hàng
    private final List<OrderItem> cartItems = new ArrayList<>();

    // Tổng tiền của đơn hàng
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Danh sách các sách có trong kho để hiển thị trên combobox
    private List<Book> availableBooks;

    // CardLayout và panel chứa các phương thức thanh toán
    private CardLayout paymentCardLayout;
    private boolean orderCreatedSuccessfully = false;

    public OrderDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);

        cartTableModel = (DefaultTableModel) tblCart.getModel();
        bookComboBoxModel = (DefaultComboBoxModel<String>) cbBook.getModel();

        initModels();
        loadBooksAndCustomers();
        addListeners();
        updateUI();
        initPaymentMethodPanelLogic(); // Khởi tạo logic cho panel phương thức thanh toán
    }

    OrderDialog(Frame frame, boolean b, Order order, List<OrderItem> orderDetails) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void initModels() {
        // Thiết lập model cho bảng giỏ hàng
        String[] columnNames = {"Tên sách", "Số lượng", "Giá", "Tổng"};
        cartTableModel.setColumnIdentifiers(columnNames);
        tblCart.setModel(cartTableModel);

        // Thiết lập model cho combobox sách
        cbBook.setModel(bookComboBoxModel);
    }
    
    public boolean isOrderCreatedSuccessfully() {
        return orderCreatedSuccessfully;
    }
    
    private void loadBooksAndCustomers() {
        try {
            availableBooks = bookDAO.getAllBooks();
            bookComboBoxModel.removeAllElements();
            if (availableBooks != null) {
                for (Book book : availableBooks) {
                    bookComboBoxModel.addElement(book.getBookName());
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu sách", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu sách: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addListeners() {
        // Lắng nghe sự thay đổi của trường nhập số tiền khách đưa (txtCustomerMoney1)
        txtCustomerMoney1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateChange();
            }
        });
        
        txtCustomerMoney.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateChange();
            }
        });
        

        // Lắng nghe sự thay đổi của ComboBox phương thức thanh toán
        cbPaymentMethod.addActionListener(e -> {
            String selectedMethod = (String) cbPaymentMethod.getSelectedItem();
            if ("TRANSFER".equals(selectedMethod)) { // Sử dụng "TRANSFER" như trong initComponents()
                paymentCardLayout.show(pnPaymentDetails, "card3"); // "card3" là tên card cho pnTransfer
            } else { // Mặc định là "CASH"
                paymentCardLayout.show(pnPaymentDetails, "card2"); // "card2" là tên card cho pnCash
            }
        });
    }

    private void updateUI() {
        lblTotalValue.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(totalAmount));
        lblOrderIdValue.setText(UUID.randomUUID().toString().substring(0, 8));
        btnPay.setEnabled(!cartItems.isEmpty());
        btnDeleteBook.setEnabled(tblCart.getSelectedRow() != -1);
        calculateChange(); // Cập nhật tiền thừa khi tổng tiền thay đổi
    }

    private void calculateChange() {
        try {
            BigDecimal customerMoney1 = new BigDecimal(txtCustomerMoney1.getText().replace(",", "").trim());
            BigDecimal customerMoney = new BigDecimal(txtCustomerMoney.getText().replace(",", "").trim());
            BigDecimal change1 = customerMoney1.subtract(totalAmount);
            BigDecimal change = customerMoney.subtract(totalAmount);
            if (change1.compareTo(BigDecimal.ZERO) < 0 || change.compareTo(BigDecimal.ZERO) < 0 ) {
                lblChangeMoneyValue.setForeground(Color.RED);
                lblChangeMoneyValue1.setForeground(Color.RED);
            } else {
                lblChangeMoneyValue1.setForeground(Color.BLACK);
                lblChangeMoneyValue.setForeground(Color.BLACK);
            }
            lblChangeMoneyValue1.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(change));
            lblChangeMoneyValue.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(change));
        } catch (NumberFormatException ex) {
            lblChangeMoneyValue1.setText("...");
            lblChangeMoneyValue1.setForeground(Color.BLACK);
            lblChangeMoneyValue.setText("...");
            lblChangeMoneyValue.setForeground(Color.BLACK);
        }
    }

    private void addBookToCart() {
        int selectedBookIndex = cbBook.getSelectedIndex();
        if (selectedBookIndex == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity = 0;
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
            if (quantity <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book selectedBook = availableBooks.get(selectedBookIndex);
        if (selectedBook.getQuantity() < quantity) {
            JOptionPane.showMessageDialog(this, "Số lượng trong kho không đủ. Chỉ còn " + selectedBook.getQuantity() + " cuốn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cập nhật giỏ hàng
        OrderItem newItem = new OrderItem();
        newItem.setBookID(selectedBook.getBookID());
        newItem.setBookName(selectedBook.getBookName());
        newItem.setQuantity(quantity);
        newItem.setUnitPrice(selectedBook.getPrice());
        newItem.setSubtotal(selectedBook.getPrice().multiply(new BigDecimal(quantity)));
        cartItems.add(newItem);

        // Cập nhật tổng tiền và bảng
        totalAmount = totalAmount.add(newItem.getSubtotal());
        cartTableModel.addRow(new Object[]{
            newItem.getBookName(),
            newItem.getQuantity(),
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(newItem.getUnitPrice()),
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(newItem.getSubtotal())
        });

        // Cập nhật lại UI
        updateUI();
    }

    private void deleteBookFromCart() {
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow != -1) {
            totalAmount = totalAmount.subtract(cartItems.get(selectedRow).getSubtotal());
            cartItems.remove(selectedRow);
            cartTableModel.removeRow(selectedRow);
            updateUI();
        }
    }

    private void performPayment() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String customerId = txtCustomerId.getText().trim();
        String paymentMethod = (String) cbPaymentMethod.getSelectedItem();

        try {
            // Kiểm tra khách hàng
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                // Tạo khách hàng mới nếu không tồn tại
                customer = new Customer(customerId, "Khách vãng lai", "Địa chỉ mặc định", "0000000000");
                customerDAO.addCustomer(customer);
            }

            // Tạo đối tượng Order
            Order newOrder = new Order();
            newOrder.setOrderID(UUID.randomUUID().toString());
            newOrder.setOrderDate(LocalDate.now());
            newOrder.setCustomerID(customerId);
            newOrder.setTotalAmount(totalAmount);
            newOrder.setStatus("Đã thanh toán");
            newOrder.setPaymentMethod(paymentMethod); // Lưu phương thức thanh toán

            // Lưu Order vào database
            boolean orderAdded = orderDAO.insertOrder(newOrder);
            if (!orderAdded) {
                throw new SQLException("Không thể thêm đơn hàng vào database.");
            }

            // Lưu OrderDetails vào database
            for (OrderItem detail : cartItems) {
                detail.setOrderID(newOrder.getOrderID()); // Gán OrderID cho OrderDetail
                orderDetailDAO.insertOrderDetail(detail); // Cần có phương thức này trong OrderDetailDAO
            }

            // Cập nhật số lượng sách trong kho
            for (OrderItem detail : cartItems) {
                Book book = bookDAO.getBookById(detail.getBookID());
                if (book != null) {
                    book.setQuantity(book.getQuantity() - detail.getQuantity());
                    bookDAO.updateBook(book);
                }
            }
            
            JOptionPane.showMessageDialog(this, "Đơn hàng đã được thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            this.orderCreatedSuccessfully = true; // Đặt cờ thành true khi thanh toán thành công
            this.dispose();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xử lý thanh toán", ex);
            this.orderCreatedSuccessfully = false;
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý thanh toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức để khởi tạo logic cho panel phương thức thanh toán
    private void initPaymentMethodPanelLogic() {
        // Lấy CardLayout từ pnPaymentDetails đã được initComponents() tạo
        paymentCardLayout = (CardLayout) pnPaymentDetails.getLayout();

        // Đảm bảo card "CASH" được hiển thị mặc định khi dialog mở
        paymentCardLayout.show(pnPaymentDetails, "card2"); // "card2" là tên card cho pnCash

        // Thiết lập hình ảnh QR Code
        lblQRCode.setIcon(getQRCodeImage());
    }

    // Phương thức giả định để lấy hình ảnh QR code
    private ImageIcon getQRCodeImage() {
        // Trong một ứng dụng thực tế, bạn sẽ tạo mã QR từ thông tin tài khoản ngân hàng.
        // Ví dụ: "Ngân hàng: ABC, STK: 123456, Tên: Nguyen Van A, Số tiền: [totalAmount]"
        // Sử dụng thư viện như ZXing để tạo mã QR từ chuỗi này.
        // Ở đây, chúng ta sẽ sử dụng một placeholder đơn giản.
        // Đảm bảo URL này có thể truy cập được hoặc thay thế bằng hình ảnh QR cục bộ
        String imageUrl = "https://media.istockphoto.com/id/1095468748/vi/vec-to/m%C3%A3-qr-m%E1%BA%ABu-m%C3%A3-v%E1%BA%A1ch-hi%E1%BB%87n-%C4%91%E1%BA%A1i-vector-tr%E1%BB%ABu-t%C6%B0%E1%BB%A3ng-%C4%91%E1%BB%83-qu%C3%A9t-%C4%91i%E1%BB%87n-tho%E1%BA%A1i-th%C3%B4ng-minh-b%E1%BB%8B-c%C3%B4-l%E1%BA%ADp-tr%C3%AAn.jpg?s=612x612&w=0&k=20&c=nCjpoa8qW4lREJGqVCQZsWcrKGOcKKuy5RSsSVzqlL8=zz";
        try {
            ImageIcon qrIcon = new ImageIcon(new java.net.URL(imageUrl));
            Image image = qrIcon.getImage();
            Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải hình ảnh QR Code: " + e.getMessage(), e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnContent = new javax.swing.JPanel();
        pnBookSelection = new javax.swing.JPanel();
        lblBook = new javax.swing.JLabel();
        cbBook = new javax.swing.JComboBox<>();
        txtQuantity = new javax.swing.JTextField();
        btnAddBook = new javax.swing.JButton();
        btnDeleteBook = new javax.swing.JButton();
        tblCart = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        pnPayment = new javax.swing.JPanel();
        lblOrderID = new javax.swing.JLabel();
        cbPaymentMethod = new javax.swing.JComboBox<>();
        txtCustomerId = new javax.swing.JTextField();
        lblCustomerMoney = new javax.swing.JLabel();
        txtCustomerMoney = new javax.swing.JTextField();
        lblChangeMoney = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblCustomerId = new javax.swing.JLabel();
        lblOrderIdValue = new javax.swing.JLabel();
        lblTotalValue = new javax.swing.JLabel();
        lblChangeMoneyValue = new javax.swing.JLabel();
        lblPaymentMethod = new javax.swing.JLabel();
        pnPaymentDetails = new javax.swing.JPanel();
        pnCash = new javax.swing.JPanel();
        lblCustomerMoney1 = new javax.swing.JLabel();
        txtCustomerMoney1 = new javax.swing.JTextField();
        lblChangeMoney1 = new javax.swing.JLabel();
        lblChangeMoneyValue1 = new javax.swing.JLabel();
        pnTransfer = new javax.swing.JPanel();
        lblTitleScan = new javax.swing.JLabel();
        lblQRCode = new javax.swing.JLabel();
        pnButton = new javax.swing.JPanel();
        btnPay = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CREATE NEW ORDER");
        setModal(true);
        setPreferredSize(new java.awt.Dimension(800, 600));
        setResizable(false);

        pnContent.setPreferredSize(new java.awt.Dimension(500, 400));
        pnContent.setLayout(new java.awt.BorderLayout());

        pnBookSelection.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose Book"));
        pnBookSelection.setPreferredSize(new java.awt.Dimension(500, 250));
        pnBookSelection.setLayout(new java.awt.GridBagLayout());

        lblBook.setText("BOOK");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnBookSelection.add(lblBook, gridBagConstraints);

        cbBook.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnBookSelection.add(cbBook, gridBagConstraints);

        txtQuantity.setColumns(20);
        txtQuantity.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnBookSelection.add(txtQuantity, gridBagConstraints);

        btnAddBook.setText("ADD");
        btnAddBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBookActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnBookSelection.add(btnAddBook, gridBagConstraints);

        btnDeleteBook.setText("DELETE");
        btnDeleteBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteBookActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnBookSelection.add(btnDeleteBook, gridBagConstraints);

        tblCart.setModel(new javax.swing.table.DefaultTableModel(
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnBookSelection.add(tblCart, gridBagConstraints);

        jLabel1.setText("QUANTITY: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnBookSelection.add(jLabel1, gridBagConstraints);

        pnContent.add(pnBookSelection, java.awt.BorderLayout.NORTH);

        pnPayment.setBorder(javax.swing.BorderFactory.createTitledBorder("PAY"));
        pnPayment.setMinimumSize(new java.awt.Dimension(700, 500));
        pnPayment.setPreferredSize(new java.awt.Dimension(500, 400));
        pnPayment.setLayout(new java.awt.GridBagLayout());

        lblOrderID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblOrderID.setText("ORDER ID: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(lblOrderID, gridBagConstraints);

        cbPaymentMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CASH", "TRANSFER", " " }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(cbPaymentMethod, gridBagConstraints);

        txtCustomerId.setColumns(15);
        txtCustomerId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustomerIdActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(txtCustomerId, gridBagConstraints);

        lblCustomerMoney.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCustomerMoney.setText("CUSTOMER MONEY: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(lblCustomerMoney, gridBagConstraints);

        txtCustomerMoney.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustomerMoneyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(txtCustomerMoney, gridBagConstraints);

        lblChangeMoney.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblChangeMoney.setText("CHANGE MONEY:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(lblChangeMoney, gridBagConstraints);

        lblTotal.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 51, 51));
        lblTotal.setText("TOTAL MONEY:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(lblTotal, gridBagConstraints);

        lblCustomerId.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblCustomerId.setText("CUSTOMER ID: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(lblCustomerId, gridBagConstraints);

        lblOrderIdValue.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblOrderIdValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblOrderIdValue.setText("AUTO GENERATE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(lblOrderIdValue, gridBagConstraints);

        lblTotalValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalValue.setText("0 VND");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(lblTotalValue, gridBagConstraints);

        lblChangeMoneyValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblChangeMoneyValue.setText("0 VND");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(lblChangeMoneyValue, gridBagConstraints);

        lblPaymentMethod.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblPaymentMethod.setText("PAYMEND METHOD: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnPayment.add(lblPaymentMethod, gridBagConstraints);

        pnPaymentDetails.setLayout(new java.awt.CardLayout());

        pnCash.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblCustomerMoney1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCustomerMoney1.setText("CUSTOMER MONEY: ");
        pnCash.add(lblCustomerMoney1);

        txtCustomerMoney1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustomerMoney1ActionPerformed(evt);
            }
        });
        pnCash.add(txtCustomerMoney1);

        lblChangeMoney1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblChangeMoney1.setText("CHANGE MONEY:");
        pnCash.add(lblChangeMoney1);

        lblChangeMoneyValue1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblChangeMoneyValue1.setText("0 VND");
        pnCash.add(lblChangeMoneyValue1);

        pnPaymentDetails.add(pnCash, "card2");

        pnTransfer.setLayout(new java.awt.BorderLayout());

        lblTitleScan.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblTitleScan.setText("SCAN QR TO TRANSFER");
        pnTransfer.add(lblTitleScan, java.awt.BorderLayout.NORTH);
        pnTransfer.add(lblQRCode, java.awt.BorderLayout.CENTER);

        pnPaymentDetails.add(pnTransfer, "card3");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnPayment.add(pnPaymentDetails, gridBagConstraints);

        pnContent.add(pnPayment, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnContent, java.awt.BorderLayout.CENTER);

        pnButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        btnPay.setText("PAY");
        btnPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayActionPerformed(evt);
            }
        });
        pnButton.add(btnPay);

        btnCancel.setText("CANCEL");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnButton.add(btnCancel);

        getContentPane().add(pnButton, java.awt.BorderLayout.SOUTH);

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

    private void btnAddBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBookActionPerformed
        addBookToCart();
    }//GEN-LAST:event_btnAddBookActionPerformed

    private void btnDeleteBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteBookActionPerformed
       deleteBookFromCart();

    }//GEN-LAST:event_btnDeleteBookActionPerformed

    private void btnPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayActionPerformed
        // TODO add your handling code here:
        performPayment();
    }//GEN-LAST:event_btnPayActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed

        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtCustomerMoneyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerMoneyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerMoneyActionPerformed

    private void txtCustomerIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerIdActionPerformed

    private void txtCustomerMoney1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerMoney1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerMoney1ActionPerformed

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
                OrderDialog dialog = new OrderDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAddBook;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeleteBook;
    private javax.swing.JButton btnPay;
    private javax.swing.JComboBox<String> cbBook;
    private javax.swing.JComboBox<String> cbPaymentMethod;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblBook;
    private javax.swing.JLabel lblChangeMoney;
    private javax.swing.JLabel lblChangeMoney1;
    private javax.swing.JLabel lblChangeMoneyValue;
    private javax.swing.JLabel lblChangeMoneyValue1;
    private javax.swing.JLabel lblCustomerId;
    private javax.swing.JLabel lblCustomerMoney;
    private javax.swing.JLabel lblCustomerMoney1;
    private javax.swing.JLabel lblOrderID;
    private javax.swing.JLabel lblOrderIdValue;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblQRCode;
    private javax.swing.JLabel lblTitleScan;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalValue;
    private javax.swing.JPanel pnBookSelection;
    private javax.swing.JPanel pnButton;
    private javax.swing.JPanel pnCash;
    private javax.swing.JPanel pnContent;
    private javax.swing.JPanel pnPayment;
    private javax.swing.JPanel pnPaymentDetails;
    private javax.swing.JPanel pnTransfer;
    private javax.swing.JTable tblCart;
    private javax.swing.JTextField txtCustomerId;
    private javax.swing.JTextField txtCustomerMoney;
    private javax.swing.JTextField txtCustomerMoney1;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables

}
