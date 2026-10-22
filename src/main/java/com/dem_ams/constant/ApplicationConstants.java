package com.dem_ams.constant;

import java.util.Set;

public class ApplicationConstants {
    public static final Set<String> USER_ROLES =
            Set.of("ROLE_ADMIN", "ROLE_TENANT", "ROLE_LANDLORD");

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_TENANT = "ROLE_TENANT";
    public static final String ROLE_LANDLORD = "ROLE_LANDLORD";

    public static final Set<String> TENANT_PERMISSIONS =
            Set.of("payments:create", "payments:read", "properties:read");
    public static final Set<String> ALL_PERMISSIONS =
            Set.of("payments:create", "payments:update", "payments:read", "payments:delete",
                    "properties:create", "properties:update", "properties:read", "properties:delete");
}
