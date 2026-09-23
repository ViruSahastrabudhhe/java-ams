package com.dem_ams.domain.dto;

import java.io.Serializable;

public record PaymentFormDTO(Double amount, Long user_id) implements Serializable {
}
