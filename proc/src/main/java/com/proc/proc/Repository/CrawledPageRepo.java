package com.proc.proc.Repository;

import com.proc.proc.Model.CrawledPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrawledPageRepo extends JpaRepository<CrawledPage,Long> {
    Page<CrawledPage> findByTitleContainingIgnoreCaseOrderByCrawledAtDesc(String title, Pageable pageable);

    @Query("""
    SELECT p FROM CrawledPage p
    WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(p.html) LIKE LOWER(CONCAT('%', :keyword, '%'))
    ORDER BY p.crawledAt DESC
""")
    Page<CrawledPage> searchByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
