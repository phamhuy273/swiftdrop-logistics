package com.swifdrop_logistics.repository;
import com.swifdrop_logistics.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Kiểm tra email đã tồn tại hay chưa (Dùng cho API Register)
    boolean existsByEmail(String email);

    // 2. Kiểm tra số điện thoại đã tồn tại hay chưa (Dùng cho API Register)
    boolean existsByPhoneNumber(String phoneNumber);

    // 3. Tìm user theo email (Sau này sẽ dùng cho API Login)
    Optional<User> findByEmail(String email);
}