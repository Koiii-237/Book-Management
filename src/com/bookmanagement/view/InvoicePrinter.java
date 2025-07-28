/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.view;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.Dao.BookManagementDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.Dao.InvoiceDAO;
import com.bookmanagement.model.Invoice;
import com.bookmanagement.model.OrderDetail;

import java.awt.*;
import java.awt.print.*;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import com.bookmanagement.Dao.OrderDetailDAO;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class InvoicePrinter implements Printable {

    private static final Logger LOGGER = Logger.getLogger(InvoicePrinter.class.getName());
    private final Invoice header; // Thông tin hóa đơn chính
    private final List<OrderDetail> items; // Danh sách các mặt hàng trong hóa đơn
    private final BookManagementDAO bookDAO; // DAO để truy xuất thông tin sách

    public InvoicePrinter(String orderId) throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        this.bookDAO = new BookManagementDAO(); // Khởi tạo BookManagementDAO

        // Lấy thông tin hóa đơn dựa trên orderId
        this.header = invoiceDAO.getInvoiceByOrderId(orderId);
        if (this.header == null) {
            throw new SQLException("Không tìm thấy hóa đơn cho mã đơn hàng: " + orderId);
        }
        // Lấy chi tiết đơn hàng dựa trên orderId
        this.items = orderDetailDAO.getOrderDetailByOrderId(orderId);
        if (this.items == null || this.items.isEmpty()) {
             throw new SQLException("Không tìm thấy chi tiết đơn hàng cho mã đơn hàng: " + orderId);
        }
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE; // Chỉ in một trang hóa đơn
        }

        Graphics2D g = (Graphics2D) graphics;
        g.translate(pf.getImageableX(), pf.getImageableY()); // Di chuyển gốc tọa độ đến vùng in
        g.setFont(new Font("Serif", Font.PLAIN, 12)); // Đặt font mặc định

        int y = 20; // Vị trí Y bắt đầu vẽ
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Định dạng ngày
        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")); // Định dạng tiền tệ Việt Nam

        // --- Header của hóa đơn ---
        g.setFont(new Font("Serif", Font.BOLD, 16));
        g.drawString("HÓA ĐƠN BÁN HÀNG", (int)pf.getImageableWidth() / 2 - g.getFontMetrics().stringWidth("HÓA ĐƠN BÁN HÀNG") / 2, y); // Căn giữa
        y += 30;
        g.drawLine(0, y, (int)pf.getImageableWidth(), y); // Kẻ đường ngang
        y += 20;

        g.setFont(new Font("Serif", Font.PLAIN, 12));
        g.drawString("Mã HĐ: " + header.getInvoiceId(), 10, y);
        g.drawString("Ngày tạo: " + header.getDateGenerate().format(dtf), 250, y);
        y += 20;

        g.drawString("Tổng tiền: " + fmt.format(header.getMoneyTotal()), 10, y);
        y += 20;
        g.drawString("PT thanh toán: " + header.getPaymentMethod(), 10, y);
        y += 20;
        g.drawString("Tiền khách trả: " + fmt.format(header.getGuestMoney()), 10, y);
        y += 20;
        g.drawString("Tiền thừa: " + fmt.format(header.getChange()), 10, y);
        y += 30;

        g.drawLine(0, y, (int)pf.getImageableWidth(), y);
        y += 20;

        // --- Tiêu đề bảng chi tiết sản phẩm ---
        g.setFont(new Font("Serif", Font.BOLD, 12));
        g.drawString("Sản phẩm", 10, y);
        g.drawString("SL", 180, y);
        g.drawString("Đơn giá", 250, y);
        g.drawString("Thành tiền", 350, y);
        y += 20;
        g.drawLine(0, y, (int)pf.getImageableWidth(), y);
        y += 20;

        // --- Danh sách các mặt hàng ---
        g.setFont(new Font("Serif", Font.PLAIN, 12));
        BigDecimal sum = BigDecimal.ZERO;
        for (OrderDetail od : items) {
            String bookName = "Không tìm thấy sách";
            try {
                // Sử dụng BookManagementDAO đã được khởi tạo để lấy tên sách
                Book b = bookDAO.getBookById(od.getBookID());
                if (b != null) {
                    bookName = od.getBookName(); // Sử dụng bookName đã có trong OrderDetail để tránh truy vấn lại
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Lỗi khi lấy tên sách cho ID: " + od.getBookID(), e);
            }
            
            int qty = od.getQuantity();
            BigDecimal unit = od.getUnitPrice();
            BigDecimal line = od.getSubtotal(); // Sử dụng subtotal đã tính sẵn trong OrderDetail
            sum = sum.add(line);

            g.drawString(bookName, 10, y);
            g.drawString(String.valueOf(qty), 180, y);
            g.drawString(fmt.format(unit), 250, y);
            g.drawString(fmt.format(line), 350, y);
            y += 20;
        }

        // --- Chân hóa đơn ---
        y += 10;
        g.drawLine(0, y, (int)pf.getImageableWidth(), y);
        y += 20;
        g.setFont(new Font("Serif", Font.BOLD, 14));
        g.drawString("Tổng cộng: " + fmt.format(sum), 300, y);
        y += 30;
        g.setFont(new Font("Serif", Font.PLAIN, 12));
        g.drawString("Cảm ơn quý khách! Hẹn gặp lại.", (int)pf.getImageableWidth() / 2 - g.getFontMetrics().stringWidth("Cảm ơn quý khách! Hẹn gặp lại.") / 2, y); // Căn giữa

        return PAGE_EXISTS;
    }

    public static boolean printInvoice(String orderId) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new InvoicePrinter(orderId)); // Khởi tạo InvoicePrinter với orderId

            // Hiển thị hộp thoại in, nếu người dùng hủy thì trả về false
            if (!job.printDialog()) {
                return false;
            }

            // Thực hiện in
            job.print();
            return true;

        } catch (PrinterAbortException ex) {
            // Người dùng hủy in
            LOGGER.log(Level.INFO, "In hóa đơn bị hủy bởi người dùng cho đơn hàng: " + orderId);
            return false;

        } catch (PrinterException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi in hóa đơn cho đơn hàng: " + orderId, ex);
            JOptionPane.showMessageDialog(null,
                    "In hóa đơn thất bại: " + ex.getMessage(),
                    "Lỗi in ấn",
                    JOptionPane.ERROR_MESSAGE);
            return false;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Không thể tải dữ liệu hóa đơn cho đơn hàng: " + orderId, ex);
            JOptionPane.showMessageDialog(null,
                    "Không thể tải dữ liệu hóa đơn: " + ex.getMessage(),
                    "Lỗi kết nối",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
