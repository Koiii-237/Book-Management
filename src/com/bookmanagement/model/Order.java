package com.bookmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; // Đảm bảo có import này nếu bạn có các thuộc tính List khác

public class Order {

    private int orderId;
    private Integer customerId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscount;
    private Integer promotionId;

    public Order() {
    }

    public Order(int orderId, Integer customerId, LocalDateTime orderDate, String status, BigDecimal totalAmount, BigDecimal totalDiscount, Integer promotionId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.totalDiscount = totalDiscount;
        this.promotionId = promotionId;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }
    
    public BigDecimal getActualAmount() {
        if (totalAmount != null && totalDiscount != null) {
            return totalAmount.subtract(totalDiscount);
        }
        return BigDecimal.ZERO;
    }
    
    public void setActualAmount(BigDecimal actualAmount) {
        if (totalAmount != null && actualAmount != null) {
            this.totalDiscount = totalAmount.subtract(actualAmount);
        } else {
            this.totalDiscount = BigDecimal.ZERO;
        }
    }
    
    public BigDecimal getTotalRevenue() {
        // Tránh lỗi NullPointerException nếu các trường chưa được khởi tạo
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
        if (totalDiscount == null) {
            totalDiscount = BigDecimal.ZERO;
        }
        return totalAmount.subtract(totalDiscount);
    }
    
}
