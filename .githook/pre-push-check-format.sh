#!/bin/bash
set -e

#echo -e "🔧 Kotlin 自动格式化 (ktlintFormat)..."
#./gradlew ktlintFormat

echo -e "🔍 Kotlin 代码检查 (ktlintCheck)..."
./gradlew ktlintCheck

#echo -e "🔧 TypeScript 自动格式化 (tsFormat)..."
#./gradlew tsFormat

echo -e "🔍 TypeScript Lint 检查 (lint-ts)..."
./gradlew lint-ts

## 检查是否有新的改动
#if ! git diff --quiet; then
#  echo "❌ 检测到未提交的格式化修改，请先 add/commit 再 push！"
#  exit 1
#fi

echo "✅ 所有格式化任务执行成功，继续 push。"
