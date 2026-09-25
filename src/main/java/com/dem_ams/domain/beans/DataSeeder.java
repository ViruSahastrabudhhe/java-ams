package com.dem_ams.domain.beans;

import com.dem_ams.domain.model.Payment;
import com.dem_ams.domain.model.Role;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.PaymentRepository;
import com.dem_ams.repository.RoleRepository;
import com.dem_ams.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public void run(String... args) throws Exception {
    }

    private void createAdminUser() {
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);

        UserModel user = new UserModel();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(adminRole));
        userRepository.save(user);
    }

//    private void createPayments() {
//        Double[] amounts = new Double[4];
//        amounts[0] = 100.00;
//        amounts[1] = 5.00;
//        amounts[2] = 250.00;
//        amounts[3] = 110.10;
//
//        Long[] ids = new Long[4];
//        ids[0] = 90L;
//        ids[1] = 91L;
//        ids[2] = 92L;
//        ids[3] = 93L;
//
//        for (int i = 0; i < 4; i++) {
//            Payment payment = new Payment(ids[i], amounts[i], 1L);
//            paymentRepository.save(payment);
//        }
//    }
}