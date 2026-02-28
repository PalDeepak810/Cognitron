package com.proc.proc.Repository;

import com.proc.proc.Model.VisitedUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitedUrlRepo extends JpaRepository<VisitedUrl, String> {
    boolean existsByUrlHash(String urlHash);
}
