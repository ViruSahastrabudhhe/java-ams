package com.dem_ams.domain.model;

import com.dem_ams.domain.dto.PaymentResponse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table(name="payments")
@Entity
@NoArgsConstructor
@SQLDelete(sql = "UPDATE payments SET deleted_at = NOW() WHERE id = ?")
//@SQLRestriction("deleted_at IS NULL")
public class Payment implements Serializable {

    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    private Double amountPaid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserModel user;

    @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP", insertable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static PaymentResponse toJson(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmountPaid(),
                payment.getUser().getId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getDeletedAt()
        );
    }
}
