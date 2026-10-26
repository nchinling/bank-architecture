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
import java.time.LocalDate;

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

    // --- Profile fields (all optional) ---

    @Column(name = "phone")
    private String phone;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "preferred_channel")
    private String preferredChannel;

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

    /**
     * Sets the optional profile details. Kept separate from the constructor so
     * onboarding stays simple and profile data can be added/updated independently.
     */
    public void setProfile(String phone, String addressLine1, String addressLine2, String city,
                           String postalCode, String country, LocalDate dateOfBirth,
                           String occupation, String preferredChannel) {
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.occupation = occupation;
        this.preferredChannel = preferredChannel;
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

    public String getPhone() {
        return phone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getPreferredChannel() {
        return preferredChannel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
