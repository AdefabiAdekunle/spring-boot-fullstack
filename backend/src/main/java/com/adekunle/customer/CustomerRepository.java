package com.adekunle.customer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Integer id);
    Optional<Customer> findCustomersByEmail(String email);
    @Modifying(clearAutomatically = true)
    @Query(" UPDATE Customer c SET c.profileImageId = ?1 WHERE c.id = ?2")// we have a JPQL since we are modifying only one field
    int updateProfileImageId(String profileImageId, Integer customerId);
}
