package sg.edu.rp.sandbox.transaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    /**
     * History for one account owned by the given customer, optionally filtered by
     * a booked-at date range. Null range bounds are ignored.
     */
    @Query("""
            select e from LedgerEntry e
            where e.accountNumber = :accountNumber
              and e.customerRef = :customerRef
              and (:from is null or e.bookedAt >= :from)
              and (:to   is null or e.bookedAt <= :to)
            order by e.bookedAt desc, e.id desc
            """)
    List<LedgerEntry> findHistory(@Param("accountNumber") String accountNumber,
                                  @Param("customerRef") String customerRef,
                                  @Param("from") Instant from,
                                  @Param("to") Instant to);
}
