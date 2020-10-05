FROM navikt/java:8-appdynamics
ENV APPD_ENABLED=true
ENV JAVA_OPTS="${JAVA_OPTS} -XX:MaxRAMPercentage=65.0"
#ENV JAVA_OPTS="${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"

COPY target/mininnboks-api /app
