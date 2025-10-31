# **Bản thảo Đặc tả Kỹ thuật: Ứng dụng Cửa hàng Điện thoại**

Tài liệu này phác thảo các yêu cầu kỹ thuật, kiến trúc, và các tính năng cho ứng dụng di động "Cửa hàng Điện thoại", dựa trên tài liệu "PE PRM392.pdf". Ứng dụng sẽ tập trung vào việc hiển thị, quản lý và bán các sản phẩm là điện thoại di động.

## **1\. Kiến trúc (Architecture)**

Để đảm bảo ứng dụng có khả năng bảo trì, mở rộng và kiểm thử (testable), dự án sẽ áp dụng kiến trúc **MVVM (Model-View-ViewModel)** kết hợp với **Repository Pattern**.

Kiến trúc này tuân thủ nguyên tắc "separation of concerns" (phân tách các mối quan tâm), phù hợp với yêu cầu của đề bài về việc "Tổ chức code thành các thành phần rõ ràng".

### **1.1. Tổng quan Kiến trúc**

\[Sơ đồ kiến trúc MVVM (Model-View-ViewModel) cho Android\]

Kiến trúc sẽ bao gồm ba lớp chính:

1. **View (Lớp Giao diện):**
    * Bao gồm các Activity và Fragment.
    * Chỉ chịu trách nhiệm hiển thị dữ liệu lên UI và chuyển tiếp các tương tác của người dùng (như click, nhập liệu) tới ViewModel.
    * Sử dụng LiveData để quan sát (observe) các thay đổi dữ liệu từ ViewModel và tự động cập nhật UI.
2. **ViewModel (Lớp Logic):**
    * Lưu trữ và quản lý trạng thái (state) liên quan đến UI.
    * Cung cấp dữ liệu cho View và xử lý logic nghiệp vụ (business logic) khi người dùng tương tác.
    * ViewModel sẽ tồn tại qua các thay đổi cấu hình (như xoay màn hình), bảo toàn trạng thái của UI.
    * Nó sẽ giao tiếp với Repository để lấy hoặc gửi dữ liệu.
3. **Model (Lớp Dữ liệu):**
    * Được triển khai bằng **Repository Pattern**. Repository sẽ là nguồn cung cấp dữ liệu duy nhất (Single Source of Truth) cho các ViewModel.
    * Repository sẽ trừu tượng hóa (abstract) nguồn dữ liệu. Nó sẽ quyết định lấy dữ liệu từ đâu: Nguồn dữ liệu Local (Database) hay Nguồn dữ liệu Remote (API/Firebase).

### **1.2. Cấu trúc Thành phần Chi tiết**

#### **Lớp View (UI Layer)**

* **AuthActivity**: Quản lý các màn hình Đăng nhập và Đăng ký.
    * LoginFragment (Tính năng 2\)
    * RegisterFragment (Tính năng 1\)
* **MainActivity**: Activity chính sau khi đăng nhập, sử dụng **Navigation Component** để quản lý các màn hình con.
    * ProductListFragment (Tính năng 3, 5, 6): Hiển thị RecyclerView danh sách điện thoại, thanh tìm kiếm, menu sắp xếp.
    * ProductDetailFragment (Tính năng 4): Hiển thị chi tiết sản phẩm (thông số kỹ thuật, hình ảnh).
    * AddEditProductFragment (Tính năng 4): Form để Thêm/Sửa sản phẩm điện thoại.
    * CartFragment (Tính năng 7): Hiển thị giỏ hàng (RecyclerView).
    * RevenueFragment (Tính năng 8): Hiển thị thống kê doanh thu.

#### **Lớp ViewModel (ViewModel Layer)**

* **AuthViewModel**: Xử lý logic cho LoginFragment và RegisterFragment. Quản lý việc lưu "Remember Password" vào SharedPreferences.
* **ProductViewModel**: Cung cấp danh sách điện thoại, xử lý logic CRUD (Thêm/Sửa/Xóa), tìm kiếm và sắp xếp.
* **CartViewModel**: Quản lý các mặt hàng trong giỏ, cập nhật số lượng, tính tổng tiền và xử lý logic "Checkout".
* **RevenueViewModel**: Lấy và xử lý dữ liệu đơn hàng đã hoàn thành từ OrderRepository để tính toán doanh thu.

#### **Lớp Model (Data Layer)**

* **Repositories:**
    * **UserRepository**: Cung cấp các phương thức như registerUser(name, email, pass), loginUser(email, pass).
    * **ProductRepository**: Cung cấp LiveData\<List\<Product\>\>, getProductById(id), searchProducts(name), addProduct(product), v.v.
    * **OrderRepository**: Cung cấp các phương thức saveOrder(cartItems), getRevenueData(filterBy).
* **Data Sources (Nguồn dữ liệu):**
    * **Local Data Source:**
        * **Room Database (SQLite):** Sẽ được sử dụng làm nguồn lưu trữ chính cho Users, Products (điện thoại), và Orders (theo yêu cầu lưu trữ SQLite).
        * **SharedPreferences:** Chỉ sử dụng để lưu trữ token đăng nhập hoặc tùy chọn "Remember Password".
    * **Remote Data Source (Tùy chọn):**
        * Sẽ định nghĩa các interface cho Firebase, MongoDB hoặc Mock API (JSON Server) nếu dự án quyết định mở rộng ra mô hình client-server.

### **1.3. Sơ đồ Lớp (Class Diagram) \- Phác thảo**

Phần này định nghĩa các thực thể (Entities) chính sẽ được sử dụng trong Room Database.

@startuml  
skinparam classAttributeIconSize 0

' Định nghĩa các lớp Entity cho Room DB  
class User {  
\+ userId: int (PK)  
\+ fullName: String  
\+ email: String (Unique)  
\+ password: String  
}

class Product {  
\+ productId: int (PK)  
\+ modelName: String  
\+ brand: String  
\+ description: String  
\+ price: double  
\+ imageUrl: String  
}

class Order {  
\+ orderId: int (PK)  
\+ userId: int (FK)  
\+ orderDate: Date  
\+ totalAmount: double  
}

' Bảng trung gian cho mối quan hệ Nhiều-Nhiều  
class OrderItem {  
\+ orderItemId: int (PK)  
\+ orderId: int (FK)  
\+ productId: int (FK)  
\+ quantity: int  
\+ pricePerUnit: double  
}

' Định nghĩa các mối quan hệ  
User "1" \-- "\*" Order : places  
Order "1" \-- "\*" OrderItem : contains  
Product "1" \-- "\*" OrderItem : details

@enduml

* **User (Entity)**
    * userId (int, Primary Key, Auto-generate)
    * fullName (String)
    * email (String, Unique Index)
    * password (String)
* **Product (Entity)**
    * productId (int, Primary Key, Auto-generate)
    * modelName (String)
    * brand (String)
    * description (String)
    * price (double)
    * imageUrl (String)
* **Order (Entity)**
    * orderId (int, Primary Key, Auto-generate)
    * userId (int, Foreign Key \- liên kết tới User)
    * orderDate (Date)
    * totalAmount (double)
* **OrderItem (Entity \- Bảng trung gian)**
    * orderItemId (int, Primary Key, Auto-generate)
    * orderId (int, Foreign Key \- liên kết tới Order)
    * productId (int, Foreign Key \- liên kết tới Product)
    * quantity (int)
    * pricePerUnit (double)
* **Mối quan hệ:**
    * User 1-\* Order (Một User có thể có nhiều Order).
    * Order \*-\* Product (Một Order có thể chứa nhiều Product, và một Product có thể nằm trong nhiều Order). Mối quan hệ này được giải quyết bằng bảng OrderItem.

## **2\. Đặc tả Tính năng (Phác thảo)**

### **2.1. Xác thực Người dùng (Tính năng 1, 2\)**

* **Màn hình Đăng ký:**
    * Form: Họ tên, Email, Mật khẩu, Xác nhận Mật khẩu.
    * Validation: Email (định dạng regex), Mật khẩu (trùng khớp).
    * Logic: Gọi authViewModel.registerUser(...).
* **Màn hình Đăng nhập:**
    * Form: Email, Mật khẩu.
    * Checkbox: "Remember Password" (sử dụng SharedPreferences).
    * Logic: Gọi authViewModel.loginUser(...).

### **2.2. Quản lý Sản phẩm (Tính năng 3, 4\)**

* **Màn hình Danh sách (ProductListFragment):**
    * Hiển thị danh sách điện thoại bằng RecyclerView.
    * Mỗi item hiển thị: Ảnh, Tên Model, Hãng, Giá.
    * Click item: Điều hướng đến ProductDetailFragment.
    * Long-click item (tùy chọn): Hiển thị menu nhanh (Sửa/Xóa).
* **Màn hình Thêm/Sửa (AddEditProductFragment):**
    * Form: Tên Model, Hãng sản xuất (Vd: Apple, Samsung), Mô tả/Thông số kỹ thuật, Giá, URL hình ảnh.
    * Validation: Không để trống các trường bắt buộc, giá phải là số.
    * Logic: Gọi productViewModel.addProduct(...) hoặc productViewModel.updateProduct(...).

### **2.3. Tìm kiếm & Sắp xếp (Tính năng 5, 6\)**

* **Tìm kiếm:** Tích hợp SearchView trong ProductListFragment.
    * Logic: Tìm kiếm theo Tên Model. productViewModel.searchProducts(query) sẽ cập nhật LiveData.
* **Sắp xếp:** Tích hợp Menu trên ActionBar.
    * Tùy chọn: Giá tăng dần, Giá giảm dần. (Có thể mở rộng: Lọc theo Hãng).
    * Logic: productViewModel.sortProducts(sortBy) sẽ cập nhật LiveData.

### **2.4. Giỏ hàng (Tính năng 7\)**

* **Màn hình Giỏ hàng (CartFragment):**
    * Hiển thị danh sách item trong giỏ bằng RecyclerView.
    * Mỗi item: Tên Model, Giá, Số lượng (có thể chỉnh sửa), Nút Xóa.
    * Hiển thị Tổng tiền.
    * Nút "Checkout": Mô phỏng đặt hàng, gọi cartViewModel.checkout().
* **Logic Thêm vào giỏ:** Từ ProductListFragment hoặc ProductDetailFragment.

### **2.5. Thống kê Doanh thu (Tính năng 8\)**

* **Màn hình Doanh thu (RevenueFragment):**
    * Hiển thị Tổng doanh thu.
    * (Tùy chọn) Cung cấp Filter (bộ lọc) theo Ngày/Tháng/Năm.
    * Logic: revenueViewModel.loadRevenue(...).

## **3\. Yêu cầu Kỹ thuật (Sơ bộ)**

* **Ngôn ngữ chính:** Java
* **Hệ thống Build:** Build Gradle
* **Kiến trúc:** MVVM \+ Repository
* **Lưu trữ Local:** Room Database (cho SQLite).
* **Xử lý bất đồng bộ:** Sử dụng LiveData và Executors.
* **Các thư viện chính (Android Jetpack):**
    * Lifecycle (ViewModel, LiveData)
    * Room
    * Navigation Component
    * RecyclerView
    * Data Binding / View Binding (Khuyến nghị)