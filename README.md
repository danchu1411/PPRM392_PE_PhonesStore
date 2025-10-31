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
    *   `productId` (Primary Key, Auto-generate)
    *   `modelName`
    *   `brand`
    *   `description`
    *   `price`
    *   `imageUrl`

*   **`User`**: Đại diện cho thông tin người dùng.
    *   `userId` (Primary Key, Auto-generate)
    *   `fullName`
    *   `email` (Unique Index)
    *   `password`

*   **`Order`**: Đại diện cho một đơn hàng.
    *   `orderId` (Primary Key, Auto-generate)
    *   `userId` (Foreign Key tới `User`)
    *   `orderDate` (Sử dụng `DateConverter`)
    *   `totalAmount`

*   **`OrderItem`**: Bảng trung gian, đại diện cho các sản phẩm cụ thể trong một đơn hàng (giải quyết mối quan hệ nhiều-nhiều giữa `Order` và `Product`).
    *   `orderItemId` (Primary Key, Auto-generate)
    *   `orderId` (Foreign Key tới `Order`)
    *   `productId` (Foreign Key tới `Product`)
    *   `quantity`
    *   `pricePerUnit`

*   **`DateConverter`**: Một lớp `TypeConverter` được sử dụng bởi Room để chuyển đổi kiểu dữ liệu `java.util.Date` sang `Long` (timestamp) và ngược lại, giúp lưu trữ ngày tháng trong SQLite.

## Công nghệ sử dụng

*   **Ngôn ngữ:** Java
*   **Hệ thống Build:** Gradle
*   **Kiến trúc:** MVVM + Repository Pattern
*   **Database:** Room Database (dựa trên SQLite)
*   **Xử lý bất đồng bộ:** LiveData

## Cài đặt và Chạy

1.  **Clone repository:**
    ```bash
    git clone <URL_repository_của_bạn>
    cd PRM392_PE_PhonesStore
    ```
2.  **Mở trong Android Studio:** Mở thư mục dự án trong Android Studio.
3.  **Đồng bộ Gradle:** Đảm bảo Gradle được đồng bộ hóa thành công để tải về tất cả các dependencies.
4.  **Chạy ứng dụng:** Chạy ứng dụng trên một thiết bị Android emulator hoặc thiết bị vật lý.

### Mock API (Tùy chọn cho phát triển)

Trong quá trình phát triển ban đầu, ứng dụng có thể sử dụng Mock API (ví dụ: JSON Server) để giả lập dữ liệu từ máy chủ. Điều này giúp phát triển giao diện người dùng và logic ViewModel mà không cần chờ đợi triển khai database hoàn chỉnh.

Để sử dụng JSON Server:
1.  Cài đặt Node.js.
2.  Tạo file `db.json` với cấu trúc dữ liệu mong muốn tại thư mục gốc của dự án.
3.  Chạy lệnh:
    ```bash
    npx json-server --watch db.json
    ```
4.  Cấu hình `ProductRepository` (hoặc các Repository khác) để lấy dữ liệu từ endpoint mà JSON Server cung cấp (ví dụ: `http://localhost:3000/products`).
