package com.cell.advanced_mapping.pojo;

public class UserDetail {
    private Integer id;
    private String address;
    private String phone;

    public UserDetail(){

    }

    public UserDetail(Integer id, String address, String phone) {
        this.id = id;
        this.address = address;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
