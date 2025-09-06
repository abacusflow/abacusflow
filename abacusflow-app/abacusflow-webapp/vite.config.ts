import { fileURLToPath, URL } from "node:url";

import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";
// import checker from "vite-plugin-checker";

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const envVars = loadEnv(mode, process.cwd());

  return {
    base: "./", // 确保是相对路径
    plugins: [
      vue(),
      vueDevTools()
      // checker({
      //   typescript: {
      //     tsconfigPath: "./tsconfig.app.json"
      //   },
      //   vueTsc: {
      //     tsconfigPath: "./tsconfig.app.json"
      //   }
      // })
    ],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url))
      }
    },
    server: {
      host: true,
      proxy: {
        "/api": {
          target: envVars.VITE_SERVER_ENDPOINT,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, "") // 这一步就是去掉/api
        },
        "/login": {
          target: envVars.VITE_SERVER_ENDPOINT
        },
        "/static": {
          target: envVars.VITE_SERVER_ENDPOINT,
          changeOrigin: true
        },
        "/cubejs-api": {
          target: envVars.VITE_CUBE_ENDPOINT,
          changeOrigin: true
        }
      }
    }
  };
});
