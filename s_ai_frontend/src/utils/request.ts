import axios from "axios";
import { getDeviceId } from "./ID";



const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
        'deviceId': getDeviceId()
    }
})

request.interceptors.request.use(config => {
    return config
}, error => Promise.reject(error))


request.interceptors.response.use(response => {
    if (response.status === 200) {
        return response.data
    } else {
        return Promise.reject(new Error(response.data.message || 'Error'))
    }
})

const get = <T = any>(url: string, params: Record<string, any>): Promise<T> => {
    return request.get<any, any>(url, { params }) as Promise<T>
}

const post = <T = any>(url: string, data: Record<string, any>): Promise<T> => {
    return request.post<any, any>(url, data) as Promise<T>
}

export { get, post }