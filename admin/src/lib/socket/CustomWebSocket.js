class CustomWebSocket {
    constructor(url, {
      maxAttempts = 5,
      reconnectDelay = 1000,
      timeout = 20000,
      onOpen = () => {},
      onClose = () => {},
      onMessage = () => {},
      onError = () => {},
    }) {
      this.url = url;
      this.maxAttempts = maxAttempts;
      this.reconnectDelay = reconnectDelay;
      this.timeout = timeout;
  
      this.onOpen = onOpen;
      this.onClose = onClose;
      this.onMessage = onMessage;
      this.onError = onError;
  
      this.socket = null;
      this.attempts = 0;
      this.connected = false;
  
      this.connect();
    }
  
    connect() {
      this.socket = new WebSocket(this.url);
      const timeoutId = setTimeout(() => {
        if (!this.connected) {
          this.socket.close();
        }
      }, this.timeout);
  
      this.socket.onopen = () => {
        clearTimeout(timeoutId);
        this.connected = true;
        this.attempts = 0;
        this.onOpen();
      };
  
      this.socket.onmessage = (event) => {
        this.onMessage(event.data);
      };
  
      this.socket.onerror = (error) => {
        this.onError(error);
      };
  
      this.socket.onclose = () => {
        clearTimeout(timeoutId);
        this.connected = false;
        this.onClose();
  
        if (this.attempts < this.maxAttempts) {
          this.attempts++;
          console.log(`[WebSocket] Reconnecting in ${this.reconnectDelay}ms... (attempt ${this.attempts})`);
          setTimeout(() => this.connect(), this.reconnectDelay);
        } else {
          console.warn("[WebSocket] Max reconnection attempts reached");
        }
      };
    }
  
    send(data) {
      if (this.connected) {
        this.socket.send(data);
      } else {
        console.warn("[WebSocket] Cannot send, socket is not connected");
      }
    }
  
    close() {
      this.socket.close();
    }
  }

  export default CustomWebSocket;
  