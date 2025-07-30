import { contextBridge, ipcRenderer } from "electron";

// 向渲染进程暴露安全的 API
contextBridge.exposeInMainWorld("electronAPI", {
  // 应用信息
  getVersion: () => ipcRenderer.invoke("app-version"),

  // 消息对话框
  showMessageBox: (options) => ipcRenderer.invoke("show-message-box", options),

  // 应用控制
  minimize: () => ipcRenderer.invoke("window-minimize"),
  maximize: () => ipcRenderer.invoke("window-maximize"),
  close: () => ipcRenderer.invoke("window-close"),

  // 通知系统
  showNotification: (title, body, options = {}) => {
    if ("Notification" in window && Notification.permission === "granted") {
      return new Notification(title, { body, ...options });
    }
  },

  // 请求通知权限
  requestNotificationPermission: async () => {
    if ("Notification" in window) {
      const permission = await Notification.requestPermission();
      return permission;
    }
    return "denied";
  },

  // 键盘快捷键支持
  onKeyboardShortcut: (callback) => {
    document.addEventListener("keydown", (event) => {
      const shortcuts = {
        "F5": "refresh",
        "F11": "fullscreen",
        "F12": "devtools",
        "Ctrl+R": "refresh",
        "Ctrl+Shift+R": "force-refresh",
        "Ctrl+Shift+I": "devtools"
      };

      const key = event.key;
      const ctrl = event.ctrlKey || event.metaKey;
      const shift = event.shiftKey;

      let shortcutKey = "";
      if (ctrl && shift) shortcutKey += "Ctrl+Shift+";
      else if (ctrl) shortcutKey += "Ctrl+";
      shortcutKey += key;

      if (shortcuts[shortcutKey]) {
        event.preventDefault();
        callback(shortcuts[shortcutKey]);
      }
    });
  }
});

// 增强页面体验的脚本
window.addEventListener("DOMContentLoaded", () => {
  // 添加加载指示器样式
  const loadingStyle = document.createElement("style");
  loadingStyle.textContent = `
    .electron-loading {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 4px;
      background: linear-gradient(90deg, #4CAF50, #45a049);
      z-index: 9999;
      animation: loading 2s infinite;
    }

    @keyframes loading {
      0% { transform: translateX(-100%); }
      50% { transform: translateX(0%); }
      100% { transform: translateX(100%); }
    }

    .electron-offline {
      position: fixed;
      top: 10px;
      right: 10px;
      background: #f44336;
      color: white;
      padding: 8px 16px;
      border-radius: 4px;
      font-size: 14px;
      z-index: 10000;
      animation: slideIn 0.3s ease-out;
    }

    @keyframes slideIn {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
  `;
  document.head.appendChild(loadingStyle);

  // 网络状态监听
  let offlineNotification = null;

  window.addEventListener("online", () => {
    if (offlineNotification) {
      offlineNotification.remove();
      offlineNotification = null;
    }
    console.log("网络已连接");
  });

  window.addEventListener("offline", () => {
    offlineNotification = document.createElement("div");
    offlineNotification.className = "electron-offline";
    offlineNotification.textContent = "网络连接已断开";
    document.body.appendChild(offlineNotification);
    console.log("网络连接已断开");
  });

  // 页面加载状态指示
  if (document.readyState === "loading") {
    const loadingBar = document.createElement("div");
    loadingBar.className = "electron-loading";
    document.body.appendChild(loadingBar);

    window.addEventListener("load", () => {
      setTimeout(() => {
        if (loadingBar && loadingBar.parentNode) {
          loadingBar.remove();
        }
      }, 500);
    });
  }

  // 右键菜单增强
  document.addEventListener("contextmenu", (event) => {
    // 可以在这里添加自定义右键菜单逻辑
    // event.preventDefault(); // 如果想禁用右键菜单
  });

  // 拖拽文件处理（如果需要）
  document.addEventListener("dragover", (event) => {
    event.preventDefault();
  });

  document.addEventListener("drop", (event) => {
    event.preventDefault();
    // 处理拖拽文件的逻辑
    console.log("文件拖拽:", event.dataTransfer.files);
  });

  // 阻止某些默认行为
  document.addEventListener("keydown", (event) => {
    // 阻止 Ctrl+Shift+J 打开控制台（生产环境）
    if (event.ctrlKey && event.shiftKey && event.code === "KeyJ") {
      event.preventDefault();
    }

    // 阻止 F12 打开开发者工具（生产环境）
    if (event.code === "F12" && !process.env.NODE_ENV === "development") {
      event.preventDefault();
    }
  });
});

// 控制台美化
console.log(
  "%c🚀 AbacusFlow Admin Desktop App",
  "color: #4CAF50; font-size: 16px; font-weight: bold;"
);

console.log(
  "%c应用已启动，享受更好的桌面体验！",
  "color: #666; font-size: 12px;"
);


// contextBridge.exposeInMainWorld("api", {

//   fetch: (endpoint) => wo.invoke("fetch-api", endpoint)
// });

// window.addEventListener("DOMContentLoaded", () => {
//   const replaceText = (selector, text) => {
//     const element = document.getElementById(selector);
//     if (element) element.innerText = text;
//   };

//   for (const type of ["chrome", "node", "electron"]) {
//     replaceText(`${type}-version`, process.versions[type]);
//   }
// });
