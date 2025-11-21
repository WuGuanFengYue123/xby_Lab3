# 使用 multi-arch devcontainer 基镜像（包含 Java 17）
FROM mcr.microsoft.com/devcontainers/java:0-17

# 非交互
ENV DEBIAN_FRONTEND=noninteractive

# 安装必要工具（以 root 执行）
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

# 确保 .m2 目录存在并归属 vscode（如果镜像内创建，在 volume mount 时可能被覆盖，但保持容器层正确所有权是有益的）
RUN mkdir -p /home/vscode/.m2/repository \
  && chown -R 1000:1000 /home/vscode/.m2 || true

# 设置 JAVA_HOME（镜像通常已配置，但我们显式设置以保证一致）
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# 切回 vscode 用户并设置工作目录
USER vscode
WORKDIR /workspace

# 确保 mvnw 可执行（如果存在）在新 shell 启动时生效
RUN echo 'if [ -f /workspace/mvnw ]; then chmod +x /workspace/mvnw; fi' >> ~/.bashrc