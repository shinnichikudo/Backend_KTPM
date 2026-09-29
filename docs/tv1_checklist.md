# Checklist TV1 — Base, Auth và Security

Phạm vi và tiêu chí lấy theo [team_plan_pha1.md](team_plan_pha1.md) và [specv2.md](specv2.md). Cập nhật ô kiểm sau khi hoàn thành hoặc kiểm tra lại từng mục.

## Trạng thái hiện tại

- [x] Docker Compose khởi động MySQL và backend; Swagger truy cập được.
- [x] Đăng ký thành công trả HTTP `201`, role mặc định `CUSTOMER`.
- [x] Mật khẩu yếu bị từ chối với `WEAK_PASSWORD`.
- [x] Đăng nhập đúng trả HTTP `200` và JWT Bearer.
- [x] Đăng ký lại email đã tồn tại trả HTTP `409`.
- [x] Đăng nhập sai mật khẩu trả HTTP `401`.
- [x] Các thay đổi Auth hiện tại đã được commit.

## TV1 — Việc cần hoàn thành

### 1. Hồ sơ người dùng

- [ ] `GET /api/users/me`: chỉ lấy danh tính từ principal/JWT, không nhận user id từ request.
- [ ] Endpoint trả đúng `id`, `email`, `fullName`, `role` theo `UserDTO`.
- [ ] Không có token hoặc token sai: request bị từ chối.
- [ ] Có token hợp lệ: trả hồ sơ đúng user đang đăng nhập.

### 2. Đổi mật khẩu

- [ ] Hoàn thiện request DTO cho `PUT /api/users/me/password`.
- [ ] Yêu cầu mật khẩu hiện tại hợp lệ trước khi đổi.
- [ ] Mật khẩu mới được kiểm tra theo chính sách và BCrypt-hash trước khi lưu.
- [ ] Mật khẩu hiện tại sai bị từ chối; mật khẩu cũ không đăng nhập được sau khi đổi, mật khẩu mới đăng nhập được.
- [ ] Endpoint yêu cầu JWT hợp lệ.

### 3. Seed ADMIN

- [ ] Chỉ cấu hình khi có cả `APP_ADMIN_EMAIL` và `APP_ADMIN_PASSWORD`.
- [ ] Nếu chỉ có một biến hoặc giá trị rỗng, báo lỗi cấu hình rõ ràng.
- [ ] Dùng `CommandLineRunner`; hash mật khẩu bằng BCrypt.
- [ ] Chạy idempotent theo email; tài khoản đã tồn tại thì không reset mật khẩu.
- [ ] Không log mật khẩu; không commit giá trị bí mật.
- [ ] Khởi động lại app và xác minh seed không tạo user trùng.

### 4. Bàn giao và chất lượng

- [ ] Giữ nguyên Auth contract: `UserDTO getUserByEmail(String email)`.
- [ ] Xác minh endpoint được bảo vệ bằng cả request không token và token hợp lệ.
- [ ] Kiểm tra user đăng ký được lưu trong MySQL và tồn tại sau khi restart container.
- [ ] Build lại Docker image sau các thay đổi code; kiểm tra log có `Started BookingServiceApplication`.
- [ ] Cập nhật README/demo khi API hoặc biến môi trường thay đổi.
- [ ] Commit riêng phần hồ sơ/đổi mật khẩu và phần seed nếu thay đổi đủ độc lập.

## Ghi chú chạy local

- DB Docker được publish ra host ở cổng `3308`; app trong Compose kết nối bằng hostname `db`, cổng `3306`.
- Giữ `.env` local ở ngoài Git; dùng `.env.example` chỉ với giá trị mẫu nếu cần chia sẻ cấu hình.
- Sau khi sửa code: `docker compose up -d --build app`.
- Xem trạng thái: `docker compose ps`; xem log: `docker compose logs app --tail 80`.
