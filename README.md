# Ứng dụng Cửa hàng Điện thoại

Đây là ứng dụng di động "Cửa hàng Điện thoại" được phát triển như một dự án PRM392. Ứng dụng tập trung vào việc hiển thị, quản lý và bán các sản phẩm điện thoại di động, tuân thủ kiến trúc MVVM (Model-View-ViewModel) và Repository Pattern để đảm bảo khả năng bảo trì, mở rộng và kiểm thử.

## Kiến trúc

Ứng dụng được xây dựng dựa trên kiến trúc **MVVM (Model-View-ViewModel)**, phân tách các mối quan tâm thành ba lớp chính:

*   **View (UI Layer):** Gồm các Activity và Fragment, chịu trách nhiệm hiển thị UI và chuyển tiếp tương tác người dùng tới ViewModel.
*   **ViewModel Layer:** Quản lý trạng thái UI, cung cấp dữ liệu cho View và xử lý logic nghiệp vụ, giao tiếp với Repository.
*   **Model (Data Layer):** Bao gồm Repository Pattern, Room Database và các nguồn dữ liệu khác. Đây là nguồn cung cấp dữ liệu duy nhất cho ViewModel.

## ViewModel Layer

Các lớp ViewModel chịu trách nhiệm chuẩn bị và quản lý dữ liệu cho View. Chúng tồn tại qua các thay đổi cấu hình (như xoay màn hình) và xử lý tất cả logic nghiệp vụ, giúp cho lớp UI trở nên đơn giản nhất có thể.

*   **`AuthViewModel`**: Xử lý logic đăng ký, đăng nhập và quản lý phiên làm việc của người dùng (Auto-Login).
*   **`ProductViewModel`**: Cung cấp danh sách sản phẩm, xử lý logic tìm kiếm và sắp xếp một cách hiệu quả thông qua `Transformations.switchMap`.
*   **`CartViewModel`**: Quản lý giỏ hàng (tạm thời trong bộ nhớ) và điều phối quá trình checkout.
*   **`RevenueViewModel`**: Cung cấp `LiveData` chứa tổng doanh thu từ `OrderRepository`.

## Data Layer Components

### Repositories

Các lớp Repository đóng vai trò là nguồn cung cấp dữ liệu duy nhất (Single Source of Truth) cho các ViewModel. Chúng trừu tượng hóa các nguồn dữ liệu và quản lý việc thực thi tác vụ trên các luồng nền.

*   **`ProductRepository`**: Quản lý dữ liệu sản phẩm.
*   **`UserRepository`**: Quản lý việc đăng ký, đăng nhập và phiên làm việc của người dùng.
*   **`OrderRepository`**: Quản lý việc tạo đơn hàng và thống kê doanh thu.

### Room Database

*   **Entities**: `User`, `Product`, `Order`, `OrderItem` định nghĩa cấu trúc bảng cho SQLite.
*   **DAOs**: `UserDao`, `ProductDao`, `OrderDao`, `OrderItemDao` cung cấp các phương thức truy vấn cơ sở dữ liệu.
*   **`AppDatabase`**: Lớp trung tâm quản lý database.

### Preferences

*   **`UserPreferences`**: Một lớp helper sử dụng `SharedPreferences` để quản lý phiên đăng nhập của người dùng (Auto-Login), lưu trữ `userId` một cách an toàn.

## Testing

Dự án chú trọng vào việc kiểm thử để đảm bảo chất lượng code:
*   **Unit Tests** (`/test`): Sử dụng **Mockito** để kiểm thử logic nội tại của các lớp ViewModel một cách độc lập.
*   **Component Tests** (`/androidTest`): Kiểm thử sự tích hợp giữa các thành phần (ViewModel, Repository, Room in-memory database) để đảm bảo luồng dữ liệu hoạt động chính xác.

## Công nghệ sử dụng

*   **Ngôn ngữ:** Java
*   **Hệ thống Build:** Gradle
*   **Kiến trúc:** MVVM + Repository Pattern
*   **Thành phần Jetpack:**
    *   **ViewModel**: Quản lý trạng thái UI.
    *   **LiveData**: Xây dựng luồng dữ liệu có thể quan sát và nhận biết vòng đời.
    *   **Room**: Lưu trữ dữ liệu cục bộ.
*   **Quản lý phiên:** SharedPreferences
*   **Xử lý bất đồng bộ:** `ExecutorService`
*   **Kiểm thử:** JUnit, Mockito, Espresso Test Framework.
