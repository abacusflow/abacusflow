import { app, BrowserWindow, session, Menu, shell, dialog, ipcMain } from "electron";
import path from "path";
import { dirname } from "path";
import fs from "fs";
import { fileURLToPath } from "url";
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// 应用配置
const APP_CONFIG = {
  window: {
    width: 1200,
    height: 800,
    minWidth: 800,
    minHeight: 600
  },
  url: "https://admin.abacusflow.cn"
};

class WindowManager {
  constructor() {
    this.mainWindow = null;
    this.windowState = this.loadWindowState();
  }

  // 加载窗口状态
  loadWindowState() {
    try {
      const userDataPath = app.getPath("userData");
      const stateFile = path.join(userDataPath, "window-state.json");

      if (fs.existsSync(stateFile)) {
        return JSON.parse(fs.readFileSync(stateFile, "utf8"));
      }
    } catch (error) {
      console.log("Failed to load window state:", error);
    }

    return {
      width: APP_CONFIG.window.width,
      height: APP_CONFIG.window.height,
      x: undefined,
      y: undefined,
      isMaximized: false
    };
  }

  // 保存窗口状态
  saveWindowState() {
    if (!this.mainWindow) return;

    try {
      const userDataPath = app.getPath("userData");
      const stateFile = path.join(userDataPath, "window-state.json");

      const bounds = this.mainWindow.getBounds();
      const state = {
        ...bounds,
        isMaximized: this.mainWindow.isMaximized()
      };

      fs.writeFileSync(stateFile, JSON.stringify(state, null, 2));
    } catch (error) {
      console.log("Failed to save window state:", error);
    }
  }

  createMainWindow() {
    // 创建主窗口
    this.mainWindow = new BrowserWindow({
      width: this.windowState.width,
      height: this.windowState.height,
      x: this.windowState.x,
      y: this.windowState.y,
      minWidth: APP_CONFIG.window.minWidth,
      minHeight: APP_CONFIG.window.minHeight,

      // 窗口样式优化
      titleBarStyle: process.platform === "darwin" ? "hiddenInset" : "default",
      frame: true,
      transparent: false,
      hasShadow: true,

      // 图标设置
      icon: path.join(__dirname, "dist", "favicon.ico"),

      // 显示优化
      show: false, // 先隐藏，等加载完成后显示
      backgroundColor: "#ffffff",

      webPreferences: {
        nodeIntegration: false,
        contextIsolation: true,
        enableRemoteModule: false,
        webSecurity: true,
        allowRunningInsecureContent: false,

        // 预加载脚本
        preload: path.join(__dirname, "preload.js"),

        // 性能优化
        backgroundThrottling: false,
        offscreen: false
      }
    });

    // 恢复窗口最大化状态
    if (this.windowState.isMaximized) {
      this.mainWindow.maximize();
    }

    // 设置窗口事件监听
    this.setupWindowEvents();

    // 设置内容安全策略
    this.setupSecurityPolicies();

    // 加载URL
    this.loadContent();

    return this.mainWindow;
  }

  setupWindowEvents() {
    // 窗口准备显示时才显示，避免白屏
    this.mainWindow.once("ready-to-show", () => {
      this.mainWindow.show();

      // 开发环境下打开开发者工具
      // this.mainWindow.webContents.openDevTools();
    });

    // 窗口关闭前保存状态
    this.mainWindow.on("close", () => {
      this.saveWindowState();
    });

    // 阻止新窗口打开，改为在默认浏览器中打开
    this.mainWindow.webContents.setWindowOpenHandler(({ url }) => {
      shell.openExternal(url);
      return { action: "deny" };
    });

    // 处理导航事件，增强安全性
    this.mainWindow.webContents.on("will-navigate", (event, navigationUrl) => {
      const parsedUrl = new URL(navigationUrl);
      const allowedDomains = ["admin.abacusflow.cn"];

      if (!allowedDomains.includes(parsedUrl.hostname)) {
        event.preventDefault();
        shell.openExternal(navigationUrl);
      }
    });

    // 处理页面标题变化
    this.mainWindow.webContents.on("page-title-updated", (event, title) => {
      this.mainWindow.setTitle(`${title} - AbacusFlow Admin`);
    });

    // 网络错误处理
    this.mainWindow.webContents.on("did-fail-load", (event, errorCode, errorDescription) => {
      if (errorCode !== -3) {
        // -3 是用户主动取消加载
        this.showErrorPage(errorDescription);
      }
    });
  }

  setupSecurityPolicies() {
    // 设置 CSP 头
    session.defaultSession.webRequest.onHeadersReceived((details, callback) => {
      callback({
        responseHeaders: {
          ...details.responseHeaders,
          "Content-Security-Policy": [
            "default-src 'self' https://admin.abacusflow.cn; " +
              "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://admin.abacusflow.cn; " +
              "style-src 'self' 'unsafe-inline' https://admin.abacusflow.cn; " +
              "img-src 'self' data: https:; " +
              "connect-src 'self' https://admin.abacusflow.cn wss://admin.abacusflow.cn;"
          ]
        }
      });
    });

    // 清除缓存和存储（可选）
    // session.defaultSession.clearStorageData();
  }

  loadContent() {
    // 设置用户代理，让服务器知道这是 Electron 应用
    this.mainWindow.webContents.setUserAgent(
      `${this.mainWindow.webContents.getUserAgent()} AbacusFlowAdmin/${app.getVersion()}`
    );

    // 加载URL
    this.mainWindow.loadURL(APP_CONFIG.url).catch((error) => {
      console.error("Failed to load URL:", error);
      this.showErrorPage("无法连接到服务器");
    });
  }

  showErrorPage(error) {
    const errorHtml = `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="UTF-8">
        <title>连接错误</title>
        <style>
          body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
          }
          .error-container {
            text-align: center;
            background: rgba(255, 255, 255, 0.1);
            padding: 40px;
            border-radius: 10px;
            backdrop-filter: blur(10px);
          }
          .retry-btn {
            background: #4CAF50;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 20px;
          }
          .retry-btn:hover {
            background: #45a049;
          }
        </style>
      </head>
      <body>
        <div class="error-container">
          <h1>🚫 连接失败</h1>
          <p>错误信息: ${error}</p>
          <button class="retry-btn" onclick="location.reload()">重试连接</button>
        </div>
      </body>
      </html>
    `;

    this.mainWindow.loadURL(`data:text/html;charset=utf-8,${encodeURIComponent(errorHtml)}`);
  }
}

// 应用菜单设置
function createApplicationMenu() {
  if (process.platform === "darwin") {
    // macOS 菜单
    const template = [
      {
        label: app.getName(),
        submenu: [
          { role: "about" },
          { type: "separator" },
          { role: "services" },
          { type: "separator" },
          { role: "hide" },
          { role: "hideothers" },
          { role: "unhide" },
          { type: "separator" },
          { role: "quit" }
        ]
      },
      {
        label: "编辑",
        submenu: [
          { role: "undo" },
          { role: "redo" },
          { type: "separator" },
          { role: "cut" },
          { role: "copy" },
          { role: "paste" },
          { role: "selectall" }
        ]
      },
      {
        label: "视图",
        submenu: [
          { role: "reload" },
          { role: "forceReload" },
          { role: "toggleDevTools" },
          { type: "separator" },
          { role: "resetZoom" },
          { role: "zoomIn" },
          { role: "zoomOut" },
          { type: "separator" },
          { role: "togglefullscreen" }
        ]
      },
      {
        label: "窗口",
        submenu: [{ role: "minimize" }, { role: "close" }]
      }
    ];

    Menu.setApplicationMenu(Menu.buildFromTemplate(template));
  } else {
    // Windows/Linux 菜单
    Menu.setApplicationMenu(null); // 隐藏菜单栏，或者自定义菜单
  }
}

// 应用实例管理
const windowManager = new WindowManager();

// 应用事件处理
app.whenReady().then(async () => {
  // 创建应用菜单
  createApplicationMenu();

  // 安全设置
  app.setAsDefaultProtocolClient("abacusflow");

  // 创建主窗口
  windowManager.createMainWindow();

  // macOS 激活时重新创建窗口
  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      windowManager.createMainWindow();
    }
  });
});

// 所有窗口关闭时退出应用（macOS 除外）
app.on("window-all-closed", () => {
  if (process.platform !== "darwin") {
    app.quit();
  }
});

// 阻止多个实例运行
const gotTheLock = app.requestSingleInstanceLock();

if (!gotTheLock) {
  app.quit();
} else {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  app.on("second-instance", (event, commandLine, workingDirectory) => {
    // 当运行第二个实例时，将会聚焦到主窗口
    if (windowManager.mainWindow) {
      if (windowManager.mainWindow.isMinimized()) {
        windowManager.mainWindow.restore();
      }
      windowManager.mainWindow.focus();
    }
  });
}

// IPC 事件处理（与渲染进程通信）
ipcMain.handle("app-version", () => {
  return app.getVersion();
});

ipcMain.handle("show-message-box", async (event, options) => {
  const result = await dialog.showMessageBox(windowManager.mainWindow, options);
  return result;
});

// 优雅退出
process.on("SIGTERM", () => {
  app.quit();
});

process.on("SIGINT", () => {
  app.quit();
});

