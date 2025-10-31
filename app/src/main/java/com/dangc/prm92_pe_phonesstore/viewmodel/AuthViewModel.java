package com.dangc.prm92_pe_phonesstore.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.repository.UserRepository;
import com.dangc.prm92_pe_phonesstore.util.ValidationUtil;

import java.util.concurrent.ExecutorService;

public class AuthViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final ExecutorService executorService;

    private final MutableLiveData<LoginCredentials> loginRequest = new MutableLiveData<>();
    public final LiveData<User> loggedInUser;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>(null);
    public final LiveData<String> toastMessage = _toastMessage;

    private static class LoginCredentials {
        String email;
        String password;
        LoginCredentials(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        this.userRepository = new UserRepository(
                db.userDao(),
                application.getApplicationContext(),
                db.databaseWriteExecutor
        );
        this.executorService = db.databaseWriteExecutor;
        this.loggedInUser = createLoggedInUserLiveData();
    }

    public AuthViewModel(@NonNull Application application, @NonNull UserRepository userRepository, @NonNull ExecutorService executorService) {
        super(application);
        this.userRepository = userRepository;
        this.executorService = executorService;
        this.loggedInUser = createLoggedInUserLiveData();
    }

    private LiveData<User> createLoggedInUserLiveData() {
        return Transformations.switchMap(loginRequest, credentials -> {
            MutableLiveData<User> result = new MutableLiveData<>();
            executorService.execute(() -> {
                User user = userRepository.login(credentials.email, credentials.password);
                if (user != null) {
                    userRepository.saveLoginSession(user);
                    result.postValue(user);
                } else {
                    _toastMessage.postValue("Email hoặc mật khẩu không chính xác.");
                    result.postValue(null);
                }
            });
            return result;
        });
    }

    public void register(String fullName, String email, String password) {
        if (!ValidationUtil.isEmailValid(email)) {
            _toastMessage.setValue("Invalid email address.");
            return;
        }
        if (!ValidationUtil.isPasswordValid(password)) {
            _toastMessage.setValue("Password must be at least 6 characters long.");
            return;
        }
        if (fullName.isEmpty()) {
            _toastMessage.setValue("Full name cannot be empty.");
            return;
        }

        User newUser = new User(fullName, email, password);
        userRepository.register(newUser);
        _toastMessage.setValue("Đăng ký thành công!");
    }

    public void login(String email, String password) {
        if (!ValidationUtil.isEmailValid(email)) {
            _toastMessage.setValue("Invalid email address.");
            return;
        }
        if (password.isEmpty()) {
            _toastMessage.setValue("Password cannot be empty.");
            return;
        }
        loginRequest.setValue(new LoginCredentials(email, password));
    }

    public void logout() {
        userRepository.clearLoginSession();
    }

    public void doneShowingToast() {
        _toastMessage.setValue(null);
    }
}