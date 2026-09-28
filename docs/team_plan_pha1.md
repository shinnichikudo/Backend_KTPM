# Kế hoạch nhóm — Pha 1

## 1. Mục tiêu bàn giao

Hoàn thành backend đặt phòng khách sạn có thể khởi động từ repository mới bằng Docker Compose; API chạy đúng các quyền đã công bố; dữ liệu MySQL được giữ qua lần restart; Swagger, README, seed và baseline tải Kaggle có thể tái lập.

Đặc tả chức năng và quyết định kiến trúc nằm trong [specv2.md](specv2.md). Nếu kế hoạch và đặc tả mâu thuẫn, nhóm chốt lại đặc tả trước khi chia task.

## 2. Phân công đề xuất

| Thành viên | Phạm vi sở hữu | Bàn giao cụ thể | Phụ thuộc |
|---|---|---|---|
| TV1 — Base, Auth, Security | Khung Spring Boot; cấu hình chung; đăng ký/đăng nhập; hồ sơ; JWT; xử lý lỗi; hợp đồng user cho module khác | App khởi động; user persistence; role CUSTOMER khi đăng ký; endpoint auth/profile; token được filter xác minh; seed ADMIN bằng `CommandLineRunner` theo env | Hoàn thành Auth contract và seed config trước khi TV3 tích hợp |
| TV2 — Inventory | Quản lý và đọc phòng | Entity/repository/service/controller; CRUD ADMIN; danh sách/chi tiết public; lọc cơ bản; ngừng kinh doanh phòng; hợp đồng Room cho Reservation | Cung cấp đúng Room contract/DTO trong đặc tả; bỏ method khóa bi quan |
| TV3 — Reservation | Tạo, đọc và hủy booking; chống đặt trùng | Booking API; kiểm tra ngày/sức chứa/quyền sở hữu; tính giá server; transaction; lịch sử cá nhân; xử lý conflict 409 | Cần Auth contract từ TV1, Room contract từ TV2 và quyết định chống trùng |
| TV4 — Tích hợp, chất lượng và demo | Compose/README/API demo; dữ liệu seed tích hợp; kịch bản tải Kaggle và baseline; hỗ trợ tích hợp module | Hướng dẫn clone/run; Compose app+DB; Swagger; dữ liệu demo; báo cáo baseline có thông số chạy | Cần API/schema ổn định từ TV1–3; không tự thay đổi hợp đồng mà chưa báo chủ module |

Phân công là đề xuất theo ranh giới module. Chỉ sau khi MVP hoàn thành, nếu nhóm ưu tiên endpoint tìm phòng trống hoặc lọc availability, giao rõ cho TV3 sở hữu truy vấn/endpoint và TV2 cung cấp room contract; không giao chung “hỗ trợ reservation” cho hai người.

## 3. Giai đoạn và tiêu chí hoàn thành

### Giai đoạn A — Chốt hợp đồng (hạn: cuối ngày làm việc đầu tiên)

- Áp dụng phạm vi đã chốt trong [specv2.md](specv2.md): một phòng/đơn; không tích hợp hoặc mô phỏng thanh toán; availability và Admin list để sau MVP.
- Chốt chi tiết endpoint, DTO, status/error code và field request/response; giữ quy ước ngày `[check_in, check_out)` cùng quyền endpoint như đặc tả.
- Dùng `booking_nights` với unique `(room_id, night_date)` làm cơ chế chống đặt trùng duy nhất trong Pha 1.
- Dùng Spring `@Service`; đặt `@Transactional` ở use case ghi; service không import Spring Web, Spring Data/JPA hoặc entity.
- Lưu baseline Kaggle CPU ở Pha 1 với workload có thể chạy lại ở Pha 2.
- TV1 và TV2 commit các interface/DTO trong phụ lục [specv2.md](specv2.md#10-phụ-lục--hợp-đồng-giữa-các-module); TV3 tạo enum booking status đúng hai giá trị `CONFIRMED`/`CANCELLED`. Chốt các phần này trước khi TV3 bắt đầu tích hợp.
- TV1 ghi hướng dẫn seed ADMIN: đặt cả `APP_ADMIN_EMAIL` và `APP_ADMIN_PASSWORD`; `CommandLineRunner` chạy idempotent và BCrypt-hash password.
- Tạo skeleton chạy được, branch/PR, quy tắc format và review.

**Hoàn thành khi:** các chi tiết API/schema, status và contract được commit; mỗi module có thể bắt đầu mà không phải đoán signature hoặc đổi endpoint của module khác. Nếu đã qua ngày đầu, chốt trong buổi làm việc kế tiếp và trước khi TV3 tích hợp.

### Giai đoạn B — Làm module độc lập

- TV1 hoàn thành Auth/Security trước để cấp token và giao diện user contract.
- TV2 và TV3 phát triển song song trên contract đã chốt; dùng mock adapter tạm thời khi phụ thuộc chưa xong.
- TV4 tạo môi trường Compose/README và chuẩn bị seed/workload.

**Hoàn thành khi:** module biên dịch và PR được review; DTO/entity không rò qua module; error response nhất quán.

### Giai đoạn C — Tích hợp đầu-cuối

- Dựng bằng `docker compose up --build` trên môi trường sạch.
- Xác nhận app đợi DB healthy, tạo schema, đăng ký/đăng nhập được.
- Demo: public GET phòng; endpoint GET được bảo vệ; POST đặt phòng cần JWT; DELETE hủy đơn đúng quyền.
- Kiểm tra hai request đặt cùng phòng/cùng đêm; chỉ một request thành công, request còn lại nhận 409 và không để lại dữ liệu booking/giữ chỗ một phần.
- Kiểm tra restart không làm mất dữ liệu volume.

**Hoàn thành khi:** các ca demo chạy trên Compose, lỗi có status/body đúng và README đủ để thành viên khác làm lại.

### Giai đoạn D — Baseline và đóng gói

- Chạy cùng seed, commit, cấu hình Kaggle CPU và workload cố định.
- Lưu lệnh chạy, phần cứng, phiên bản phần mềm, duration, concurrency, tỷ lệ đọc/ghi, p50/p95, throughput, lỗi và giới hạn phép đo.
- Ghi baseline trong `docs/benchmarks/`; không chỉnh workload giữa lần đo baseline và Pha 2.
- Chốt README, OpenAPI và lịch sử PR/commit.

**Hoàn thành khi:** người khác có thể tái tạo phép đo và phân biệt rõ kết quả chức năng với kết quả hiệu năng.

## 4. Thứ tự phụ thuộc và API nội bộ

```text
TV1 Auth/User contract ──────────┐
                                 ├── TV3 Reservation (MVP)
TV2 Room contract ───────────────┘

TV1 + TV2 + TV3 ─────────────────── TV4 tích hợp, demo, workload
```

- Auth contract Pha 1 là `UserDTO getUserByEmail(String email)`; lấy email từ JWT principal, trả DTO có `id`, `email`, `fullName`, `role`, ném `USER_NOT_FOUND` nếu không có.
- Inventory contract Pha 1 là `RoomDTO getActiveRoomById(Long roomId)`; trả `id`, `roomNumber`, `roomType`, `basePrice`, `capacity`, `status`, ném `ROOM_NOT_FOUND` nếu thiếu hoặc INACTIVE. Không có `getRoomForUpdate`.
- Reservation không được yêu cầu Inventory truy vấn booking; dữ liệu đặt phòng và availability thuộc Reservation để tránh phụ thuộc vòng.
- Endpoint `GET /api/rooms/available` nằm sau MVP; nếu được ưu tiên, TV3 sở hữu endpoint/truy vấn booking, TV2 cung cấp room contract.

## 5. Definition of Done cho mỗi task

- Có mô tả phạm vi, request/response hoặc contract liên quan.
- Code nằm đúng module/layer; không thêm dependency chéo trái quy tắc.
- Có xử lý lỗi/validation tối thiểu và kiểm tra quyền ở server.
- Có test phù hợp cho luật nghiệp vụ và đường lỗi chính; không chỉ kiểm tra happy path.
- Chạy được build ở môi trường dự án; PR nhỏ, có ít nhất một review; không commit secret, `.env`, dữ liệu cá nhân hoặc số thẻ.
- Tài liệu API/README được cập nhật khi thay đổi contract hoặc cách chạy.

## 6. Rủi ro và cách giảm

| Rủi ro | Tác động | Cách giảm |
|---|---|---|
| TV1 Auth chậm làm cả nhóm kẹt | Reservation không xác thực được user | Chốt contract sớm; dùng mock adapter ở local; ưu tiên auth tối thiểu trước profile nâng cao |
| Schema/DTO đổi giữa chừng | Merge conflict và phải sửa nhiều module | Chốt ERD/contract ở giai đoạn A; thay đổi cần PR/thông báo chủ module |
| Hai booking đồng thời cùng phòng | Đặt trùng | Unique `(room_id, night_date)` trên `booking_nights`; integration test concurrency và rollback |
| Transaction tách vụn ở từng repository | Dữ liệu booking và giữ chỗ lệch nhau | Transaction bao trọn use case ghi; rollback booking và nights cùng nhau |
| Thanh toán bị thêm vào giữa chừng | Trễ tiến độ, tăng rủi ro xử lý dữ liệu nhạy cảm | Không nhận hoặc lưu dữ liệu thẻ trong MVP; thay đổi phạm vi cần nhóm duyệt và cập nhật cả đặc tả lẫn kế hoạch |
| TV3 tự đoán contract/status | Lỗi tích hợp và merge conflict | Khóa signature, DTO và status cuối ngày làm việc đầu tiên; TV3 bắt đầu tích hợp sau khi TV1/TV2 commit |
| Seed ADMIN không lặp lại được hoặc lộ mật khẩu | Không demo được CRUD ADMIN hoặc lộ credential | `CommandLineRunner` chạy khi có cả hai env `APP_ADMIN_EMAIL`/`APP_ADMIN_PASSWORD`; BCrypt, idempotent theo email, không log password; `.env` bị ignore |
| Kaggle/Docker khác máy phát triển | Không tái hiện được kết quả | Ghi rõ môi trường; giữ workload, seed và commit cố định; không coi Docker là điều kiện nếu môi trường Kaggle không hỗ trợ |
| Tối ưu trước khi có số đo | Pha 1 phình to, khó chứng minh tác dụng | Chỉ lưu baseline; chọn một cải tiến Pha 2 sau khi xem bottleneck |

## 7. Liên hệ với yêu cầu môn học

- REST/JSON, các method POST/GET/DELETE, Swagger: thể hiện qua API contract và demo.
- Phân tầng: controller → nghiệp vụ → repository/persistence; service không import Spring Web/JPA.
- Đăng nhập và endpoint được bảo vệ: Auth + SecurityFilterChain/JWT filter; demo cả thiếu token và token hợp lệ.
- Docker: Compose đóng gói app và MySQL; README có lệnh chạy.
- README/Git: kiến trúc, đặc tả, hướng dẫn; commit nhỏ và PR được review.
- Kaggle CPU: baseline cố định ở Pha 1; Pha 2 so sánh trước/sau trên cùng cấu hình và workload.

Các lựa chọn như JWT, MySQL, modular monolith, `booking_nights`, xóa mềm, lọc giá và phân quyền ADMIN/CUSTOMER là quyết định thiết kế của nhóm, không phải yêu cầu nguyên văn của đề. MVP thống nhất một phòng/đơn, status `CONFIRMED`/`CANCELLED`, không có thanh toán, và dùng unique `booking_nights` để chống trùng. `GET /api/rooms/available` và danh sách booking cho ADMIN là phần mở rộng sau MVP; nếu đổi phạm vi, cập nhật cả đặc tả lẫn kế hoạch trước khi bắt đầu.

