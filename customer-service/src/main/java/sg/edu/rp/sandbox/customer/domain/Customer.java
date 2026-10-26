package sg.edu.rp.sandbox.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import sg.edu.rp.sandbox.common.kyc.KycStatus;

import java.time.Instant;

/**
 * A bank customer. The {@code customerRef} is the stable public identifier used
 * across services and matched against the authenticated token subject.
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_ref", nullable = false, unique = true, updatable = false)
    private String customerRef;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "national_id")
    private String nationalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false)
    private KycStatus kycStatus;

    @Column(name = "kyc_reason")
    private String kycReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Customer() {
        // for JPA
    }

    public Customer(String customerRef, String fullName, String email, String nationalId,
                    KycStatus kycStatus, String kycReason) {
        this.customerRef = customerRef;
        this.fullName = fullName;
        this.email = email;
        this.nationalId = nationalId;
        this.kycStatus = kycStatus;
        this.kycReason = kycReason;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getCustomerRef() {
        return customerRef;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getNationalId() {
        return nationalId;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public String getKycReason() {
        return kycReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
