# User/Admin Refactor Plan

## 1. Mục tiêu (Goals)

*   **Chính:** Triển khai cơ chế phân quyền rõ ràng giữa vai trò "User" và "Admin" trong ứng dụng.
*   **Chi tiết:**
    *   Mỗi người dùng (User) sẽ có một `role` (`"User"` hoặc `"Admin"`).
    *   Quyền truy cập các tính năng sẽ được điều chỉnh dựa trên `role` này.
    *   **Admin:** Có quyền quản lý sản phẩm (thêm, sửa, xóa), và xem thống kê doanh thu.
    *   **User thường:** Chỉ có quyền mua sắm (xem sản phẩm, xem chi tiết, thêm vào giỏ hàng, và checkout). Không thể thêm, sửa, xóa sản phẩm. Không thể xem doanh thu.
    *   Giao diện người dùng (UI) sẽ tự động điều chỉnh (ẩn/hiện các nút, menu) dựa trên vai trò của người dùng hiện tại.
    *   Đảm bảo tính bảo mật cơ bản (ví dụ: không cho phép User thường truy cập các chức năng Admin).

## 2. Phạm vi (Scope)

*   **Các lớp/Thành phần bị ảnh hưởng trực tiếp:**
    *   `data.entity.User.java`
    *   `data.database.AppDatabase.java`
    *   `data.dao.UserDao.java`
    *   `viewmodel.AuthViewModel.java`
    *   `ui.product.ProductListFragment.java`
    *   `ui.product.ProductAdapter.java`
    *   `main_menu.xml` (Menu chung)
*   **Các lớp/Thành phần có thể bị ảnh hưởng/cần kiểm tra:**
    *   `ui.product.ProductDetailFragment.java` (nếu có nút Admin-specific)
    *   `ui.revenue.RevenueFragment.java` (Nếu muốn đảm bảo chỉ Admin mới thấy)
    *   `nav_graph.xml` (kiểm tra `android:label` cho tiêu đề)

*   **Các tính năng không thuộc phạm vi của đợt refactor này:**
    *   Màn hình quản lý người dùng (ví dụ: Admin thêm/sửa/xóa User khác).
    *   Cơ chế phân quyền phức tạp hơn (ví dụ: nhiều loại Admin, quyền hạn chi tiết hơn từng chức năng nhỏ).
    *   Mã hóa mật khẩu mạnh mẽ (sẽ sử dụng mật khẩu clear-text cho đơn giản trong lần này).
    *   Thêm lựa chọn vai trò khi đăng ký tài khoản (Admin sẽ được tạo thủ công hoặc bởi Admin khác).

## 3. Các bước thực hiện chi tiết (Detailed Implementation Steps)

### Bước 3.1: Cập nhật `User` Entity

*   **File:** `app/src/main/java/com/dangc/prm92_pe_phonesstore/data/entity/User.java`
*   **Thay đổi:**
    *   Thêm trường `private String role;`.
    *   Cập nhật constructor `User(String fullName, String email, String password)` để gán `this.role = "User";` mặc định.
    *   Cập nhật constructor đầy đủ (`User(int userId, String fullName, String email, String password, String role)`) để bao gồm `role`.
    *   Thêm getter (`getRole()`) và setter (`setRole(String role)`) cho `role`.

### Bước 3.2: Xử lý Migration Database

*   **File:** `app/src/main/java/com/dangc/prm92_pe_phonesstore/data/database/AppDatabase.java`
*   **Thay đổi:**
    *   **Tăng `version`:** Thay đổi `version` trong `@Database` annotation từ `1` lên `2`.
    *   **Thêm `Migration`:** Thêm một `static final Migration MIGRATION_1_2` để thêm cột `role` vào bảng `users` với giá trị mặc định là `"User"`:
        ```java
        static final Migration MIGRATION_1_2 = new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("ALTER TABLE users ADD COLUMN role TEXT NOT NULL DEFAULT 'User'");
            }
        };
        ```
    *   Thêm `addMigrations(MIGRATION_1_2)` vào `Room.databaseBuilder`.
    *   **Thêm `RoomDatabase.Callback` và Admin mặc định:**
        *   Thêm `static final RoomDatabase.Callback sRoomDatabaseCallback` để chèn một `User Admin` mặc định khi database được tạo lần đầu:
        ```java
        private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                databaseWriteExecutor.execute(() -> {
                    UserDao dao = INSTANCE.userDao();
                    // Nếu không có user nào, thêm một admin mặc định
                    if (dao.getUserCount() == 0) { // Cần phương thức getUserCount() trong UserDao
                        User adminUser = new User(
                                0, // ID sẽ được tự động gán
                                "Admin User",
                                "admin@example.com",
                                "admin123", // Mật khẩu ví dụ, nên hash trong ứng dụng thực tế
                                "Admin"
                        );
                        dao.insert(adminUser);
                    }
                });
            }
        };
        ```
        *   Thêm `addCallback(sRoomDatabaseCallback)` vào `Room.databaseBuilder`.

### Bước 3.3: Cập nhật `UserDao` (Hỗ trợ Admin mặc định)

*   **File:** `app/src/main/java/com/dangc/prm92_pe_phonesstore/data/dao/UserDao.java`
*   **Thay đổi:**
    *   Thêm phương thức `public int getUserCount();` để kiểm tra số lượng user hiện có.

### Bước 3.4: Cập nhật `AuthViewModel` (Xử lý vai trò)

*   **File:** `app/src/main/java/com/dangc/prm92_pe_phonesstore/viewmodel/AuthViewModel.java`
*   **Thay đổi:**
    *   Đảm bảo `_loggedInUser` LiveData chứa đối tượng `User` đầy đủ (bao gồm `role`) sau khi đăng nhập/tự động đăng nhập (`postValue(user)`).
    *   Thêm phương thức `public boolean isAdmin() { ... }` để kiểm tra vai trò của người dùng hiện tại (dựa trên `loggedInUser.getValue().getRole()`).
    *   Cập nhật phương thức `checkCurrentUser()` để lấy `User` đầy đủ bằng `userRepository.getUserByIdSync(userId)` và đặt vào `_loggedInUser`.

### Bước 3.5: Cập nhật `ProductListFragment` (Điều chỉnh UI)

*   **File:** `app/src/main/java/com/dangc/prm92_pe_phonesstore/ui/product/ProductListFragment.java`
*   **Thay đổi:**
    *   Trong `onViewCreated()`, quan sát `authViewModel.loggedInUser` để:
        *   Ẩn/hiện `fabAddProduct` (nút thêm sản phẩm) dựa trên `authViewModel.isAdmin()`.
        *   Gọi `adapter.setAdminMode(authViewModel.isAdmin())` để cập nhật `ProductAdapter`.
        *   Gọi `requireActivity().invalidateOptionsMenu()` để yêu cầu Activity cập nhật lại menu.
    *   Trong `onCreateOptionsMenu()`, sử dụng `authViewModel.isAdmin()` để ẩn/hiện `action_view_revenue`.

### Bước 3.6: Cập nhật `ProductAdapter` (Ẩn/hiện nút Edit/Delete)

*   **File:** `app/src/main/java/com/dangc/prm92_pe_phonesstore/ui/product/ProductAdapter.java`
*   **Thay đổi:**
    *   Thêm biến `private boolean isAdmin = false;`.
    *   Thêm phương thức `public void setAdminMode(boolean isAdmin)` để cập nhật biến này và gọi `notifyDataSetChanged()`.
    *   Trong `onBindViewHolder()`, sử dụng biến `isAdmin` để ẩn/hiện `buttonEdit` và `buttonDelete`.

### Bước 3.7: Điều chỉnh các Fragment khác (Nếu cần)

*   **`ProductDetailFragment.java`:** Nếu có các nút Admin-specific (ví dụ: nút "Edit" trên màn hình chi tiết), cần điều chỉnh tương tự `ProductListFragment` để ẩn/hiện chúng dựa trên `authViewModel.isAdmin()`.
*   **`RevenueFragment.java`:** Nếu bạn muốn chặn hoàn toàn User thường truy cập vào màn hình này, bạn có thể kiểm tra `authViewModel.isAdmin()` trong `onViewCreated()` và điều hướng về màn hình Product List nếu không phải Admin.

### Bước 3.8: Cập nhật `nav_graph.xml` (Kiểm tra nhãn)

*   **File:** `app/src/main/res/navigation/nav_graph.xml`
*   **Thay đổi:** Đảm bảo `android:label` của các `<fragment>` (ví dụ: "Revenue Statistics", "Add/Edit Product") được đặt đúng, vì chúng sẽ là tiêu đề `Toolbar` mặc định.

## 4. Kiểm thử (Testing)

*   **Kiểm thử Admin User:**
    *   Đăng nhập bằng tài khoản Admin mặc định (`admin@example.com`, `admin123`).
    *   Xác nhận rằng FAB "Add Product" hiển thị.
    *   Xác nhận rằng các nút "Edit", "Delete" trên từng item hiển thị.
    *   Xác nhận rằng tùy chọn "View Revenue" trong menu "ba chấm" hiển thị và có thể truy cập.
*   **Kiểm thử User thường:**
    *   Đăng ký tài khoản mới hoặc đăng nhập bằng tài khoản User hiện có.
    *   Xác nhận rằng FAB "Add Product" KHÔNG hiển thị.
    *   Xác nhận rằng các nút "Edit", "Delete" trên từng item KHÔNG hiển thị.
    *   Xác nhận rằng tùy chọn "View Revenue" trong menu "ba chấm" KHÔNG hiển thị.
    *   Xác nhận rằng các chức năng mua sắm (Add to Cart, Checkout) vẫn hoạt động bình thường.
*   **Kiểm thử Migration:**
    *   Chạy ứng dụng từ một phiên bản cũ (trước khi thêm cột `role`).
    *   Đăng ký một user mới.
    *   Cập nhật ứng dụng lên phiên bản mới.
    *   Đảm bảo ứng dụng không crash và các user cũ vẫn có `role = "User"`.
    *   Đảm bảo tài khoản Admin mặc định vẫn tồn tại (nếu bạn thêm nó qua Callback).
