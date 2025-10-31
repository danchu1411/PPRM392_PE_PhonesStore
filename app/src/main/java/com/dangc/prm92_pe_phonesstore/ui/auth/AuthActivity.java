package com.dangc.prm92_pe_phonesstore.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.ui.MainActivity;
import com.dangc.prm92_pe_phonesstore.viewmodel.AuthViewModel;

public class AuthActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Luôn quan sát trạng thái đăng nhập
        authViewModel.loggedInUser.observe(this, user -> {
            if (user != null) {
                // Chỉ điều hướng nếu chúng ta chưa ở trong quá trình điều hướng
                if (!isFinishing()) {
                    navigateToMain();
                }
            }
        });

        // Nếu lúc khởi tạo mà chưa đăng nhập, thì không làm gì cả
        // Giao diện (LoginFragment) sẽ tự hiển thị
    }

    private void navigateToMain() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        // Xóa tất cả các activity trước đó khỏi stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
