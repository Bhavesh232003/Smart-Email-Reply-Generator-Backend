# Smart Email Reply Generator - Backend Service

An AI-powered email reply generation service built with Spring Boot, featuring JWT authentication, rate limiting, and intelligent data masking for sensitive information.

## 🚀 Live Demo
**Base URL**: `https://smart-email-reply-generator-backend.onrender.com`

## 🌟 Features

### 🔐 **Authentication & Security**
- JWT-based authentication system
- BCrypt password encryption
- Email domain validation (Gmail & Yahoo only)
- CORS configuration for cross-origin requests
- Comprehensive data masking for sensitive information

### 🤖 **AI-Powered Email Generation**
- Integration with Google Gemini 2.0 Flash AI model
- Customizable email tone (formal, urgent, professional, etc.)
- Context-aware email reply generation
- Intelligent handling of business communications

### 🛡️ **Data Protection**
- **Automatic Data Masking** for:
  - Email addresses
  - Phone numbers
  - Credit card numbers
  - Aadhaar numbers
  - PAN numbers
  - Passwords
  - Date of birth
- Data is masked before AI processing and unmasked in responses

### ⚡ **Rate Limiting**
- **Login Protection**: 10 attempts per day per user
- **Email Generation**: 4 requests per minute per user
- Powered by Resilience4j

### 📊 **Monitoring & Health Checks**
- Spring Boot Actuator integration
- Health check endpoints
- Application info endpoints

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with JWT
- **Database**: MySQL/PostgreSQL with JPA/Hibernate
- **AI Integration**: Google Gemini AI API
- **Rate Limiting**: Resilience4j
- **Documentation**: OpenAPI/Swagger ready
- **Deployment**: Render.com

## 📋 Prerequisites

- Java 17 or higher
- PostgreSQL database
- Google Gemini API key
- Maven 3.6+

## ⚙️ Environment Variables

Create a `.env` file or set the following environment variables:

```env
# Database Configuration
DB_URL=jdbc:postgresql://your-database-host:5432/your-database
DB_USER=your-database-username
DB_PASS=your-database-password

# JWT Configuration
JWT_SECRET=your-very-long-and-secure-jwt-secret-key

# Google Gemini AI
GEMINI_API_KEY=your-gemini-api-key

# CORS Configuration
CORS_ALLOWED_ORIGIN=http://localhost:5173
```

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd smart-email-reply-generator-backend
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "testuser2",
  "password": "password123",
  "email": "testuser2@gmail.com"
}
```

**Response**: 
```json
"User registered successfully"
```

**Validation Rules**:
- Username must be unique
- Password minimum 6 characters
- Email must be Gmail or Yahoo domain
- Email must be unique

#### Login User
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser2",
  "password": "password123"
}
```

**Response**: 
```json
{
  "jwtToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjIi...",
  "username": "testuser2"
}
```

**Rate Limit**: 10 requests per day per user

### Email Generation Endpoints

#### Generate Email Reply
```http
POST /api/email/generate
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "content": "Hi team, I will be out of office tomorrow due to a doctor's appointment. Please contact Sarah for any urgent matters.",
  "tone": "formal"
}
```

**Available Tones**:
- `formal` - Professional and respectful
- `urgent` - Direct and immediate
- `professional` - Business-appropriate
- `friendly` - Warm and approachable

**Response Example**:
```text
Please be advised that I will be out of the office tomorrow, [Date], due to a pre-scheduled doctor's appointment.

For any urgent matters requiring immediate attention, please contact Sarah [Sarah's Last Name] at [Sarah's Email Address] or [Sarah's Phone Number].

Thank you for your understanding.
```

**Rate Limit**: 4 requests per minute per user

#### Data Masking Example
**Input with Sensitive Data**:
```json
{
  "content": "Please send documents to testperson@example.com. My Aadhaar number is 123456789012.",
  "tone": "urgent"
}
```

The system automatically:
1. Masks sensitive data before AI processing
2. Processes the request with masked data
3. Unmasks the data in the final response

### Health Check Endpoints

#### Application Health
```http
GET /actuator/health
```

**Response**:
```json
{
  "status": "UP",
  "groups": [
    "liveness",
    "readiness"
  ]
}
```

#### Application Info
```http
GET /actuator/info
```

#### Detailed Actuator (Authenticated)
```http
GET /actuator
Authorization: Bearer <jwt-token>
```

## 🔒 Security Features

### JWT Token Management
- Tokens expire after 1 hour
- Secure token generation with HMAC-SHA algorithm
- Automatic token validation on protected endpoints

### Data Protection
- Sensitive data is never stored in logs
- AI processing uses masked data only
- Original data restored only in final response

### Rate Limiting Responses
- **429 Status Code** when limits exceeded
- **Login Limit**: "Login limit exceeded. Max 10 logins per day."
- **Email Limit**: "Too many requests. Please try again later."

## 🌐 CORS Configuration

The application supports CORS for frontend integration:
- Configurable allowed origins
- Support for all standard HTTP methods
- Credentials support enabled

## 🐳 Deployment

### Render.com Deployment
The application is optimized for deployment on Render.com:

1. Connect your GitHub repository
2. Set environment variables in Render dashboard
3. Deploy automatically on code changes

### Environment-Specific Configuration
- **Development**: H2 in-memory database support
- **Production**: PostgreSQL with connection pooling
- **Staging**: Full feature parity with production

## 🧪 Testing

### Manual Testing with Postman

Import the following collection for comprehensive API testing:

```json
{
  "info": {
    "name": "Smart Email Writer API"
  },
  "auth": {
    "type": "bearer",
    "bearer": [
      {
        "key": "token",
        "value": "{{jwt_token}}"
      }
    ]
  }
}
```

### Test Scenarios

1. **User Registration Flow**
2. **Authentication and Token Generation**
3. **Protected Endpoint Access**
4. **Email Generation with Different Tones**
5. **Rate Limiting Validation**
6. **Data Masking Verification**
7. **Health Check Monitoring**

## 🔧 Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Database
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
spring.app.jwtExpirationMs=3600000

# Gemini AI
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=

# Actuator
management.endpoints.web.exposure.include=health,info
```

## 📊 Monitoring and Logging

- **Health Checks**: `/actuator/health`
- **Application Metrics**: Built-in Spring Boot metrics
- **Structured Logging**: JSON format for production
- **Error Tracking**: Comprehensive exception handling

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the health endpoint for system status
- Review logs for debugging information

## 🚦 API Status Codes

- **200 OK** - Successful request
- **201 Created** - Resource created successfully
- **400 Bad Request** - Invalid request data
- **401 Unauthorized** - Authentication required
- **429 Too Many Requests** - Rate limit exceeded
- **500 Internal Server Error** - Server error

---

**Built with ❤️ using Spring Boot and Google Gemini AI**
