/* eslint-disable @typescript-eslint/no-require-imports */

const { FusesPlugin } = require("@electron-forge/plugin-fuses");
const { FuseV1Options, FuseVersion } = require("@electron/fuses");

module.exports = {
  packagerConfig: {
    asar: true,
    executableName: "abacusflow-webapp",
    // afterPack: [
    //   (context, packagerResult) => {
    //     //可以在此将package.json中的name替换
    //   }
    // ],
    // executableName: getExecutableName(process.platform),
    // 应用信息
    appBundleId: "cn.abacusflow.webapp", // macOS Bundle ID
    appCategoryType: "public.app-category.productivity", // macOS 应用分类
    // 图标配置（需要准备相应文件）
    icon: "./src/assets/logo.ico", // 会自动寻找 .ico/.icns/.png
    // 版本信息
    appVersion: process.env.VITE_ABACUSFLOW_VERSION || "1.0.0",
    buildVersion: process.env.BUILD_VERSION || "1",
    // 忽略不需要的文件
    ignore: [
      /^\/src\//,
      /^\/\.git/,
      /^\/\.vscode/,
      /^\/node_modules\/\.cache/,
      /\.map$/,
      /README\.md$/,
      /\.gitignore$/
    ],
    // 额外资源文件
    extraResource: [
      // "./assets/extra-resources"
    ]
  },
  rebuildConfig: {},
  makers: [
    {
      name: "@electron-forge/maker-squirrel",
      config: {
        authors: "AbacusFlow Team",
        description: "AbacusFlow - 智能零售进销存系统 - 静默安装版",
        setupExe: "AbacusFlow-Silent-Setup.exe",
        setupIcon: "./src/assets/logo.ico"
        // loadingGif: "./assets/loading.gif" // 可选：安装时的加载动画
      }
    },
    {
      name: "@electron-forge/maker-nsis",
      config: {
        authors: "AbacusFlow Team",
        description: "AbacusFlow - 智能零售进销存系统 - 传统安装版",
        setupExe: "AbacusFlow-Wizard-Setup.exe",
        setupIcon: "./src/assets/logo.ico",
        // NSIS 特有的常用配置
        installerIcon: "./src/assets/logo.ico", // 安装程序自身的图标
        uninstallerIcon: "./src/assets/logo.ico", // 卸载程序的图标
        oneClick: false, // 设置为 false 来显示安装向导
        allowToChangeInstallationDirectory: true, // 允许用户更改安装路径
        createDesktopShortcut: true, // 创建桌面快捷方式
        createStartMenuShortcut: true // 创建开始菜单快捷方式
      }
    },
    {
      name: "@electron-forge/maker-zip",
      platforms: ["darwin", "linux"],
      config: {
        // macOS 特定配置
        options: {
          darwinDarkModeSupport: true
        }
      }
    },
    {
      name: "@electron-forge/maker-deb",
      config: {
        options: {
          maintainer: "AbacusFlow Team <bruce@abacusflow.cn>",
          homepage: "https://abacusflow.cn",
          description: "专业的数据分析工具",
          productDescription: "AbacusFlow 是一款智能零售进销存系统",
          genericName: "Data Analysis Tool",
          categories: ["Office", "Development"],
          // 图标
          icon: "./src/assets/logo.png",
          // 依赖
          depends: ["gconf2", "gconf-service", "libnotify4", "libappindicator1"],
          // 建议的包
          recommends: ["pulseaudio"]
        }
      }
    },
    {
      name: "@electron-forge/maker-rpm",
      config: {
        options: {
          license: "MIT",
          maintainer: "AbacusFlow Team <bruce@abacusflow.cn>",
          homepage: "https://abacusflow.cn",
          description: "专业的数据分析工具",
          productDescription: "AbacusFlow 是一款智能零售进销存系统",
          categories: ["Office", "Development"],
          // 图标
          icon: "./src/assets/logo.png",
          // 依赖
          requires: ["alsa-lib", "GConf2", "gtk3", "libnotify", "nss"]
        }
      }
    },
    {
      name: "@electron-forge/maker-dmg",
      config: {
        format: "ULFO", // 压缩格式
        setupIcon: "./src/assets/logo.icns",
        // background: "./assets/dmg-background.png", // 可选：DMG 背景图
        contents: (opts) => {
          return [
            { x: 448, y: 344, type: "link", path: "/Applications" },
            { x: 192, y: 344, type: "file", path: opts.appPath }
          ];
        }
      }
    }
  ],
  plugins: [
    {
      name: "@electron-forge/plugin-auto-unpack-natives",
      config: {}
    },
    // Fuses are used to enable/disable various Electron functionality
    // at package time, before code signing the application
    new FusesPlugin({
      version: FuseVersion.V1,
      // 禁用 Node.js 运行时（提高安全性）
      [FuseV1Options.RunAsNode]: false,
      // 启用 Cookie 加密
      [FuseV1Options.EnableCookieEncryption]: true,
      // 禁用 NODE_OPTIONS 环境变量
      [FuseV1Options.EnableNodeOptionsEnvironmentVariable]: false,
      // 禁用 Node.js CLI 检查参数
      [FuseV1Options.EnableNodeCliInspectArguments]: false,
      // 启用 ASAR 完整性验证
      [FuseV1Options.EnableEmbeddedAsarIntegrityValidation]: true,
      // 只从 ASAR 加载应用
      [FuseV1Options.OnlyLoadAppFromAsar]: true,
      // 启用上下文隔离（推荐）
      [FuseV1Options.LoadBrowserProcessSpecificV8Snapshot]: false
    })
  ],

  // 构建钩子
  hooks: {
    // 打包前钩子
    packageAfterCopy: async (config, buildPath, electronVersion, platform, arch) => {
      // 可以在这里执行自定义操作，如清理临时文件等
      console.log(`正在为 ${platform}-${arch} 平台打包...`);
    },

    // 打包后钩子
    packageAfterPrune: async (config, buildPath, electronVersion, platform, arch) => {
      // 可以在这里执行打包后的清理工作
      console.log(`${platform}-${arch} 平台打包完成`);
    }
  }
};
