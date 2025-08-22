# Imagem base do Java 17
FROM openjdk:17-jdk-alpine

# Diretório de trabalho dentro do container
WORKDIR /app

# Copia o JAR gerado para dentro do container
COPY target/*.jar app.jar

# Expor a porta usada pela aplicação
EXPOSE 8081

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]