package com.dem_ams.domain.dto;

import java.io.Serializable;

public record TenantProfileRequest(
        String firstName,
        String middleName,
        String lastName
) implements Serializable {
}
