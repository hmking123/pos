package com.example.pos.repository;

import com.example.pos.model.DashboardWidget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardWidgetRepository extends JpaRepository<DashboardWidget, Long> {
}
