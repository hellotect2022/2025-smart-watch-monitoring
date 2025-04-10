import {reqeustPost} from './request'

export const loginApi = (id, password) => reqeustPost('/api/login',JSON.stringify({id:id, password:password}))

export const registUserApi = (data) => reqeustPost('/api/registUser',JSON.stringify(data))

export const getAllUserApi = () => reqeustPost('/api/getAllUsers')