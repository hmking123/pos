package com.example.pos.model;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dashboard_widgets")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class DashboardWidget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Type(type = "jsonb")
    @Column(name = "default_config", columnDefinition = "jsonb")
    private String defaultConfig;
}
