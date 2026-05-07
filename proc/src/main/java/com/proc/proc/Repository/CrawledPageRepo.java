package com.proc.proc.Repository;

import com.proc.proc.Model.CrawledPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawledPageRepo extends JpaRepository<CrawledPage,Long> {
    Page<CrawledPage> findByTitleContainingIgnoreCaseOrderByCrawledAtDesc(String title, Pageable pageable);

    @org.springframework.data.jpa.repository.Query(
            value = """
                    SELECT *
                    FROM crawled_page p
                    WHERE LOWER(IFNULL(p.title, '')) LIKE LOWER(CONCAT('%', ?1, '%'))
                       OR LOWER(IFNULL(p.text, '')) LIKE LOWER(CONCAT('%', ?1, '%'))
                    ORDER BY p.crawled_at DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM crawled_page p
                    WHERE LOWER(IFNULL(p.title, '')) LIKE LOWER(CONCAT('%', ?1, '%'))
                       OR LOWER(IFNULL(p.text, '')) LIKE LOWER(CONCAT('%', ?1, '%'))
                    """,
            nativeQuery = true
    )
    Page<CrawledPage> searchByKeyword(String keyword, Pageable pageable);

}
