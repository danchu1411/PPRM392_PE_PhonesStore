# Ứng dụng Cửa hàng Điện thoại

Đây là ứng dụng di động "Cửa hàng Điện thoại" được phát triển như một dự án PRM392. Ứng dụng tập trung vào việc hiển thị, quản lý và bán các sản phẩm điện thoại di động, tuân thủ kiến trúc MVVM (Model-View-ViewModel) và Repository Pattern để đảm bảo khả năng bảo trì, mở rộng và kiểm thử.

## Kiến trúc

Ứng dụng được xây dựng dựa trên kiến trúc **MVVM (Model-View-ViewModel)** kết hợp với **Repository Pattern**, phân tách các mối quan tâm thành ba lớp chính:

*   **View:** Gồm các Activity và Fragment, chịu trách nhiệm hiển thị UI và xử lý tương tác người dùng.
*   **ViewModel:** Quản lý trạng thái UI, cung cấp dữ liệu cho View và xử lý logic nghiệp vụ, giao tiếp với Repository.
*   **Model (Data Layer):** Bao gồm Repository Pattern và các nguồn dữ liệu (Local/Remote). Đây là nguồn cung cấp dữ liệu duy nhất cho ViewModel.

## Các Model (Entities) đã triển khai

Các lớp Entity dưới đây đã được tạo và cấu hình với Room Database, đại diện cho cấu trúc dữ liệu chính trong ứng dụng:

*   **`Product`**: Đại diện cho thông tin chi tiết về một sản phẩm điện thoại.
*   **`User`**: Đại diện cho thông tin người dùng.
*   **`Order`**: Đại diện cho một đơn hàng.
*   **`OrderItem`**: Bảng trung gian giải quyết mối quan hệ nhiều-nhiều giữa `Order` và `Product`.
*   **`DateConverter`**: Lớp `TypeConverter` để lưu trữ kiểu dữ liệu `Date` trong Room.

## Data Layer Components

### Data Access Objects (DAO)

Các interface DAO cung cấp một API an toàn và trừu tượng để tương tác với database. Mỗi Entity có một DAO tương ứng chịu trách nhiệm cho các hoạt động truy vấn dữ liệu.

*   **`ProductDao`**: Cung cấp các phương thức để Thêm, Sửa, Xóa, Lấy danh sách sản phẩm, Tìm kiếm và Sắp xếp sản phẩm theo giá.
*   **`UserDao`**: Cung cấp các phương thức để đăng ký và xác thực người dùng.
*   **`OrderDao`**: Cung cấp phương thức để lưu một đơn hàng mới và tính tổng doanh thu.
*   **`OrderItemDao`**: Cung cấp phương thức để lưu các mục chi tiết của một đơn hàng.

### Repositories

Các lớp Repository đóng vai trò là nguồn cung cấp dữ liệu duy nhất (Single Source of Truth) cho các ViewModel. Chúng trừu tượng hóa các nguồn dữ liệu (local database, remote API) và quản lý việc thực thi các tác vụ trên các luồng nền thích hợp.

*   **`ProductRepository`**: Quản lý dữ liệu sản phẩm.
*   **`UserRepository`**: Quản lý việc đăng ký, đăng nhập và phiên làm việc của người dùng.
*   **`OrderRepository`**: Quản lý việc tạo đơn hàng và thống kê doanh thu.

### Preferences

*   **`UserPreferences`**: Một lớp helper sử dụng `SharedPreferences` để quản lý phiên đăng nhập của người dùng (Auto-Login). Nó lưu `userId` của người dùng khi đăng nhập thành công và xóa khi đăng xuất.

## Công nghệ sử dụng

*   **Ngôn ngữ:** Java
*   **Hệ thống Build:** Gradle
*   **Kiến trúc:** MVVM + Repository Pattern
*   **Database:** Room Database (dựa trên SQLite)
*   **Quản lý phiên:** SharedPreferences
*   **Xử lý bất đồng bộ:** LiveData, `ExecutorService`

## Cài đặt và Chạy

1.  **Clone repository:**
    ```bash
    git clone <URL_repository_của_bạn>
    cd PRM392_PE_PhonesStore
    ```
2.  **Mở trong Android Studio:** Mở thư mục dự án trong Android Studio.
3.  **Đồng bộ Gradle:** Đảm bảo Gradle được đồng bộ hóa thành công để tải về tất cả các dependencies.
4.  **Chạy ứng dụng:** Chạy ứng dụng trên một thiết bị Android emulator hoặc thiết bị vật lý.
