package com.dem_ams.domain.model;

import com.dem_ams.constant.ApplicationConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserModel user;
    private Set<? extends GrantedAuthority> roles;

    public CustomUserDetails(UserModel user) {
        this.user = user;
        this.roles = new HashSet<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public boolean isAdmin() {
        return roles.contains(new SimpleGrantedAuthority(ApplicationConstants.ROLE_ADMIN));
    }

    public boolean isLandlord() {
        return roles.contains(new SimpleGrantedAuthority(ApplicationConstants.ROLE_LANDLORD));
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
}
