package com.sm.base.repo.repoReport;


import com.sm.base.model.modelReport.Tbltruyvan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ADMIN
 */
@Repository
public interface TbltruyvanRepo extends JpaRepository<Tbltruyvan, Long>{
    
}
