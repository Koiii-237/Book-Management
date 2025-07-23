/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.model;

import java.math.BigDecimal;

/**
 *
 * @author ADMIN
 */
public class Inventory {
    private String inventoryId;
    private int qunatity;
    private String address;
    private String bookId;

    public Inventory() {
    }

    public Inventory(String inventoryId, int qunatity, String address, String bookId) {
        this.inventoryId = inventoryId;
        this.qunatity = qunatity;
        this.address = address;
        this.bookId = bookId;
    }
    
    
    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getQunatity() {
        return qunatity;
    }

    public void setQunatity(int qunatity) {
        this.qunatity = qunatity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
    
    
}
