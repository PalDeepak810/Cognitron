package com.proc.proc.Repository;

import com.proc.proc.Model.CrawledPage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawledPageRepo extends JpaRepository<CrawledPage,Long> {

}
