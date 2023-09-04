package com.qamp.app.Modal;

public class DeviceContactModal {
    private String name;
    private String phoneNumber;

    public DeviceContactModal(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}