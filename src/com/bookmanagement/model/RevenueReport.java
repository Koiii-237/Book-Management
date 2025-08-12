/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.model;

/**
 *
 * @author ADMIN
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RevenueReport {
    private int orderId;
    private String customerName;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscount;
    private BigDecimal totalRevenue;
    private BigDecimal netProfit;
    
    // Dữ liệu cho các biểu đồ
    private Map<Integer, BigDecimal> monthlyRevenueData;
    private Map<String, BigDecimal> revenueByBookData;
    private Map<String, BigDecimal> revenueByCategoryData;
    
    private List<Map<String, Object>> detailedSalesData;

    public RevenueReport(int orderId, String customerName, LocalDate orderDate, BigDecimal totalAmount, BigDecimal totalDiscount, BigDecimal totalRevenue, BigDecimal netProfit) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.totalDiscount = totalDiscount;
        this.totalRevenue = totalRevenue;
        this.netProfit = netProfit;
    }

    public RevenueReport() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }

    public Map<Integer, BigDecimal> getMonthlyRevenueData() {
        return monthlyRevenueData;
    }

    public void setMonthlyRevenueData(Map<Integer, BigDecimal> monthlyRevenueData) {
        this.monthlyRevenueData = monthlyRevenueData;
    }

    public Map<String, BigDecimal> getRevenueByBookData() {
        return revenueByBookData;
    }

    public void setRevenueByBookData(Map<String, BigDecimal> revenueByBookData) {
        this.revenueByBookData = revenueByBookData;
    }

    public Map<String, BigDecimal> getRevenueByCategoryData() {
        return revenueByCategoryData;
    }

    public void setRevenueByCategoryData(Map<String, BigDecimal> revenueByCategoryData) {
        this.revenueByCategoryData = revenueByCategoryData;
    }

    public List<Map<String, Object>> getDetailedSalesData() {
        return detailedSalesData;
    }

    public void setDetailedSalesData(List<Map<String, Object>> detailedSalesData) {
        this.detailedSalesData = detailedSalesData;
    }
    
    
    
}
