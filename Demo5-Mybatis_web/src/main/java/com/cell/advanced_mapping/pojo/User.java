package com.cell.advanced_mapping.pojo;

public class User {
    private Integer id;
    private String username;
    private UserDetail userDetail; // 一对一关系

    public User(){

    }

    public User(Integer id, String username, UserDetail userDetail) {
        this.id = id;
        this.username = username;
        this.userDetail = userDetail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", userDetail=" + userDetail +
                '}';
    }
}
