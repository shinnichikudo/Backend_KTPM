# Booking Service

Dịch vụ backend đặt phòng khách sạn, xây dựng bằng Java 21 và Spring Boot. Hệ thống cung cấp REST API trao đổi JSON, lưu dữ liệu trong MySQL và dùng JWT để xác thực các thao tác cần tài khoản.

## 1. Bài toán và phạm vi

Khách cần xem phòng khách sạn, chọn ngày lưu trú và đặt phòng mà không tạo ra hai booking chiếm cùng một phòng trong cùng một đêm. Nhân viên quản trị cần quản lý danh mục phòng và tra cứu các booking để hỗ trợ vận hành.

Phiên bản này tập trung vào ba nghiệp vụ: quản lý tài khoản, quản lý phòng và đặt phòng. Một booking áp dụng cho **một phòng** và một khoảng ngày. Giá được tính theo giá cơ bản của phòng nhân số đêm. Hệ thống có thanh toán mô phỏng để hoàn chỉnh luồng booking, nhưng không kết nối ngân hàng hay cổng thanh toán thật.

### Vai trò

- **CUSTOMER:** đăng ký, đăng nhập, xem/cập nhật hồ sơ mật khẩu, tra cứu phòng, tạo booking, xem booking của mình, thanh toán mô phỏng và hủy booking khi trạng thái cho phép.
- **ADMIN:** quản lý phòng; xem booking theo tài khoản, xem chi tiết và hủy booking.
- **Khách chưa đăng nhập:** xem danh sách và thông tin chi tiết phòng; đăng ký hoặc đăng nhập.

### Quy tắc nghiệp vụ chính

1. Booking mới có trạng thái `PENDING`. Hệ thống xác định chủ booking từ email trong JWT, không nhận `userId` từ request tạo booking.
2. Ngày nhận phòng phải trước ngày trả phòng; số khách phải nằm trong sức chứa phòng. Tổng tiền bằng `basePrice × số đêm`, trong đó ngày trả phòng không tính thành một đêm.
3. Các đêm được giữ trong `booking_nights`. Ràng buộc duy nhất trên `(room_id, night_date)` ngăn hai booking cùng giữ một phòng/đêm, kể cả khi có request đồng thời. Hai khoảng liền kề `[12, 14)` và `[14, 16)` không trùng nhau.
4. Thanh toán mô phỏng hợp lệ chuyển booking `PENDING` sang `PAID`. Hủy chuyển `PENDING` sang `CANCELLED` và giải phóng các đêm đã giữ. Booking đã thanh toán hiện chưa hỗ trợ hủy/hoàn tiền.
5. API chi tiết booking kiểm tra chủ sở hữu; ADMIN được xem booking của mọi người. API thanh toán chỉ cho CUSTOMER thanh toán booking của chính họ.

## 2. API và bảo mật

API tuân theo mô hình REST, dùng các method HTTP phù hợp (`GET`, `POST`, `PUT`, `DELETE`) và JSON request/response. Tài liệu tương tác được cung cấp bằng Swagger UI tại <http://localhost:8080/swagger-ui/index.html> khi ứng dụng đang chạy.

| Method | Endpoint | Quyền | Mục đích |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Public | Đăng ký tài khoản CUSTOMER |
| `POST` | `/api/auth/login` | Public | Đăng nhập và nhận access token |
| `GET` | `/api/users/me` | Đã đăng nhập | Xem hồ sơ của mình |
| `PUT` | `/api/users/me/password` | Đã đăng nhập | Đổi mật khẩu |
| `GET` | `/api/rooms` | Public | Liệt kê phòng ACTIVE; lọc bằng `roomType`, `capacity`, `minPrice`, `maxPrice` |
| `GET` | `/api/rooms/{id}` | Public | Xem chi tiết phòng ACTIVE |
| `POST` | `/api/rooms` | ADMIN | Tạo phòng |
| `PUT` | `/api/rooms/{id}` | ADMIN | Cập nhật phòng |
| `DELETE` | `/api/rooms/{id}` | ADMIN | Ngừng kinh doanh phòng |
| `POST` | `/api/bookings` | CUSTOMER | Tạo booking cho tài khoản đang đăng nhập |
| `GET` | `/api/bookings/my-bookings` | CUSTOMER/ADMIN | Xem booking của tài khoản đang đăng nhập |
| `GET` | `/api/bookings/user/{userId}` | ADMIN | Tra cứu booking theo người dùng |
| `GET` | `/api/bookings/{id}` | Chủ booking/ADMIN | Xem chi tiết booking |
| `POST` | `/api/bookings/{id}/pay` | CUSTOMER là chủ booking | Thanh toán mô phỏng |
| `DELETE` | `/api/bookings/{id}` | Chủ booking/ADMIN | Hủy booking nếu trạng thái cho phép |

Spring Security áp dụng xác thực và phân quyền ở filter chain; JWT được kiểm tra trong `JwtAuthFilter`. Quyền sở hữu booking được kiểm tra trong nghiệp vụ. Các lỗi API dùng phản hồi JSON có `code` và `message`; dữ liệu đầu vào được kiểm tra bằng Bean Validation ở tầng API.

## 3. Kiến trúc và tổ chức mã nguồn

Dự án là **modular monolith**: một ứng dụng backend được chia thành module theo nghiệp vụ. Mã nguồn chính nằm trong `backend/src/main/java/uet/edu/net/booking_service/`:

```text
+-- core/
|   +-- config/       # Security, CORS, OpenAPI
|   +-- exception/    # Mã lỗi và xử lý lỗi dùng chung
|   +-- security/     # JWT request filter
+-- module/
    +-- auth/         # Đăng ký, đăng nhập, tài khoản và hồ sơ
    +-- inventory/    # Danh mục và trạng thái phòng
    +-- reservation/ # Booking, giữ đêm và thanh toán mô phỏng
```

Trong từng module, các phần quan trọng được phân lớp:

- `api/`: controller, request/response DTO và chuyển đổi dữ liệu HTTP.
- `domain/`, `service/`: mô hình và quy tắc nghiệp vụ thuần Java.
- `service/port/` hoặc `contract/`: giao diện mà nghiệp vụ cần để truy cập dữ liệu hay gọi module khác.
- `persistence/`: entity JPA, Spring Data repository và adapter ánh xạ giữa database với mô hình nghiệp vụ.
- `config/`: khởi tạo use case và tích hợp transaction của Spring ở bên ngoài nghiệp vụ.

Luồng xử lý là **API → use case nghiệp vụ → port/contract → adapter persistence**. Các use case chính (`AuthServiceImpl`, `UserServiceImpl`, `RoomService`, `BookingService`, `PaymentService`) không phụ thuộc controller, Spring Data hay entity JPA. Vì vậy tầng nghiệp vụ đáp ứng yêu cầu không import framework web hoặc thư viện truy cập database; framework được dùng ở các lớp API, cấu hình và adapter.

## 4. Framework và yêu cầu kỹ thuật

| Yêu cầu | Cách đáp ứng |
| --- | --- |
| REST API, giao tiếp JSON | Spring MVC controllers và DTO cho request/response |
| Có `POST`, `GET`, `DELETE` | Đăng ký/đăng nhập, tra cứu phòng/booking, ngừng kinh doanh phòng và hủy booking |
| Tài liệu API | springdoc OpenAPI và Swagger UI |
| Tách API, nghiệp vụ, truy cập dữ liệu | Các lớp `api`, use case/domain, port và adapter `persistence` |
| ORM / Data Access Layer | Spring Data JPA, Hibernate, MySQL và repository adapter |
| Đăng nhập và endpoint được bảo vệ | JWT Bearer token, Spring Security filter chain và phân quyền theo role |
| Đóng gói triển khai | Dockerfile nhiều giai đoạn và Docker Compose gồm ứng dụng, MySQL, health check và volume |
| Quản lý cấu hình nhạy cảm | `.env.example` làm mẫu; `.env` và cấu hình local được Git ignore |

Framework sử dụng: **Spring Boot 4.1.1**, **Spring Security**, **Spring Data JPA/Hibernate**, **JJWT 0.12.6**, **springdoc OpenAPI 3.0.0**, **MySQL 8.0** và **Docker Compose**.

## 5. Chạy ứng dụng

Cần Docker Desktop với Docker Engine đang chạy. Từ thư mục gốc repository, tạo file cấu hình local:

```powershell
Copy-Item .env.example .env
```

Đổi giá trị mật khẩu MySQL và `JWT_SECRET` trong `.env`; không commit file này. `MYSQL_HOST_PORT` là cổng MySQL mở trên máy host và có thể đổi nếu bị trùng. Ứng dụng trong Docker luôn kết nối MySQL qua tên service và cổng nội bộ `3306`.

Khởi động và xem trạng thái:

```powershell
docker compose up --build -d
docker compose ps
docker compose logs -f app
```

Mở Swagger UI: <http://localhost:8080/swagger-ui/index.html>. Chọn **Authorize**, nhập `Bearer <accessToken>` sau khi đăng nhập để gọi API cần xác thực. Nếu muốn tự tạo ADMIN, điền cả `APP_ADMIN_EMAIL` và `APP_ADMIN_PASSWORD` trong `.env`; nếu để trống cả hai, ứng dụng bỏ qua bước seed ADMIN.

Dừng container nhưng giữ dữ liệu database:

```powershell
docker compose down
```

Xóa cả volume và dữ liệu local (không thể khôi phục từ lệnh này):

```powershell
docker compose down -v
```

Compose tạo database `booking_db`; Hibernate cập nhật schema khi ứng dụng khởi động. Không cần tạo database thủ công trong Workbench.

## 6. Giới hạn hiện tại và nội dung demo

- Thanh toán là mô phỏng, không xác nhận giao dịch với bên thứ ba; trạng thái `SUCCESS` chỉ biểu thị luồng demo hoàn tất.
- Chưa có hủy booking đã thanh toán và hoàn tiền. ADMIN chỉ hủy được booking đang ở trạng thái cho phép.
- Booking `PENDING` giữ đêm cho tới khi được thanh toán hoặc hủy; phiên bản hiện tại chưa có thời hạn tự giải phóng booking chờ.
- Danh sách phòng hiện lọc theo thuộc tính phòng, chưa có endpoint tra cứu phòng trống theo khoảng ngày.
- Schema được quản lý bằng `ddl-auto=update` để thuận tiện cho MVP; hệ thống dài hạn nên dùng công cụ migration schema.
- Kiểm thử tải trên Kaggle CPU là yêu cầu triển khai riêng; kết quả cần được ghi nhận sau khi nhóm chạy và xác nhận thực nghiệm.

Các giới hạn này xác định rõ phạm vi bản MVP và là hướng phát triển tiếp theo, không thay đổi các yêu cầu kỹ thuật nền tảng của bài tập.
