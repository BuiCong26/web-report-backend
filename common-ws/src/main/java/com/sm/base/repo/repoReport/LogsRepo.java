package com.sm.base.repo.repoReport;


import com.sm.base.model.modelReport.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepo extends JpaRepository<Logs, Long> {

}
