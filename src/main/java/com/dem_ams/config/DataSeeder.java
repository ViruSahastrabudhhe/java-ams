package com.dem_ams.config;

import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.*;
import com.dem_ams.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserModelRepository userModelRepository;
    private final RoleRepository roleRepository;
    private final TenantProfileRepository tenantProfileRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    private final String[] permissionNames = {
            "CREATE_PAYMENTS", "UPDATE_PAYMENTS", "GET_PAYMENTS", "DELETE_PAYMENTS",
            "CREATE_INVOICES", "UPDATE_INVOICES", "GET_INVOICES", "DELETE_INVOICES",
    };
    private List<UserModel> userModels;
    private List<TenantProfile> tenantProfiles;
    private List<Invoice> invoices;

    public DataSeeder(
            UserModelRepository userModelRepository,
            RoleRepository roleRepository,
            TenantProfileRepository tenantProfileRepository,
            PermissionRepository permissionRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userModelRepository = userModelRepository;
        this.roleRepository = roleRepository;
        this.tenantProfileRepository = tenantProfileRepository;
        this.permissionRepository = permissionRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (permissionRepository.count() > 0
                || userModelRepository.existsByUsername("admin")
                || roleRepository.count() > 0
                || userModelRepository.count() > 0
                || tenantProfileRepository.count() > 0
                || invoiceRepository.count() > 0
                || paymentRepository.count() > 0
        ) {
            return;
        }
        seedPermissions();
        seedAdminRoleAndUser();
        seedTenantAndLandlordRoles();
        seedTenantUserAccounts();
        seedTenantProfiles();
        seedInvoices();
        seedPayments();
    }

    private void seedPermissions() {
        for (int i = 0; i < permissionNames.length; i++) {
            Permission permission = new Permission();
            permission.setName(permissionNames[i]);
            permissionRepository.save(permission);
        }
    }

    private void seedTenantAndLandlordRoles() {
        Role tenant = new Role();
        tenant.setName("ROLE_TENANT");
        tenant.setPermissions(getTenantPermissions());

        Role landlord = new Role();
        landlord.setName("ROLE_LANDLORD");
        landlord.setPermissions(getLandlordPermissions());

        roleRepository.save(tenant);
        roleRepository.save(landlord);
    }

    private void seedAdminRoleAndUser() {
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole.setPermissions(getAdminPermissions());
        roleRepository.save(adminRole);

        UserModel user = new UserModel();
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(adminRole));
        userModelRepository.save(user);
    }

    private Set<Permission> getAdminPermissions() {
        return new HashSet<>(permissionRepository.findAll());
    }

    private Set<Permission> getLandlordPermissions() {
        Set<String> permissions = Set.of("CREATE_INVOICES", "UPDATE_INVOICES", "GET_INVOICES", "DELETE_INVOICES");

        return permissionRepository.findAll().stream()
                .filter(p -> permissions.contains(p.getName()))
                .collect(Collectors.toSet());
    }

    private Set<Permission> getTenantPermissions() {
        Set<String> permissions = Set.of("GET_PAYMENTS", "CREATE_PAYMENTS", "GET_INVOICES");

        return permissionRepository.findAll().stream()
                .filter(p -> permissions.contains(p.getName()))
                .collect(Collectors.toSet());
    }

    private void seedTenantUserAccounts() {
        Role tenantRole = roleRepository.findById(2L).orElseThrow(() -> new ResourceNotFoundException("Could not find role with id", 2L));

        userModels = List.of(
                createUserHelper("tenant@example.com", "tenant", "pass123", Set.of(tenantRole)),
                createUserHelper("renter@example.com", "renter", "word456", Set.of(tenantRole)),
                createUserHelper("notenant@example.com", "notenant", "pass123", Set.of(tenantRole))
        );
    }

    private void seedTenantProfiles() {
        tenantProfiles = List.of(
                createTenantProfileHelper("JOHN", "MAN", "TENANT", userModels.get(0)),
                createTenantProfileHelper("JANE", "WOMAN", "RENTER", userModels.get(1))
        );
    }

    private void seedInvoices() {
        invoices = List.of(
            createInvoiceHelper(23, 4500.00, Invoice.InvoiceStatus.PAID, LocalDate.of(2026, 6, 1), userModels.get(0), tenantProfiles.get(0)),
            createInvoiceHelper(27, 5000.00, Invoice.InvoiceStatus.DRAFT, LocalDate.of(2026, 9, 1), userModels.get(1), tenantProfiles.get(1))
        );
    }

    private void seedPayments() {
        createPaymentHelper(4500.00, userModels.get(0), Payment.PaymentType.CASH, invoices.get(0), tenantProfiles.get(0));
        createPaymentHelper(5000.00, userModels.get(1), Payment.PaymentType.CASH, invoices.get(1), tenantProfiles.get(1));
    }

    private UserModel createUserHelper(
            String email, String username, String password, Set<Role> roles
    ) {
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        return userModelRepository.saveAndFlush(user);
    }

    private TenantProfile createTenantProfileHelper(
            String firstName, String middleName, String lastName, UserModel user
    ) {
        TenantProfile tenantProfile = new TenantProfile();
        tenantProfile.setFirstName(firstName);
        tenantProfile.setMiddleName(middleName);
        tenantProfile.setLastName(lastName);
        tenantProfile.setUser(user);
        return tenantProfileRepository.saveAndFlush(tenantProfile);
    }

    private Invoice createInvoiceHelper(
            Integer invoiceNumber, Double invoiceTotal, Invoice.InvoiceStatus invoiceStatus,
            LocalDate dueDate, UserModel user, TenantProfile tenantProfile
    ) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setInvoiceTotal(invoiceTotal);
        invoice.setInvoiceStatus(invoiceStatus);
        invoice.setDueDate(dueDate);
        invoice.setTenantProfile(tenantProfile);
        invoice.setUser(user);
        return invoiceRepository.saveAndFlush(invoice);
    }

    private Payment createPaymentHelper(
            Double amountPaid, UserModel user, Payment.PaymentType paymentType, Invoice invoice,
            TenantProfile tenantProfile
    ) {
        Payment payment = new Payment();
        payment.setAmountPaid(amountPaid);
        payment.setUser(user);
        payment.setPaymentType(paymentType);
        payment.setInvoice(invoice);
        payment.setTenantProfile(tenantProfile);
        return paymentRepository.saveAndFlush(payment);
    }
}