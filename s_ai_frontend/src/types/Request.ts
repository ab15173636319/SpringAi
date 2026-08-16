


interface IResponse<T> {
    code: number;
    message: string;
    data: T;
}


export type Response<T = any> = IResponse<T>