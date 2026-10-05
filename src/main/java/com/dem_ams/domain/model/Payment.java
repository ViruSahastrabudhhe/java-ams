package com.dem_ams.domain.model;

import com.dem_ams.domain.dto.PaymentResponse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.SQLDelete;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table(name="payments")
@Entity
@NoArgsConstructor
@SQLDelete(sql = "UPDATE payments SET deleted_at = NOW() WHERE id = ?")
//@SQLRestriction("deleted_at IS NULL")
public class Payment {
    public enum PaymentType {
        CASH, CARD, E_WALLET
    }

    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    @Column(name="amount_paid")
    private Double amountPaid;

    @Generated
    @Column(columnDefinition = "double precision default 0.0")
    private Double amountChange;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="invoice_id", nullable=false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenant_profile_id", nullable=false)
    private TenantProfile tenantProfile;

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
                payment.getAmountChange(),
                payment.getPaymentType(),
                payment.getInvoice().getId(),
                payment.getTenantProfile().getId(),
                payment.getUser().getId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getDeletedAt()
        );
    }
}
