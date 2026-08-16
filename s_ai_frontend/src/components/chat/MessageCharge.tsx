import axios from "axios";
import type { Amount } from "../../types/Message";
import { get } from "../../utils/request";
import Button from "../base/Button";
import { useEffect, useState } from "react";
import IconSend from "../../icons/Icon";


export default function MessageCharge({ amount }: { amount: Amount }) {

    const [close, setClose] = useState(false)

    return (
        <>
            <div>
                <div className=" py-1 text-sm flex gap-4">
                    <div>
                        <span>剩余次数：</span>
                        <span className=" text-amber-800">{amount.max - amount.amount}</span>
                    </div>
                    <Button className=" py-0 text-sm bg-blue-300! text-white hover:bg-blue-600!" onClick={() => setClose(true)}>次数不足，购买次数</Button>
                </div>
            </div>


            {
                close ?
                    <div className=" absolute inset-0 flex flex-col items-center justify-center">
                        <div className=" w-screen h-screen bg-[rgba(0,0,0,0.5)] fixed inset-0 z-10" onClick={()=>setClose(false)}></div>
                        <div className=" w-100 bg-white rounded-md p-4 relative z-11">
                            <ChargeModal />
                        </div>
                        <div className=" relative z-11" onClick={() => setClose(false)}>
                            <IconSend className="text-green-500 text-5xl! cursor-pointer mt-2" icon="Qingkong24"  />
                        </div>
                    </div> : ""
            }

        </>
    )
}
function ChargeModal() {
console.log(1);

    const [charges, setCharges] = useState<any[]>([])

    const getCharge = async () => {
        console.log("==============");
        
        const res = await axios.get("/api/charge/getCharges", {})
        console.log(res.data.data);
        
        setCharges(res.data.data)
    }

    useEffect(() => {
        getCharge()
    }, [])
    return (
        <>
            {
                charges.map(item => {
                    return <>
                        <div className=" p-4 w-full flex items-center justify-between bg-green-100 rounded-md my-2" key={item.id}>
                            <span>购买<b>{item.charge}</b>次</span>
                            <Button className=" text-sm bg-green-300! text-black! hover:bg-blue-600! hover:text-white!">￥{item.price}</Button>
                        </div>
                    </>
                })
            }
        </>
    )
}