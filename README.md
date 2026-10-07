#  Booking Service — Hotel Reservation Backend

REST API backend cho hệ thống đặt phòng khách sạn, xây dựng bằng **Spring Boot 4** và **MySQL**.

---

##  Mục lục

- [Giới thiệu](#giới-thiệu)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Kiến trúc hệ thống](#kiến-trúc-hệ-thống)
- [Tính năng](#tính-năng)
- [Cài đặt và chạy](#cài-đặt-và-chạy)
- [Cấu hình môi trường](#cấu-hình-môi-trường)
- [API Endpoints](#api-endpoints)
- [Luồng nghiệp vụ](#luồng-nghiệp-vụ)

---

## Giới thiệu

Hệ thống cung cấp các REST API cho phép:

- Người dùng **đăng ký**, **đăng nhập** và quản lý tài khoản bằng JWT.
- Xem danh sách **phòng** và đặt phòng theo ngày.
- Thực hiện **thanh toán mô phỏng** (kiểm tra số thẻ Luhn, tạo transaction ref ngẫu nhiên).
- **Hủy đơn** đặt phòng khi còn trong trạng thái chờ.
- ADMIN quản lý kho phòng (thêm, sửa, ngừng kinh doanh).

---

## Công nghệ sử dụng

| Thành phần                     | Phiên bản |
| ------------------------------ | --------- |
| Java                           | 21        |
| Spring Boot                    | 4.1.x     |
| Spring Security + JWT (JJWT)   | 0.12.x    |
| MySQL                          | 9.x       |
| JPA / Hibernate                | 7.x       |
| Docker + Docker Compose        | —         |
| springdoc-openapi (Swagger UI) | —         |

---

## Kiến trúc hệ thống

Dự án theo kiến trúc **Modular Monolith** — một ứng dụng Spring Boot duy nhất, chia theo nghiệp vụ thành 3 module độc lập:

```
uet.edu.net.booking_service
├── core/                   # Cấu hình bảo mật, xử lý lỗi chung, OpenAPI
└── module/
    ├── auth/               # Đăng ký, đăng nhập, quản lý tài khoản
    │   ├── api/            # AuthController, UserController
    │   ├── contract/       # AuthServiceContract, UserDTO  ← public
    │   ├── persistence/    # UserEntity, JPA repository
    │   └── service/        # AuthService, UserService, UserDetailsService
    ├── inventory/          # Quản lý kho phòng
    │   ├── api/            # RoomController
    │   ├── contract/       # RoomServiceContract, RoomDTO  ← public
    │   ├── domain/         # Room, RoomStatus, RoomType
    │   ├── persistence/    # RoomEntity, JPA repository
    │   └── service/        # RoomService, RoomServiceContractImpl
    └── reservation/        # Đặt phòng, thanh toán, hủy đơn
        ├── controller/     # BookingController
        ├── dto/            # CreateBookingRequest, PayBookingRequest, ...
        ├── entity/         # Booking, BookingDetail, Payment
        ├── repository/     # BookingRepository, BookingDetailRepository
        ├── service/        # BookingService, PaymentService
        └── domain/         # BookingPolicy (luật nghiệp vụ ngày/giá)
```

**Luồng phụ thuộc:**  
`api → service → port/contract → persistence`  
Module `reservation` chỉ giao tiếp với `auth` và `inventory` qua interface công khai (`contract/`) — không bao giờ gọi trực tiếp repository của module khác.

---

## Tính năng

### Auth

-  Đăng ký tài khoản (role mặc định: `CUSTOMER`)
-  Đăng nhập, nhận JWT Bearer token
-  Xem hồ sơ cá nhân (`/api/users/me`)
-  Đổi mật khẩu
-  Tài khoản ADMIN được seed tự động qua `AdminSeeder`

### Inventory (Quản lý phòng)

-  Xem danh sách phòng đang hoạt động, có filter theo loại phòng, sức chứa, giá
-  Xem chi tiết phòng
-  ADMIN: Tạo phòng mới
-  ADMIN: Cập nhật thông tin phòng
-  ADMIN: Ngừng kinh doanh phòng (soft delete: `ACTIVE` → `INACTIVE`)

### Reservation (Đặt phòng)

-  Tạo đơn đặt phòng (nhận diện user từ JWT)
-  Xem lịch sử đặt phòng của chính mình
-  Xem lịch sử đặt phòng theo userId
-  Xem chi tiết 1 đơn
-  Thanh toán mô phỏng (kiểm tra Luhn, tạo transaction ref)
-  Hủy đơn (chỉ chủ đơn, chỉ khi đang `PENDING`)
-  Chống đặt trùng phòng (kiểm tra overlap ngày)

---

## Cài đặt và chạy

### Yêu cầu

- Java 21+
- Maven 3.9+
- MySQL đang chạy (cấu hình port trong `application.properties`)

### Chạy trực tiếp

```bash
# Clone repo
git clone <repo-url>
cd backend

# Build và chạy
./mvnw spring-boot:run
```

Ứng dụng sẽ khởi động tại: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### Chạy bằng Docker Compose

```bash
docker compose up --build
```

> Database `booking_db` được tạo tự động. Bảng được tạo qua `ddl-auto=update`.

---

## Cấu hình môi trường


| Biến                         | Mô tả                                                | Mặc định                                     |
| ---------------------------- | ---------------------------------------------------- | -------------------------------------------- |
| `SPRING_DATASOURCE_URL`      | JDBC URL kết nối MySQL                               | `jdbc:mysql://localhost:3306/booking_db?...` |
| `spring.datasource.username` | Username MySQL                                       | `root`                                       |
| `spring.datasource.password` | Password MySQL                                       | —                                            |
| `JWT_SECRET`                 | Chuỗi bí mật ký JWT (≥32 ký tự)                      | fallback dev (không dùng production)         |
| `APP_ADMIN_EMAIL`            | Email tài khoản ADMIN được seed                      | —                                            |
| `APP_ADMIN_PASSWORD`         | Mật khẩu tài khoản ADMIN (≥8 ký tự, có chữ hoa + số) | —                                            |


---

## API Endpoints

>  = Yêu cầu header `Authorization: Bearer <token>`  
>  = Chỉ ADMIN

### Auth

| Method | Endpoint             | Quyền  | Mô tả                      | Request Body                                   | Response                           |
| ------ | -------------------- | ------ | -------------------------- | ---------------------------------------------- | ---------------------------------- |
| POST   | `/api/auth/register` | Public | Đăng ký tài khoản CUSTOMER | `email`, `password`, `fullName`, `phoneNumber` | `201 UserDTO`                      |
| POST   | `/api/auth/login`    | Public | Đăng nhập, nhận JWT        | `email`, `password`                            | `accessToken`, `tokenType`, `role` |

### Users

| Method | Endpoint                 | Quyền | Mô tả             | Request Body                 | Response         |
| ------ | ------------------------ | ----- | ----------------- | ---------------------------- | ---------------- |
| GET    | `/api/users/me`          | 🔒    | Xem hồ sơ cá nhân | —                            | `UserDTO`        |
| PUT    | `/api/users/me/password` | 🔒    | Đổi mật khẩu      | `oldPassword`, `newPassword` | `204 No Content` |

### Rooms

| Method | Endpoint          | Quyền  | Mô tả                                      | Request Body / Params                                            | Response             |
| ------ | ----------------- | ------ | ------------------------------------------ | ---------------------------------------------------------------- | -------------------- |
| GET    | `/api/rooms`      | Public | Danh sách phòng ACTIVE                     | Query: `roomType`, `capacity`, `minPrice`, `maxPrice`            | `List<RoomResponse>` |
| GET    | `/api/rooms/{id}` | Public | Chi tiết một phòng ACTIVE                  | —                                                                | `RoomResponse`       |
| POST   | `/api/rooms`      | 🔒 🛡️  | Tạo phòng mới                              | `roomNumber`, `roomType`, `capacity`, `basePrice`, `description` | `201 RoomResponse`   |
| PUT    | `/api/rooms/{id}` | 🔒 🛡️  | Cập nhật thông tin phòng                   | `roomNumber`, `roomType`, `capacity`, `basePrice`, `description` | `RoomResponse`       |
| DELETE | `/api/rooms/{id}` | 🔒 🛡️  | Ngừng kinh doanh phòng (ACTIVE → INACTIVE) | —                                                                | `204 No Content`     |

### Bookings

| Method | Endpoint                      | Quyền       | Mô tả                                 | Request Body / Params                                 | Response                |
| ------ | ----------------------------- | ----------- | ------------------------------------- | ----------------------------------------------------- | ----------------------- |
| POST   | `/api/bookings`               | 🔒 CUSTOMER | Tạo đơn đặt phòng (userId lấy từ JWT) | `roomId`, `checkInDate`, `checkOutDate`, `totalGuest` | `BookingResponse`       |
| GET    | `/api/bookings/my-bookings`   | 🔒          | Lịch sử đặt phòng của mình (từ JWT)   | —                                                     | `List<BookingResponse>` |
| GET    | `/api/bookings/user/{userId}` | 🔒          | Lịch sử đặt phòng theo userId         | —                                                     | `List<BookingResponse>` |
| GET    | `/api/bookings/{id}`          | 🔒          | Chi tiết 1 đơn đặt phòng              | —                                                     | `BookingResponse`       |
| POST   | `/api/bookings/{id}/pay`      | 🔒          | Thanh toán đơn (PENDING → PAID)       | `cardNumber`, `paymentMethod`                         | `PaymentResponse`       |
| DELETE | `/api/bookings/{id}/cancel`   | 🔒          | Hủy đơn (PENDING → CANCELLED)         | Query: `?userId=`                                     | `BookingResponse`       |

---

## Luồng nghiệp vụ

### Trạng thái đơn đặt phòng

```
          ┌─────────┐
    tạo   │         │   thanh toán
  ──────► │ PENDING │ ──────────────► PAID
          │         │
          │         │   hủy đơn
          └─────────┘ ──────────────► CANCELLED
```

> Chỉ có thể thanh toán hoặc hủy khi đơn đang ở `PENDING`.

### Tính giá

```
total_price = basePrice × số_đêm
số_đêm = checkOutDate - checkInDate  (ngày check-out không tính là đêm lưu trú)
```

### Chống đặt trùng

Trước khi tạo booking mới, hệ thống kiểm tra danh sách phòng đã được đặt trong khoảng ngày đó (`findBookedRoomIds`). Hai booking `[12→14]` và `[14→16]` **không xung đột** (khoảng nửa mở).
