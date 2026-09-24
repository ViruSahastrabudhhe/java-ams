package com.dem_ams.domain.beans;

import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.Role;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

import org.springframework.transaction.annotation.Transactional;

@Service("CustomUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CustomUserDetails.CustomUserDetailsBuilder customUser = CustomUserDetails.builder();
        return customUser
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(getAuthorities(user))
                .build();
    }

    private List<GrantedAuthority> getAuthorities(UserModel user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}

