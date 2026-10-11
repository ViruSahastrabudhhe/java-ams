package com.dem_ams.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invoice_lines")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE invoice_lines SET deleted_at = NOW() WHERE id = ?")
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoiceId", nullable = false)
    private Invoice invoice;

    private String itemName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
