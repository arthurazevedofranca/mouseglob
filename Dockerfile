# Dockerfile para testar MouseGlob em ambiente multiplataforma
FROM eclipse-temurin:21-jdk

# Instalar dependências necessárias
RUN apt-get update && apt-get install -y \
    libopencv-dev \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar arquivos do projeto
COPY . .

# Build do projeto
RUN ./gradlew build --no-daemon

# Comando padrão - pode ser substituído ao executar
CMD ["./gradlew", ":MouseGlob:run", "--no-daemon"]
