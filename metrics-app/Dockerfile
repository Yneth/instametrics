# Use an official Java runtime as a parent image
FROM java:8-jre

ARG SERVER_PORT=8082
ENV SERVER_PORT ${SERVER_PORT}

# Set the working directory to /app
WORKDIR /app

# Copy the target directory contents into the container at /app
ADD ./target/metrics-app.jar /app

# Make specified port available to the world outside this container
EXPOSE ${SERVER_PORT}

# run java application
CMD ["java", "-jar", "/app/metrics-app.jar"]
