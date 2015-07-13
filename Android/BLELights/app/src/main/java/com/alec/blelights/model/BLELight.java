package com.alec.blelights.model;

import java.io.Serializable;

/**
 * Created by alec on 7/12/15.
 */
public class BLELight implements Serializable {

    private String name, address, nickname;
    private boolean isPrimary;

    public BLELight(String name, String address, String nickname, boolean isPrimary) {
        this.name = name;
        this.address = address;
        this.nickname = nickname;
        this.isPrimary = isPrimary;
    }

    public BLELight(String name, String address, boolean isPrimary) {
        this(name, address, name, isPrimary);
    }

    public BLELight(String name, String address) {
        this(name, address, false);
    }

    public BLELight(String name, String address, String nickname) {
        this(name, address, nickname, false);
    }

    public BLELight() {
        this("", "", "", false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    @Override
    public String toString() {
        return "BLELight{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", nickname='" + nickname + '\'' +
                ", isPrimary=" + isPrimary +
                '}';
    }
}
