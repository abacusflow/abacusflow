// src/cubejs.ts
import cubejs from "@cubejs-client/core";

const CUBE_API_URL = "http://localhost:4000/cubejs-api/v1";
const CUBE_API_TOKEN = "default";

const cubejsApi = cubejs(CUBE_API_TOKEN, {
  apiUrl: `${CUBE_API_URL}`
});

export default cubejsApi;
