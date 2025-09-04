# Smart Email Reply Generator API 📧✨

This project is a secure, AI-powered backend service built with **Spring Boot** that generates intelligent, context-aware email replies. It leverages the **Google Gemini API** for content generation and includes robust security features like JWT authentication, rate limiting, and automatic PII (Personally Identifiable Information) data masking.

The live API is deployed on Render: `https://smart-email-reply-generator-backend.onrender.com`

---

## 🚀 Core Features

* **🤖 AI-Powered Replies**: Integrates with the Google Gemini API to generate high-quality email replies based on user-provided content and desired tone.
* **🔐 Secure JWT Authentication**: Implements user registration and login with JSON Web Tokens (JWT) to secure all sensitive endpoints.
* **🛡️ Rate Limiting**: Protects the API from abuse with per-user rate limiting on both login attempts (10/day) and email generation requests (4/minute), powered by Resilience4j.
* **🎭 PII Data Masking**: Automatically detects and masks sensitive data (emails, phone numbers, credit cards, etc.) before sending it to the AI model and unmasks the response, ensuring user privacy and security.
* **🩺 Health Monitoring**: Includes Spring Boot Actuator endpoints (`/actuator/health`, `/actuator/info`) for easy monitoring and health checks.
* **🛠️ Built with Spring Boot**: A robust and scalable backend built on the industry-standard Spring Boot framework with Spring Data JPA and PostgreSQL.

---

## 🛠️ Tech Stack

* **Backend**: Java, Spring Boot, Spring Security, Spring Data JPA
* **Database**: PostgreSQL
* **Authentication**: JSON Web Tokens (JWT)
* **AI**: Google Gemini API
* **Rate Limiting**: Resilience4j
* **API Testing**: Postman

---

## ⚙️ Setup and Installation

To get a local copy up and running, follow these steps.

### **Prerequisites**

* Java Development Kit (JDK) 17 or higher
* Maven or Gradle
* A PostgreSQL database instance

### **Installation**

1.  **Clone the repository**
    ```sh
    git clone [https://github.com/your-username/your-repo-name.git](https://github.com/your-username/your-repo-name.git)
    ```
2.  **Navigate to the project directory**
    ```sh
    cd your-repo-name
    ```
3.  **Configure Environment Variables**

    Create an `application.properties` file (or use environment variables) with the following content. Replace the `${...}` placeholders with your actual credentials.

    ```properties
    # Server Configuration
    server.port=8080

    # Database (PostgreSQL)
    spring.datasource.url=${DB_URL}
    spring.datasource.username=${DB_USER}
    spring.datasource.password=${DB_PASS}
    spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
    spring.jpa.hibernate.ddl-auto=update

    # Security - JWT
    spring.app.jwtSecret=${JWT_SECRET} # A long, random string for signing tokens
    spring.app.jwtExpirationMs=3600000 # 1 hour

    # External APIs
    gemini.api.key=${GEMINI_API_KEY}
    gemini.api.url=[https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=](https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=)
    ```

4.  **Run the application**
    ```sh
    mvn spring-boot:run
    ```

---

## 📖 API Usage & Screenshots

Here is a walkthrough of the API endpoints using Postman.

### **1. User Registration (`/api/auth/signup`)**

Create a new user by providing a username, password, and email.

![User Registration](https://i.imgur.com/l54I9kM.png)

The user data is stored in the database with the password securely hashed.

![Database after registration](https://i.imgur.com/gYq8O2M.png)

### **2. User Login (`/api/auth/login`)**

Log in with the registered credentials to receive a JWT access token.

![User Login](https://i.imgur.com/vQx92D4.png)

### **3. Generating an Email Reply (`/api/email/generate`)**

Use the JWT token from the login step as a **Bearer Token** in the `Authorization` header for all protected endpoints.

![Setting Bearer Token](https://i.imgur.com/0F45c6E.png)

Send the email content and desired tone to the `/api/email/generate` endpoint to receive an AI-generated reply.

**Example 1: Professional Tone**
![Generate Professional Email](https://i.imgur.com/eB93N9E.png)

**Example 2: Formal Tone**
![Generate Formal Email](https://i.imgur.com/C53z4g3.png)

### **4. Data Masking in Action**

The API automatically masks sensitive information before processing. For example, an email address and Aadhaar number are hidden from the AI model.

![Data Masking Example](https://i.imgur.com/eXo58gP.png)

### **5. Security & Rate Limiting**

-   **Unauthorized Access**: Endpoints protected by JWT will return a `401 Unauthorized` error if no valid token is provided.

    ![Unauthorized Access](https://i.imgur.com/Gz5m4wH.png)

-   **Login Rate Limiting**: After 10 login attempts in a day, the API will block further requests from that user, returning a `429 Too Many Requests` error.

    ![Login Rate Limit Exceeded](https://i.imgur.com/f7jYy3T.png)

-   **Email Generation Rate Limiting**: To prevent spam, the API limits users to 4 generation requests per minute.

    ![Email Generation Rate Limit Exceeded](https://i.imgur.com/2KkH2f7.png)

### **6. Actuator Endpoints (Monitoring)**

Publicly available actuator endpoints provide application health and information.

-   `/actuator/health`
    ![Health Endpoint](https://i.imgur.com/1m048mG.png)

-   `/actuator` (when authenticated)
    ![Actuator Endpoint](https://i.imgur.com/l21Q668.png)

---

## 🤝 Contributing

Contributions make the open-source community an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

---

## 📜 License

Distributed under the MIT License. See `LICENSE` file for more information.

---

## 📬 Contact

Bhavesh - [bhavesh.work.23@gmail.com](mailto:bhavesh.work.23@gmail.com)

Project Link: [https://github.com/Bhavesh232003/Smart-Email-Reply-Generator-Backend](https://github.com/Bhavesh232003/Smart-Email-Reply-Generator-Backend)
