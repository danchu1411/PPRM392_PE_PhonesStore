package com.dangc.prm92_pe_phonesstore.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore; // THÊM IMPORT NÀY

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "full_name")
    private String fullName;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "role") // ĐÃ CÓ
    private String role; // ĐÃ CÓ

    // Constructors
    // Constructor khi đăng ký (User mặc định, Admin sẽ được tạo riêng)
    @Ignore // THÊM DÒNG NÀY ĐỂ ROOM BỎ QUA CONSTRUCTOR NÀY KHI ĐỌC TỪ DB
    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = "User"; // Mặc định là "User" khi đăng ký
    }

    // Constructor đầy đủ (cho Room khi đọc hoặc Update, có role)
    // Room sẽ tự động chọn constructor này vì nó có tất cả các trường, bao gồm cả PrimaryKey
    public User(int userId, String fullName, String email, String password, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role; // Thêm role vào đây
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
}