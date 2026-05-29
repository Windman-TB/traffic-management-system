# Traffic Management System

Traffic Management System là ứng dụng desktop quản lý giao thông được xây dựng bằng JavaFX, Maven và Oracle Database.

Ứng dụng hỗ trợ đăng nhập theo vai trò, quản lý tài khoản, nhân viên, khu vực, tuyến đường, nút giao, đoạn đường, dữ liệu lưu lượng, theo dõi giao thông, phân tích tình trạng giao thông và ghi nhật ký hệ thống.

## Công nghệ sử dụng

- Ngôn ngữ: Java
- Giao diện: JavaFX
- Build tool: Apache Maven
- CSDL: Oracle Database
- Driver CSDL: Oracle JDBC `ojdbc11`
- API ngoài: Goong Speed Limit API
- Workflow tự động: Kestra
- Quản lý mã nguồn: Git/GitHub

## Cấu trúc thư mục được push lên GitHub

```text
trafficsystem/
|
|-- .gitignore
|-- README.md
|-- pom.xml
|-- docker-compose.yaml
|-- traffic_every_30_minutes.yaml
|
|-- database/
|   |-- 01_schema_clean.sql
|   |-- 02_sequences_triggers.sql
|   |-- csv/
|       |-- ACCOUNT.csv
|       |-- ACCOUNT_ROLE.csv
|       |-- AREA.csv
|       |-- AREA_BOUNDARY.csv
|       |-- EMPLOYEE.csv
|       |-- NODE.csv
|       |-- SEGMENT.csv
|       |-- STREET.csv
|       |-- TRAFFIC_SOURCE.csv
|
|-- src/main/java/com/mycompany/trafficsystem/
    |-- Main.java
    |-- controller/
    |-- database/
    |-- model/
    |-- service/
    |-- util/
    |-- view/
```

Các thư mục/file sinh ra khi build như `target/`, file `.class`, file cấu hình cá nhân của IDE và file chứa API key local không được đưa lên GitHub.

## Giải thích các file và thư mục chính

### `pom.xml`

File cấu hình Maven của project. File này khai báo Java release `25`, JavaFX `25.0.3`, Oracle JDBC `ojdbc11`, main class `com.mycompany.trafficsystem.Main` và plugin chạy JavaFX.

### `database/`

Chứa script và dữ liệu mẫu cho Oracle Database:

- `01_schema_clean.sql`: tạo các bảng chính như `EMPLOYEE`, `ACCOUNT`, `ACCOUNT_ROLE`, `AREA`, `AREA_BOUNDARY`, `STREET`, `NODE`, `SEGMENT`, `TRAFFIC`, `TRAFFIC_SOURCE`, `PASSWORD_RESET`, `SYSTEM_LOG`.
- `02_sequences_triggers.sql`: tạo sequence/trigger phục vụ sinh mã và xử lý dữ liệu trong CSDL.
- `csv/`: dữ liệu mẫu dùng để import vào các bảng.

### `docker-compose.yaml`

Cấu hình chạy Kestra và PostgreSQL metadata cho Kestra. Kestra được dùng để chạy workflow tự động nạp dữ liệu giao thông theo chu kỳ.

### `traffic_every_30_minutes.yaml`

Workflow Kestra nạp từng batch dữ liệu từ bảng `TRAFFIC_SOURCE` sang bảng `TRAFFIC` mỗi 30 phút. Dữ liệu mới được chọn theo `RECORDED_AT` và tránh insert trùng `STATUS_ID`.

### `src/main/java/com/mycompany/trafficsystem/Main.java`

Điểm khởi chạy ứng dụng JavaFX. File này tạo màn hình đăng nhập đầu tiên của hệ thống.

### `view/`

Chứa các lớp giao diện JavaFX:

- `LoginView.java`: màn hình đăng nhập.
- `AdminView.java`, `TechnicianView.java`, `AnalystView.java`: giao diện theo vai trò người dùng.
- `AccountView.java`, `EmployeeView.java`, `AreaView.java`, `StreetView.java`, `NodeView.java`, `SegmentView.java`: các màn hình quản lý dữ liệu danh mục.
- `TrafficView.java`: màn hình tra cứu, sửa, xóa dữ liệu lưu lượng.
- `TrafficMonitoringView.java`: màn hình theo dõi lưu lượng giao thông.
- `TrafficAnalysisView.java`: màn hình phân tích tình trạng giao thông và xuất CSV.
- `SystemLogView.java`: màn hình xem nhật ký hoạt động.
- Các view khôi phục mật khẩu: `ForgotPasswordView.java`, `OtpVerificationView.java`, `ResetPasswordView.java`, `ResetPasswordSuccessView.java`.

### `controller/`

Chứa lớp xử lý nghiệp vụ giữa giao diện và database:

- Kiểm tra dữ liệu nhập từ giao diện.
- Chuẩn hóa mã hiển thị như `KV`, `NG`, `SEG`.
- Gọi database để thêm, sửa, xóa, tìm kiếm.
- Ghi log thao tác thành công/thất bại qua `SystemLogUtil`.

### `database/` trong source code

Chứa các lớp truy vấn Oracle Database:

- `ConnectDB.java`: tạo kết nối Oracle. Có thể cấu hình bằng biến môi trường `TRAFFIC_DB_URL`, `TRAFFIC_DB_USERNAME`, `TRAFFIC_DB_PASSWORD`; nếu không có thì dùng mặc định `jdbc:oracle:thin:@localhost:1521:XE`, user `traffic_user`, password `123456`.
- Các file `AccountDatabase.java`, `EmployeeDatabase.java`, `AreaDatabase.java`, `StreetDatabase.java`, `NodeDatabase.java`, `SegmentDatabase.java`, `TrafficDatabase.java`, `TrafficAnalyticsDatabase.java`, `SystemLogDatabase.java` xử lý truy vấn cho từng nhóm chức năng.

### `model/`

Chứa các lớp mô hình dữ liệu như `Account`, `Employee`, `Area`, `Street`, `Node`, `Segment`, `Traffic`, `SystemLog`, `TrafficMonitoringRow`, `TrafficAnalysisRow`.

### `service/`

Chứa service gọi API ngoài:

- `GoongSpeedLimitService.java`: gọi Goong Speed Limit API để lấy tốc độ tối đa tại vị trí trung điểm của đoạn đường.

### `util/`

Chứa tiện ích dùng chung:

- `Session.java`: lưu thông tin phiên đăng nhập hiện tại.
- `SystemLogUtil.java`: hỗ trợ ghi nhật ký thao tác hệ thống.

## Chức năng chính

### Đăng nhập và phân quyền

- Người dùng đăng nhập bằng tài khoản trong bảng `ACCOUNT`.
- Giao diện sau đăng nhập thay đổi theo vai trò: quản trị viên, kỹ thuật viên hoặc người phân tích.
- Hệ thống lưu phiên đăng nhập bằng `Session`.

### Quản lý tài khoản và nhân viên

- Thêm, sửa, xóa mềm và tìm kiếm tài khoản.
- Thêm, sửa, xóa mềm và tìm kiếm nhân viên.
- Tài khoản gắn với nhân viên và vai trò trong hệ thống.
- Khi thêm lại dữ liệu trùng với bản ghi đã xóa mềm, một số module có cơ chế khôi phục bản ghi cũ thay vì tạo trùng.

### Quản lý khu vực, tuyến đường, nút giao và đoạn đường

- Quản lý khu vực (`AREA`), tuyến đường (`STREET`), nút giao (`NODE`) và đoạn đường (`SEGMENT`).
- Tự sinh mã mới dựa trên mã lớn nhất hiện có trong CSDL.
- Chuẩn hóa mã nhập/tìm kiếm dạng hiển thị, ví dụ `KV1`, `NG5`, `SEG10`.
- Nút giao kiểm tra hợp lệ vĩ độ trong khoảng `-90` đến `90` và kinh độ trong khoảng `-180` đến `180`.

### Tự tính thông tin đoạn đường

Khi thêm hoặc cập nhật đoạn đường, hệ thống tự bổ sung dữ liệu hình học:

- Lấy tọa độ nút đầu và nút cuối.
- Tính chiều dài đoạn đường bằng công thức khoảng cách theo bán kính Trái Đất.
- Tính trung điểm đoạn đường.
- Tự xác định `AREA_ID` bằng cách kiểm tra trung điểm có nằm trong vùng `AREA_BOUNDARY` hay không.
- `AREA_BOUNDARY` lưu ranh giới khu vực dạng WKT `POLYGON` hoặc `MULTIPOLYGON`.

### Lấy tốc độ tối đa từ Goong Speed Limit API

Khi thêm hoặc cập nhật đoạn đường, nếu người dùng không nhập `MAX_VELOCITY`, hệ thống sẽ:

- Tính trung điểm của đoạn đường.
- Gọi Goong Speed Limit API bằng tọa độ trung điểm.
- Đọc trường `max_speed` từ phản hồi API.
- Tự điền tốc độ tối đa cho đoạn đường nếu API trả dữ liệu hợp lệ.

API key được đọc theo thứ tự:

1. File local tên `API_Key`.
2. Biến môi trường `GOONG_API_KEY`.

File `API_Key` không nên push lên GitHub. Có thể tạo file local với nội dung:

```text
GOONG_API_KEY=your_goong_api_key_here
```

### Kiểm tra trước khi xóa

Các chức năng xóa dùng cơ chế xóa mềm bằng trường `IS_DELETED`, đồng thời kiểm tra ràng buộc nghiệp vụ trước khi xóa:

- Không cho xóa khu vực nếu còn đoạn đường đang hoạt động thuộc khu vực đó.
- Không cho xóa tuyến đường nếu còn đoạn đường đang hoạt động thuộc tuyến đó.
- Không cho xóa đoạn đường nếu còn dữ liệu lưu lượng trong 30 ngày gần đây.
- Các thao tác xóa thành công/thất bại đều được ghi vào nhật ký hệ thống.

### Quản lý dữ liệu lưu lượng

- Dữ liệu trong bảng `TRAFFIC` chủ yếu được nạp tự động từ workflow.
- Giao diện cho phép tra cứu, sửa và xóa dữ liệu lưu lượng.
- Có màn hình theo dõi lưu lượng theo đoạn đường và trạng thái giao thông.

### Phân tích giao thông

- Tổng hợp, thống kê và phân tích dữ liệu lưu lượng.
- Hiển thị dữ liệu phục vụ đánh giá tình trạng giao thông.
- Hỗ trợ xuất dữ liệu phân tích ra file CSV.

### Quên mật khẩu

- Hỗ trợ tạo yêu cầu đặt lại mật khẩu.
- Có màn hình nhập OTP và đặt lại mật khẩu.
- Dữ liệu reset được lưu trong bảng `PASSWORD_RESET`.

### Nhật ký hệ thống

- Ghi nhận thao tác thêm, sửa, xóa và trạng thái thành công/thất bại.
- Lưu thông tin bảng bị tác động, mã bản ghi, giá trị cũ, giá trị mới và thời gian thao tác.
- Có màn hình xem nhật ký hệ thống.

## Cách chạy project

### 1. Chuẩn bị môi trường

- Cài JDK 25 hoặc phiên bản tương thích với cấu hình Maven.
- Cài Apache Maven.
- Cài Docker Desktop.
- Cài Apache NetBeans.

### 2. Chạy Oracle Database XE bằng Docker

Tạo network dùng chung cho Oracle và Kestra:

```bash
docker network create traffic_network
```

Chạy Oracle XE container:

```bash
docker run -d --name oracle-xe -p 1521:1521 --network traffic_network -e ORACLE_PASSWORD=123456 gvenzl/oracle-xe
```

Đợi Oracle khởi động xong, sau đó kết nối bằng tài khoản quản trị `SYSTEM` với password `123456`.

### 3. Tạo user cho ứng dụng

Chạy các lệnh SQL sau bằng SQL Developer, dbForge Studio for Oracle hoặc công cụ quản lý Oracle bạn đang dùng:

```sql
CREATE USER traffic_user IDENTIFIED BY 123456;
GRANT CONNECT, RESOURCE TO traffic_user;
ALTER USER traffic_user QUOTA UNLIMITED ON USERS;
```

Sau khi tạo user, kết nối lại bằng:

```text
Username: traffic_user
Password: 123456
URL: jdbc:oracle:thin:@localhost:1521:XE
```

### 4. Tạo CSDL và import dữ liệu

Chạy lần lượt:

```text
database/01_schema_clean.sql
database/02_sequences_triggers.sql
```

Sau đó import các file CSV trong `database/csv/` vào các bảng tương ứng:

- `EMPLOYEE.csv`
- `ACCOUNT.csv`
- `ACCOUNT_ROLE.csv`
- `AREA.csv`
- `AREA_BOUNDARY.csv`
- `STREET.csv`
- `NODE.csv`
- `SEGMENT.csv`
- `TRAFFIC_SOURCE.csv`

### 5. Cấu hình Goong API key

Tạo file `API_Key` ở thư mục chạy ứng dụng:

```text
GOONG_API_KEY=your_goong_api_key_here
```

Hoặc cấu hình biến môi trường `GOONG_API_KEY`.

### 6. Chạy ứng dụng trên Apache NetBeans

- Mở Apache NetBeans.
- Chọn `File` -> `Open Project`.
- Chọn thư mục project `trafficsystem`.
- Đợi NetBeans tải Maven dependencies.
- Chạy project bằng nút `Run Maven`.
- Nhấn nút `Others goal` rồi nhập `javafx:run`
Có thể chạy bằng terminal nếu cần:

```bash
mvn clean javafx:run
```

### 7. Chạy Kestra bằng Docker Compose

Sau khi ứng dụng và CSDL đã sẵn sàng, chạy Kestra:

```bash
docker compose up -d
```

Mở trình duyệt và truy cập:

```text
http://localhost:8080
```

Tài khoản Kestra mặc định trong `docker-compose.yaml`:

```text
Username: admin@kestra.io
Password: Admin1234!
```

Import flow từ file:

```text
traffic_every_30_minutes.yaml
```

Sau đó chạy flow trên Kestra để nạp dữ liệu từ `TRAFFIC_SOURCE` sang `TRAFFIC`. Flow này được cấu hình chạy tự động mỗi 30 phút.
