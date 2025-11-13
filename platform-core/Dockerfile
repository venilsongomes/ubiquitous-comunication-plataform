# --- Estágio 1: Build (Compilação) ---
# Usamos uma imagem com Maven e Java 21 para compilar o projeto
FROM maven:3.9-eclipse-temurin-21 AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo de dependências primeiro (para aproveitar o cache do Docker)
COPY pom.xml .

# Baixa as dependências (se o pom.xml não mudou, o Docker usa o cache aqui)
RUN mvn dependency:go-offline -B

# Copia o resto do código fonte
COPY src ./src

# Compila o projeto e gera o JAR (pula os testes para ser mais rápido no build)
RUN mvn clean package -DskipTests

# --- Estágio 2: Runtime (Execução) ---
# Usamos uma imagem JRE leve apenas para rodar o Java
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia apenas o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta 8080
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]