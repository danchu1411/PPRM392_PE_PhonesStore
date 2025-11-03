package com.dangc.prm92_pe_phonesstore.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.prefs.UserPreferences;
import com.dangc.prm92_pe_phonesstore.data.repository.UserRepository;
import com.dangc.prm92_pe_phonesstore.util.ValidationUtil; // Đã sửa: utils -> util

import java.util.concurrent.ExecutorService;

public class AuthViewModel extends AndroidViewModel {

    private static final String TAG = "AuthViewModel";

    private final UserRepository userRepository;
    private final UserPreferences userPreferences; // BIẾN MỚI, cần cho Remember Me
    private final ExecutorService executorService;

    private final MutableLiveData<User> _loggedInUser = new MutableLiveData<>();
    public final LiveData<User> loggedInUser = _loggedInUser;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public final LiveData<String> toastMessage = _toastMessage;

    // Constructor chính (nếu dùng ViewModelFactory)
    public AuthViewModel(@NonNull Application application, @NonNull UserRepository userRepository, @NonNull UserPreferences userPreferences, @NonNull ExecutorService executorService) {
        super(application);
        this.userRepository = userRepository;
        this.userPreferences = userPreferences; // Khởi tạo UserPreferences
        this.executorService = executorService;

        // Kiểm tra đăng nhập tự động khi VM khởi tạo
        if (userPreferences.getRememberMeStatus()) { // Chỉ tự động đăng nhập nếu Remember Me được tích
            checkCurrentUser();
        } else {
            _loggedInUser.setValue(null); // Nếu Remember Me tắt, coi như không có ai đăng nhập
        }
    }

    // Constructor mặc định (sẽ sử dụng nếu bạn không tạo ViewModelFactory)
    public AuthViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        this.userPreferences = new UserPreferences(application.getApplicationContext()); // Khởi tạo UserPreferences
        this.userRepository = new UserRepository(
                application.getApplicationContext(),
                db.userDao(),
                db.databaseWriteExecutor,
                this.userPreferences // Truyền UserPreferences đã khởi tạo vào UserRepository
        );
        this.executorService = db.databaseWriteExecutor;

        if (userPreferences.getRememberMeStatus()) {
            checkCurrentUser();
        } else {
            _loggedInUser.setValue(null);
        }
    }

    public void register(String fullName, String email, String password) {
        executorService.execute(() -> {
            if (fullName.isEmpty()) {
                _toastMessage.postValue("Tên đầy đủ không được để trống.");
                return;
            }
            if (!ValidationUtil.isEmailValid(email)) {
                _toastMessage.postValue("Địa chỉ email không hợp lệ.");
                return;
            }
            if (!ValidationUtil.isPasswordValid(password)) {
                _toastMessage.postValue("Mật khẩu phải có ít nhất 6 ký tự.");
                return;
            }

            User existingUser = userRepository.findByEmail(email);
            if (existingUser != null) {
                _toastMessage.postValue("Email đã được đăng ký.");
                return;
            }

            User newUser = new User(fullName, email, password); // Constructor này gán role = "User"
            long id = userRepository.register(newUser);
            if (id > 0) {
                _toastMessage.postValue("Đăng ký thành công!");
            } else {
                _toastMessage.postValue("Đăng ký thất bại.");
            }
        });
    }

    // Cập nhật phương thức login để nhận tham số rememberMe
    public void login(String email, String password, boolean rememberMe) {
        executorService.execute(() -> {
            if (!ValidationUtil.isEmailValid(email)) {
                _toastMessage.postValue("Địa chỉ email không hợp lệ.");
                return;
            }
            if (password.isEmpty()) {
                _toastMessage.postValue("Mật khẩu không được để trống.");
                return;
            }

            User user = userRepository.login(email, password);
            if (user != null) {
                _loggedInUser.postValue(user); // Đảm bảo User có role được post lên
                userPreferences.saveCurrentUser(user.getUserId());
                userPreferences.setRememberMeStatus(rememberMe); // Lưu trạng thái rememberMe
                _toastMessage.postValue("Đăng nhập thành công!");
            } else {
                _toastMessage.postValue("Email hoặc mật khẩu không đúng.");
            }
        });
    }

    public void logout() {
        userPreferences.clearCurrentUser();
        _loggedInUser.setValue(null);
        _toastMessage.setValue("Đã đăng xuất.");
    }

    // Cập nhật checkCurrentUser để lấy User đầy đủ và đặt vào LiveData
    public void checkCurrentUser() {
        executorService.execute(() -> {
            int userId = userPreferences.getCurrentUserId(); // Phương thức này đã có logic rememberMe
            if (userId != UserPreferences.NO_USER_LOGGED_IN) {
                User user = userRepository.getUserByIdSync(userId); // LẤY USER ĐẦY ĐỦ Ở ĐÂY
                if (user != null) {
                    _loggedInUser.postValue(user); // Đặt User đầy đủ (có role) vào LiveData
                } else {
                    userPreferences.clearCurrentUser(); // User không tồn tại nữa
                    _loggedInUser.postValue(null);
                }
            } else {
                _loggedInUser.postValue(null);
            }
        });
    }

    public int getCurrentUserId() {
        return userPreferences.getCurrentUserId(); // Lấy ID qua UserPreferences
    }

    public boolean getRememberMeStatus() {
        return userPreferences.getRememberMeStatus();
    }

    public void doneShowingToast() {
        _toastMessage.setValue(null);
    }

    public void updateUser(User userToUpdate, String currentPassword, String newPassword, String confirmNewPassword) {
        executorService.execute(() -> {
            User existingUser = userRepository.findByEmail(userToUpdate.getEmail());
            if (existingUser == null) {
                 _toastMessage.postValue("Lỗi: Không tìm thấy người dùng hiện tại.");
                 return;
            }

            if (!currentPassword.isEmpty() && !existingUser.getPassword().equals(currentPassword)) {
                 _toastMessage.postValue("Mật khẩu hiện tại không đúng.");
                 return;
            }

            if (!newPassword.isEmpty()) {
                if (!newPassword.equals(confirmNewPassword)) {
                    _toastMessage.postValue("Mật khẩu mới và xác nhận mật khẩu không khớp.");
                    return;
                }
                if (!ValidationUtil.isPasswordValid(newPassword)) {
                    _toastMessage.postValue("Mật khẩu mới phải có ít nhất 6 ký tự.");
                    return;
                }
                existingUser.setPassword(newPassword);
            } else if (currentPassword.isEmpty() && !newPassword.isEmpty()){
                _toastMessage.postValue("Vui lòng nhập mật khẩu hiện tại để đổi mật khẩu.");
                return;
            }

            existingUser.setFullName(userToUpdate.getFullName());
            // Role không được thay đổi từ màn hình profile thông thường
            userRepository.updateUser(existingUser);
            _toastMessage.postValue("Cập nhật thông tin thành công!");
            _loggedInUser.postValue(existingUser); // Cập nhật LiveData của người dùng đang đăng nhập
        });
    }
    
    // PHƯƠNG THỨC MỚI: Kiểm tra vai trò của người dùng
    public boolean isAdmin() {
        User user = _loggedInUser.getValue();
        return user != null && "Admin".equals(user.getRole());
    }
}