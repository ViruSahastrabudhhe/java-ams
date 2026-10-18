package com.dem_ams.config;

import com.dem_ams.domain.model.Permission;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.Role;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

@Service("CustomUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserModelRepository userModelRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userModelRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CustomUserDetails.CustomUserDetailsBuilder customUser = CustomUserDetails.builder();
        return customUser
                .user(user)
                .roles(getAuthorities(user))
                .build();
    }

    private Set<GrantedAuthority> getAuthorities(UserModel user) {
//        return user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getName()))
//                .collect(Collectors.toList());
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        return authorities;
    }
}

