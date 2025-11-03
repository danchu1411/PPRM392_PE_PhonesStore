package com.dangc.prm92_pe_phonesstore.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.repository.UserRepository;
import com.dangc.prm92_pe_phonesstore.util.ValidationUtil;

import java.util.concurrent.ExecutorService;

public class AuthViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final ExecutorService executorService;

    private final MutableLiveData<User> _loggedInUser = new MutableLiveData<>(null);
    public final LiveData<User> loggedInUser = _loggedInUser;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>(null);
    public final LiveData<String> toastMessage = _toastMessage;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        this.userRepository = new UserRepository(
                db.userDao(),
                application.getApplicationContext(),
                db.databaseWriteExecutor
        );
        this.executorService = db.databaseWriteExecutor;
        checkCurrentUser();
    }

    public AuthViewModel(@NonNull Application application, @NonNull UserRepository userRepository, @NonNull ExecutorService executorService) {
        super(application);
        this.userRepository = userRepository;
        this.executorService = executorService;
        checkCurrentUser();
    }

    public int getCurrentUserId() {
        return userRepository.getCurrentUserId();
    }

    private void checkCurrentUser() {
        if (userRepository.isLoggedIn()) {
            int userId = userRepository.getCurrentUserId();
            LiveData<User> userSource = userRepository.getUserById(userId);
            userSource.observeForever(new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        _loggedInUser.setValue(user);
                        userSource.removeObserver(this);
                    }
                }
            });
        }
    }

    public void register(String fullName, String email, String password) {
        // Make sure all fields have value
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _toastMessage.setValue("Please fill in all fields.");
            return;
        }
        // Check email format
        if (!ValidationUtil.isEmailValid(email)) {
            _toastMessage.setValue("Invalid email format.");
            return;
        }
        // Check password length
        if (!ValidationUtil.isPasswordValid(password)) {
            _toastMessage.setValue("Password must be at least 6 characters long.");
            return;
        }
        User newUser = new User(fullName, email, password);
        userRepository.register(newUser);
        _toastMessage.setValue("Register successfully!");
    }

    public void login(String email, String password, boolean isRememberMeChecked) {
        // Make sure all fields have value
        if (email.isEmpty() || password.isEmpty()) {
            _toastMessage.setValue("Please fill in all fields.");
            return;
        }
        // Check email format
        if (!ValidationUtil.isEmailValid(email)) {
            _toastMessage.setValue("Invalid email format.");
            return;
        }
        // Check password length
        if (!ValidationUtil.isPasswordValid(password)) {
            _toastMessage.setValue("Password must be at least 6 characters long.");
            return;
        }

        executorService.execute(() -> {
            User user = userRepository.login(email, password);
            if (user != null) {
                if (isRememberMeChecked) {
                    userRepository.saveLoginSession(user);
                } else {
                    userRepository.clearLoginSession();
                }
                _toastMessage.postValue("Login successfully!");
                _loggedInUser.postValue(user);
            } else {
                _toastMessage.postValue("Login failed. Please check your credentials.");
            }
        });
    }

    public void updateUser(User userToUpdate, String currentPassword, String newPassword, String confirmPassword) {
        executorService.execute(() -> {
            User existingUser = userRepository.login(userToUpdate.getEmail(), currentPassword);
            // Check if the current password is correct
            if(existingUser == null) {
                _toastMessage.postValue("Current password is incorrect.");
                return;
            }
            // Check if the new password is valid
            if(!newPassword.isEmpty()) {
                if(!newPassword.equals(confirmPassword)) {
                    _toastMessage.postValue("New password and confirm password do not match.");
                    return;
                }
                if(!ValidationUtil.isPasswordValid(newPassword)) {
                    _toastMessage.postValue("New password must be at least 6 characters long.");
                    return;
                }
                userToUpdate.setPassword(newPassword);
            } else {
                userToUpdate.setPassword(currentPassword);
            }
            // Check if the email is valid
            if(!userToUpdate.getFullName().isEmpty()) {
                userToUpdate.setFullName(userToUpdate.getFullName());
            }
            // Update the user
            userRepository.updateUser(userToUpdate);
            _toastMessage.postValue("Update successfully!");
        });
    }

    public void logout() {
        userRepository.clearLoginSession();
        _loggedInUser.setValue(null);
    }
    
    public boolean isUserLoggedIn() {
        return userRepository.isLoggedIn();
    }

    public void doneShowingToast() {
        _toastMessage.setValue(null);
    }
}