package com.dem_ams.config;

import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.*;
import com.dem_ams.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserModelRepository userModelRepository;
    private final RoleRepository roleRepository;
    private final TenantProfileRepository tenantProfileRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvoiceRepository invoiceRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final PaymentRepository paymentRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyDetailRepository propertyDetailRepository;
    private final LeaseRepository leaseRepository;

    private final String[] permissionNames = {
            "payments:create", "payments:update", "payments:read", "payments:delete",
            "properties:create", "properties:update", "properties:read", "properties:delete",
    };
    private Map<String, UserModel> userModels = new HashMap<>();
    private List<TenantProfile> tenantProfiles;
    private List<LandlordProfile> landlordProfiles;
    private List<Invoice> invoices;
    private List<PropertyDetail> propertyDetails;
    private List<Property> properties;
    private List<Lease> leases;

    public DataSeeder(
            UserModelRepository userModelRepository,
            RoleRepository roleRepository,
            TenantProfileRepository tenantProfileRepository,
            LandlordProfileRepository landlordProfileRepository,
            PermissionRepository permissionRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            PropertyRepository propertyRepository,
            PropertyDetailRepository propertyDetailRepository,
            LeaseRepository leaseRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userModelRepository = userModelRepository;
        this.roleRepository = roleRepository;
        this.tenantProfileRepository = tenantProfileRepository;
        this.permissionRepository = permissionRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.propertyRepository = propertyRepository;
        this.propertyDetailRepository = propertyDetailRepository;
        this.leaseRepository = leaseRepository;
        this.landlordProfileRepository = landlordProfileRepository;
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
                || propertyRepository.count() > 0
                || propertyDetailRepository.count() > 0
                || leaseRepository.count() > 0
        ) {
            return;
        }
        seedPermissions();
        seedAdminRoleAndUser();
        seedTenantAndLandlordRoles();
        seedTenantUserAccounts();
        seedLandlordUserAccounts();
        seedLandlordProfiles();
        seedProperties();
        seedPropertyDetails();
        seedLeases();
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
        Set<String> permissions = Set.of("payments:read", "payments:create", "properties:create", "properties:update", "properties:read", "properties:delete");

        return permissionRepository.findAll().stream()
                .filter(p -> permissions.contains(p.getName()))
                .collect(Collectors.toSet());
    }

    private Set<Permission> getTenantPermissions() {
        Set<String> permissions = Set.of("payments:read", "payments:create", "properties:read");

        return permissionRepository.findAll().stream()
                .filter(p -> permissions.contains(p.getName()))
                .collect(Collectors.toSet());
    }

    private void seedTenantUserAccounts() {
        Role tenantRole = roleRepository.findById(2L).orElseThrow(() -> new ResourceNotFoundException("Could not find role with id", 2L));

        Map<String, UserModel> tenantAccounts = new HashMap<>();
        tenantAccounts.put("tenant", createUserHelper("tenant@example.com", "tenant", "pass123", Set.of(tenantRole)));
        tenantAccounts.put("renter", createUserHelper("renter@example.com", "renter", "word456", Set.of(tenantRole)));
        tenantAccounts.put("notenant", createUserHelper("notenant@example.com", "notenant", "pass123", Set.of(tenantRole)));
        userModels.putAll(tenantAccounts);
    }

    private void seedLandlordUserAccounts() {
        Role landlordRole = roleRepository.findById(3L).orElseThrow(() -> new ResourceNotFoundException("Could not find role with id", 3L));

        Map<String, UserModel> landlordAccounts = new HashMap<>();
        landlordAccounts.put("landlord", createUserHelper("landlord@example.com", "landlord", "land123", Set.of(landlordRole)));
        landlordAccounts.put("noprofile", createUserHelper("nolandlord@example.com", "nolandlord", "lord456", Set.of(landlordRole)));
        userModels.putAll(landlordAccounts);
    }

    private void seedTenantProfiles() {
        tenantProfiles = List.of(
                createTenantProfileHelper("JOHN", "MAN", "TENANT", userModels.get("tenant"), Set.of(leases.get(0))),
                createTenantProfileHelper("JANE", "WOMAN", "RENTER", userModels.get("renter"), Set.of(leases.get(1)))
        );
    }

    private void seedInvoices() {
        invoices = List.of(
            createInvoiceHelper(23, 4500.00, Invoice.InvoiceStatus.PAID, LocalDate.of(2026, 6, 1), userModels.get("tenant"), tenantProfiles.get(0)),
            createInvoiceHelper(27, 5000.00, Invoice.InvoiceStatus.DRAFT, LocalDate.of(2026, 9, 1), userModels.get("renter"), tenantProfiles.get(1))
        );
    }

    private void seedPayments() {
        createPaymentHelper(4500.00, userModels.get("tenant"), Payment.PaymentType.CASH, invoices.get(0), tenantProfiles.get(0));
        createPaymentHelper(5000.00, userModels.get("renter"), Payment.PaymentType.CASH, invoices.get(1), tenantProfiles.get(1));
    }

    private void seedLandlordProfiles() {
        landlordProfiles = List.of(
                createLandlordProfileHelper("ALFRED", "LIM", "LANDLORD", userModels.get("landlord")),
                createLandlordProfileHelper("NOLANDLORD", "STILL", "PROFILE", userModels.get("noprofile"))
        );
    }

    private void seedProperties() {
        properties = List.of(
            createPropertyHelper(landlordProfiles.get(0)),
            createPropertyHelper(landlordProfiles.get(1)),
            createPropertyHelper(landlordProfiles.get(0))
        );
    }

    private void seedPropertyDetails() {
        propertyDetails = List.of(
                createPropertyDetailHelper(
                        "Laguna Apartment",
                        "Apartments",
                        4500.00,
                        BillingType.MONTHLY,
                        2, 1, "80 sqm", "Apartment in Laguna.",
                        properties.get(0)
                ),
                createPropertyDetailHelper(
                        "Lynville Apartment",
                        "Apartments",
                        5500.00,
                        BillingType.MONTHLY,
                        3, 1, "120 sqm", "Diamond Apartment in Lynville.",
                        properties.get(1)
                ),
                createPropertyDetailHelper(
                        "TeraVille Luxury Apartment",
                        "Apartments",
                        6500.00,
                        BillingType.MONTHLY,
                        3, 2, "160 sqm", "Luxury Apartment Unit in TeraVille",
                        properties.get(2)
                )
        );
    }

    private void seedLeases() {
        leases = List.of(
                createLeaseHelper(
                        properties.get(0), LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 1),
                        propertyDetails.get(0).getBillingType(), propertyDetails.get(0).getPropertyValue(), 4500.00, Lease.LeaseStatus.DRAFT),
                createLeaseHelper(
                        properties.get(1), LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 1),
                        propertyDetails.get(1).getBillingType(), propertyDetails.get(1).getPropertyValue(), 5500.00, Lease.LeaseStatus.DRAFT)
        );
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
            String firstName, String middleName, String lastName, UserModel user,
            Set<Lease> leases
    ) {
        TenantProfile tenantProfile = new TenantProfile();
        tenantProfile.setFirstName(firstName);
        tenantProfile.setMiddleName(middleName);
        tenantProfile.setLastName(lastName);
        tenantProfile.setUser(user);
        tenantProfile.setLeases(leases);
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

    private LandlordProfile createLandlordProfileHelper(
            String firstName, String middleName, String lastName, UserModel user
    ) {
        LandlordProfile landlordProfile = new LandlordProfile();
        landlordProfile.setFirstName(firstName);
        landlordProfile.setMiddleName(middleName);
        landlordProfile.setLastName(lastName);
        landlordProfile.setUser(user);
        return landlordProfileRepository.saveAndFlush(landlordProfile);
    }

    private PropertyDetail createPropertyDetailHelper(
            String name, String propertyType, Double propertyValue, BillingType billingType,
            Integer bedroomQuantity, Integer bathroomQuantity, String size, String description,
            Property property
    ) {
        PropertyDetail propertyDetail = new PropertyDetail();
        propertyDetail.setName(name);
        propertyDetail.setPropertyType(propertyType);
        propertyDetail.setPropertyValue(propertyValue);
        propertyDetail.setBillingType(billingType);
        propertyDetail.setBedroomQuantity(bedroomQuantity);
        propertyDetail.setBathroomQuantity(bathroomQuantity);
        propertyDetail.setSize(size);
        propertyDetail.setDescription(description);
        propertyDetail.setProperty(property);
        return propertyDetailRepository.saveAndFlush(propertyDetail);
    }

    private Property createPropertyHelper(
            LandlordProfile landlordProfile
    ) {
        Property property = new Property();
        property.setLandlordProfile(landlordProfile);
        return propertyRepository.saveAndFlush(property);
    }

    private Lease createLeaseHelper(
            Property property, LocalDate startDate, LocalDate endDate,
            BillingType billingType, Double rent, Double deposit, Lease.LeaseStatus status
    ) {
        Lease lease = new Lease();
        lease.setProperty(property);
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.setBillingType(billingType);
        lease.setRent(rent);
        lease.setDeposit(deposit);
        lease.setStatus(status);
        return leaseRepository.saveAndFlush(lease);
    }
}