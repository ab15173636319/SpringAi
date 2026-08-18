// 禁用右键菜单
window.addEventListener("contextmenu", (e: MouseEvent) => {
    e.preventDefault();
})

// 禁用F12
// window.addEventListener("keydown", (e: KeyboardEvent) => {
//     if (e.key === "F12") {
//         e.preventDefault();
//     }
// })

// 禁用Ctrl+Shift+I
window.addEventListener("keydown", (e: KeyboardEvent) => {
    if (e.ctrlKey && e.shiftKey && e.key === "I") {
        e.preventDefault();
    }
})

// 禁用Ctrl+U
window.addEventListener("keydown", (e: KeyboardEvent) => {
    if (e.ctrlKey && e.key === "U") {
        e.preventDefault();
    }
})

// 禁用Ctrl+Shift+J
window.addEventListener("keydown", (e: KeyboardEvent) => {
    if (e.ctrlKey && e.shiftKey && e.key === "J") {
        e.preventDefault();
    }
})