package com.bookmanagement.model;

import java.math.BigDecimal;

public class OrderDetail {

    private String orderDetailID;
    private int quantity;
    private BigDecimal unitPrice; // Giá tại thời điểm đặt hàng
    private BigDecimal subtotal;  // quantity * unitPrice
    private String orderID;
    private String bookID;
    private String bookName; // Thêm để hiển thị trong bảng mà không cần truy vấn lại sách

    // Constructors
    public OrderDetail() {
    }

    public OrderDetail(String orderDetailID, int quantity, BigDecimal unitPrice, BigDecimal subtotal, String orderID, String bookID, String bookName) {
        this.orderDetailID = orderDetailID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
        this.orderID = orderID;
        this.bookID = bookID;
        this.bookName = bookName;
    }

    // Getters and Setters
    public String getOrderDetailID() {
        return orderDetailID;
    }

    public void setOrderDetailID(String orderDetailID) {
        this.orderDetailID = orderDetailID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
