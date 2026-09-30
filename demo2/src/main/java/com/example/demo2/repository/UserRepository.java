package com.example.demo2.repository;

import com.example.demo2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm user theo username (QUAN TRỌNG: dùng cho loadUserByUsername)
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Lấy danh sách user theo role
    List<User> findByRole(String role);

    // Lấy danh sách user đang hoạt động
    List<User> findByEnabledTrue();
}