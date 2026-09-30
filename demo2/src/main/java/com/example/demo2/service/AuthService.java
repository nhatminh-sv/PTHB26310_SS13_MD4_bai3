package com.example.demo2.service;

import com.example.demo2.dto.LoginRequest;
import com.example.demo2.dto.LoginResponse;
import com.example.demo2.dto.RegisterRequest;
import com.example.demo2.dto.RegisterResponse;
import com.example.demo2.entity.User;
import com.example.demo2.exception.BusinessException;
import com.example.demo2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // ===== ĐĂNG KÝ =====
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("🔵 Register attempt for username: {}", request.getUsername());

        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new BusinessException("Username already exists: " + request.getUsername());
        }

        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new BusinessException("Email already exists: " + request.getEmail());
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");  // Mặc định role = USER
        user.setEnabled(true); // Mặc định enabled = true

        User savedUser = userRepository.save(user);
        log.info("✅ User registered successfully: {}", savedUser.getUsername());

        return RegisterResponse.builder()
                .message("Registration successful")
                .username(savedUser.getUsername())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .registered(true)
                .build();
    }

    // ===== ĐĂNG NHẬP =====
    public LoginResponse login(LoginRequest request) {
        log.info("🔵 Login attempt for user: {}", request.getUsername());

        try {
            // Tìm user
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Username or password incorrect"));

            // Kiểm tra mật khẩu
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Invalid password for user: {}", request.getUsername());
                throw new BadCredentialsException("Username or password incorrect");
            }

            // Kiểm tra user có bị disabled không
            if (!user.getEnabled()) {
                log.warn("Account disabled for user: {}", request.getUsername());
                throw new BadCredentialsException("Account is disabled");
            }

            // Tạo Authentication token và xác thực
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    );

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("✅ Login successful for user: {}", user.getUsername());

            return LoginResponse.builder()
                    .message("Login successful")
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .authenticated(true)
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Login failed: {}", e.getMessage());
            throw new BadCredentialsException("Username or password incorrect");
        }
    }

    // ===== ĐĂNG XUẤT =====
    public void logout() {
        SecurityContextHolder.clearContext();
        log.info("User logged out");
    }

    // ===== KIỂM TRA TRẠNG THÁI =====
    public boolean checkStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());
    }
}