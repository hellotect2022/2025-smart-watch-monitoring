import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import UserManagePage from './pages/UserManagePage';
import { SocketProvider } from './context/SocketContext';

function App() {
  return (
    <SocketProvider>
      <Router>
        <Routes>
          <Route path="/" element={<LoginPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/users" element={<UserManagePage />} />
        </Routes>
      </Router>
    </SocketProvider>
  );
}

export default App;
