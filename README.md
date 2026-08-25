# 🚀 SwiftDrop – Real-Time On-Demand Logistics Platform

SwiftDrop là hệ thống điều phối và giao vận nội thành theo thời gian thực được xây dựng bằng kiến trúc Microservices/Modular Monolith với **Spring Boot 3** và ứng dụng di động đa nền tảng bằng **Flutter** (Clean Architecture + BLoC).

---

## 🏗 System Architecture

```mermaid
graph TD
    subgraph Client ["Client Layer"]
        UserApp["Flutter Customer App"]
        DriverApp["Flutter Driver App"]
    end

    subgraph Backend ["Backend Layer (Spring Boot 3)"]
        Gateway["REST & WebSocket Gateway"]
        AuthService["Auth & Security (JWT)"]
        OrderService["Order State Machine"]
        GeoService["Geo-spatial Matching Service"]
    end

    subgraph Storage ["Data & Messaging Layer"]
        Postgres[("PostgreSQL + PostGIS")]
        Redis[("Redis GEO & Distributed Locks")]
        MQ["RabbitMQ (Event Broker)"]
    end

    UserApp -->|REST / HTTPS| Gateway
    DriverApp -->|STOMP / WebSocket| Gateway
    Gateway --> AuthService
    Gateway --> OrderService
    Gateway --> GeoService

    OrderService --> Postgres
    OrderService --> MQ
    GeoService --> Redis
```

---

## 🛠 Tech Stack

* **Mobile App:** Flutter, Dart, BLoC Pattern, Drift (Offline Sync), Mapbox / Google Maps SDK.
* **Backend:** Java 17/21, Spring Boot 3, Spring Security, Spring WebSocket.
* **Databases & Cache:** PostgreSQL (PostGIS), Redis (Redis GEO, Redisson Lock).
* **Messaging:** RabbitMQ / Kafka.
* **DevOps:** Docker, Docker Compose, GitHub Actions.

---

## ⚡ Quick Start

### 1. Khởi động hạ tầng cơ sở dữ liệu & Message Queue
```bash
docker compose -f deployments/docker-compose.yml up -d
```

### 2. Chạy Backend (Spring Boot)
```bash
cd apps/backend
./mvnw spring-boot:run
```

### 3. Chạy Mobile App (Flutter)
```bash
cd apps/mobile
flutter pub get
flutter run
```