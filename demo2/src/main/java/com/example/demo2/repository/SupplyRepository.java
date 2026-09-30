package com.example.demo2.repository;

import com.example.demo2.entity.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, Long> {

    // Lấy vật tư đang hoạt động (chưa bị xóa)
    List<Supply> findByIsDeletedFalse();

    // Tìm kiếm theo tên (LIKE)
    @Query("SELECT s FROM Supply s WHERE s.isDeleted = false AND LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Supply> searchByName(@Param("keyword") String keyword);

    // Kiểm tra tồn tại theo ID và chưa bị xóa
    Optional<Supply> findByIdAndIsDeletedFalse(Long id);

    // Kiểm tra tên đã tồn tại chưa
    boolean existsByNameAndIsDeletedFalse(String name);
}