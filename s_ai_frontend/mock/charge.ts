import { MockMethod } from 'vite-plugin-mock'
import Mock from 'mockjs'
import charge from "./database/charge"

export default [
    {
        url: '/api/charge/getCharges',
        method: 'get',
        response: () => {
            return {
                code: 200,
                message: "查询成功",
                data: Mock.mock([
                    ...charge
                ])
            }
        }
    }
] 