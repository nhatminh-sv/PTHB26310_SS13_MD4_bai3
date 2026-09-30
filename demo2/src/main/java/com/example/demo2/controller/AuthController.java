package com.example.demo2.controller;

import com.example.demo2.dto.ApiResponse;
import com.example.demo2.dto.LoginRequest;
import com.example.demo2.dto.LoginResponse;
import com.example.demo2.dto.RegisterRequest;
import com.example.demo2.dto.RegisterResponse;
import com.example.demo2.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // ===== API ĐĂNG KÝ =====
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("📝 Registration request for username: {}", request.getUsername());

        try {
            RegisterResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created("Registration successful", response));

        } catch (IllegalArgumentException e) {
            log.warn("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));

        } catch (RuntimeException e) {
            log.warn("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));

        } catch (Exception e) {
            log.error("Registration error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("Registration failed: " + e.getMessage()));
        }
    }

    // ===== API ĐĂNG NHẬP =====
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("🔐 Login request for username: {}", request.getUsername());

        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));

        } catch (BadCredentialsException e) {
            log.warn("Login failed for user: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Username or password incorrect"));

        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("Login failed: " + e.getMessage()));
        }
    }

    // ===== API ĐĂNG XUẤT =====
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        authService.logout();
        return ResponseEntity.ok(ApiResponse.success("Logout successful", "Logged out"));
    }

    // ===== API KIỂM TRA TRẠNG THÁI =====
    @PostMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> checkStatus() {
        boolean isAuthenticated = authService.checkStatus();
        return ResponseEntity.ok(ApiResponse.success("Status checked", isAuthenticated));
    }
}