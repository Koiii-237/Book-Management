/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.model;

/**
 *
 * @author ADMIN
 */
public class Setting {

    private int settingId;
    private String settingName;
    private String settingValue;

    public Setting() {
    }

    public Setting(int settingId, String settingName, String settingValue) {
        this.settingId = settingId;
        this.settingName = settingName;
        this.settingValue = settingValue;
    }

    // Getters and Setters
    public int getSettingId() {
        return settingId;
    }

    public void setSettingId(int settingId) {
        this.settingId = settingId;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
}
