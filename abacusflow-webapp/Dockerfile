FROM nginx:alpine

# 复制构建好的前端代码到 nginx 静态目录
COPY dist /usr/share/nginx/html

# 拷贝模板配置
COPY docker/nginx-templates/default.conf.template /etc/nginx/templates/default.conf.template

