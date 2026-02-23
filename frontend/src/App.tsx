import React, { useState } from 'react';
import LoginForm from './components/LoginForm';
import Dashboard from './components/Dashboard';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // This function will be passed to the Dashboard to handle the redirect
  const handleLogout = () => {
    setIsLoggedIn(false);
  };

  return (
    <div className="min-h-screen bg-[#020617]">
      {isLoggedIn ? (
        // We pass the logout function as a prop called 'onLogout'
        <Dashboard onLogout={handleLogout} />
      ) : (
        /* Note: In your actual LoginForm, you should call a function 
          that sets setIsLoggedIn(true) upon a successful backend response.
        */
        <div onClick={() => setIsLoggedIn(true)}>
          <LoginForm />
        </div>
      )}
    </div>
  );
}

export default App;