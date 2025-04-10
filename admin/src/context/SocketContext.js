import { createContext,useState,useEffect } from "react";
import CustomWebSocket from "@/lib/socket/CustomWebSocket"; 

export const SocketContext = createContext();


export const SocketProvider = ({children}) => {
    const [socketInstance, setSocketInstance] = useState(null);
    const [isConnected, setIsConnected] = useState(false);
    const [lastMessage, setLastMessage] = useState(null);

    useEffect(()=>{
        const socket = new CustomWebSocket("ws://192.168.10.218:8080/ws/admin", {
            onOpen: () => {
              console.log("[WS] connected");
              setIsConnected(true);
            },
            onClose: () => {
              console.log("[WS] disconnected");
              setIsConnected(false);
            },
            onMessage: (msg) => {
                console.log("msg",msg)
              try {
                const parsed = JSON.parse(msg);
                setLastMessage(parsed)
              } catch (err) {
                console.warn("[WS] invalid message", msg);
              }
            },
            onError: (err) => {
              console.error("[WS] connection error", err);
            },
          });
      
          setSocketInstance(socket);
      
          return () => {
            socket.close();
          };

    },[])

    return (
        <SocketContext.Provider value={{socket : socketInstance, isConnected, lastMessage}}>
            {children}
        </SocketContext.Provider>
    );
}