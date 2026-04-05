package com.proc.proc.Repository;

import com.proc.proc.Model.JobSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobSubscriptionRepo extends JpaRepository<JobSubscription, Long> {
    Optional<JobSubscription> findByEmail(String email);
    List<JobSubscription> findByActiveTrue();
}
