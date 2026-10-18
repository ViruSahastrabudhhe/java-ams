package com.dem_ams.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="invoices")
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    public enum InvoiceStatus {
        DRAFT, SENT, PAID, PARTIALLY_PAID, OVERDUE, VOID
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double invoiceTotal;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;
    private LocalDate dueDate;

    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<InvoiceLine> invoiceLines;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenantProfileId", nullable=false)
    private TenantProfile tenantProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserModel user;

    @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP", insertable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
