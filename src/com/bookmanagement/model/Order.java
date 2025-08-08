package com.bookmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List; // Đảm bảo có import này nếu bạn có các thuộc tính List khác

public class Order {
    private String orderID;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private String customerID;
    private String status;
    private String paymentMethod;
    
    public Order() {
    }

    public Order(String orderID, LocalDate orderDate, BigDecimal totalAmount, String customerID, String status, String paymentMethod) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.customerID = customerID;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    
}
