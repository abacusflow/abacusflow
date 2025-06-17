## 使用Rocky Linux 9.3作为基础镜像，包含Java 17 JDK
#FROM rockylinux:9.3
#
## 安装Java 17和一些工具
#RUN dnf install -y java-17-openjdk-devel.x86_64 nc openssh-clients procps && \
#    dnf clean all && rm -rf /var/cache
#
## 声明构建参数 JAR_NAME，默认值可以不写，由构建时传入
#ARG JAR_NAME
#
## 设置运行时环境变量 JAR_NAME（从构建参数传入）
#ENV JAR_NAME=$JAR_NAME
#
## 创建工作目录
#WORKDIR /app
#
## 把本地构建输出的 JAR 包拷贝到容器内的 /app 目录
## 假设你在构建时 docker build 的上下文是项目根目录，且 build/libs/your-app.jar 路径正确
#COPY build/libs/$JAR_NAME /app/$JAR_NAME
#
## 运行时执行的命令，启动jar包
#ENTRYPOINT ["java", "-jar", "/app/${JAR_NAME}"]
