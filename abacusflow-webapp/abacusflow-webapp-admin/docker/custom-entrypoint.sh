#!/bin/sh
set -e

# 处理 .inc.template 文件
if [ -f "/etc/nginx/templates/common_spa.inc.template" ]; then
    echo "Processing common_spa.inc.template..."
    envsubst < /etc/nginx/templates/common_spa.inc.template > /etc/nginx/conf.d/common_spa.inc
fi

# 调用原始的 nginx entrypoint 来处理 .conf.template 文件
exec /docker-entrypoint.sh "$@"