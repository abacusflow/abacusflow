#!/bin/bash
set -e

echo -e "🔧 Kotlin 自动格式化 (ktlintFormat)..."
./gradlew ktlintFormat

echo -e "🔍 Kotlin 代码检查 (ktlintCheck)..."
./gradlew ktlintCheck

echo -e "🔧 TypeScript 自动格式化 (tsFormat)..."
./gradlew tsFormat

echo -e "🔍 TypeScript Lint 检查 (lint-ts)..."
./gradlew lint-ts

echo "✅ 所有格式化任务执行成功，继续 push。"
