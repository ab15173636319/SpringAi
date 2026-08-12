import { v4 } from "uuid"


export const generateId = () => {
    const id = v4().replaceAll("-", "")
    if (!localStorage)
        throw new Error("不支持localStorage")
    localStorage.setItem("deviceId", id)
    return id
}

export const getDeviceId = () => {
    let deviceId = localStorage.getItem("deviceId")
    if (deviceId) return deviceId
    return generateId()
}