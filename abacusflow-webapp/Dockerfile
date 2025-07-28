FROM nginx:alpine

LABEL org.opencontainers.image.description="这是一个用于构建 AbacusFlow Webapp 的镜像"

ENV REDIRECT_TO_HTTPS=true

# 复制构建好的前端代码到 nginx 静态目录
COPY dist /usr/share/nginx/html

# 复制可复用的配置片段
COPY docker/nginx-templates/common_spa.inc.template /etc/nginx/templates/common_spa.inc.template

# 复制主 Nginx 模板
COPY docker/nginx-templates/default.conf.template /etc/nginx/templates/default.conf.template

# 复制自定义启动脚本
COPY docker/custom-entrypoint.sh /custom-entrypoint.sh
RUN chmod +x /custom-entrypoint.sh

ENTRYPOINT ["/custom-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]