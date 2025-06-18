import { fileURLToPath, URL } from "node:url";

import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const envVars = loadEnv(mode, process.cwd());

  return {
    plugins: [vue(), vueDevTools()],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url))
      }
    },
    server: {
      proxy: {
        "/api": {
          target: envVars.VITE_SERVER_ENDPOINT,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, "") // 这一步就是去掉/api
        },
        "/login": {
          target: envVars.VITE_SERVER_ENDPOINT,
          changeOrigin: true
        },
        "/static": {
          target: envVars.VITE_SERVER_ENDPOINT,
          changeOrigin: true
        }
      }
    }
  };
});
