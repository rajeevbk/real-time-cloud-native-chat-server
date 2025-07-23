-- Used onluy for development purposes

-- Keycloak DB
CREATE DATABASE IF NOT EXISTS keycloak CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Chat DB user
CREATE USER IF NOT EXISTS 'chatuser'@'%' IDENTIFIED BY 'chatpassword123';
GRANT ALL PRIVILEGES ON chat_server.* TO 'chatuser'@'%';

-- Keycloak DB user
CREATE USER IF NOT EXISTS 'keycloak_user'@'%' IDENTIFIED BY 'keycloak_password123';
GRANT ALL PRIVILEGES ON keycloak.* TO 'keycloak_user'@'%';

-- Apply changes
FLUSH PRIVILEGES;