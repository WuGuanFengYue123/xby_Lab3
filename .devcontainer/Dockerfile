# 使用 multi-arch devcontainer 基镜像（支持 arm64）
FROM mcr.microsoft.com/devcontainers/java:0-17

# 非交互
ENV DEBIAN_FRONTEND=noninteractive

# 安装必要工具
USER root
RUN apt-get update \
  && apt-get install -y --no-install-recommends \
     maven \
     git \
     ca-certificates \
     curl \
     tree \
     dos2unix \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*

# 设置 JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# 切回 vscode 用户
USER vscode
WORKDIR /workspace

# 确保 mvnw 可执行（如果存在）
RUN echo 'if [ -f /workspace/mvnw ]; then chmod +x /workspace/mvnw; fi' >> ~/.bashrc