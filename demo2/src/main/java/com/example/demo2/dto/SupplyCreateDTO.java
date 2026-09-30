package com.example.demo2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyCreateDTO {

    @NotBlank(message = "Tên vật tư không được để trống")
    private String name;

    private String specification;

    private String provider;

    private String unit;
}