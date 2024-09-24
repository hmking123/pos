package com.example.pos.service;

import com.example.pos.model.DashboardWidget;
import com.example.pos.model.User;
import com.example.pos.model.UserDashboardPreference;
import com.example.pos.repository.DashboardWidgetRepository;
import com.example.pos.repository.UserDashboardPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private DashboardWidgetRepository dashboardWidgetRepository;

    @Autowired
    private UserDashboardPreferenceRepository userDashboardPreferenceRepository;

    public List<DashboardWidget> getAllWidgets() {
        return dashboardWidgetRepository.findAll();
    }

    public List<UserDashboardPreference> getUserDashboardPreferences(User user) {
        return userDashboardPreferenceRepository.findByUser(user);
    }

    public UserDashboardPreference saveUserDashboardPreference(UserDashboardPreference preference) {
        return userDashboardPreferenceRepository.save(preference);
    }
}
