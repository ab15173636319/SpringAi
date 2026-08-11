import type { MessageSend } from "../types/Message";

async function* sendMessage(messageSend: MessageSend) {

    const aiRequest = await fetch(`http://localhost:9999/ai/chat/${messageSend.id}?message=${messageSend.message}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    })

    if (!aiRequest.ok) {
        throw new Error(`请求失败: ${aiRequest.status} ${aiRequest.statusText}`)
    }

    const reader = aiRequest.body.getReader()

    if (!reader) return;

    const decode = new TextDecoder("utf-8")
    while (true) {
        const { done, value } = await reader.read()
        if (done) break;
        const text = decode.decode(value, { stream: true })
        yield text
    }

}

export { sendMessage }