#  SwiftDrop – High-Level Architecture & Product Design

> **Tài liệu:** Bản thiết kế Kiến trúc Tổng quan (High-Level Design - HLD)  
> **Dự án:** SwiftDrop – Nền tảng Giao vận & Điều phối Nội thành Thời gian thực  
> **Tác giả:** Pham Viet Quang Huy  
> **Ngày tạo:** 2026-08-27  

---

## 1. Tuyên ngôn Sản phẩm & Bài toán Giải quyết (Product Vision)

SwiftDrop là nền tảng công nghệ kết nối giữa **Người gửi hàng (Customer)** và **Tài xế giao hàng (Driver)**:
* **Vấn đề giải quyết:** Tối ưu hóa thời gian giao hàng tức thì trong nội thành; tính cước tự động dựa trên khoảng cách; theo dõi hành trình trực tiếp của tài xế trên bản đồ.
* **Đối tượng phục vụ:**
  1. *Khách hàng (Customer):* Cần gửi đồ nhanh, theo dõi vị trí xe thời gian thực.
  2. *Tài xế (Driver):* Nhận đơn quanh khu vực đang đứng, nhận tiền cước tức thì vào ví.
  3. *Hệ thống (Backend Engine):* Tự động điều phối, ghép nối tài xế gần nhất trong bán kính 3km và chống tranh chấp đơn hàng.

---

## 2. Kiến trúc Hệ thống Tổng quan (System Architecture)

Hệ thống được thiết kế theo kiến trúc **Modular Monolith** phân tầng rõ ràng:

```mermaid
graph TD
    subgraph ClientLayer ["1. Tầng Client (Mobile Apps - Flutter)"]
        UserApp["📱 Flutter Customer App<br/>(Đặt hàng, Theo dõi lộ trình)"]
        DriverApp["🛵 Flutter Driver App<br/>(Bật GPS, Nhận cuốc, Cập nhật trạng thái)"]
    end

    subgraph GatewayLayer ["2. Tầng Giao tiếp & Bảo mật (Spring Boot 3)"]
        RestAPI["🌐 REST API Gateway<br/>(HTTPS / Stateless JWT Auth)"]
        SocketAPI["⚡ WebSocket / STOMP Server<br/>(Bắn tọa độ GPS & Phát thông báo đơn)"]
    end

    subgraph ServiceLayer ["3. Tầng Nghiệp vụ cốt lõi (Core Business Logic)"]
        AuthSvc["🔐 Auth Service<br/>(BCrypt / Token Rotation)"]
        OrderSvc["📦 Order State Machine<br/>(Quản lý vòng đời đơn)"]
        GeoSvc["📍 Geo Matching Engine<br/>(Quét tài xế lân cận)"]
        LockSvc["🔒 Concurrency Manager<br/>(Redisson Distributed Lock)"]
    end

    subgraph DataLayer ["4. Tầng Dữ liệu & Hàng đợi (Storage & Messaging)"]
        Postgres[("🐘 PostgreSQL + PostGIS<br/>(Lưu User, Order, Spatial Query)")]
        Redis[("⚡ Redis Cache & Redis GEO<br/>(Lưu tọa độ GPS tức thời & Khóa dữ liệu)")]
        RabbitMQ[("🐰 RabbitMQ Message Broker<br/>(Xử lý thanh toán & Push Notification ngầm)")]
    end

    UserApp -->|HTTPS / REST| RestAPI
    DriverApp -->|STOMP over WebSocket| SocketAPI
    
    RestAPI --> AuthSvc
    RestAPI --> OrderSvc
    SocketAPI --> GeoSvc
    OrderSvc --> LockSvc

    AuthSvc --> Postgres
    OrderSvc --> Postgres
    OrderSvc --> RabbitMQ
    GeoSvc --> Redis
    LockSvc --> Redis
```

---

## 3. Vòng đời Trạng thái Vận đơn (Order State Machine)

Toàn bộ quy trình từ lúc khách bấm đặt đơn đến khi giao hàng thành công:

```mermaid
stateDiagram-v2
    [*] --> PENDING: Khách tạo đơn hàng
    PENDING --> MATCHING: Hệ thống tính giá cước & bắt đầu quét tìm tài xế
    
    MATCHING --> ACCEPTED: Tài xế gần nhất bấm nhận (Được khóa bởi Redisson Lock)
    MATCHING --> CANCELLED: Hết thời gian tìm kiếm hoặc khách hủy đơn
    
    ACCEPTED --> PICKING_UP: Tài xế bắt đầu di chuyển đến điểm lấy hàng
    PICKING_UP --> IN_TRANSIT: Tài xế đã lấy hàng và bắt đầu chở đi
    
    IN_TRANSIT --> DELIVERED: Tài xế giao hàng thành công & chụp ảnh bằng chứng
    DELIVERED --> [*]: Backend trừ tiền ví khách, cộng tiền ví tài xế
```

---

## 4. Lộ trình Triển khai Dự án (Engineering Roadmap)

* **Chặng 1: Xác thực & Quản lý Người dùng (Authentication & User Management)**
  * Thiết kế Entity `User`, bảng `users`, mã hóa mật khẩu `BCrypt`.
  * Cấp phát thẻ bài xác thực `JWT` (Access Token / Refresh Token).
* **Chặng 2: Quản lý Vận đơn & Tính cước (Order Management & Pricing Engine)**
  * Thiết kế bảng `orders`, tích hợp thuật toán tính khoảng cách (Haversine / PostGIS).
  * Xây dựng luồng chuyển đổi trạng thái đơn hàng theo State Machine.
* **Chặng 3: Không gian Địa lý & Thời gian thực (Real-time Geo Tracking)**
  * Tích hợp `Redis GEO` lưu vết tọa độ tài xế.
  * Tích hợp `WebSocket (STOMP)` phát đơn hàng tức thời.
  * Cài đặt `Redisson Distributed Lock` chống 2 tài xế nhận cùng 1 đơn.
  * Ứng dụng Flutter hiển thị Google Maps và vẽ xe di chuyển mượt mà.
* **Chặng 4: Tự động hóa Bất đồng bộ & Hoàn thiện (Async Events & Polish)**
  * Tích hợp `RabbitMQ` gửi thông báo đẩy (Push Notification) và xử lý ví tiền.
  * Cơ chế ngoại tuyến `Offline-First` cho app tài xế.v# 🚀 SwiftDrop – High-Level Architecture & Product Design

> **Tài liệu:** Bản thiết kế Kiến trúc Tổng quan (High-Level Design - HLD)  
> **Dự án:** SwiftDrop – Nền tảng Giao vận & Điều phối Nội thành Thời gian thực  
> **Tác giả:** Pham Viet Quang Huy  
> **Ngày tạo:** 2026-08-27  

---

