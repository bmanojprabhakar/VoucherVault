import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { GoogleOAuthProvider } from '@react-oauth/google';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';

const ProtectedRoute = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) return <div className="min-h-screen bg-gray-950 flex items-center justify-center text-white">Loading...</div>;
  if (!user) return <Navigate to="/login" />;

  return children;
};

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={
        <ProtectedRoute>
          <Layout />
        </ProtectedRoute>
      }>
        <Route index element={<Dashboard />} />
      </Route>
    </Routes>
  );
};

const ConfigError = () => (
  <div className="min-h-screen bg-gray-950 flex items-center justify-center p-4">
    <div className="max-w-md w-full bg-gray-900 border border-red-500/30 rounded-2xl p-8 text-center shadow-2xl">
      <div className="w-16 h-16 bg-red-500/10 rounded-full flex items-center justify-center mx-auto mb-6">
        <svg className="w-8 h-8 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
      </div>
      <h1 className="text-2xl font-bold text-white mb-2">Configuration Missing</h1>
      <p className="text-gray-400 mb-6">
        The Google Client ID is not configured. Please check your environment variables.
      </p>
      <div className="bg-black/50 rounded-lg p-4 text-left font-mono text-sm text-red-400 border border-red-500/10">
        VITE_GOOGLE_CLIENT_ID is undefined
      </div>
    </div>
  </div>
);

function App() {
  const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

  if (!GOOGLE_CLIENT_ID || GOOGLE_CLIENT_ID === "your_google_client_id" || GOOGLE_CLIENT_ID === "") {
    return <ConfigError />;
  }

  return (
    <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
      <BrowserRouter>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </BrowserRouter>
    </GoogleOAuthProvider>
  );
}

export default App;
