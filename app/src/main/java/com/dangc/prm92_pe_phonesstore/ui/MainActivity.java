package com.dangc.prm92_pe_phonesstore.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dangc.prm92_pe_phonesstore.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ở đây, NavHostFragment trong activity_main.xml sẽ tự động
        // hiển thị Fragment bắt đầu của nav_graph.xml (ProductListFragment).
        // Activity này không cần chứa logic điều hướng trực tiếp nữa.
    }
}