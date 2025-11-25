Retix – Secondary Ticket Marketplace (Backend)

Retix is a backend application that enables secure buying, selling, and transferring of event tickets.
It supports user roles, ticket listings, booking workflows, payment status updates, and QR-based ticket verification


Features
User registration & login
Ticket creation, search, deletion
Buyer → Seller booking request flow
Seller approval system
Payment confirmation APIs
QR code generation and verification
MySQL database with JPA
Complete Postman API collection included (/postman/Retix-collection.postman_collection.json)

Tech Stack
Backend
Java 17
Spring Boot
Spring Web
Spring Data JPA (Hibernate)

MySQL(Database)
Maven
Lombok

Tools
GitHub
Postman
VS Code

Setup Instructions
1️⃣ Clone the Project
git clone https://github.com/nikitha-56/retix.git

cd retix

2️⃣ Create MySQL Database
CREATE DATABASE retix;

3️⃣ Configure Database in application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/retix
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

4️⃣ Install Dependencies
mvn clean install

5️⃣ Run the Server
mvn spring-boot:run

6️⃣ Access APIs
http://localhost:8080/

 Postman Collection is available here:
/postman/Retix-collection.postman_collection.json

API Documentation

The Postman collection includes:
User APIs
Ticket APIs
Booking/Request APIs
Payment APIs
QR Code Generation APIs
Ticket Verification APIs

Bonus Features Implemented
QR code generation using ZXing library
Secure QR token containing: ticketId, sellerId, expiry time
Ticket verification endpoint using QR decode
Modular service-repository-controller architecture
Postman collection of all APIs
Status-tracking for booking workflow (REQUESTED → APPROVED → PAID)

<img width="400" height="500" alt="image" src="https://github.com/user-attachments/assets/6e6abf64-a472-4d5c-b382-6d553a59abce" />

<img width="400" height="200" alt="image" src="https://github.com/user-attachments/assets/11a1ee19-b23d-4ae1-b8f1-e3dd01d58b84" />

<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/4d0ad727-fca1-49a7-a051-358e936cb769" />

<img width="635" height="401" alt="image" src="https://github.com/user-attachments/assets/b2a918b2-dfac-4095-9627-441dc95f8cbe" />

<img width="400" height="500" alt="image" src="https://github.com/user-attachments/assets/6439bc27-11f9-40d2-ba08-0a95636ca22a" />

<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/bda45bc4-9e5d-4e12-b1af-2a777b71eed7" />

<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/290ff135-27e1-4377-91e4-dfc269e8c95c" />

<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/422f3c43-8192-4848-80cc-a4bf59c30477" />

<img width="634" height="417" alt="image" src="https://github.com/user-attachments/assets/c66f1522-0b14-4f25-b45a-81a9f5d27783" />

<img width="400" height="391" alt="image" src="https://github.com/user-attachments/assets/9bfd8cbd-f90c-4fd5-ae79-1966a32ef5db" />

<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/d1849d1f-9faf-4f47-9c2d-ae6a1293e034" />


Assumptions
Payments are simulated (only status is updated, no real gateway).
QR code verification is done using Base64 + ZXing.
Booking flow is linear: REQUESTED → APPROVED → PAID.

“Open Postman → Click Import → Choose Upload Files → Select Retix-collection.postman_collection.json → Click Import.
