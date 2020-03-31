FROM maven:3.6.1-jdk-8-alpine as builder

# sett riktig tidssone
ENV TZ Europe/Oslo
RUN ln -fs /usr/share/zoneinfo/Europe/Oslo /etc/localtime

ADD / /source
WORKDIR /source
RUN mvn package -DskipTests

FROM navikt/java:8-appdynamics
ENV JAVA_OPTS="${JAVA_OPTS} -XX:MaxRAMPercentage=65.0"
ENV JAVA_OPTS="${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"
ENV APPD_ENABLED=true

COPY --from=builder /source/target/mininnboks-api /app
