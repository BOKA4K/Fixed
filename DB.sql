create database fixed2;
use fixed2;
-- Creating the 'users' table for storing both customer and technician data
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15),
	status ENUM('online', 'offline', 'busy', 'away') DEFAULT 'offline', -- Added status column
    role ENUM('customer', 'technician') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Creating the 'admins' table for storing admin data separately
CREATE TABLE admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
	status ENUM('online', 'offline', 'busy', 'away') DEFAULT 'offline', -- Added status column
    phone_number VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating the 'chats' table to store messages exchanged between users
CREATE TABLE chats (
    chat_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT,
    receiver_id INT,
    message TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_status BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Creating the 'service_requests' table for storing customer service requests
CREATE TABLE service_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    technician_id INT,
    problem_description TEXT,
    location VARCHAR(255),
    status ENUM('pending', 'in_progress', 'completed') DEFAULT 'pending',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    appointment_time DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (technician_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Creating the 'appointments' table to manage appointment scheduling
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    service_request_id INT,
    technician_id INT,
    scheduled_time DATETIME,
    status ENUM('scheduled', 'completed', 'cancelled') DEFAULT 'scheduled',
    FOREIGN KEY (service_request_id) REFERENCES service_requests(request_id) ON DELETE CASCADE,
    FOREIGN KEY (technician_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Creating the 'payments' table for managing customer payments
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    service_request_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('credit_card', 'debit_card', 'paypal', 'wallet') NOT NULL,
    payment_status ENUM('pending', 'completed', 'failed') DEFAULT 'pending',
    paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (service_request_id) REFERENCES service_requests(request_id) ON DELETE CASCADE
);

-- Creating the 'reviews' table for customers to review technicians after service completion
CREATE TABLE reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    service_request_id INT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (service_request_id) REFERENCES service_requests(request_id) ON DELETE CASCADE
);

-- Creating the 'notifications' table for tracking notifications sent to customers and technicians
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    message TEXT,
    notification_type ENUM('appointment', 'payment', 'service_update', 'reminder') NOT NULL,
    read_status BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
-- Inserting test data into 'users' table
-- Inserting test data into 'users' table with more descriptive skills for technicians
INSERT INTO users (name, email, password, phone_number, role, status, skills)
VALUES 
    ('Alice Johnson', 'alice.johnson@example.com', 'password123', '123-456-7890', 'customer', 'online', NULL),
    ('Bob Smith', 'bob.smith@example.com', 'password123', '234-567-8901', 'customer', 'offline', NULL),
    ('Charlie Brown', 'charlie.brown@example.com', 'password123', '345-678-9012', 'technician', 'busy', 'Electrical wiring, Plumbing installation, HVAC system maintenance, Smart home automation'),
    ('Diana White', 'diana.white@example.com', 'password123', '456-789-0123', 'technician', 'away', 'Advanced Carpentry, Plumbing repair and installation, Kitchen and bathroom remodeling'),
    ('Eva Green', 'eva.green@example.com', 'password123', '567-890-1234', 'customer', 'offline', NULL),
    ('Frank Harris', 'frank.harris@example.com', 'password123', '678-901-2345', 'technician', 'online', 'Residential and commercial Electrical work, HVAC installation and repairs, Energy efficiency solutions');

-- Inserting test data into 'admins' table
-- Inserting test data into 'admins' table
INSERT INTO admins (name, email, password, phone_number, status)
VALUES 
    ('Grace Lee', 'grace.lee@example.com', 'adminpassword1', '789-012-3456', 'online'),
    ('Henry Miller', 'henry.miller@example.com', 'adminpassword2', '890-123-4567', 'offline');
ALTER TABLE users
ADD COLUMN skills TEXT DEFAULT NULL;
ALTER TABLE users
ADD COLUMN hourlyrate TEXT DEFAULT NULL;
select * from service_requests;
select * from appointments;


