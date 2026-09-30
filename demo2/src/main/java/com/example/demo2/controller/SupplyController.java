package com.example.demo2.controller;

import com.example.demo2.dto.ApiResponse;
import com.example.demo2.dto.SupplyCreateDTO;
import com.example.demo2.dto.SupplyQuantityDTO;
import com.example.demo2.dto.SupplyUpdateDTO;
import com.example.demo2.entity.Supply;
import com.example.demo2.service.SupplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/supplies")
@RequiredArgsConstructor
@Slf4j
public class SupplyController {

    private final SupplyService supplyService;

    // ===== 1. CREATE: POST /api/v1/supplies =====
    @PostMapping
    public ResponseEntity<ApiResponse<Supply>> createSupply(@Valid @RequestBody SupplyCreateDTO dto) {
        Supply supply = supplyService.createSupply(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Tạo vật tư thành công", supply));
    }

    // ===== 2. UPDATE: PUT /api/v1/supplies/{id} =====
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Supply>> updateSupply(
            @PathVariable Long id,
            @Valid @RequestBody SupplyUpdateDTO dto) {
        Supply supply = supplyService.updateSupply(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật vật tư thành công", supply));
    }

    // ===== 3. DELETE: DELETE /api/v1/supplies/{id} =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupply(@PathVariable Long id) {
        supplyService.deleteSupply(id);
        return ResponseEntity.noContent().build();
    }

    // ===== 4. GET ALL: GET /api/v1/supplies =====
    @GetMapping
    public ResponseEntity<ApiResponse<List<Supply>>> getAllSupplies() {
        List<Supply> supplies = supplyService.getAllSupplies();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách vật tư thành công", supplies));
    }

    // ===== 5. SEARCH: GET /api/v1/supplies/search =====
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Supply>>> searchSupplies(
            @RequestParam("name") String keyword) {
        List<Supply> supplies = supplyService.searchSupplies(keyword);
        return ResponseEntity.ok(ApiResponse.success("Tìm kiếm vật tư thành công", supplies));
    }

    // ===== 6. EXPORT: PATCH /api/v1/supplies/{id}/export =====
    @PatchMapping("/{id}/export")
    public ResponseEntity<ApiResponse<Supply>> exportSupply(
            @PathVariable Long id,
            @Valid @RequestBody SupplyQuantityDTO dto) {
        Supply supply = supplyService.exportSupply(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Xuất kho thành công", supply));
    }

    // ===== 7. IMPORT: PATCH /api/v1/supplies/{id}/import =====
    @PatchMapping("/{id}/import")
    public ResponseEntity<ApiResponse<Supply>> importSupply(
            @PathVariable Long id,
            @Valid @RequestBody SupplyQuantityDTO dto) {
        Supply supply = supplyService.importSupply(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Nhập kho thành công", supply));
    }

    // ===== 8. DAILY EXPORT STATISTICS: GET /api/v1/supplies/statistics/daily-export =====
    @GetMapping("/statistics/daily-export")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDailyExportStatistics() {
        List<Map<String, Object>> statistics = supplyService.getDailyExportStatistics();
        return ResponseEntity.ok(ApiResponse.success("Thống kê xuất trong ngày", statistics));
    }

    // ===== 9. TOP EXPORT: GET /api/v1/supplies/statistics/top-export =====
    @GetMapping("/statistics/top-export")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTopExportSupply() {
        Map<String, Object> topSupply = supplyService.getTopExportSupply();
        return ResponseEntity.ok(ApiResponse.success("Vật tư xuất nhiều nhất", topSupply));
    }
}
