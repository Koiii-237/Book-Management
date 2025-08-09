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
public class Warehouse {

    private int warehouseId;
    private String warehouseName;
    private String location;

    public Warehouse() {
    }

    public Warehouse(int warehouseId, String warehouseName, String location) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.location = location;
    }
    
    // Getters and Setters
    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
