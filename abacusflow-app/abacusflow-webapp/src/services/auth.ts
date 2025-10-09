import {
  createAuth0Client,
  type Auth0Client,
  type RedirectLoginOptions,
  type LogoutOptions,
  type GetTokenSilentlyOptions
} from "@auth0/auth0-spa-js";
import { jwtDecode } from "jwt-decode";

interface Auth0Config {
  domain: string;
  clientId: string;
  audience: string;
  redirectUri: string;
  scope: string;
}

interface UserProfile {
  sub?: string;
  name?: string;
  email?: string;
  picture?: string;
  nickname?: string;
  exp?: number;
  iat?: number;
  aud?: string | string[];
  iss?: string;
  [key: string]: unknown;
}

class AuthService {
  private auth0Client: Auth0Client | null = null;
  private config: Auth0Config;

  constructor() {
    this.config = {
      domain: import.meta.env.VITE_AUTH0_DOMAIN || "dev-st5cs3qsjm2174ua.us.auth0.com",
      clientId: import.meta.env.VITE_AUTH0_CLIENT_ID || "",
      audience: import.meta.env.VITE_AUTH0_AUDIENCE || "https://admin.abacusflow.cn",
      redirectUri: `${window.location.origin}/callback`,
      scope: "openid profile email"
    };

    if (!this.config.clientId) {
      console.error(
        "Auth0 Client ID is not configured. Please set VITE_AUTH0_CLIENT_ID environment variable."
      );
    }
  }

  async initialize(): Promise<void> {
    if (this.auth0Client) return;

    const options = {
      domain: this.config.domain,
      clientId: this.config.clientId,
      authorizationParams: {
        redirect_uri: this.config.redirectUri,
        audience: this.config.audience,
        scope: this.config.scope
      },
      useRefreshTokens: true,
      cacheLocation: "localstorage" as const
    };

    this.auth0Client = await createAuth0Client(options);
  }

  async login(options?: RedirectLoginOptions): Promise<void> {
    if (!this.auth0Client) {
      throw new Error("Auth0 client not initialized");
    }
    debugger;

    await this.auth0Client.loginWithRedirect({
      ...options,
      authorizationParams: {
        redirect_uri: this.config.redirectUri,
        audience: this.config.audience,
        scope: this.config.scope,
        ...options?.authorizationParams
      }
    });
  }

  async handleRedirectCallback(): Promise<void> {
    if (!this.auth0Client) {
      throw new Error("Auth0 client not initialized");
    }

    await this.auth0Client.handleRedirectCallback();
    window.history.replaceState({}, document.title, window.location.pathname);
  }

  async logout(options?: LogoutOptions): Promise<void> {
    if (!this.auth0Client) {
      throw new Error("Auth0 client not initialized");
    }

    await this.auth0Client.logout({
      logoutParams: {
        returnTo: window.location.origin
      },
      ...options
    });
  }

  async isAuthenticated(): Promise<boolean> {
    if (!this.auth0Client) return false;
    return await this.auth0Client.isAuthenticated();
  }

  async getAccessToken(options?: GetTokenSilentlyOptions): Promise<string> {
    if (!this.auth0Client) {
      throw new Error("Auth0 client not initialized");
    }

    try {
      return await this.auth0Client.getTokenSilently({
        authorizationParams: {
          audience: this.config.audience,
          scope: this.config.scope
        },
        ...options
      });
    } catch (error) {
      console.error("Error getting access token:", error);
      throw error;
    }
  }

  async getUserProfile(): Promise<UserProfile | null> {
    try {
      const token = await this.getAccessToken();
      const decodedToken = jwtDecode<UserProfile>(token);
      return decodedToken;
    } catch (error) {
      console.error("Error getting user profile:", error);
      return null;
    }
  }

  async getUser(): Promise<UserProfile | undefined> {
    if (!this.auth0Client) {
      throw new Error("Auth0 client not initialized");
    }

    return await this.auth0Client.getUser();
  }

  isTokenExpired(token: string): boolean {
    try {
      const decoded = jwtDecode(token);
      const currentTime = Date.now() / 1000;
      return decoded.exp ? decoded.exp < currentTime : true;
    } catch {
      return true;
    }
  }
}

export const authService = new AuthService();
export default authService;
