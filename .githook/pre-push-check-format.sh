#!/bin/bash
set -e

echo "🔧 正在执行 Kotlin 格式化 (ktlintFormat)..."
./gradlew ktlintFormat

echo "🔧 正在执行 TypeScript 格式化 (tsFormat)..."
./gradlew tsFormat

echo "✅ 所有格式化任务执行成功，继续 push。"
