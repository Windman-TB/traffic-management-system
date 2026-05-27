# Traffic Management System

Traffic Management System là ứng dụng desktop được xây dựng bằng JavaFX và Maven.
Ứng dụng hỗ trợ quản lý thông tin giao thông, tài khoản, nhân viên, khu vực, đường, nút giao, đoạn đường, theo dõi lưu lượng và phân tích tình trạng giao thông.

## Cấu trúc thư mục

```text
traffic-management-system/
|
|-- pom.xml
|-- README.md
|-- .gitignore
|
|-- src/
|   |
|   |-- main/
|   |   |
|   |   |-- java/
|   |       |
|   |       |-- com/mycompany/trafficsystem/
|   |           |
|   |           |-- Main.java
|   |           |
|   |           |-- view/
|   |           |-- controller/
|   |           |-- model/
|   |           |-- database/
|   |           |-- util/
|   |
|   |-- test/
```

## Giải thích các file và thư mục

### `pom.xml`

File cấu hình Maven của project. NetBeans đọc file này để nhận diện project Java Maven, tải dependency và chạy ứng dụng.

Trong project này, `pom.xml` khai báo:

- Java release: `25`
- JavaFX version: `25.0.3`
- Oracle JDBC driver: `ojdbc11`
- Main class: `com.mycompany.trafficsystem.Main`
- Plugin chạy JavaFX: `javafx-maven-plugin`

### `README.md`

File tài liệu của project. File này dùng để giải thích mục đích ứng dụng, cấu trúc mã nguồn, cách cài đặt và cách chạy chương trình.

### `.gitignore`

File cấu hình cho Git biết những file/thư mục không cần đưa lên GitHub.

Ví dụ:

- `target/`: thư mục build do Maven tạo ra
- `*.class`: file biên dịch từ Java
- `.vscode/`: cấu hình riêng của Visual Studio Code
- `.agents/`, `.codex/`: metadata của môi trường làm việc


### `src/`

Thư mục chứa mã nguồn chính của ứng dụng.

```text
src/
|
|-- main/
|   |
|   |-- java/
|       |
|       |-- com/mycompany/trafficsystem/
|
|-- test/
```

### `src/main/java/com/mycompany/trafficsystem/Main.java`

Điểm khởi chạy của ứng dụng JavaFX.

`Main.java` kế thừa `Application`, tạo màn hình đăng nhập `LoginView` và hiển thị cửa sổ đầu tiên của chương trình.

### `view/`

Package chứa các lớp giao diện JavaFX. Các lớp trong package này chịu trách nhiệm tạo màn hình, bảng dữ liệu, form nhập liệu, dialog và các thành phần hiển thị cho người dùng.

Một số file tiêu biểu:

- `LoginView.java`: màn hình đăng nhập
- `AdminView.java`: giao diện chính cho quản trị viên
- `TechnicianView.java`: giao diện cho kỹ thuật viên
- `AnalystView.java`: giao diện cho người phân tích
- `AccountView.java`: màn hình quản lý tài khoản
- `EmployeeView.java`: màn hình quản lý nhân viên
- `TrafficView.java`: màn hình quản lý dữ liệu giao thông
- `TrafficMonitoringView.java`: màn hình theo dõi giao thông
- `TrafficAnalysisView.java`: màn hình phân tích giao thông
- `BaseView.java`: lớp giao diện có các thành phần dùng chung

### `controller/`

Package chứa các lớp điều khiển luồng xử lý giữa giao diện, model và database.

Controller nhận yêu cầu từ `view`, kiểm tra dữ liệu, gọi các lớp trong `database/`, sau đó trả kết quả lại cho giao diện.

Một số file tiêu biểu:

- `LoginController.java`: xử lý đăng nhập
- `AccountController.java`: xử lý nghiệp vụ tài khoản
- `EmployeeController.java`: xử lý nghiệp vụ nhân viên
- `TrafficController.java`: xử lý dữ liệu giao thông
- `TrafficAnalyticsController.java`: xử lý thống kê và phân tích giao thông
- `BaseController.java`: xử lý chung cho các controller

### `model/`

Package chứa các lớp mô hình dữ liệu của hệ thống. Mỗi lớp thường đại diện cho một đối tượng nghiệp vụ hoặc một dòng dữ liệu cần hiển thị.

Một số file tiêu biểu:

- `Account.java`: thông tin tài khoản
- `Employee.java`: thông tin nhân viên
- `Area.java`: thông tin khu vực
- `Street.java`: thông tin đường
- `Node.java`: thông tin nút giao
- `Segment.java`: thông tin đoạn đường
- `Traffic.java`: thông tin giao thông
- `SystemLog.java`: thông tin nhật ký hệ thống
- `TrafficMonitoringRow.java`: dữ liệu hiển thị trên màn hình theo dõi giao thông
- `TrafficAnalysisRow.java`: dữ liệu hiển thị trên màn hình phân tích giao thông

### `database/`

Package chứa các lớp làm việc với cơ sở dữ liệu Oracle.

Mỗi lớp trong package này thường phụ trách truy vấn, thêm, sửa, xóa hoặc lấy dữ liệu cho một nhóm chức năng.

Một số file tiêu biểu:

- `ConnectDB.java`: tạo kết nối đến cơ sở dữ liệu
- `AccountDatabase.java`: truy vấn dữ liệu tài khoản
- `EmployeeDatabase.java`: truy vấn dữ liệu nhân viên
- `TrafficDatabase.java`: truy vấn dữ liệu giao thông
- `TrafficAnalyticsDatabase.java`: truy vấn dữ liệu phân tích giao thông
- `SystemLogDatabase.java`: truy vấn nhật ký hệ thống

### `util/`

Package chứa các lớp tiện ích dùng chung trong ứng dụng.

Hiện tại gồm:

- `Session.java`: lưu thông tin phiên đăng nhập hiện tại
- `SystemLogUtil.java`: hỗ trợ ghi nhật ký hoạt động của hệ thống

### `src/test/`

Thư mục dành cho mã nguồn test. Hiện tại project chưa có test tự động.

### `target/`

Thư mục do Maven tạo ra khi build project. Thư mục này chứa file `.class`, file `.jar` và các file build trung gian.

Không cần push `target/` lên GitHub vì người khác có thể build lại bằng Maven.
