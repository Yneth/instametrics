FROM openjdk:8-jdk

WORKDIR /app

COPY . /app

VOLUME /app

CMD sed -i 's/\r//' /app/mvnw && /app/mvnw clean install -Dmaven.repo.local=/app/.mvn/.m2/repository
