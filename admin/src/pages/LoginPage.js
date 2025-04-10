import React, { useState } from 'react';

export default function LoginPage() {
    const [id, setId] = useState('');
    const [password, setPassword] = useState('');
  
    const handleLogin = () => {
      console.log('Login with', id, password);
      window.location.href = '/dashboard';
    };
  
    const containerStyle = {
      display: 'flex',
      height: '100vh',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(to right, #bae6fd, #c7d2fe)',
    };
  
    const boxStyle = {
      backgroundColor: 'white',
      padding: '40px',
      borderRadius: '20px',
      boxShadow: '0 10px 20px rgba(0, 0, 0, 0.1)',
      width: '380px',
    };
  
    const inputStyle = {
      width: '100%',
      padding: '12px',
      marginBottom: '16px',
      borderRadius: '6px',
      border: '1px solid #ccc',
      fontSize: '16px',
    };
  
    const buttonStyle = {
      width: '100%',
      padding: '12px',
      backgroundColor: '#2563eb',
      color: 'white',
      border: 'none',
      borderRadius: '6px',
      fontWeight: 'bold',
      fontSize: '16px',
      cursor: 'pointer',
    };
  
    const headingStyle = {
      textAlign: 'center',
      fontSize: '24px',
      fontWeight: 'bold',
      marginBottom: '24px',
      color: '#374151',
    };
  
    return (
      <div style={containerStyle}>
        <div style={boxStyle}>
          <h2 style={headingStyle}>Smart Health 로그인</h2>
          <input
            style={inputStyle}
            placeholder="아이디"
            value={id}
            onChange={e => setId(e.target.value)}
          />
          <input
            type="password"
            style={inputStyle}
            placeholder="비밀번호"
            value={password}
            onChange={e => setPassword(e.target.value)}
          />
          <button
            style={buttonStyle}
            onClick={handleLogin}
          >
            로그인
          </button>
        </div>
      </div>
    );
  }
