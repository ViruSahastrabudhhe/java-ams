package com.dem_ams.config;

import com.dem_ams.constant.ApplicationConstants;
import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.*;
import com.dem_ams.domain.model.enums.BillingType;
import com.dem_ams.domain.model.enums.UnitStatus;
import com.dem_ams.domain.model.enums.UnitType;
import com.dem_ams.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(DataSeeder.class.getName());

    private final UserModelRepository userModelRepository;
    private final RoleRepository roleRepository;
    private final TenantProfileRepository tenantProfileRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvoiceRepository invoiceRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final PaymentRepository paymentRepository;
    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;
    private final ResidentialUnitRepository residentialUnitRepository;
    private final LeaseRepository leaseRepository;

    private final String[] permissionNames = ApplicationConstants.ALL_PERMISSIONS.toArray(String[]::new);
    private Map<String, UserModel> userModels = new HashMap<>();
    private Map<String, Role> roles = new HashMap<>();
    private List<TenantProfile> tenantProfiles;
    private List<LandlordProfile> landlordProfiles;
    private List<Invoice> invoices;
    private List<Property> properties;
    private List<Lease> leases;
    private List<Unit> units;
    private List<ResidentialUnit> residentialUnits;

    public DataSeeder(
            UserModelRepository userModelRepository,
            RoleRepository roleRepository,
            TenantProfileRepository tenantProfileRepository,
            LandlordProfileRepository landlordProfileRepository,
            PermissionRepository permissionRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            PropertyRepository propertyRepository,
            UnitRepository unitRepository,
            ResidentialUnitRepository residentialUnitRepository,
            LeaseRepository leaseRepository,
            PasswordEncoder passwordEncoder) {
        this.userModelRepository = userModelRepository;
        this.roleRepository = roleRepository;
        this.tenantProfileRepository = tenantProfileRepository;
        this.permissionRepository = permissionRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.propertyRepository = propertyRepository;
        this.unitRepository = unitRepository;
        this.residentialUnitRepository = residentialUnitRepository;
        this.leaseRepository = leaseRepository;
        this.landlordProfileRepository = landlordProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (permissionRepository.count() > 0
                || userModelRepository.existsByUsername("admin")
                || roleRepository.count() > 0
                || userModelRepository.count() > 0
                || tenantProfileRepository.count() > 0
                || invoiceRepository.count() > 0
                || paymentRepository.count() > 0
                || propertyRepository.count() > 0
                || unitRepository.count() > 0
                || residentialUnitRepository.count() > 0
                || leaseRepository.count() > 0) {
            return;
        }
        seedPermissions();
        seedAdminRoleAndUser();
        seedTenantAndLandlordRoles();
        seedTenantUserAccounts();
        seedLandlordUserAccounts();
        seedLandlordProfiles();
        seedProperties();
        seedUnits();
        seedResidentialUnits();
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
        roles.put("tenant", tenant);
        roles.put("landlord", landlord);
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
        Role tenantRole = roles.get("tenant");

        Map<String, UserModel> tenantAccounts = new HashMap<>();
        tenantAccounts.put("tenant", createUserHelper("tenant@example.com", "tenant", "pass123", Set.of(tenantRole)));
        tenantAccounts.put("renter", createUserHelper("renter@example.com", "renter", "word456", Set.of(tenantRole)));
        tenantAccounts.put("notenant", createUserHelper("notenant@example.com", "notenant", "pass123", Set.of(tenantRole)));
        userModels.putAll(tenantAccounts);
    }

    private void seedLandlordUserAccounts() {
        Role landlordRole = roles.get("landlord");

        Map<String, UserModel> landlordAccounts = new HashMap<>();
        landlordAccounts.put("landlord", createUserHelper("landlord@example.com", "landlord", "land123", Set.of(landlordRole)));
        landlordAccounts.put("noprofile", createUserHelper("nolandlord@example.com", "nolandlord", "lord456", Set.of(landlordRole)));
        userModels.putAll(landlordAccounts);
    }

    private void seedTenantProfiles() {
        tenantProfiles = List.of(
                createTenantProfileHelper("JOHN", "MAN", "TENANT", userModels.get("tenant"), Set.of(leases.get(0)), "09828287272"),
                createTenantProfileHelper("JANE", "WOMAN", "RENTER", userModels.get("renter"), Set.of(leases.get(1)), "09773332121")
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
                createLandlordProfileHelper("ALFRED", "LIM", "LANDLORD", userModels.get("landlord"), "09992221212"),
                createLandlordProfileHelper("NOLANDLORD", "STILL", "PROFILE", userModels.get("noprofile"),"09331214545")
        );
    }

    private void seedProperties() {
        properties = List.of(
            createPropertyHelper(landlordProfiles.get(0), "Laguna Apartment", "Apartments",
                    4500.00, BillingType.MONTHLY, 80.0, "Apartment in Laguna."),
            createPropertyHelper(landlordProfiles.get(1), "Lynville Apartment", "Apartments",
                    5500.00, BillingType.MONTHLY, 120.0, "Diamond Apartment in Lynville."),
            createPropertyHelper(landlordProfiles.get(0), "TeraVille Luxury Apartment", "Apartments",
                    6500.00, BillingType.MONTHLY, 160.0, "Luxury Apartment Unit in TeraVille")
        );
    }

    private void seedUnits() {
        units = List.of(
                createUnitHelper(properties.get(0), UnitType.RESIDENTIAL, 25.0, UnitStatus.OCCUPIED),
                createUnitHelper(properties.get(0), UnitType.RESIDENTIAL, 25.0, UnitStatus.OCCUPIED),
                createUnitHelper(properties.get(1), UnitType.RESIDENTIAL, 25.0, UnitStatus.OCCUPIED)
        );
    }

    private void seedResidentialUnits() {
        residentialUnits = List.of(
                createResidentialUnitHelper(units.get(0), "1A", 1, 2, 1),
                createResidentialUnitHelper(units.get(1), "1B", 1, 2, 1),
                createResidentialUnitHelper(units.get(2), "1C", 1, 2, 1)
        );
    }

    private void seedLeases() {
        leases = List.of(
                createLeaseHelper(
                        units.get(0), LocalDate.of(2026, 6, 1), LocalDate.of(2026, 12, 1),
                        units.get(0).getProperty().getBillingType(), units.get(0).getProperty().getPropertyValue(), 4500.00, Lease.LeaseStatus.DRAFT),
                createLeaseHelper(
                        units.get(1), LocalDate.of(2026, 6, 1), LocalDate.of(2026, 12, 1),
                        units.get(1).getProperty().getBillingType(), units.get(1).getProperty().getPropertyValue(), 5500.00, Lease.LeaseStatus.DRAFT)
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
            Set<Lease> leases, String phoneNumber
    ) {
        TenantProfile tenantProfile = new TenantProfile();
        tenantProfile.setFirstName(firstName);
        tenantProfile.setMiddleName(middleName);
        tenantProfile.setLastName(lastName);
        tenantProfile.setUser(user);
        tenantProfile.setPhoneNumber(phoneNumber);
        tenantProfile.setLeases(leases);
        return tenantProfileRepository.saveAndFlush(tenantProfile);
    }

    private Invoice createInvoiceHelper(
            Integer invoiceNumber, Double invoiceTotal, Invoice.InvoiceStatus invoiceStatus,
            LocalDate dueDate, UserModel user, TenantProfile tenantProfile
    ) {
        Invoice invoice = new Invoice();
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
            String firstName, String middleName, String lastName, UserModel user, String phoneNumber
    ) {
        LandlordProfile landlordProfile = new LandlordProfile();
        landlordProfile.setFirstName(firstName);
        landlordProfile.setMiddleName(middleName);
        landlordProfile.setLastName(lastName);
        landlordProfile.setPhoneNumber(phoneNumber);
        landlordProfile.setUser(user);
        return landlordProfileRepository.saveAndFlush(landlordProfile);
    }

    private Property createPropertyHelper(
            LandlordProfile landlordProfile, String name, String propertyType, Double propertyValue,
            BillingType billingType, Double size, String description
    ) {
        Property property = new Property();
        property.setLandlordProfile(landlordProfile);
        property.setName(name);
        property.setPropertyType(propertyType);
        property.setPropertyValue(propertyValue);
        property.setBillingType(billingType);
        property.setSize(size);
        property.setDescription(description);
        return propertyRepository.saveAndFlush(property);
    }

    private Unit createUnitHelper(
            Property property, UnitType unitType, Double squareMeters, UnitStatus status
    ) {
        Unit unit = new Unit();
        unit.setProperty(property);
        unit.setUnitType(unitType);
        unit.setSquareMeters(squareMeters);
        unit.setStatus(status);
        return unitRepository.saveAndFlush(unit);
    }

    private ResidentialUnit createResidentialUnitHelper(
            Unit unit, String unitNumber, Integer floor, Integer bedroomQuantity, Integer bathroomQuantity
    ) {
        ResidentialUnit residentialUnit = new ResidentialUnit();
        residentialUnit.setUnit(unit);
        residentialUnit.setUnitNumber(unitNumber);
        residentialUnit.setFloor(floor);
        residentialUnit.setBedroomQuantity(bedroomQuantity);
        residentialUnit.setBathroomQuantity(bathroomQuantity);
        return residentialUnitRepository.saveAndFlush(residentialUnit);
    }

    private Lease createLeaseHelper(
            Unit unit, LocalDate startDate, LocalDate endDate,
            BillingType billingType, Double rent, Double deposit, Lease.LeaseStatus status
    ) {
        Lease lease = new Lease();
        lease.setUnit(unit);
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.setBillingType(billingType);
        lease.setRent(rent);
        lease.setDeposit(deposit);
        lease.setStatus(status);
        return leaseRepository.saveAndFlush(lease);
    }
}