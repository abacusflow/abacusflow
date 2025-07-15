FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

# 安装基础工具
RUN apt-get update && \
    apt-get install -y curl wget git unzip bash gnupg2 lsb-release sudo && \
    apt-get clean

# 安装 JDK 17
RUN apt-get install -y openjdk-17-jdk

# 安装 Node.js 22
RUN curl -fsSL https://deb.nodesource.com/setup_22.x | bash - && \
    apt-get install -y nodejs
