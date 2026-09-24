package com.dem_ams.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Collection;

@Data
@Table(name="payments")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    private Double amount;

    private Long user_id;
}
