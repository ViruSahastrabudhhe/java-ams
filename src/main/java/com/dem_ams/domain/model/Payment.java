package com.dem_ams.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Table(name="payments")
@Entity
@NoArgsConstructor
public class Payment {

    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    private Double amountPaid;

    private Long user_id;

    private Timestamp dateCreated = Timestamp.from(Instant.now());

    private Timestamp dateUpdated;

    private Timestamp dateDeleted;
}
