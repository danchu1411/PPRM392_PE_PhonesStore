package com.dangc.prm92_pe_phonesstore.util;

import android.util.Patterns;

public class ValidationUtil {

    /**
     * Kiểm tra xem một chuỗi có phải là định dạng email hợp lệ hay không.
     * @param email Chuỗi cần kiểm tra.
     * @return true nếu hợp lệ, false nếu không.
     */
    public static boolean isEmailValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Patterns.EMAIL_ADDRESS là một regex tiêu chuẩn của Android để kiểm tra email.
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Kiểm tra xem mật khẩu có đủ mạnh hay không (ví dụ: ít nhất 6 ký tự).
     * @param password Mật khẩu cần kiểm tra.
     * @return true nếu hợp lệ, false nếu không.
     */
    public static boolean isPasswordValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        // Ví dụ: yêu cầu mật khẩu có ít nhất 6 ký tự
        return password.length() >= 6;
    }
}