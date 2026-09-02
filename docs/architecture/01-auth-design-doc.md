# Auth-desgin

## 1. Database ERD and Schema

```mermaid
erDiagram
    Users {
        BIGINT id PK
        VARCHAR email  UK
        VARCHAR full_name
        VARCHAR phone_number
        VARCHAR role
        VARCHAR password
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }
```

### Data Dictionary: Bảng `users`

| Tên cột        | Kiểu dữ liệu   | Ràng buộc               | Mô tả                                                 |
| :------------- | :------------- | :---------------------- | :---------------------------------------------------- |
| `id`           | `BIGINT`       | PRIMARY KEY             | Khóa chính định danh người dùng                       |
| `email`        | `VARCHAR(100)` | NOT NULL, UNIQUE        | Email đăng nhập                                       |
| `full_name`    | `VARCHAR(100)` | NOT_NULL                | Họ và tên                                             |
| `phone_number` | `VARCHAR(20)`  | NOT_NULL                | Số điện thoại                                         |
| `role`         | `VARCHAR(20)`  | NOT_NULL                | Vai trò: `ROLE_CUSTOMER`, `ROLE_DRIVER`, `ROLE_ADMIN` |
| `password`     | `VARCHAR(60)`  | NOT_NULL                | Mật khẩu tài khoản                                    |
| `created_at`   | `TIMESTAMPTZ`  | NOT_NULL, DEFAULT NOW() | Thời gian tạo tài khoản                               |
| `updated_at`   | `TIMESTAMPTZ`  | NULL                    | Thời gian cập nhật                                    |

## API constract

[HTTP Method] /api/v1/[tên-tài-nguyên-số-nhiều]/[hành-động-hoặc-id]

- **EndPoint:** `POST /api/v1/auth/register`

* **Content type:** application/json

### Request payload(From mobile)

```json
{
  "email": "user@gmail.com",
  "fullName": "user name",
  "password": "abcd123",
  "phoneNumber": "012345678"
}
```
### Response
* **1. Success(201)**
```json
{
    "status": "success",
    "status_code":201,
    "data":{
        "id":"001",
        "fullName":"Name",
        "email":"user@gmail.com",
        "phoneNumber":"098765432",
        "status":"active",
        "createdAt":"2026-12-29T12:00:00Z"
    }
}
```
* **2.Error Validation failed(400)**
```json
{
    "status":"error",
    "statusCode":400,
    "errorCode":"INVALID_INPUT",
    "message":"Invalid request payload. Check input fields",
    "details":[
        {
            "field": "fullName",
            "issue":"Name is not null"
        },

        {
            "field":"email",
            "issue":"Email is required and must be a valid email address"
        },

        {
            "field":"password",
            "issue":"Password is required and must be atleast 8 characters"
        },

        {
            "field":"phoneNumber",
            "issue":"Phone number is required and must be atleast 10 digit-number"        
        }
    ]
}
```
* **3. Conflict Information(409)**
```json
{
    "status":"error",
    "statusCode":409,
    "errorCode":"EMAIL_ALREADY_EXISTS",
    "message":"This email address is already registered",
    "detail":[]
}
```
```json
{
    "status":"error",
    "statusCode":409,
    "errorCode":"PHONE_NUMBER_ALREADY_EXISTS",
    "message":"This phone number is already registered",
    "detail":[]
}
```
