/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author ADMIN
 */
public class Invoice {
    private String invoiceId;
    private LocalDate dateGenerate;
    private BigDecimal moneyTotal;
    private String paymentMethod;
    private BigDecimal guestMoney;
    private BigDecimal change;
    private String orderId;

    public Invoice() {
    }

    public Invoice(String invoiceId, LocalDate dateGenerate, BigDecimal moneyTotal, String paymentMethod, BigDecimal guestMoney, BigDecimal change, String orderId) {
        this.invoiceId = invoiceId;
        this.dateGenerate = dateGenerate;
        this.moneyTotal = moneyTotal;
        this.paymentMethod = paymentMethod;
        this.guestMoney = guestMoney;
        this.change = change;
        this.orderId = orderId;
    }

    public Invoice(String generateUniqueId, String orderId, LocalDate now, BigDecimal totalAmount, BigDecimal customerMoney, BigDecimal subtract) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDate getDateGenerate() {
        return dateGenerate;
    }

    public void setDateGenerate(LocalDate dateGenerate) {
        this.dateGenerate = dateGenerate;
    }

    public BigDecimal getMoneyTotal() {
        return moneyTotal;
    }

    public void setMoneyTotal(BigDecimal moneyTotal) {
        this.moneyTotal = moneyTotal;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getGuestMoney() {
        return guestMoney;
    }

    public void setGuestMoney(BigDecimal guestMoney) {
        this.guestMoney = guestMoney;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    
}
