// ✅ src/pages/UserManagePage.jsx (사이드바 포함 레이아웃)
import React, { useState, useEffect } from 'react';
import { getAllUserApi, registUserApi } from '../api/userApi';

const layoutStyle = {
  display: 'flex',
  minHeight: '100vh',
  fontFamily: 'Arial, sans-serif',
};

const sidebarStyle = {
  width: '240px',
  backgroundColor: '#1f2937',
  color: 'white',
  padding: '20px',
  flexShrink: 0,
};

const contentStyle = {
  flex: 1,
  padding: '40px',
  backgroundColor: '#f9fafb',
};

const tableStyle = {
  width: '100%',
  borderCollapse: 'collapse',
  marginBottom: '30px',
};

const thTdStyle = {
  border: '1px solid #d1d5db',
  padding: '10px',
  textAlign: 'center',
};

const formStyle = {
  display: 'flex',
  gap: '10px',
  marginBottom: '20px',
};

const inputStyle = {
  padding: '8px',
  border: '1px solid #ccc',
  borderRadius: '4px',
};

const buttonStyle = {
  padding: '8px 12px',
  border: 'none',
  borderRadius: '4px',
  backgroundColor: '#2563eb',
  color: 'white',
  cursor: 'pointer',
};

export default function UserManagePage() {
  const [users, setUsers] = useState([]);

  const [form, setForm] = useState({ name: '', serial: '', admin: false });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
  };

  const handleAdd = async () => {
    const now = new Date().toISOString().slice(0, 10);
    const newUser = {
      id: Date.now(),
      name: form.name,
      serial: form.serial,
      admin: form.admin,
      createdAt: now,
      updatedAt: now,
    };
    // 사용자 리스트에 추가 
    await registUserApi(newUser)
    .then(result => {
      console.log(result);
      if (result.status == "SUCCESS") {
        setUsers([...users, result.data]);    
      }else {
        console.log("regisUserApi->","ther is some error");
      }
    })
    

    // 입력창 초기화
    setForm({ name: '', serial: '', admin: false });
  };

  const handleDelete = (id) => {
    setUsers(users.filter((u) => u.id !== id));
  };


  useEffect(()=>{
    getAllUserApi().then(result=>{
      console.log('get-all-users',result);
      if (result.status == "SUCCESS"){
        setUsers(result.data)
      }
    })

  },[])

  


  return (
    <div style={layoutStyle}>
      <div style={sidebarStyle}>
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '20px' }}>메뉴</h2>
        <ul style={{ listStyle: 'none', padding: 0, lineHeight: '2' }}>
          <li><a href="/dashboard" style={{ color: 'white', textDecoration: 'none' }}>대시보드</a></li>
          <li><a href="/users" style={{ color: 'white', textDecoration: 'none' }}>사용자 관리</a></li>
        </ul>
      </div>
      <div style={contentStyle}>
        <h2 style={{ fontSize: '24px', marginBottom: '20px' }}>사용자 관리</h2>

        <div style={formStyle}>
          <input
            style={inputStyle}
            placeholder="이름"
            name="name"
            value={form.name}
            onChange={handleChange}
          />
          <input
            style={inputStyle}
            placeholder="기기 시리얼 번호"
            name="serial"
            value={form.serial}
            onChange={handleChange}
          />
          <label style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
            <input type="checkbox" name="admin" checked={form.admin} onChange={handleChange} /> 관리자
          </label>
          <button style={buttonStyle} onClick={handleAdd}>등록</button>
        </div>

        <table style={tableStyle}>
          <thead>
            <tr>
              <th style={thTdStyle}>이름</th>
              <th style={thTdStyle}>기기 시리얼</th>
              <th style={thTdStyle}>등록일</th>
              <th style={thTdStyle}>수정일</th>
              <th style={thTdStyle}>관리자</th>
              <th style={thTdStyle}>삭제</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td style={thTdStyle}>{user.name}</td>
                <td style={thTdStyle}>{user.serial}</td>
                <td style={thTdStyle}>{user.createdAt}</td>
                <td style={thTdStyle}>{user.updatedAt}</td>
                <td style={thTdStyle}>{user.admin ? '✔️' : ''}</td>
                <td style={thTdStyle}>
                  <button onClick={() => handleDelete(user.id)} style={{ ...buttonStyle, backgroundColor: '#dc2626' }}>삭제</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}