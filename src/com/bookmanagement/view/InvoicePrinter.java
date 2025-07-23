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
import com.bookmanagement.model.Order;
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

    private final Invoice header;
    private final List<OrderDetail> items;

    public InvoicePrinter(String orderId) throws SQLException {
        this.header = new InvoiceDAO().findById(orderId);
        this.items = (List<OrderDetail>) new OrderDetailDAO().findById(orderId);
    }

    @Override
    public int print(Graphics graphics, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g = (Graphics2D) graphics;
        g.translate(pf.getImageableX(), pf.getImageableY());
        g.setFont(new Font("Serif", Font.PLAIN, 12));

        int y = 20;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // --- Header ---
        g.drawString("HÓA ĐƠN BÁN HÀNG", 200, y);
        y += 30;
        g.drawLine(0, y, 500, y);
        y += 20;

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

        g.drawLine(0, y, 500, y);
        y += 20;

        // --- Table header ---
        g.drawString("Sản phẩm", 10, y);
        g.drawString("SL", 200, y);
        g.drawString("Đơn giá", 250, y);
        g.drawString("Thành tiền", 350, y);
        y += 20;
        g.drawLine(0, y, 500, y);
        y += 20;

        // --- Items ---
        BigDecimal sum = BigDecimal.ZERO;
        BookManagementDAO bookDAO = new BookManagementDAO();
        for (OrderDetail od : items) {
            String name = bookDAO.readBookById(od.getBookId()).getBookName();
            int qty = od.getQuantity();
            BigDecimal unit = od.getCoin();
            BigDecimal line = unit.multiply(BigDecimal.valueOf(qty));
            sum = sum.add(line);

            g.drawString(name, 10, y);
            g.drawString(String.valueOf(qty), 200, y);
            g.drawString(fmt.format(unit), 250, y);
            g.drawString(fmt.format(line), 350, y);
            y += 20;
        }

        // --- Footer ---
        y += 10;
        g.drawLine(0, y, 500, y);
        y += 20;
        g.drawString("Tổng cộng: " + fmt.format(sum), 300, y);
        y += 30;
        g.drawString("Cảm ơn quý khách! Hẹn gặp lại.", 10, y);

        return PAGE_EXISTS;
    }

    /**
     * In và trả về true nếu thành công
     */
    public static boolean printInvoice(String orderId) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new InvoicePrinter(orderId));

            // nếu người dùng hủy ở dialog, trả về false ngay
            if (!job.printDialog()) {
                return false;
            }

            // thử in
            job.print();
            return true;

        } catch (PrinterAbortException ex) {
            // Người dùng hủy in
            System.out.println("In hóa đơn bị hủy bởi người dùng.");
            return false;

        } catch (PrinterException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "In hóa đơn thất bại: " + ex.getMessage(),
                    "Lỗi in ấn",
                    JOptionPane.ERROR_MESSAGE);
            return false;

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Không thể tải dữ liệu hóa đơn: " + ex.getMessage(),
                    "Lỗi kết nối",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
