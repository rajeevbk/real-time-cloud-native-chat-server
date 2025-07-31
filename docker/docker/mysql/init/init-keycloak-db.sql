-- This script is executed on the first startup of the MySQL container.
-- It creates the database and user that Keycloak needs to connect.

-- Create the Keycloak database if it doesn't exist
CREATE DATABASE IF NOT EXISTS `keycloak` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create the Keycloak user and grant privileges
-- Note: Use the same credentials as defined in the docker-compose.yml for the keycloak service
CREATE USER 'keycloak_user'@'%' IDENTIFIED BY 'keycloak_password123';
GRANT ALL PRIVILEGES ON `keycloak`.* TO 'keycloak_user'@'%';

-- Apply the changes
FLUSH PRIVILEGES;