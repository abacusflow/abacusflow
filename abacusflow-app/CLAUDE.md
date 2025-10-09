# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AbacusFlow is a multi-platform application suite consisting of three main applications:

* **abacusflow-webapp**: Vue 3 + TypeScript web application with optional Electron desktop support (desktop is **not urgent**)
* **abacusflow-miniprogram**: UniApp-based mini-program for multiple platforms (**no active development planned for now**)
* **abacusflow-nativeapp**: Flutter mobile application for iOS and Android (**highest priority and urgent for development**)

## Development Commands

### Web Application (abacusflow-webapp)
```bash
cd abacusflow-webapp
npm install
npm run dev                # Development server
npm run build              # Production build
npm run type-check         # TypeScript type checking
npm run lint               # ESLint linting
npm run format             # Prettier formatting
npm run generate           # Generate OpenAPI client
npm run electron           # Run Electron app
npm run pack               # Package Electron app
npm run dist               # Build and package Electron app
```

### Mini Program (abacusflow-miniprogram)
```bash
cd abacusflow-miniprogram
npm install
npm run serve              # Development (H5)
npm run build              # Production build (H5)
npm run dev:mp-weixin      # WeChat mini-program development
npm run build:mp-weixin    # WeChat mini-program build
npm run dev:mp-alipay      # Alipay mini-program development
npm run build:mp-alipay    # Alipay mini-program build
# Additional platforms: mp-baidu, mp-toutiao, mp-qq, app-plus, etc.
```

### Native App (abacusflow-nativeapp)
```bash
cd abacusflow-nativeapp
flutter pub get
flutter pub run build_runner build --delete-conflicting-outputs
flutter run                # Development
flutter build apk          # Android build
flutter build ios          # iOS build
```

### Gradle Build System
Each subproject has Gradle integration:
```bash
# From project root
./gradlew :abacusflow-webapp:build
./gradlew :abacusflow-webapp:lint-ts
./gradlew :abacusflow-webapp:openapiGenerateTs
./gradlew :abacusflow-webapp:webappBuildImage
./gradlew :abacusflow-webapp:webappBuildElectron
```

## Architecture

### Web Application (abacusflow-webapp)
- **Framework**: Vue 3 with Composition API
- **Build Tool**: Vite
- **UI Library**: Ant Design Vue
- **State Management**: Pinia
- **HTTP Client**: Generated from OpenAPI specs using `@openapitools/openapi-generator-cli`
- **Desktop**: Electron wrapper for cross-platform desktop deployment
- **Charts**: ECharts with Vue integration
- **Development**: Hot module replacement with proxy configuration for API endpoints

Key directories:
- `src/core/openapi/` - Auto-generated API client (regenerate with `npm run generate`)
- `src/components/` - Reusable Vue components
- `src/views/` - Page components
- `src/stores/` - Pinia stores
- `src/router/` - Vue Router configuration

### Mini Program (abacusflow-miniprogram)
- **Framework**: UniApp (Vue 2 based)
- **Multi-platform**: Supports WeChat, Alipay, Baidu, Toutiao, QQ, H5, and native apps
- **State Management**: Vuex
- **HTTP**: FlyIO for network requests
- **TypeScript**: Supported with Vue class-style components

### Native App (abacusflow-nativeapp)
- **Framework**: Flutter
- **SDK**: Dart 3.8.1+
- **OpenAPI**: Generated client in `build/generated/openapi/`
- **JSON**: Serialization with `json_annotation` and `json_serializable`

## Code Generation

### OpenAPI Client Generation
- **Web App**: Run `npm run generate` in `abacusflow-webapp/` to generate TypeScript client
- **Flutter App**: OpenAPI client generated in `build/generated/openapi/`
- **Source**: OpenAPI spec located at `../../abacusflow-portal/abacusflow-portal-web/src/main/resources/static/openapi.yaml`

### Flutter Code Generation
```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

## Development Environment

### Proxy Configuration (Web App)
Development server proxies API calls:
- `/api/*` → Backend server (removes `/api` prefix)
- `/login` → Backend server
- `/static/*` → Backend server
- `/cubejs-api/*` → CubeJS server

### Environment Variables
Web app uses Vite environment variables:
- `VITE_SERVER_ENDPOINT` - Backend API server
- `VITE_CUBE_ENDPOINT` - CubeJS analytics server

## Build and Deployment

### Docker Support
Web application includes Dockerfile for containerized deployment. Build with:
```bash
cd abacusflow-webapp
./gradlew webappBuildImage
```

### Electron Packaging
Cross-platform desktop apps with different targets:
- **macOS**: DMG and ZIP for x64/arm64
- **Windows**: NSIS installer and portable for x64/ia32
- **Linux**: AppImage, DEB, RPM, tar.gz for x64

## Testing
- **Mini Program**: Jest test suites for different platforms (`test:mp-weixin`, `test:h5`, etc.)
- **Flutter**: Standard Flutter testing framework
- **Web App**: Currently no test configuration (consider adding)