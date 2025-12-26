# VoucherVault

VoucherVault is a secure web application for managing private coupons, built with Spring Boot and React.

## Project Structure

- `backend`: Java Spring Boot application (Maven)
- `frontend`: React application (Vite)

## Local Development Setup

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL

### 1. Database Setup
Ensure PostgreSQL is running and create a database named `vouchervault`.

### 2. Backend Setup
1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Create a `.env` file based on `.env.example` (if available) or populate environment variables:
   ```properties
   DB_URL=jdbc:postgresql://localhost:5432/vouchervault
   DB_USERNAME=your_db_user
   DB_PASSWORD=your_db_password
   GOOGLE_CLIENT_ID=your_google_client_id
   GOOGLE_CLIENT_SECRET=your_google_client_secret
   JWT_SECRET=your_very_long_jwt_secret
   SERVER_MASTER_SECRET=your_encryption_key
   CORS_ALLOWED_ORIGINS=http://localhost:5173
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### 3. Frontend Setup
1. Navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Create `.env.development` (if not exists) with:
   ```
   VITE_API_URL=http://localhost:8080/api
   ```
4. Run the development server:
   ```bash
   npm run dev
   ```

## Production Deployment

### Hosting
- **Backend**: Dockerized deployment (e.g., Koyeb)
- **Frontend**: Static site hosting (e.g., Vercel)
- **Database**: Managed PostgreSQL (e.g., Neon)

### Configuration
Ensure all environment variables listed in the Backend Setup are configured in your production environment settings. Update `CORS_ALLOWED_ORIGINS` to point to your production frontend URL.
