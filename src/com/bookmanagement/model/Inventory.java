/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.model;

/**
 *
 * @author ADMIN
 */
public class Inventory {

    private int inventoryId;
    private int bookId;
    private int warehouseId;
    private int quantity;

    public Inventory() {
    }

    public Inventory(int inventoryId, int bookId, int warehouseId, int quantity) {
        this.inventoryId = inventoryId;
        this.bookId = bookId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
