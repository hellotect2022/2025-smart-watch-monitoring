// ✅ src/pages/DashboardPage.jsx
import React, { useState, useEffect, useContext } from 'react';
import { getAllUserApi } from '../api/userApi';
import { SocketContext } from '../context/SocketContext';

const containerStyle = {
  display: 'flex',
  height: '100vh',
  fontFamily: 'Arial, sans-serif',
  overflow: 'hidden',
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
  padding: '20px',
  backgroundColor: '#f3f4f6',
  display: 'flex',
  flexWrap: 'wrap',
  gap: '20px',
  overflow: 'auto',
};

const cardStyle = {
  flex: '1 1 400px',
  minWidth: '350px',
  backgroundColor: 'white',
  borderRadius: '10px',
  padding: '20px',
  boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
};

const tableStyle = {
  width: '100%',
  borderCollapse: 'collapse',
};

const thTdStyle = {
  border: '1px solid #e5e7eb',
  padding: '12px',
  textAlign: 'center',
  cursor: 'pointer',
};

const graphStyle = {
  height: '200px',
  backgroundColor: '#e0e7ff',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  fontSize: '18px',
  color: '#374151',
  borderRadius: '8px',
};

export default function DashboardPage() {
  const { lastMessage } = useContext(SocketContext);

  const [selectedUser, setSelectedUser] = useState(null);
  const [users, setUsers] = useState([]);

  const renderLocation = (loc) => {
    return loc.startsWith('beacon-') ? `비콘 ID: ${loc}` : `좌표: ${loc}`;
  };

  // ✅ 초기 사용자 목록 로딩
  useEffect(() => {
    const fetchUsers = async () => {
      await getAllUserApi()
        .then(result => {
          console.log("getAllUserApi", result);
          if (result.status == "SUCCESS") {
            const initUsers = result.data.map(user => ({
              name: user.name,
              bpm: 0,
              step: '',
              location: '',
              beacon:'',
              ...user,
            }));
            setUsers(initUsers);
          }
        }); // 서버 API 호출
    };

    fetchUsers();
  }, []);

  // ✅ 실시간 데이터 수신 시 사용자 정보 업데이트
  useEffect(() => {
    if (!lastMessage || !lastMessage.sender) return;

    setUsers(prevUsers => {
      return prevUsers.map(user => {
        if (user.name !== lastMessage.sender) return user;

        const updatedFields = {};
        switch (lastMessage.sensor) {
          case 'gps':
            updatedFields.location = `${lastMessage.value.lng},${lastMessage.value.lat}`;
            break;
          case 'bpm':
            updatedFields.bpm = lastMessage.value;
            break;
          case 'step':
            updatedFields.step = lastMessage.value;
            break;
          case 'beacon':
            updatedFields.beacon = `${lastMessage.value.major}
            ,${lastMessage.value.minor}
            ,${lastMessage.value.rssi}`;
            break;
          default:
            break;
        }

        return { ...user, ...updatedFields };
      });
    });

    if (selectedUser?.name === lastMessage.sender) {
      setSelectedUser(prev => {
        const updated = { ...prev };

        switch (lastMessage.sensor) {
          case 'gps':
            updated.location = `${lastMessage.value.lng},${lastMessage.value.lat}`;
            break;
          case 'bpm':
            updated.bpm = lastMessage.value;
            break;
          case 'step':
            updated.step = lastMessage.value;
            break;
          case 'beacon':
            updated.beacon = `${lastMessage.value.major}
            ,${lastMessage.value.minor}
            ,${lastMessage.value.rssi}`;
            break;
          default:
            break;
        }

        return updated;
      });
    }
  }, [lastMessage]);

  return (
    <div style={containerStyle}>
      <div style={sidebarStyle}>
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '20px' }}>메뉴</h2>
        <ul style={{ listStyle: 'none', padding: 0, lineHeight: '2' }}>
          <li><a href="/dashboard" style={{ color: 'white', textDecoration: 'none' }}>대시보드</a></li>
          <li><a href="/users" style={{ color: 'white', textDecoration: 'none' }}>사용자 관리</a></li>
        </ul>
      </div>
      <div style={contentStyle}>
        <div style={cardStyle}>
          <h3 style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '10px' }}>사용자 목록</h3>
          <table style={tableStyle}>
            <thead>
              <tr>
                <th style={thTdStyle}>이름</th>
                <th style={thTdStyle}>심박수</th>
                <th style={thTdStyle}>위치</th>
                <th style={thTdStyle}>걸음수</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user, i) => (
                <tr key={i} onClick={() => setSelectedUser(user)}>
                  <td style={thTdStyle}>{user.name}</td>
                  <td style={{
                    ...thTdStyle,
                    fontWeight: 'bold',
                    color: user.bpm > 85 ? 'red' : '#111827',
                  }}>{user.bpm} bpm</td>
                  <td style={thTdStyle}>{renderLocation(user.location)}/
                    ({user.beacon})
                  </td>
                  <td style={thTdStyle}>{user.step}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {selectedUser && (
          <div style={cardStyle}>
            <h3 style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '10px' }}>심전도 그래프</h3>
            <div style={graphStyle}>[{selectedUser.name}의 심전도 그래프]</div>
            <h3 style={{ fontSize: '18px', fontWeight: 'bold', marginTop: '20px' }}>심박수</h3>
            <div style={graphStyle}>{selectedUser.bpm} bpm</div>
            <h3 style={{ fontSize: '18px', fontWeight: 'bold', marginTop: '20px' }}>위치</h3>
            <div style={{ ...graphStyle, backgroundColor: '#d1fae5' }}>{renderLocation(selectedUser.location)}</div>
          </div>
        )}
      </div>
    </div>
  );
}


// // ✅ src/pages/DashboardPage.jsx (심박수 표시 추가 + overflow 대응)
// import React, { useState, useEffect, useContext } from 'react';
// import { getAllUserApi } from '../api/userApi';
// import { SocketContext } from '../context/SocketContext';

// const containerStyle = {
//   display: 'flex',
//   height: '100vh',
//   fontFamily: 'Arial, sans-serif',
//   overflow: 'hidden',
// };

// const sidebarStyle = {
//   width: '240px',
//   backgroundColor: '#1f2937',
//   color: 'white',
//   padding: '20px',
//   flexShrink: 0,
// };

// const contentStyle = {
//   flex: 1,
//   padding: '20px',
//   backgroundColor: '#f3f4f6',
//   display: 'flex',
//   flexWrap: 'wrap',
//   gap: '20px',
//   overflow: 'auto',
// };

// const cardStyle = {
//   flex: '1 1 400px',
//   minWidth: '350px',
//   backgroundColor: 'white',
//   borderRadius: '10px',
//   padding: '20px',
//   boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
// };

// const tableStyle = {
//   width: '100%',
//   borderCollapse: 'collapse',
// };

// const thTdStyle = {
//   border: '1px solid #e5e7eb',
//   padding: '12px',
//   textAlign: 'center',
//   cursor: 'pointer',
// };

// const graphStyle = {
//   height: '200px',
//   backgroundColor: '#e0e7ff',
//   display: 'flex',
//   alignItems: 'center',
//   justifyContent: 'center',
//   fontSize: '18px',
//   color: '#374151',
//   borderRadius: '8px',
// };

// export default function DashboardPage() {

//   const {lastMessage} = useContext(SocketContext);
  
//   const [selectedUser, setSelectedUser] = useState(null);

//   const [users, setUsers] = useState([]);

//   const renderLocation = (loc) => {
//     return loc.startsWith('beacon-') ? `비콘 ID: ${loc}` : `좌표: ${loc}`;
//   };

//   useEffect(() => {
//     console.log("lastMessage updated",lastMessage)

//   },[lastMessage])

//   return (
//     <div style={containerStyle}>
//       <div style={sidebarStyle}>
//         <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '20px' }}>메뉴</h2>
//         <ul style={{ listStyle: 'none', padding: 0, lineHeight: '2' }}>
//           <li><a href="/dashboard" style={{ color: 'white', textDecoration: 'none' }}>대시보드</a></li>
//           <li><a href="/users" style={{ color: 'white', textDecoration: 'none' }}>사용자 관리</a></li>
//         </ul>
//       </div>
//       <div style={contentStyle}>
//         <div style={cardStyle}>
//           <h3 style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '10px' }}>사용자 목록</h3>
//           <table style={tableStyle}>
//             <thead>
//               <tr>
//                 <th style={thTdStyle}>이름</th>
//                 <th style={thTdStyle}>심박수</th>
//                 <th style={thTdStyle}>위치</th>
//                 <th style={thTdStyle}>심전도</th>
//               </tr>
//             </thead>
//             <tbody>
//               {users.map((user, i) => (
//                 <tr key={i} onClick={() => setSelectedUser(user)}>
//                   <td style={thTdStyle}>{user.name}</td>
//                   <td style={{
//                     ...thTdStyle,
//                     fontWeight: 'bold',
//                     color: user.bpm > 85 ? 'red' : '#111827',
//                   }}>{user.bpm} bpm</td>
//                   <td style={thTdStyle}>{renderLocation(user.location)}</td>
//                   <td style={thTdStyle}>{user.ecg}</td>
//                 </tr>
//               ))}
//             </tbody>
//           </table>
//         </div>

//         {selectedUser && (
//           <div style={cardStyle}>
//             <h3 style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '10px' }}>심전도 그래프</h3>
//             <div style={graphStyle}>[{selectedUser.name}의 심전도 그래프]</div>
//             <h3 style={{ fontSize: '18px', fontWeight: 'bold', marginTop: '20px' }}>심박수</h3>
//             <div style={graphStyle}>{selectedUser.bpm} bpm</div>
//             <h3 style={{ fontSize: '18px', fontWeight: 'bold', marginTop: '20px' }}>위치</h3>
//             <div style={{ ...graphStyle, backgroundColor: '#d1fae5' }}>{renderLocation(selectedUser.location)}</div>
//           </div>
//         )}
//       </div>
//     </div>
//   );
// }