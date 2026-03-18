import React, { useEffect, useState } from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import LoginForm from './components/LoginForm';
import Dashboard from './components/Dashboard';
import RegisterForm from './components/RegisterForm';

function App() {
  const navigate = useNavigate();
  // We still want to know if they are logged in!
  const isAuthenticated = !!localStorage.getItem('token'); 

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login'); // Instantly kick them to the login URL
  };

  return (
    <div className="min-h-screen bg-[#020617]">
      <Routes>
        
        {/* 1. Login Route */}
        <Route 
          path="/login" 
          element={isAuthenticated ? <Navigate to="/dashboard" /> : <LoginForm onLoginSuccess={() => navigate('/dashboard')} onNavigateToRegister={() => navigate('/register')} />} 
        />

        {/* 2. Register Route */}
        <Route 
          path="/register" 
          element={isAuthenticated ? <Navigate to="/dashboard" /> : <RegisterForm onRegisterSuccess={() => navigate('/login')} onBackToLogin={() => navigate('/login')} />} 
        />

        {/* 3. Dashboard Route (Default Tab) */}
        <Route 
          path="/dashboard" 
          element={isAuthenticated ? <Dashboard activeTab="home" onLogout={handleLogout} /> : <Navigate to="/login" />} 
        />

        {/* 4. Dashboard Route (IP Lookup Tab) */}
        <Route 
          path="/ip-lookup/:ip" 
          element={isAuthenticated ? <Dashboard activeTab="ip-lookup" onLogout={handleLogout} /> : <Navigate to="/login" />} 
        />

        <Route 
          path="/ip-lookup/" 
          element={isAuthenticated ? <Dashboard activeTab="ip-lookup" onLogout={handleLogout} /> : <Navigate to="/login" />} 
        />

        {/* 5. Dashboard Route (CISA KEV Tab) */}
        <Route 
          path="/cisa-kev" 
          element={isAuthenticated ? <Dashboard activeTab="cisa-kev" onLogout={handleLogout} /> : <Navigate to="/login" />} 
        />

        {/* 6. Base URL "/" automatically sends them to login or dashboard */}
        <Route 
          path="/" 
          element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />} 
        />

        {/* 7. Catch-All for fake URLs like /123 (The 404 Page) */}
        <Route 
          path="*" 
          element={
            <div className="text-white text-center mt-20">
              <h1 className="text-4xl font-bold">404 - Page Not Found</h1>
              <button onClick={() => navigate('/')} className="mt-4 text-blue-500 underline">Go Home</button>
            </div>
          } 
        />

      </Routes>
    </div>
  );
}

export default App;