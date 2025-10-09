# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AbacusFlow is an inventory management platform built with Domain-Driven Design (DDD) and Clean Architecture principles. The system is organized into five core domains: Product, Inventory, Transaction, Partner, and Storage Point (Depot).

## Architecture

### Backend (Kotlin/Java - Spring Boot)
The project follows Clean Architecture with DDD:

- **Infrastructure Layer (`abacusflow-infra/`)**: Database access and common utilities
  - `abacusflow-db`: Database configurations and entities
  - `abacusflow-commons`: Shared utilities and common code
  
- **Core Domain Layer (`abacusflow-core/`)**: Business logic and domain models
  - `abacusflow-user`: User domain
  - `abacusflow-product`: Product domain
  - `abacusflow-inventory`: Inventory management domain
  - `abacusflow-transaction`: Transaction/ordering domain
  - `abacusflow-partner`: Partner (customers/suppliers) domain
  - `abacusflow-depot`: Storage point/warehouse domain

- **Use Case Layer (`abacusflow-usecase/`)**: Application services and business rules
  - Corresponding usecase modules for each domain
  - `abacusflow-usecase-commons`: Shared use case utilities

- **Portal Layer (`abacusflow-portal/`)**: API interfaces and web controllers
  - `abacusflow-portal-web`: REST API endpoints and OpenAPI spec

- **Tools Layer (`abacusflow-tools/`)**: Supporting tools and monitoring
  - `abacusflow-monitor`: Application monitoring

- **Server Layer (`abacusflow-server/`)**: Main Spring Boot application startup

### Frontend Applications (`abacusflow-app/`)
Three client applications with different priorities:
- **`abacusflow-webapp`**: Vue 3 + TypeScript web application (active development)
- **`abacusflow-nativeapp`**: Flutter mobile app for iOS/Android (highest priority)
- **`abacusflow-miniprogram`**: UniApp mini-program (no active development)

## Key Commands

### Backend Development
```bash
# Build entire project
./gradlew build

# Run Spring Boot application
./gradlew :abacusflow-server:bootRun

# Build without version in jar name
./gradlew :abacusflow-server:bootJar -PnoVersion

# Install Git hooks for formatting
./gradlew installGitHooks

# Clean and rebuild
./gradlew clean build
```

### Web Application (abacusflow-webapp)
```bash
cd abacusflow-app/abacusflow-webapp
npm install
npm run dev                # Development server
npm run build             # Production build
npm run type-check        # TypeScript checking
npm run lint              # ESLint
npm run generate          # Generate OpenAPI client
./gradlew :abacusflow-webapp:build  # Gradle build
./gradlew :abacusflow-webapp:webappBuildImage  # Docker build
```

### Flutter App (abacusflow-nativeapp)
```bash
cd abacusflow-app/abacusflow-nativeapp
flutter pub get
flutter pub run build_runner build --delete-conflicting-outputs
flutter run               # Development
flutter build apk         # Android build
flutter build ios         # iOS build
```

### Mini Program (abacusflow-miniprogram)
```bash
cd abacusflow-app/abacusflow-miniprogram
npm install
npm run dev:mp-weixin     # WeChat development
npm run build:mp-weixin   # WeChat build
```

## Development Workflow

### OpenAPI Client Generation
- Backend generates OpenAPI spec at `abacusflow-portal/abacusflow-portal-web/src/main/resources/static/openapi.yaml`
- Web app: `npm run generate` in `abacusflow-webapp/`
- Flutter app: Clients generated in `build/generated/openapi/`

### Code Formatting
- Pre-push Git hook enforces code formatting
- Install with `./gradlew installGitHooks`

### Docker Development
- Use `docker-compose-base.yml` and `docker-compose-prod.yml` for environment setup
- Web app includes Docker support via Gradle tasks

## Testing and Quality

### Running Tests
```bash
# Backend tests
./gradlew test

# Web app type checking
cd abacusflow-app/abacusflow-webapp
npm run type-check
npm run lint

# Flutter tests
cd abacusflow-app/abacusflow-nativeapp
flutter test
```

## Key Technologies

### Backend Stack
- **Framework**: Spring Boot with Kotlin/Java
- **Build**: Gradle with Kotlin DSL
- **Architecture**: DDD + Clean Architecture
- **API**: OpenAPI/Swagger documentation

### Frontend Stack
- **Web**: Vue 3, TypeScript, Vite, Ant Design Vue, Pinia
- **Mobile**: Flutter with Dart
- **Mini Program**: UniApp (Vue 2 based)

### Development Tools
- Git hooks for code formatting
- OpenAPI code generation
- Docker containerization
- Gradle multi-module builds

## Important Notes

- The system is modularized by business domains following DDD principles
- Each domain has its own core, use case, and potentially portal modules
- OpenAPI specs drive client code generation across platforms
- Pre-push hooks ensure code quality and formatting consistency
- Flutter app has highest development priority among client applications