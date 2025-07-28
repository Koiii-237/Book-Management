/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.BookManagementDAO;
import com.bookmanagement.Dao.CustomerDAO;
import com.bookmanagement.Dao.InvoiceDAO;
import com.bookmanagement.Dao.OrderDAO;
import com.bookmanagement.Dao.OrderDetailDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Customer;
import com.bookmanagement.model.Invoice;
import com.bookmanagement.model.Order;
import com.bookmanagement.model.OrderDetail;


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
import java.util.UUID;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class OrderDialog extends javax.swing.JDialog {
    private static final Logger LOGGER = Logger.getLogger(OrderDialog.class.getName());
    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private BookManagementDAO bookManagementDAO;
    private CustomerDAO customerDAO;
    private InvoiceDAO invoiceDAO;

    private DefaultTableModel cartTableModel;
    private List<OrderDetail> cartItems; // Danh sách các mặt hàng trong giỏ hàng
    private BigDecimal totalAmount = BigDecimal.ZERO; // Tổng số tiền của đơn hàng
    private String currentOrderId; // Lưu trữ ID đơn hàng được tạo hoặc tải
    private Order currentOrder; // Đối tượng Order nếu đang xem/chỉnh sửa
    private boolean isViewMode = false; // Chế độ xem (true) hay tạo mới (false)
    private boolean isSucceeded = false; // Biến để kiểm tra thao tác có thành công không

    /**
     * Tạo một OrderDialog mới để tạo đơn hàng mới.
     *
     * @param parent Frame cha của dialog.
     * @param modal  true nếu dialog là modal, false nếu ngược lại.
     */
    public OrderDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initDAOs();
        initTable();
        initComboBox();
        initNewOrder(); // Khởi tạo cho đơn hàng mới
        addTableSelectionListener(); // Thêm trình nghe chọn hàng cho bảng
        addCustomerMoneyDocumentListener(); // Thêm trình nghe cho trường tiền khách đưa
        this.setLocationRelativeTo(parent); // Canh giữa dialog trên màn hình cha
    }

    /**
     * Tạo một OrderDialog mới để xem chi tiết đơn hàng hiện có.
     *
     * @param parent Frame cha của dialog.
     * @param order Đối tượng Order cần xem.
     * @param isViewMode true để đặt dialog ở chế độ chỉ xem.
     */
    public OrderDialog(java.awt.Frame parent, Order order, boolean isViewMode) {
        super(parent, true); // Luôn là modal cho dialog
        initComponents();
        initDAOs();
        initTable();
        this.currentOrder = order; // Gán đối tượng Order được truyền vào
        this.isViewMode = isViewMode; // Đặt chế độ xem
        initExistingOrder(); // Khởi tạo cho đơn hàng hiện có
        if (isViewMode) {
            setReadOnlyMode(); // Vô hiệu hóa các thành phần chỉnh sửa nếu ở chế độ xem
        }
        addTableSelectionListener(); // Vẫn thêm trình nghe để xử lý định dạng hoặc các tương tác khác
        addCustomerMoneyDocumentListener(); // Vẫn thêm trình nghe cho trường tiền khách đưa
        this.setLocationRelativeTo(parent); // Canh giữa dialog trên màn hình cha
    }
    
    /**
     * Trả về true nếu thao tác trong dialog (ví dụ: tạo đơn hàng) thành công.
     * @return true nếu thành công, false nếu bị hủy hoặc thất bại.
     */
    public boolean isSucceeded() {
        return isSucceeded;
    }

    /**
     * Khởi tạo các đối tượng DAO.
     */
    private void initDAOs() {
        orderDAO = new OrderDAO();
        orderDetailDAO = new OrderDetailDAO();
        bookManagementDAO = new BookManagementDAO();
        customerDAO = new CustomerDAO();
        invoiceDAO = new InvoiceDAO();
    }

    /**
     * Khởi tạo cấu trúc bảng giỏ hàng.
     */
    private void initTable() {
        cartTableModel = new DefaultTableModel(
                new Object[]{"Mã sách", "Tên sách", "Số lượng", "Đơn giá", "Thành tiền"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        tblCart.setModel(cartTableModel);
        cartItems = new ArrayList<>();
    }

    /**
     * Khởi tạo ComboBox chọn sách với dữ liệu từ cơ sở dữ liệu.
     */
    private void initComboBox() {
        try {
            List<Book> books = bookManagementDAO.getAllBooks();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (Book book : books) {
                model.addElement(book.getBookID() + " - " + book.getBookName() + " (Tồn: " + book.getQuantity() + ")");
            }
            cbBook.setModel(model);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải danh sách sách vào ComboBox", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách sách: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Khởi tạo giao diện cho việc tạo đơn hàng mới.
     * Tạo ID đơn hàng với tiền tố "DH".
     */
    private void initNewOrder() {
        // Tạo ID đơn hàng mới với tiền tố "DH" và một phần UUID duy nhất
        currentOrderId = "DH" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        lblOrderIdValue.setText(currentOrderId); // Cập nhật hiển thị label
        lblTotalValue.setText(String.valueOf(BigDecimal.ZERO));
        lblChangeMoneyValue.setText(String.valueOf(BigDecimal.ZERO));
        txtCustomerId.setText("");
        txtCustomerMoney.setText("0");
        txtQuantity.setText("1"); // Số lượng mặc định
        cartTableModel.setRowCount(0); // Xóa giỏ hàng
        cartItems.clear(); // Xóa danh sách nội bộ
        totalAmount = BigDecimal.ZERO;
        setCreateMode(); // Đảm bảo các nút được kích hoạt cho chế độ tạo mới
    }

    /**
     * Khởi tạo giao diện cho việc xem đơn hàng hiện có.
     * Tải thông tin đơn hàng và chi tiết đơn hàng.
     */
    private void initExistingOrder() {
        if (currentOrder != null) {
            currentOrderId = currentOrder.getOrderID();
            lblOrderIdValue.setText(currentOrderId); // Cập nhật hiển thị label
            txtCustomerId.setText(currentOrder.getCustomerID());
            lblTotalValue.setText(String.valueOf(currentOrder.getTotalAmount()));
            totalAmount = currentOrder.getTotalAmount(); // Gán tổng tiền từ đơn hàng hiện có
            // Tải chi tiết đơn hàng vào bảng giỏ hàng
            loadOrderDetails(currentOrderId);
            // Cố gắng tải chi tiết hóa đơn nếu có (để hiển thị tiền thừa)
            try {
                Invoice invoice = invoiceDAO.getInvoiceByOrderId(currentOrderId);
                if (invoice != null) {
                    txtCustomerMoney.setText(String.valueOf(invoice.getGuestMoney())); // Hiển thị số tiền khách đưa
                    lblChangeMoneyValue.setText(String.valueOf(invoice.getChange())); // Hiển thị tiền thừa
                } else {
                    txtCustomerMoney.setText("0"); // Nếu không có hóa đơn, mặc định là 0
                    lblChangeMoneyValue.setText(String.valueOf(BigDecimal.ZERO));
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi tải thông tin hóa đơn cho đơn hàng " + currentOrderId, ex);
                // Không hiển thị hộp thoại lỗi, chỉ ghi log và tiếp tục mà không có chi tiết hóa đơn
            }
        } else {
            // Trường hợp này không nên xảy ra nếu OrderManagementPanel truyền một đối tượng Order hợp lệ
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin đơn hàng để hiển thị. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            this.dispose(); // Đóng dialog nếu không có thông tin đơn hàng
        }
    }

    /**
     * Tải chi tiết đơn hàng từ cơ sở dữ liệu vào bảng giỏ hàng.
     *
     * @param orderId ID của đơn hàng cần tải chi tiết.
     */
    private void loadOrderDetails(String orderId) {
        cartTableModel.setRowCount(0); // Xóa các hàng hiện có
        cartItems.clear(); // Xóa danh sách nội bộ
        try {
            List<OrderDetail> details = orderDetailDAO.getOrderDetailByOrderId(orderId);
            for (OrderDetail detail : details) {
                cartItems.add(detail); // Thêm vào danh sách nội bộ
                cartTableModel.addRow(new Object[]{
                    detail.getBookID(),
                    detail.getBookName(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getSubtotal()
                });
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải chi tiết sách của đơn hàng: " + orderId, ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết sách của đơn hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Đặt dialog ở chế độ chỉ đọc (vô hiệu hóa các thành phần chỉnh sửa).
     */
    private void setReadOnlyMode() {
        cbBook.setEnabled(false);
        txtQuantity.setEditable(false);
        btnAddBook.setEnabled(false);
        btnDeleteBook.setEnabled(false);
        txtCustomerId.setEditable(false);
        txtCustomerMoney.setEditable(false);
        btnPay.setEnabled(false);
        // Thay đổi văn bản nút "Hủy" thành "Đóng" khi ở chế độ xem
        btnCancel.setText("Đóng");
    }
    
    /**
     * Đặt dialog ở chế độ tạo mới (kích hoạt các thành phần chỉnh sửa).
     */
    private void setCreateMode() {
        cbBook.setEnabled(true);
        txtQuantity.setEditable(true);
        btnAddBook.setEnabled(true);
        // btnDeleteBook sẽ được quản lý bởi ListSelectionListener
        txtCustomerId.setEditable(true);
        txtCustomerMoney.setEditable(true);
        btnPay.setEnabled(true);
        btnCancel.setText("Hủy"); // Đảm bảo nút là "Hủy" khi tạo mới
    }

    /**
     * Thêm trình nghe chọn hàng cho bảng giỏ hàng.
     */
    private void addTableSelectionListener() {
        tblCart.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // Kích hoạt nút xóa nếu có hàng được chọn và không ở chế độ xem
                    btnDeleteBook.setEnabled(tblCart.getSelectedRow() != -1 && !isViewMode);
                }
            }
        });
        btnDeleteBook.setEnabled(false); // Ban đầu vô hiệu hóa
    }
    
    private void addCustomerMoneyDocumentListener() {
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
    }

    /**
     * Tính toán tiền thừa và cập nhật hiển thị.
     */
    private void calculateChange() {
        if (isViewMode) return; // Không tính toán ở chế độ xem

        try {
            // Loại bỏ dấu phẩy, dấu chấm (nếu có) và tiền tệ trước khi phân tích cú pháp
            String moneyText = txtCustomerMoney.getText().replace(",", "").replace(".", "").replace("₫", "").replace("đ", "").trim();
            BigDecimal customerMoney = new BigDecimal(moneyText.isEmpty() ? "0" : moneyText); // Xử lý trường hợp chuỗi rỗng
            
            BigDecimal change = customerMoney.subtract(totalAmount);
            lblChangeMoneyValue.setText(String.valueOf(change));
        } catch (NumberFormatException e) {
            lblChangeMoneyValue.setText(String.valueOf(BigDecimal.ZERO));
        }
    }

    /**
     * Xử lý logic khi nút "Thêm vào giỏ" được nhấn.
     */
    private void handleAddBookLogic() {
        if (isViewMode) return; // Không cho phép thêm ở chế độ xem

        try {
            // 1. Lấy thông tin sách được chọn và số lượng
            String selectedBookString = (String) cbBook.getSelectedItem();
            if (selectedBookString == null || selectedBookString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String bookId = selectedBookString.split(" - ")[0]; // Lấy ID sách từ chuỗi đã chọn
            int quantityToAdd;
            try {
                quantityToAdd = Integer.parseInt(txtQuantity.getText());
                if (quantityToAdd <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Book book = bookManagementDAO.getBookById(bookId);
            if (book == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Xử lý thêm vào giỏ hàng (cập nhật nếu đã có, thêm mới nếu chưa)
            addOrUpdateCartItem(book, quantityToAdd);

            // 3. Cập nhật tổng số tiền
            updateTotalAmount(); 

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm sách vào giỏ hàng", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm sách vào giỏ hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý logic thêm sách vào giỏ hàng: cập nhật số lượng nếu sách đã có, hoặc thêm mới.
     * @param book Đối tượng sách được chọn.
     * @param quantityToAdd Số lượng muốn thêm.
     * @throws SQLException Nếu có lỗi khi truy vấn cơ sở dữ liệu.
     */
    private void addOrUpdateCartItem(Book book, int quantityToAdd) throws SQLException {
        boolean foundInCart = false;
        for (int i = 0; i < cartItems.size(); i++) {
            OrderDetail existingDetail = cartItems.get(i);
            if (existingDetail.getBookID().equals(book.getBookID())) {
                int newQuantity = existingDetail.getQuantity() + quantityToAdd;
                if (newQuantity > book.getQuantity()) {
                    JOptionPane.showMessageDialog(this, "Số lượng sách trong kho không đủ. Tồn kho: " + book.getQuantity(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                existingDetail.setQuantity(newQuantity);
                existingDetail.setSubtotal(book.getPrice().multiply(new BigDecimal(newQuantity)));
                cartTableModel.setValueAt(newQuantity, i, 2); // Cập nhật số lượng trong bảng
                cartTableModel.setValueAt(String.valueOf(existingDetail.getSubtotal()), i, 4); // Cập nhật thành tiền
                foundInCart = true;
                break;
            }
        }

        if (!foundInCart) {
            if (quantityToAdd > book.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Số lượng sách trong kho không đủ. Tồn kho: " + book.getQuantity(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String orderDetailId = UUID.randomUUID().toString(); // Tạo ID duy nhất cho chi tiết đơn hàng
            BigDecimal subtotal = book.getPrice().multiply(new BigDecimal(quantityToAdd));
            OrderDetail newDetail = new OrderDetail(orderDetailId, quantityToAdd, book.getPrice(), subtotal, currentOrderId, book.getBookName(), book.getBookID()); // Đảm bảo đúng thứ tự constructor
            cartItems.add(newDetail); // Thêm vào danh sách nội bộ
            cartTableModel.addRow(new Object[]{
                book.getBookID(),
                book.getBookName(),
                quantityToAdd,
                book.getPrice(),
                subtotal
            });
        }
    }

    /**
     * Xử lý logic khi nút "Xóa khỏi giỏ" được nhấn.
     */
    private void handleDeleteBookLogic() {
        if (isViewMode) return; // Không cho phép xóa ở chế độ xem

        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách trong giỏ hàng để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa cuốn sách này khỏi giỏ hàng?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.remove(selectedRow); // Xóa khỏi danh sách nội bộ
            cartTableModel.removeRow(selectedRow); // Xóa khỏi bảng
            updateTotalAmount(); // Cập nhật tổng số tiền
        }
    }

    /**
     * Cập nhật tổng số tiền của đơn hàng và hiển thị.
     */
    private void updateTotalAmount() {
        totalAmount = BigDecimal.ZERO;
        for (OrderDetail detail : cartItems) {
            totalAmount = totalAmount.add(detail.getSubtotal());
        }
        lblTotalValue.setText(String.valueOf(totalAmount));
        calculateChange(); // Tính toán lại tiền thừa sau khi tổng tiền thay đổi
    }

    /**
     * Xử lý logic khi nút "Thanh toán" được nhấn.
     */
    private void handlePaymentLogic() {
        if (isViewMode) return; // Không cho phép thanh toán ở chế độ xem

        // 1. Validate inputs
        String customerId = txtCustomerId.getText().trim();
        BigDecimal customerMoney;
        try {
            customerMoney = validatePaymentInputs(customerId, txtCustomerMoney.getText());
            if (customerMoney == null) return; // Validation failed
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền khách đưa không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 2. Xử lý giao dịch thanh toán
            processPaymentTransaction(customerId, customerMoney);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xử lý thanh toán: ", ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý thanh toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validate customer ID and customer money input.
     * @param customerIdText Text from customer ID field.
     * @param customerMoneyText Text from customer money field.
     * @return BigDecimal value of customer money if valid, null otherwise.
     */
    private BigDecimal validatePaymentInputs(String customerIdText, String customerMoneyText) {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống. Vui lòng thêm sách vào đơn hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (customerIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // Loại bỏ dấu phẩy, dấu chấm (nếu có) và tiền tệ trước khi phân tích cú pháp
        String cleanedMoneyText = customerMoneyText.replace(",", "").replace(".", "").replace("₫", "").replace("đ", "").trim();
        BigDecimal customerMoney = new BigDecimal(cleanedMoneyText.isEmpty() ? "0" : cleanedMoneyText);
        
        if (customerMoney.compareTo(totalAmount) < 0) {
            JOptionPane.showMessageDialog(this, "Số tiền khách đưa không đủ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return customerMoney;
    }

    /**
     * Orchestrates the entire payment transaction: order creation, order details insertion,
     * book quantity update, and invoice creation.
     * @param customerId ID of the customer.
     * @param customerMoney Amount of money given by the customer.
     * @throws SQLException If a database error occurs.
     */
    private void processPaymentTransaction(String customerId, BigDecimal customerMoney) throws SQLException {
        // 1. Xác thực khách hàng
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Mã khách hàng không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Tạo đơn hàng
        boolean orderInserted = createAndInsertOrder(customerId, totalAmount);

        if (orderInserted) {
            // 3. Tạo chi tiết đơn hàng và cập nhật số lượng sách
            boolean allDetailsInserted = insertOrderDetailsAndUpdateBooks(currentOrderId);

            // 4. Tạo hóa đơn
            BigDecimal change = customerMoney.subtract(totalAmount);
            boolean invoiceInserted = createAndInsertInvoice(currentOrderId, customerMoney, change);

            if (allDetailsInserted && invoiceInserted) {
                JOptionPane.showMessageDialog(this, "Tạo đơn hàng và thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                isSucceeded = true; // Đặt trạng thái thành công
                this.dispose(); // Đóng dialog đơn hàng
            } else {
                JOptionPane.showMessageDialog(this, "Tạo đơn hàng thành công nhưng có lỗi khi lưu chi tiết hoặc hóa đơn.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không thể tạo đơn hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates and inserts a new Order into the database.
     * @param customerId ID of the customer.
     * @param totalAmount Total amount of the order.
     * @return true if order is inserted successfully, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    private boolean createAndInsertOrder(String customerId, BigDecimal totalAmount) throws SQLException {
        Order newOrder = new Order(currentOrderId, LocalDate.now(), totalAmount, customerId, "Đã hoàn thành"); // Trạng thái mặc định
        return orderDAO.insertOrder(newOrder);
    }

    /**
     * Inserts all order details and updates book quantities in the database.
     * @param orderId ID of the newly created order.
     * @return true if all details are inserted and books updated successfully, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    private boolean insertOrderDetailsAndUpdateBooks(String orderId) throws SQLException {
        boolean allDetailsInserted = true;
        for (OrderDetail detail : cartItems) {
            detail.setOrderID(orderId); // Đảm bảo chi tiết đơn hàng liên kết với đơn hàng mới
            if (!orderDetailDAO.insertOrderDetail(detail)) {
                allDetailsInserted = false;
                LOGGER.log(Level.SEVERE, "Không thể thêm chi tiết đơn hàng: " + detail.getOrderDetailID());
                // Trong ứng dụng thực tế, bạn có thể thực hiện rollback giao dịch ở đây
            } else {
                // Cập nhật số lượng sách trong kho
                Book book = bookManagementDAO.getBookById(detail.getBookID());
                if (book != null) {
                    book.setQuantity(book.getQuantity() - detail.getQuantity());
                    bookManagementDAO.updateBook(book); // Giả sử updateBook xử lý số lượng
                }
            }
        }
        return allDetailsInserted;
    }

    /**
     * Creates and inserts a new Invoice into the database.
     * @param orderId ID of the associated order.
     * @param customerMoney Amount of money given by the customer.
     * @param change Change amount.
     * @return true if invoice is inserted successfully, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    private boolean createAndInsertInvoice(String orderId, BigDecimal customerMoney, BigDecimal change) throws SQLException {
        String invoiceId = "HD" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(); // Tạo ID hóa đơn mới
        Invoice invoice = new Invoice(invoiceId, LocalDate.now(), totalAmount, "Tiền mặt", customerMoney, change, orderId);
        return invoiceDAO.insertInvoice(invoice);
    }

    /**
     * Xử lý logic khi nút "Hủy" được nhấn.
     */
    private void handleCancelLogic() {
        isSucceeded = false; // Đặt trạng thái không thành công nếu hủy
        this.dispose();
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
        txtCustomerId = new javax.swing.JTextField();
        lblCustomerMoney = new javax.swing.JLabel();
        txtCustomerMoney = new javax.swing.JTextField();
        lblChangeMoney = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblCustomerId = new javax.swing.JLabel();
        lblOrderIdValue = new javax.swing.JLabel();
        lblTotalValue = new javax.swing.JLabel();
        lblChangeMoneyValue = new javax.swing.JLabel();
        pnButton = new javax.swing.JPanel();
        btnPay = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CREATE NEW ORDER");
        setModal(true);
        setResizable(false);

        pnContent.setPreferredSize(new java.awt.Dimension(500, 500));
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
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
        pnPayment.setPreferredSize(new java.awt.Dimension(500, 200));
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

        pnContent.add(pnPayment, java.awt.BorderLayout.PAGE_END);

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
        handleAddBookLogic();
    }//GEN-LAST:event_btnAddBookActionPerformed

    private void btnDeleteBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteBookActionPerformed
       handleDeleteBookLogic();

    }//GEN-LAST:event_btnDeleteBookActionPerformed

    private void btnPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayActionPerformed
        // TODO add your handling code here:
        handlePaymentLogic();
    }//GEN-LAST:event_btnPayActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed

      handleCancelLogic();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtCustomerMoneyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerMoneyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerMoneyActionPerformed

    private void txtCustomerIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerIdActionPerformed

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblBook;
    private javax.swing.JLabel lblChangeMoney;
    private javax.swing.JLabel lblChangeMoneyValue;
    private javax.swing.JLabel lblCustomerId;
    private javax.swing.JLabel lblCustomerMoney;
    private javax.swing.JLabel lblOrderID;
    private javax.swing.JLabel lblOrderIdValue;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalValue;
    private javax.swing.JPanel pnBookSelection;
    private javax.swing.JPanel pnButton;
    private javax.swing.JPanel pnContent;
    private javax.swing.JPanel pnPayment;
    private javax.swing.JTable tblCart;
    private javax.swing.JTextField txtCustomerId;
    private javax.swing.JTextField txtCustomerMoney;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables

}
