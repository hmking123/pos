-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

-- User_roles table (for many-to-many relationship between users and roles)
CREATE TABLE user_roles (
    user_id INTEGER REFERENCES users(id),
    role_id INTEGER REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- Permissions table
CREATE TABLE permissions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

-- Role_permissions table (for many-to-many relationship between roles and permissions)
CREATE TABLE role_permissions (
    role_id INTEGER REFERENCES roles(id),
    permission_id INTEGER REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

-- Sessions table
CREATE TABLE sessions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Two-factor authentication table
CREATE TABLE two_factor_auth (
    user_id INTEGER PRIMARY KEY REFERENCES users(id),
    secret_key VARCHAR(32) NOT NULL,
    is_enabled BOOLEAN DEFAULT false
);

-- Dashboard widgets table
CREATE TABLE dashboard_widgets (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    default_config JSONB
);

-- User dashboard preferences table
CREATE TABLE user_dashboard_preferences (
    user_id INTEGER REFERENCES users(id),
    widget_id INTEGER REFERENCES dashboard_widgets(id),
    config JSONB,
    position INTEGER,
    PRIMARY KEY (user_id, widget_id)
);

-- Trigger to update the 'updated_at' column in the users table
CREATE OR REPLACE FUNCTION update_user_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_user_timestamp
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION update_user_timestamp();

-- Trigger to ensure a user has at least one role
CREATE OR REPLACE FUNCTION ensure_user_has_role()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = NEW.id) THEN
        INSERT INTO user_roles (user_id, role_id)
        VALUES (NEW.id, (SELECT id FROM roles WHERE name = 'USER'));
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ensure_user_has_role
AFTER INSERT ON users
FOR EACH ROW
EXECUTE FUNCTION ensure_user_has_role();

-- Procedure to add a user with a role
CREATE OR REPLACE PROCEDURE add_user_with_role(
    p_username VARCHAR(50),
    p_email VARCHAR(100),
    p_password_hash VARCHAR(255),
    p_first_name VARCHAR(50),
    p_last_name VARCHAR(50),
    p_role_name VARCHAR(50)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_user_id INTEGER;
    v_role_id INTEGER;
BEGIN
    -- Insert the user
    INSERT INTO users (username, email, password_hash, first_name, last_name)
    VALUES (p_username, p_email, p_password_hash, p_first_name, p_last_name)
    RETURNING id INTO v_user_id;

    -- Get the role ID
    SELECT id INTO v_role_id FROM roles WHERE name = p_role_name;

    -- Assign the role to the user
    INSERT INTO user_roles (user_id, role_id)
    VALUES (v_user_id, v_role_id);
END;
$$;

-- Procedure to update user's dashboard preferences
CREATE OR REPLACE PROCEDURE update_user_dashboard_preferences(
    p_user_id INTEGER,
    p_widget_id INTEGER,
    p_config JSONB,
    p_position INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO user_dashboard_preferences (user_id, widget_id, config, position)
    VALUES (p_user_id, p_widget_id, p_config, p_position)
    ON CONFLICT (user_id, widget_id)
    DO UPDATE SET
        config = p_config,
        position = p_position;
END;
$$;

-- View for user details with roles
CREATE OR REPLACE VIEW user_details_with_roles AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    u.is_active,
    array_agg(r.name) AS roles
FROM 
    users u
JOIN 
    user_roles ur ON u.id = ur.user_id
JOIN 
    roles r ON ur.role_id = r.id
GROUP BY 
    u.id, u.username, u.email, u.first_name, u.last_name, u.is_active;

-- View for user permissions
CREATE OR REPLACE VIEW user_permissions AS
SELECT 
    u.id AS user_id,
    u.username,
    array_agg(DISTINCT p.name) AS permissions
FROM 
    users u
JOIN 
    user_roles ur ON u.id = ur.user_id
JOIN 
    role_permissions rp ON ur.role_id = rp.role_id
JOIN 
    permissions p ON rp.permission_id = p.id
GROUP BY 
    u.id, u.username;

-- View for active sessions
CREATE OR REPLACE VIEW active_sessions AS
SELECT 
    s.id AS session_id,
    u.id AS user_id,
    u.username,
    s.token,
    s.expires_at
FROM 
    sessions s
JOIN 
    users u ON s.user_id = u.id
WHERE 
    s.expires_at > CURRENT_TIMESTAMP;

-- View for user dashboard configuration
CREATE OR REPLACE VIEW user_dashboard_config AS
SELECT 
    udp.user_id,
    json_agg(
        json_build_object(
            'widget_id', dw.id,
            'name', dw.name,
            'config', COALESCE(udp.config, dw.default_config),
            'position', udp.position
        ) ORDER BY udp.position
    ) AS dashboard_config
FROM 
    dashboard_widgets dw
LEFT JOIN 
    user_dashboard_preferences udp ON dw.id = udp.widget_id
GROUP BY 
    udp.user_id;

