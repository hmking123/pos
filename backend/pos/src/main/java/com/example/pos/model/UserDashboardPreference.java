package com.example.pos.model;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_dashboard_preferences")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class UserDashboardPreference {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "widget_id")
    private DashboardWidget widget;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String config;

    private Integer position;
}
