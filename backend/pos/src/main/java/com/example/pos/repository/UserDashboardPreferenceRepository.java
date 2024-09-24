package com.example.pos.repository;

import com.example.pos.model.User;
import com.example.pos.model.UserDashboardPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDashboardPreferenceRepository extends JpaRepository<UserDashboardPreference, Long> {
    List<UserDashboardPreference> findByUser(User user);
}
