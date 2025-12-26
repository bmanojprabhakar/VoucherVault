# VoucherVault Deployment Plan

## Hosting Strategy
- **Backend**: Koyeb (Docker)
- **Frontend**: Vercel
- **Database**: Neon (Postgres)

## Step-by-Step Guide

### 1. Database (Neon)
1. Create a new project in Neon.
2. Get the Connection String (host, database, user, password).

### 2. Backend (Koyeb)
1. Deposit the code in GitHub (done).
2. Create a new App in Koyeb and connect GitHub repo.
3. Configure Environment Variables:
   - `DB_URL` (Use Neon connection string, append `?sslmode=require`)
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `GOOGLE_CLIENT_ID`
   - `GOOGLE_CLIENT_SECRET`
   - `JWT_SECRET`
   - `SERVER_MASTER_SECRET`
   - `CORS_ALLOWED_ORIGINS` (Set to your Vercel URL once known, initially allow all or localhost for testing if needed)

### 3. Frontend (Vercel)
1. Import the repository in Vercel.
2. Select the `frontend` directory as the root.
3. Configure Environment Variables:
   - `VITE_API_URL`: Set to the Public URL of your Koyeb Backend service (e.g., `https://app.koyeb.com/.../api`)
4. Deploy.

### 4. Finalizing
1. Update Backend `CORS_ALLOWED_ORIGINS` with the final Vercel URL.
2. Update Google Cloud Console "Authorized JavaScript origins" and "redirect URIs" to match the production URLs.
