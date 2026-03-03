import React, { useState } from 'react';
import LoginForm from './components/LoginForm';
import Dashboard from './components/Dashboard';
import RegisterForm from './components/RegisterForm';


type ViewState = 'login' | 'register' | 'dashboard';

function App(){
  const[view, setView] = useState<ViewState>('login');

  const handleLogout = () => {
    localStorage.removeItem('token');
    setView('login');
  }

return(
  <div className="min-h-screen bg-[#020617]">
    
    {/* 1. Dashboard view */}
    {view === 'dashboard' && (
      <Dashboard onLogout={handleLogout}/>
    )}

    {/* 2. Login view */}
    {view === 'login' && (
      <LoginForm
       onLoginSuccess= {() => setView ('dashboard')}
       onNavigateToRegister={() => setView('register')} 
       />
    )}

    {/* 3. Register view */}
    {view === 'register' && (
      <RegisterForm
       onRegisterSuccess= {() => setView ('login')}
       onBackToLogin={() => setView('login')} 
       />
    )}
  </div>
  );
}



// function App() {
//   const [isLoggedIn, setIsLoggedIn] = useState(false);

//   // This function will be passed to the Dashboard to handle the redirect
//   const handleLogout = () => {
//     setIsLoggedIn(false);
//   };

//   return (
//     <div className="min-h-screen bg-[#020617]">
//       {isLoggedIn ? (
//         // We pass the logout function as a prop called 'onLogout'
//         <Dashboard onLogout={handleLogout} />
//       ) : (
//         <LoginForm onLoginSuccess={() => setIsLoggedIn(true)} />
//       )}
//     </div>
//   );
// }

export default App;