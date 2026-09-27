package com.dem_ams.domain.model;

import com.dem_ams.domain.dto.PaymentResponse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table(name="payments")
@Entity
@NoArgsConstructor
@SoftDelete(strategy = SoftDeleteType.DELETED)
public class Payment implements Serializable {

    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    private Double amountPaid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserModel user;

    @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime dateCreated = LocalDateTime.now();

    private LocalDateTime dateUpdated;

    private LocalDateTime dateDeleted;

    @PreUpdate
    public void preUpdate() {
        this.dateUpdated = LocalDateTime.now();
    }

    public static PaymentResponse toJson(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmountPaid(),
                payment.getUser().getId(),
                payment.getDateCreated(),
                payment.getDateUpdated(),
                payment.getDateDeleted()
        );
    }
}
