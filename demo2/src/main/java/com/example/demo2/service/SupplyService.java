package com.example.demo2.service;

import com.example.demo2.dto.SupplyCreateDTO;
import com.example.demo2.dto.SupplyQuantityDTO;
import com.example.demo2.dto.SupplyUpdateDTO;
import com.example.demo2.entity.Supply;
import com.example.demo2.entity.Transaction;
import com.example.demo2.entity.Transaction.TransactionType;
import com.example.demo2.exception.BusinessException;
import com.example.demo2.exception.ResourceNotFoundException;
import com.example.demo2.repository.SupplyRepository;
import com.example.demo2.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplyService {

    private final SupplyRepository supplyRepository;
    private final TransactionRepository transactionRepository;

    // ===== 1. Tạo mới =====
    @Transactional
    public Supply createSupply(SupplyCreateDTO dto) {
        log.info("Bắt đầu tạo mới vật tư: {}", dto.getName());

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            log.warn("Tên vật tư bị trống");
            throw new BusinessException("Tên vật tư không được để trống");
        }

        Supply supply = new Supply();
        supply.setName(dto.getName());
        supply.setSpecification(dto.getSpecification());
        supply.setProvider(dto.getProvider());
        supply.setUnit(dto.getUnit());
        supply.setQuantity(0); // Mặc định số lượng = 0

        Supply saved = supplyRepository.save(supply);
        log.info("✅ Đã tạo mới vật tư: {} với ID: {}", saved.getName(), saved.getId());

        return saved;
    }

    // ===== 2. Cập nhật =====
    @Transactional
    public Supply updateSupply(Long id, SupplyUpdateDTO dto) {
        log.info("Bắt đầu cập nhật vật tư ID: {}", id);

        Supply supply = supplyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư với ID: " + id));

        supply.setName(dto.getName());
        supply.setSpecification(dto.getSpecification());
        supply.setProvider(dto.getProvider());

        Supply updated = supplyRepository.save(supply);
        log.info("✅ Đã cập nhật vật tư ID: {}", updated.getId());

        return updated;
    }

    // ===== 3. Xóa mềm =====
    @Transactional
    public void deleteSupply(Long id) {
        log.info("Bắt đầu xóa vật tư ID: {}", id);

        Supply supply = supplyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư với ID: " + id));

        supply.setIsDeleted(true);
        supplyRepository.save(supply);

        log.info("✅ Đã xóa mềm vật tư ID: {}", id);
    }

    // ===== 4. Danh sách =====
    public List<Supply> getAllSupplies() {
        log.debug("Lấy danh sách vật tư đang hoạt động");
        List<Supply> supplies = supplyRepository.findByIsDeletedFalse();
        log.info("Đã lấy {} vật tư từ database", supplies.size());
        return supplies;
    }

    // ===== 5. Tìm kiếm =====
    public List<Supply> searchSupplies(String keyword) {
        log.info("Tìm kiếm vật tư với từ khóa: {}", keyword);

        List<Supply> supplies = supplyRepository.searchByName(keyword);

        if (supplies.isEmpty()) {
            log.info("Không tìm thấy vật tư nào khớp với từ khóa: {}", keyword);
        } else {
            log.info("Tìm thấy {} vật tư khớp với từ khóa: {}", supplies.size(), keyword);
        }

        return supplies;
    }

    // ===== 6. Xuất kho =====
    @Transactional
    public Supply exportSupply(Long id, SupplyQuantityDTO dto) {
        log.info("Bắt đầu xuất kho vật tư ID: {}", id);

        Supply supply = supplyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư với ID: " + id));

        int currentQuantity = supply.getQuantity();
        int exportAmount = dto.getAmount();

        if (currentQuantity < exportAmount) {
            log.error("Thất bại khi xuất kho ID {}: Yêu cầu {}, hiện có {}", id, exportAmount, currentQuantity);
            throw new BusinessException("Số lượng tồn kho không đủ để xuất. Hiện có: " + currentQuantity);
        }

        int newQuantity = currentQuantity - exportAmount;
        supply.setQuantity(newQuantity);
        supplyRepository.save(supply);

        // Tạo giao dịch
        Transaction transaction = new Transaction();
        transaction.setSupply(supply);
        transaction.setType(TransactionType.EXPORT);
        transaction.setAmount(exportAmount);
        transaction.setOldQuantity(currentQuantity);
        transaction.setNewQuantity(newQuantity);
        transaction.setNote("Xuất kho " + exportAmount + " đơn vị");
        transactionRepository.save(transaction);

        log.info("✅ Xuất kho thành công ID {}, số lượng: {}, tồn mới: {}", id, exportAmount, newQuantity);

        return supply;
    }

    // ===== 7. Nhập kho =====
    @Transactional
    public Supply importSupply(Long id, SupplyQuantityDTO dto) {
        log.info("Bắt đầu nhập kho vật tư ID: {}", id);

        Supply supply = supplyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư với ID: " + id));

        int currentQuantity = supply.getQuantity();
        int importAmount = dto.getAmount();

        int newQuantity = currentQuantity + importAmount;
        supply.setQuantity(newQuantity);
        supplyRepository.save(supply);

        // Tạo giao dịch
        Transaction transaction = new Transaction();
        transaction.setSupply(supply);
        transaction.setType(TransactionType.IMPORT);
        transaction.setAmount(importAmount);
        transaction.setOldQuantity(currentQuantity);
        transaction.setNewQuantity(newQuantity);
        transaction.setNote("Nhập kho " + importAmount + " đơn vị");
        transactionRepository.save(transaction);

        log.info("✅ Nhập kho ID {}, số lượng: +{}, tồn cũ: {}, tồn mới: {}",
                id, importAmount, currentQuantity, newQuantity);

        return supply;
    }

    // ===== 8. Thống kê xuất trong ngày =====
    public List<Map<String, Object>> getDailyExportStatistics() {
        log.info("Bắt đầu thống kê xuất kho trong ngày");

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        List<Transaction> transactions = transactionRepository.findByTypeAndDate(
                TransactionType.EXPORT, startOfDay);

        Map<Long, Map<String, Object>> resultMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getSupply().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    Supply supply = list.get(0).getSupply();
                                    int total = list.stream()
                                            .mapToInt(Transaction::getAmount)
                                            .sum();
                                    return Map.of(
                                            "supplyId", supply.getId(),
                                            "supplyName", supply.getName(),
                                            "totalExported", total
                                    );
                                }
                        )
                ));

        log.info("Hoàn thành thống kê xuất kho trong ngày. Số vật tư có giao dịch: {}", resultMap.size());
        return List.copyOf(resultMap.values());
    }

    // ===== 9. Thống kê xuất nhiều nhất =====
    public Map<String, Object> getTopExportSupply() {
        log.info("Bắt đầu thống kê vật tư xuất nhiều nhất");

        List<Object[]> results = transactionRepository.getTopExportSupplies(TransactionType.EXPORT);

        if (results.isEmpty()) {
            log.warn("Chưa có dữ liệu giao dịch để thống kê");
            throw new ResourceNotFoundException("Chưa có dữ liệu giao dịch để thống kê");
        }

        Object[] top = results.get(0);
        Long supplyId = (Long) top[0];
        String supplyName = (String) top[1];
        Long totalExport = (Long) top[2];

        Map<String, Object> result = Map.of(
                "topSupplyId", supplyId,
                "topSupplyName", supplyName,
                "totalExportQuantity", totalExport
        );

        log.info("Vật tư xuất nhiều nhất: {} với {} đơn vị", supplyName, totalExport);
        return result;
    }
}