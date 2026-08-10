# TodoList API Specification

## Quy ước chung

- **Base URL**: `/api`
- **Auth**: Bearer JWT trong header `Authorization: Bearer <access_token>`, trừ các endpoint đánh dấu 🔓 Public
- **Response format**: mọi response đều wrap trong `AppResponse<T>`

```json
{
  "timestamp": "2026-08-09T10:30:00",
  "status": 200,
  "message": "Success",
  "data": { }
}
```

- **Error response**: dùng `AppResponse.error(status, message)`, `data` có thể null hoặc chứa chi tiết lỗi validation
- **Phân quyền**: cột `Quyền yêu cầu` ghi permission code (theo `@PreAuthorize("hasAuthority('...')")`) hoặc "Chính chủ" nghĩa là user chỉ thao tác được trên resource của mình (check ownership trong service layer)
- **Pagination**: query param `page` (default 0), `size` (default 10), `sort` (vd `dueDate,desc`). Response data dạng:

```json
{
  "content": [ ],
  "page": 0,
  "size": 10,
  "totalElements": 42,
  "totalPages": 5
}
```

---

## 1. Auth

| Method | Endpoint | Mô tả | Request Body | Response `data` | Quyền yêu cầu |
|---|---|---|---|---|---|
| POST | `/api/auth/register` | 🔓 Đăng ký tài khoản mới | `{ email, password, fullName }` | `{ id, email, fullName }` | Public |
| POST | `/api/auth/login` | 🔓 Đăng nhập | `{ email, password }` | `{ accessToken, refreshToken, expiresIn }` | Public |
| POST | `/api/auth/refresh` | 🔓 Làm mới access token | `{ refreshToken }` | `{ accessToken, refreshToken, expiresIn }` | Public (nhưng refresh token phải hợp lệ) |
| POST | `/api/auth/logout` | Đăng xuất, revoke refresh token | `{ refreshToken }` | `null` | Đã đăng nhập |
| GET | `/api/auth/me` | Lấy thông tin user hiện tại | — | `UserResponse` | Đã đăng nhập |

**Lỗi thường gặp:**
- `409` — email đã tồn tại (khi register)
- `401` — sai email/password (khi login)
- `401` — refresh token hết hạn hoặc đã bị revoke

---

## 2. User

| Method | Endpoint | Mô tả | Request Body | Response `data` | Quyền yêu cầu |
|---|---|---|---|---|---|
| GET | `/api/users` | Danh sách user (phân trang) | — | `Page<UserResponse>` | `user:read_any` |
| GET | `/api/users/{id}` | Chi tiết 1 user | — | `UserResponse` | `user:read_any` hoặc Chính chủ |
| PUT | `/api/users/{id}` | Cập nhật thông tin (fullName) | `{ fullName }` | `UserResponse` | Chính chủ |
| PATCH | `/api/users/{id}/password` | Đổi mật khẩu | `{ oldPassword, newPassword }` | `null` | Chính chủ |
| PATCH | `/api/users/{id}/status` | Bật/tắt tài khoản | `{ enabled }` | `UserResponse` | `user:manage` |
| PATCH | `/api/users/{id}/roles` | Gán lại danh sách role cho user | `{ roleIds: [] }` | `UserResponse` | `user:manage` |
| DELETE | `/api/users/{id}` | Xóa user (soft/hard tùy bạn chọn) | — | `null` | `user:manage` |

**UserResponse** (ví dụ):
```json
{ "id": "uuid", "email": "a@b.com", "fullName": "Nguyen Van A", "enabled": true, "roles": ["USER"], "createdAt": "..." }
```

---

## 3. Role & Permission

| Method | Endpoint | Mô tả | Request Body | Response `data` | Quyền yêu cầu |
|---|---|---|---|---|---|
| GET | `/api/roles` | Danh sách role | — | `List<RoleResponse>` | `role:manage` |
| POST | `/api/roles` | Tạo role mới | `{ name, description, permissionIds: [] }` | `RoleResponse` | `role:manage` |
| GET | `/api/roles/{id}` | Chi tiết role kèm permissions | — | `RoleResponse` | `role:manage` |
| PUT | `/api/roles/{id}` | Cập nhật role (name, description, permissions) | `{ name, description, permissionIds: [] }` | `RoleResponse` | `role:manage` |
| DELETE | `/api/roles/{id}` | Xóa role | — | `null` | `role:manage` |
| GET | `/api/permissions` | Danh sách toàn bộ permission có sẵn | — | `List<PermissionResponse>` | `role:manage` |
| POST | `/api/permissions` | Tạo permission mới (thường seed sẵn, ít khi cần API) | `{ name, description }` | `PermissionResponse` | `role:manage` |

**RoleResponse**:
```json
{ "id": "uuid", "name": "ADMIN", "description": "...", "permissions": ["task:read_any", "user:manage"] }
```

---

## 4. Task

| Method | Endpoint | Mô tả | Request Body | Response `data` | Quyền yêu cầu |
|---|---|---|---|---|---|
| GET | `/api/tasks` | Danh sách task (của chính mình), filter + phân trang | Query: `completed`, `categoryId`, `dueBefore`, `dueAfter`, `keyword` | `Page<TaskResponse>` | Chính chủ (chỉ thấy task của mình) |
| GET | `/api/tasks/{id}` | Chi tiết 1 task | — | `TaskResponse` | Chính chủ hoặc `task:read_any` |
| POST | `/api/tasks` | Tạo task mới | `{ title, description, dueDate, categoryId }` | `TaskResponse` | Đã đăng nhập |
| PUT | `/api/tasks/{id}` | Cập nhật task | `{ title, description, dueDate, categoryId }` | `TaskResponse` | Chính chủ hoặc `task:write_any` |
| PATCH | `/api/tasks/{id}/complete` | Đánh dấu hoàn thành | `{ completed: true }` | `TaskResponse` | Chính chủ |
| DELETE | `/api/tasks/{id}` | Xóa task | — | `null` | Chính chủ hoặc `task:write_any` |
| GET | `/api/tasks/overdue` | Danh sách task quá hạn chưa hoàn thành | — | `Page<TaskResponse>` | Chính chủ |
| GET | `/api/tasks/stats` | Thống kê nhanh (tổng, hoàn thành, quá hạn) | — | `{ total, completed, pending, overdue }` | Chính chủ |
| GET | `/api/admin/tasks` | Xem toàn bộ task của mọi user | Query filter tương tự | `Page<TaskResponse>` | `task:read_any` |

**TaskResponse**:
```json
{
  "id": "uuid",
  "title": "...",
  "description": "...",
  "completed": false,
  "dueDate": "2026-08-15T00:00:00Z",
  "category": { "id": "uuid", "name": "Work", "colorCode": "#FF5733" },
  "createdAt": "...",
  "updatedAt": "..."
}
```

---

## 5. Category

| Method | Endpoint | Mô tả | Request Body | Response `data` | Quyền yêu cầu |
|---|---|---|---|---|---|
| GET | `/api/categories` | Danh sách category của user hiện tại | — | `List<CategoryResponse>` | Chính chủ |
| POST | `/api/categories` | Tạo category mới | `{ name, colorCode }` | `CategoryResponse` | Đã đăng nhập |
| PUT | `/api/categories/{id}` | Cập nhật category | `{ name, colorCode }` | `CategoryResponse` | Chính chủ |
| DELETE | `/api/categories/{id}` | Xóa category (task liên quan → set category = null) | — | `null` | Chính chủ |

---

## 6. Audit Log

| Method | Endpoint | Mô tả | Request Body | Response `data` | Quyền yêu cầu |
|---|---|---|---|---|---|
| GET | `/api/admin/audit-logs` | Danh sách log, filter theo entity | Query: `entityName`, `entityId`, `action`, `fromDate`, `toDate` | `Page<AuditLogResponse>` | `audit:read` |
| GET | `/api/admin/audit-logs/{id}` | Chi tiết 1 bản ghi log | — | `AuditLogResponse` | `audit:read` |

**AuditLogResponse**:
```json
{
  "id": "uuid",
  "entityName": "Task",
  "entityId": "uuid",
  "action": "UPDATE",
  "performedBy": "user@email.com",
  "performedAt": "...",
  "oldValue": "{ \"completed\": false }",
  "newValue": "{ \"completed\": true }"
}
```

---

## 7. Bảng mã lỗi (HTTP status dùng trong `AppResponse.status`)

| Status | Ý nghĩa | Khi nào dùng |
|---|---|---|
| 200 | OK | Thành công (GET, PUT, PATCH) |
| 201 | Created | Tạo mới thành công (POST) |
| 400 | Bad Request | Validation lỗi, body sai định dạng |
| 401 | Unauthorized | Chưa đăng nhập / token hết hạn |
| 403 | Forbidden | Đã đăng nhập nhưng không đủ quyền hoặc không phải chủ sở hữu |
| 404 | Not Found | Không tìm thấy resource |
| 409 | Conflict | Trùng email, trùng tên role/category (unique constraint) |
| 500 | Internal Server Error | Lỗi hệ thống không lường trước |

---

## 8. Permission code gợi ý (seed sẵn qua Flyway)

| Code | Ý nghĩa |
|---|---|
| `task:read_any` | Xem task của mọi user |
| `task:write_any` | Sửa/xóa task của mọi user |
| `user:read_any` | Xem thông tin mọi user |
| `user:manage` | Bật/tắt tài khoản, gán role |
| `role:manage` | CRUD role/permission |
| `audit:read` | Xem audit log |

Role `USER` mặc định không có permission nào (chỉ thao tác trên resource chính chủ). Role `ADMIN` có đủ 6 permission trên.
