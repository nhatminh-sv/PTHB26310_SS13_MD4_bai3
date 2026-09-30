package com.example.demo2.security;

import com.example.demo2.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    // ===== THUỘC TÍNH =====
    private User user;  // Đối tượng người dùng
    private Collection<? extends GrantedAuthority> authorities;  // Danh sách quyền

    // ===== TẠO UserPrincipal TỪ USER =====
    public static UserPrincipal create(User user) {
        // Tạo danh sách quyền từ role của user
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );

        return new UserPrincipal(user, authorities);
    }

    // ===== CÁC PHƯƠNG THỨC CỦA UserDetails =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Tài khoản không hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Tài khoản không bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Mật khẩu không hết hạn
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();  // Trả về trạng thái enabled từ user
    }

    // ===== GETTER TIỆN ÍCH =====
    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getFullName() {
        return user.getFullName();
    }

    public String getRole() {
        return user.getRole();
    }
}