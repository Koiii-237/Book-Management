/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.service;

import com.bookmanagement.Dao.CustomerDAO;
import com.bookmanagement.Dao.OrderDAO;
import com.bookmanagement.Dao.OrderItemDAO;
import com.bookmanagement.Dao.RevenueDAO;
import com.bookmanagement.dao.BookDAO;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Customer;
import com.bookmanagement.model.Order;
import com.bookmanagement.model.OrderItem;
import com.bookmanagement.model.RevenueReport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp dịch vụ để lấy dữ liệu thống kê doanh thu từ cơ sở dữ liệu.
 * @author ADMIN
 */
public class RevenueService {

     private static final Logger LOGGER = Logger.getLogger(RevenueService.class.getName());
    private final RevenueDAO revenueDAO;
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final BookDAO bookDAO;
    private final CustomerDAO customerDAO;

    public RevenueService() {
        this.revenueDAO = new RevenueDAO();
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.bookDAO = new BookDAO();
        this.customerDAO = new CustomerDAO();
    }
    
    public RevenueReport generateRevenueReport(LocalDate startDate, LocalDate endDate, String category) throws SQLException {
        RevenueReport report = new RevenueReport();

        // Bước 1: Lấy dữ liệu bán hàng chi tiết từ DAO
        List<Map<String, Object>> salesData = revenueDAO.getDetailedSalesData(startDate, endDate, category);
        report.setDetailedSalesData(salesData);
        // Bước 2: Khởi tạo các biến tính toán
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        Map<Integer, BigDecimal> monthlyRevenueData = new HashMap<>();
        Map<String, BigDecimal> revenueByBookData = new HashMap<>();
        Map<String, BigDecimal> revenueByCategoryData = new HashMap<>();
        // Bước 3: Duyệt qua từng bản ghi để tính toán và tổng hợp dữ liệu
        for (Map<String, Object> record : salesData) {
            int quantity = (int) record.get("quantity");
            BigDecimal priceAtSale = (BigDecimal) record.get("priceAtSale");
            BigDecimal discount = (BigDecimal) record.get("discount");
            LocalDate orderDate = (LocalDate) record.get("orderDate");
            String bookTitle = (String) record.get("bookTitle");
            String bookCategory = (String) record.get("category");
            
            // Tính toán tổng doanh thu và tổng chiết khấu
            BigDecimal subtotal = priceAtSale.multiply(BigDecimal.valueOf(quantity));
            BigDecimal discountAmount = subtotal.multiply(discount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalRevenue = totalRevenue.add(subtotal.subtract(discountAmount));
            totalDiscount = totalDiscount.add(discountAmount);
            
            // Tổng hợp doanh thu theo tháng
            int month = orderDate.getMonthValue();
            monthlyRevenueData.merge(month, subtotal.subtract(discountAmount), BigDecimal::add);
            
            // Tổng hợp doanh thu theo sách
            revenueByBookData.merge(bookTitle, subtotal.subtract(discountAmount), BigDecimal::add);
            
            // Tổng hợp doanh thu theo danh mục
            revenueByCategoryData.merge(bookCategory, subtotal.subtract(discountAmount), BigDecimal::add);
        }
        // Bước 4: Đặt các giá trị đã tính toán vào báo cáo
        report.setTotalRevenue(totalRevenue);
        report.setTotalDiscount(totalDiscount);
        // Giả sử lợi nhuận ròng bằng doanh thu trừ chiết khấu, cần chỉnh sửa nếu có thêm chi phí
        report.setNetProfit(totalRevenue.subtract(totalDiscount)); // Cần xem lại công thức tính NetProfit
        report.setMonthlyRevenueData(monthlyRevenueData);
        report.setRevenueByBookData(revenueByBookData);
        report.setRevenueByCategoryData(revenueByCategoryData);

        return report;
    }
    
    
    public List<RevenueReport> getRevenueReport(LocalDate startDate, LocalDate endDate, String category) {
         try {
             List<RevenueReport> reports = new ArrayList<>();
             
             List<Order> orders = orderDAO.getOrdersByDateRange(startDate, endDate);
             for (Order order : orders) {
                 // Lấy các mặt hàng trong đơn hàng để tính toán chi tiết
                 List<OrderItem> orderItems = orderItemDAO.getOrderItemsByOrderId(order.getOrderId());
                 
                 BigDecimal totalAmount = BigDecimal.ZERO;
                 BigDecimal totalDiscount = BigDecimal.ZERO;
                 BigDecimal totalRevenue = BigDecimal.ZERO;
                 BigDecimal totalCost = BigDecimal.ZERO;
                 
                 boolean isCategoryMatch = category.equals("Tất cả");
                 
                 for (OrderItem item : orderItems) {
                     Book book = bookDAO.getBookById(item.getBookId());
                     if (book != null) {
                         if (category.equals("Tất cả") || book.getCategory().equals(category)) {
                             totalAmount = totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                             BigDecimal discountAmountForItem = item.getPrice().multiply(item.getDiscountPercentage()).multiply(BigDecimal.valueOf(item.getQuantity()));
                             totalDiscount = totalDiscount.add(discountAmountForItem);
                             totalRevenue = totalRevenue.add(item.getTotalPrice());

                             // Giả sử giá vốn bằng 70% giá bán để đơn giản hóa
                             BigDecimal costPrice = book.getPrice().multiply(BigDecimal.valueOf(0.7));
                             totalCost = totalCost.add(costPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
                             isCategoryMatch = true; 
                         }
                     }
                 }
                 
                 if (isCategoryMatch) {
                     BigDecimal netProfit = totalRevenue.subtract(totalCost);
                     
                     Customer customer = customerDAO.getCustomerById(order.getCustomerId());
                     String customerName = (customer != null) ? customer.getFullName() : "Khách hàng không tồn tại";

                     RevenueReport report = new RevenueReport(
                             order.getOrderId(),
                             customerName,
                             order.getOrderDate(),
                             totalAmount,
                             totalDiscount,
                             totalRevenue,
                             netProfit
                     );
                     reports.add(report);
                 }
             }
             return reports;
         } catch (SQLException ex) {
             LOGGER.log(Level.SEVERE, "Lỗi khi lấy báo cáo doanh thu", ex);
         }
         return null;
    }
}
