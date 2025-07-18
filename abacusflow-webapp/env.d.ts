/// <reference types="vite/client" />
interface ImportMetaEnv {
  readonly VITE_SERVER_ENDPOINT: string;
  readonly VITE_APP_VERSION: string;
  readonly VITE_CUBE_ENDPOINT: string;
  // 你可以继续加其他 VITE_ 开头的变量
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
