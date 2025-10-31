package com.dangc.prm92_pe_phonesstore.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.repository.UserRepository;

import java.util.concurrent.ExecutionException;

public class AuthViewModel extends AndroidViewModel {

    private final UserRepository userRepository;

    // Trạng thái đăng nhập của người dùng
    private final MutableLiveData<User> _loggedInUser = new MutableLiveData<>(null);
    public final LiveData<User> loggedInUser = _loggedInUser;

    // Thông báo cho UI (ví dụ: "Đăng nhập thất bại")
    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>(null);
    public final LiveData<String> toastMessage = _toastMessage;
    
    // Constructor chính cho ứng dụng
    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(
                AppDatabase.getDatabase(application).userDao(),
                application.getApplicationContext()
        );
    }

    // Constructor phụ cho việc test
    public AuthViewModel(@NonNull Application application, @NonNull UserRepository userRepository) {
        super(application);
        this.userRepository = userRepository;
    }

    public void register(String fullName, String email, String password) {
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _toastMessage.setValue("Vui lòng điền đầy đủ thông tin.");
            return;
        }
        User newUser = new User(fullName, email, password);
        userRepository.register(newUser);
        _toastMessage.setValue("Đăng ký thành công!");
    }

    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            _toastMessage.setValue("Vui lòng nhập email và mật khẩu.");
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User user = userRepository.login(email, password).get();
                if (user != null) {
                    userRepository.saveLoginSession(user);
                    _loggedInUser.postValue(user);
                } else {
                    _toastMessage.postValue("Email hoặc mật khẩu không chính xác.");
                }
            } catch (ExecutionException | InterruptedException e) {
                _toastMessage.postValue("Đã có lỗi xảy ra khi đăng nhập.");
            }
        });
    }

    public void logout() {
        userRepository.clearLoginSession();
        _loggedInUser.setValue(null);
    }
}