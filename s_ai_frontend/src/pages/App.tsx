

import { useNavigate } from "react-router";
import Button from "../components/base/Button";

function App() {
    const navigate = useNavigate();

    const toChat = () => {
        navigate("/ai");
    }


    return (
        <>
            <div className=" w-screen h-screen flex items-center justify-center font-serif">
                <Button className=" bg-yellow-100 hover:bg-yellow-200" onClick={toChat} >
                    去聊天
                </Button>
            </div>

        </>
    )
}

export default App
