package com.example.demo2.security;

import com.example.demo2.entity.User;
import com.example.demo2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("🔍 Loading user by username: {}", username);

        // Tìm user trong database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("❌ User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        log.info("✅ User found: {}", user.getUsername());
        log.info("✅ User role: {}", user.getRole());
        log.info("✅ User enabled: {}", user.getEnabled());

        // Chuyển đổi từ User Entity sang UserPrincipal
        return UserPrincipal.create(user);
    }
}