#!/bin/bash

# 安全模式：遇到错误退出，使用未定义变量退出
set -euo pipefail

# 查找并重命名文件夹
find . -depth -type d -name '*abacusflow*' -print0 | while IFS= read -r -d '' dir; do
    # 提取目录名（不包括路径）
    dir_name=$(basename "$dir")
    # 替换字符串
    new_name="${dir_name//abacusflow/abacusflow}"
    
    # 仅当名称实际改变时才重命名
    if [ "$dir_name" != "$new_name" ]; then
        # 获取父目录路径
        parent_dir=$(dirname "$dir")
        # 完整的旧路径和新路径
        new_path="${parent_dir}/${new_name}"
        
        # 安全重命名（避免覆盖已存在的目录）
        if [ ! -e "$new_path" ]; then
            echo "重命名: $dir -> $new_path"
            mv -- "$dir" "$new_path"
        else
            echo "警告: 跳过 '$dir' -> '$new_path'（目标已存在）"
        fi
    fi
done

echo "操作完成"
