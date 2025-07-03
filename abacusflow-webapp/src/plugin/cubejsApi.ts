/* eslint-disable @typescript-eslint/no-explicit-any */
import cubejs from "@cubejs-client/core";
import type { CubeApi } from "@cubejs-client/core";
import Cookies from "js-cookie";

interface CubeError {
  response?: {
    status: number;
    data?: any;
  };
  status?: number;
  message?: string;
}
const CUBE_API_URL = "/cubejs-api/v1";
const CUBE_API_TOKEN = Cookies.get("CUBE_JWT_TOKEN"); // 你可以用 js-cookie 库

const baseCubejsApi: CubeApi = cubejs(CUBE_API_TOKEN || "default", {
  apiUrl: `${CUBE_API_URL}`
});

const handleUnauthorized = () => {
  const redirectUrl = document.location.pathname;
  window.location.href = `/login?redirect=${encodeURIComponent(redirectUrl)}`;
};

const cubejsApi: CubeApi = new Proxy(baseCubejsApi, {
  get(target, prop, receiver) {
    const originalValue = Reflect.get(target, prop, receiver);

    // 只包装异步方法
    if (
      typeof originalValue === "function" &&
      ["load", "sql", "meta", "dryRun"].includes(prop as string)
    ) {
      return async (...args: any[]) => {
        try {
          return await originalValue.apply(target, args);
        } catch (error: unknown) {
          const cubeError = error as CubeError;

          if (cubeError?.message?.includes("Invalid token")) {
            handleUnauthorized();
            throw new Error("Unauthorized - redirecting to login");
          }

          throw error;
        }
      };
    }

    return originalValue;
  }
});

export default cubejsApi;
