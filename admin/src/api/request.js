import axios from  'axios'

const instance = axios.create({
    baseURL: 'http://192.168.10.218:8080',
})

instance.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

const request = async ({ method, url, data = null, params = null, headers = {} }) => {
try {
    const response = await instance({
    method,
    url,
    data,      // POST, PUT 등 body
    params,    // GET 쿼리
    headers: {
        'Content-Type': 'application/json',  // ✅ 기본 Content-Type
        ...headers                           // ✅ 사용자 설정으로 덮어쓸 수 있게
      },
    });
    return response.data;
} catch (error) {
    console.error(`[API ERROR] ${method.toUpperCase()} ${url}`, error);
    throw error;
}
};

export const reqeustPost = (url,data=null,headers={}) => request({
    method:"post",
    url:url,
    data:data,
    headers:headers,

})
