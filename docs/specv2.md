# Đặc tả hệ thống đặt phòng khách sạn

## 1. Mục tiêu và phạm vi

Hệ thống cung cấp backend REST cho nghiệp vụ đặt phòng khách sạn. Pha 1 ưu tiên luồng hoạt động đầy đủ, dễ chạy và dễ trình bày; Pha 2 dùng số đo để đánh giá một cải tiến chất lượng.

### Trong phạm vi Pha 1

- Đăng ký, đăng nhập và xác thực bằng JWT.
- Quản lý phòng: xem danh sách/chi tiết; ADMIN tạo, cập nhật và ngừng kinh doanh phòng.
- Đặt phòng một phòng cho một khoảng ngày; xem đơn, xem lịch sử và hủy đơn theo quy tắc.
- API dùng JSON, có tài liệu OpenAPI/Swagger.
- Cơ sở dữ liệu MySQL; đóng gói ứng dụng và DB bằng Docker Compose.
- Có dữ liệu seed tối thiểu và kịch bản tải cố định để chạy trên Kaggle CPU, lưu kết quả baseline.

### Ngoài phạm vi MVP

Không triển khai thanh toán (thật hay mô phỏng), không nhận/lưu thông tin thẻ, nhiều phòng trong một đơn, email/thông báo, đánh giá, ảnh, dashboard, refresh token, cache hoặc microservices. Nếu giảng viên yêu cầu thay đổi phạm vi, nhóm cập nhật cả đặc tả và kế hoạch trước khi bắt đầu.

## 2. Đối chiếu với đề bài

| Yêu cầu | Đáp ứng trong thiết kế | Cách chứng minh |
|---|---|---|
| REST API, JSON | Controller trả request/response DTO | OpenAPI và ví dụ request/response trong README |
| Có POST, GET, DELETE | Đăng ký/đăng nhập/đặt phòng; đọc phòng/đơn; hủy đơn | Thử qua Swagger |
| OpenAPI/Swagger | springdoc-openapi | Swagger UI chạy cùng ứng dụng |
| API → nghiệp vụ → truy cập dữ liệu | Controller gọi use case/service; service gọi port/repository; adapter JPA truy cập MySQL | Sơ đồ kiến trúc và kiểm tra dependency |
| Nghiệp vụ không import web/DB | Domain và use case không import Spring Web, JPA hay entity; dùng DTO/domain và port | Review package/import |
| Đăng nhập và bảo vệ endpoint | Spring Security + JWT filter; bảo vệ `GET /api/users/me` và `POST /api/bookings` | Demo request không token bị từ chối, có token thành công |
| Docker | Compose chạy app và MySQL, có healthcheck | Hướng dẫn chạy từ clone mới |
| README, GitHub công khai, commit cẩn thận | README nêu kiến trúc, chạy, API; commit nhỏ, có review | Repo và lịch sử PR |
| Tải trên Kaggle CPU | Kịch bản, dữ liệu và tải cố định | Ghi phần cứng, tham số, kết quả baseline |

Đề bài không yêu cầu MySQL, JWT, modular monolith, thanh toán, khóa chống đặt trùng hay bốn package mỗi module. Đây là lựa chọn của nhóm; không nên trình bày chúng như yêu cầu bắt buộc của giảng viên.

## 3. Kiến trúc đề xuất

Chọn **modular monolith theo nghiệp vụ** với một ứng dụng Spring Boot và một MySQL. Ba module là `auth`, `inventory` và `reservation`; chưa cần tách thành các dịch vụ hay database riêng.

```text
uet.edu.net.booking_service
├── core/                 # cấu hình bảo mật, lỗi chung, OpenAPI
└── module/
    ├── auth/
    ├── inventory/
    └── reservation/
```

Mỗi module chia `api`, `service` và `persistence`. `auth` và `inventory` công khai `contract`/DTO cùng port cần thiết để `reservation` phụ thuộc một chiều; domain model riêng chỉ thêm khi khác biệt thực sự với DTO hoặc entity. Tránh tạo lớp chỉ để khớp sơ đồ nếu chúng không có trách nhiệm riêng.

Quy tắc phụ thuộc:

- `api` nhận/trả DTO, gọi use case; không chứa truy vấn DB hay luật nghiệp vụ.
- `service` chứa luật nghiệp vụ và điều phối use case; không import Spring Web, Spring Data/JPA hoặc entity.
- `persistence` ánh xạ entity và cài đặt repository/port.
- `core` không phụ thuộc vào module nghiệp vụ.
- Module khác chỉ dùng interface/DTO công khai của module sở hữu dữ liệu; không gọi repository hoặc dùng entity chéo module.
- `reservation` có thể hỏi `auth` thông tin chủ đơn và `inventory` thông tin phòng; chiều phụ thuộc không quay ngược lại.

`@Transactional` phải bao trọn một ca sử dụng cần tính nguyên tử, không đặt tùy tiện trên từng repository. Nếu yêu cầu service thuần Java được hiểu nghiêm ngặt, đặt transaction trong adapter/application boundary của Spring và giữ luật nghiệp vụ thuần trong domain/use case.

## 4. Vai trò và mô hình dữ liệu tối thiểu

Vai trò: `ADMIN`, `CUSTOMER`. API đăng ký luôn gán `CUSTOMER`. Tài khoản ADMIN demo được seed bởi `CommandLineRunner` chỉ khi có `APP_ADMIN_EMAIL` và `APP_ADMIN_PASSWORD`; password được BCrypt-hash, seeding idempotent theo email và không log mật khẩu. Hai biến chỉ đặt trong `.env` không commit hoặc environment của môi trường chạy.

Các bảng dự kiến:

- `users`: id, email (unique), password hash, full name, phone, role.
- `rooms`: id, room number (unique), type, capacity, base price, description, status (`ACTIVE`/`INACTIVE`).
- `bookings`: id, user id, check-in, check-out, guest count, total price do server tính, status (`CONFIRMED` hoặc `CANCELLED`), timestamps. Booking được tạo là `CONFIRMED`; hủy chuyển sang `CANCELLED`. Không có `PENDING` hay `PAID` vì MVP không có thanh toán.
- `booking_details`: booking, room, giá mỗi đêm tại thời điểm đặt (snapshot). Bản đầu cho phép một chi tiết/phòng cho mỗi booking.
- `booking_nights`: booking, room, night date; unique `(room_id, night_date)` để DB ngăn hai đơn giữ cùng một phòng trong cùng một đêm.

Thanh toán nằm ngoài MVP; API không nhận thông tin thẻ hoặc CVV.

Quy tắc dữ liệu/nghiệp vụ:

- `check_out > check_in`; check-in không ở quá khứ; số khách dương và không vượt sức chứa.
- Chỉ đặt phòng `ACTIVE`; giá và tổng tiền do server tính, không tin giá từ client.
- Hủy đơn chỉ do chủ đơn hoặc ADMIN; không xóa cứng lịch sử booking.
- Email duy nhất; mật khẩu lưu bằng BCrypt; không cho client tự chỉ định role.

## 5. Hợp đồng API bản đầu

| Method | Endpoint | Quyền | Mục đích |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Tạo tài khoản CUSTOMER |
| POST | `/api/auth/login` | Public | Đăng nhập, nhận JWT |
| GET | `/api/users/me` | CUSTOMER/ADMIN | Xem hồ sơ của mình |
| PUT | `/api/users/me/password` | CUSTOMER/ADMIN | Đổi mật khẩu |
| GET | `/api/rooms` | Public | Danh sách, lọc cơ bản theo type/capacity/giá |
| GET | `/api/rooms/{id}` | Public | Chi tiết phòng |
| POST | `/api/rooms` | ADMIN | Tạo phòng |
| PUT | `/api/rooms/{id}` | ADMIN | Cập nhật phòng |
| DELETE | `/api/rooms/{id}` | ADMIN | Ngừng kinh doanh (soft delete) |
| POST | `/api/bookings` | CUSTOMER | Tạo đơn cho một phòng |
| GET | `/api/bookings/me` | CUSTOMER | Lịch sử đơn của mình |
| GET | `/api/bookings/{id}` | Chủ đơn/ADMIN | Chi tiết đơn |
| DELETE | `/api/bookings/{id}` | Chủ đơn/ADMIN | Hủy đơn theo quy tắc |

Phần mở rộng sau MVP: `GET /api/rooms/available` (TV3 sở hữu endpoint và truy vấn booking; TV2 cung cấp room contract) và `GET /api/bookings` dành cho ADMIN. Tối thiểu phải có một GET và một POST được bảo vệ bằng filter; đề không buộc phải dùng JWT cụ thể.

## 6. Chống đặt trùng và luồng đặt phòng

Khoảng lưu trú dùng quy ước nửa mở `[check_in, check_out)`: ngày check-out không tính là đêm lưu trú. Vì vậy hai booking cùng phòng 12→14 và 14→16 không xung đột.

Để tránh hai request đồng thời đặt cùng đêm, dùng bảng `booking_nights` với unique `(room_id, night_date)`. Chèn booking và các đêm giữ chỗ trong cùng transaction; duplicate key được chuyển thành HTTP 409 `ROOM_NOT_AVAILABLE`. Hủy booking phải xóa các đêm giữ chỗ trong cùng transaction. Cơ chế này là lựa chọn thống nhất của nhóm cho Pha 1; không triển khai thêm khóa bi quan song song.

Luồng bản đầu nên giữ ngắn: xác thực request → kiểm tra ngày/phòng/sức chứa → tính giá server → một transaction tạo booking và giữ ngày → trả DTO. Chưa cần trạng thái thanh toán `PENDING/PAID`, hai transaction hay cronjob nếu không có tích hợp thanh toán; các phần này làm tăng đáng kể độ phức tạp và không được đề bài yêu cầu.

## 7. Kiểm thử, chạy và triển khai

- Unit test cho luật ngày, giá, sức chứa và quyền sở hữu; integration test cho transaction và unique constraint của `booking_nights`.
- Compose khởi động MySQL trước khi app qua healthcheck; app kết nối bằng hostname service `db:3306`. Port host chỉ phục vụ Workbench và có thể đổi để tránh xung đột.
- DB credentials và JWT secret lấy từ `.env` không commit hoặc biến môi trường. `ddl-auto=update` chỉ dùng phát triển; trước khi cần môi trường lặp lại ổn định, chuyển schema sang migration có version.
- Kaggle CPU: ghi phiên bản commit, cấu hình CPU/RAM, Java/MySQL, dữ liệu seed, công cụ, số người dùng ảo, thời lượng, tỷ lệ đọc/ghi, latency p50/p95, throughput và lỗi. Cố định kịch bản để Pha 2 chạy lại cùng điều kiện.

## 8. Phân biệt Pha 1 và Pha 2

Pha 1 hoàn thành chức năng cơ bản, an toàn và có thể chạy lại; thực hiện tải baseline theo yêu cầu môn học nhưng không tối ưu sớm. Pha 2 chọn một nút thắt nhìn thấy từ baseline (ví dụ truy vấn danh sách phòng hoặc truy vấn availability), nêu giả thuyết, thay đổi một yếu tố, chạy lại cùng workload/phần cứng và so sánh số đo. Không tuyên bố cải thiện nếu thiếu số đo trước/sau.

Cache, tối ưu index, pool, rate limit, async hoặc thay kiến trúc chỉ là ứng viên Pha 2 sau khi có bằng chứng; không mặc định phải triển khai tất cả.

## 9. Đánh giá và quyết định đã chốt

Thiết kế modular monolith, MySQL trong Compose, JWT filter, OpenAPI và repository đáp ứng bài tập mà vẫn giữ ứng dụng đơn giản để triển khai.

Nhóm thống nhất các quyết định sau cho Pha 1:

1. Service là Spring bean thông thường (`@Service`) và có thể dùng `@Transactional` ở use case ghi để bảo đảm nguyên tử. Service không import Spring Web, Spring Data/JPA hoặc entity; truy cập dữ liệu qua repository/port.
2. Thực hiện và lưu baseline tải trên Kaggle CPU trong Pha 1; Pha 2 chạy lại cùng workload và cấu hình để so sánh trước/sau.
3. MVP chỉ đặt một phòng mỗi booking, không tích hợp hay mô phỏng thanh toán, không nhận dữ liệu thẻ.
4. Dùng `booking_nights` với unique `(room_id, night_date)` để ngăn đặt trùng; tạo/hủy booking và giữ/nhả các đêm trong transaction.
5. `GET /api/rooms/available` và `GET /api/bookings` dành cho ADMIN để sau MVP. Không triển khai cache, cronjob, nhiều phòng mỗi đơn hay microservices trong Pha 1.

Những lựa chọn này là phạm vi triển khai của nhóm, không phải yêu cầu nguyên văn của đề. Chỉ thay đổi khi giảng viên yêu cầu hoặc nhóm thống nhất cập nhật đồng thời đặc tả và kế hoạch.

## 10. Phụ lục — hợp đồng giữa các module

Các signature dưới đây là hợp đồng Pha 1. TV1 và TV2 tạo đúng các method/field này trước khi TV3 bắt đầu tích hợp; implementation có thể bổ sung method nội bộ nhưng không đổi hợp đồng nếu chưa cập nhật tài liệu và báo các thành viên phụ thuộc.

```java
package uet.edu.net.booking_service.module.auth.contract;

public interface AuthServiceContract {
    /** Tìm user theo email lấy từ JWT principal. Ném AppException(USER_NOT_FOUND) nếu thiếu. */
    UserDTO getUserByEmail(String email);
}
```

`UserDTO` fields: `Long id`, `String email`, `String fullName`, `String role`.

```java
package uet.edu.net.booking_service.module.inventory.contract;

public interface RoomServiceContract {
    /** Lấy phòng ACTIVE để tạo booking; ném AppException(ROOM_NOT_FOUND) nếu thiếu/INACTIVE. */
    RoomDTO getActiveRoomById(Long roomId);
}
```

`RoomDTO` fields: `Long id`, `String roomNumber`, `String roomType`, `BigDecimal basePrice`, `int capacity`, `String status`.

`reservation` lấy email từ principal đã xác thực, gọi Auth contract để lấy `userId`, và gọi Room contract để lấy giá/sức chứa/trạng thái. Room contract không có method khóa; chống đặt trùng do unique constraint của `booking_nights`.

Booking status đặt trong domain Reservation; persistence lưu enum dưới dạng chuỗi (`EnumType.STRING`):

```java
package uet.edu.net.booking_service.module.reservation.service.domain;

public enum BookingStatus {
    CONFIRMED,
    CANCELLED
}
```

## 11. Hạn chốt hợp đồng và enum booking

- **Hạn:** cuối ngày làm việc đầu tiên của nhóm; TV1/TV2 phải commit contract và DTO trước khi TV3 bắt đầu tích hợp. Nếu nhóm đã qua mốc này, chốt ngay trong buổi làm việc kế tiếp và không để TV3 tự đặt signature.
- Booking status chỉ có `CONFIRMED` và `CANCELLED`. Tạo thành công → `CONFIRMED`; hủy thành công → `CANCELLED`. Chuyển đổi status và thêm/xóa `booking_nights` cùng transaction.
- TV1 seed ADMIN bằng `CommandLineRunner` khi cả `APP_ADMIN_EMAIL` và `APP_ADMIN_PASSWORD` được cấu hình. Nếu chỉ có một biến hoặc giá trị rỗng thì app báo lỗi cấu hình; nếu tài khoản đã tồn tại thì giữ nguyên, không reset mật khẩu.

