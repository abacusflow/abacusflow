# ci.Dockerfile

FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive
ENV POSTGRES_USER=abacusflow
ENV POSTGRES_PASSWORD=abacusflow
ENV POSTGRES_DB=abacusflow

# 安装基础工具
RUN apt-get update && \
    apt-get install -y curl wget git unzip bash gnupg2 lsb-release sudo && \
    apt-get clean

# 安装 JDK 17
RUN apt-get install -y openjdk-17-jdk

# 安装 Node.js 22
RUN curl -fsSL https://deb.nodesource.com/setup_22.x | bash - && \
    apt-get install -y nodejs

# 安装 PostgreSQL server + client
RUN apt-get install -y postgresql postgresql-client && \
    mkdir -p /var/run/postgresql && chown -R postgres:postgres /var/run/postgresql

# 初始化数据库
USER postgres
RUN /etc/init.d/postgresql start && \
    psql --command "CREATE USER ${POSTGRES_USER} WITH SUPERUSER PASSWORD '${POSTGRES_PASSWORD}';" && \
    createdb -O ${POSTGRES_USER} ${POSTGRES_DB}

# 切回普通用户用于构建
USER root
RUN useradd -m ci && mkdir /app && chown -R ci:ci /app
WORKDIR /app

# 添加 PostgreSQL 启动脚本
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

USER ci
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
