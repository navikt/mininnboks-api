FROM navikt/java:11

COPY init-scripts /init-scripts

ENV APPD_ENABLED=true
ENV APP_NAME=mininnboks-api

# Netty bruker jdk.internal.misc.Unsafe så dette må åpnes opp for max ytelse
ENV JAVA_OPTS="${JAVA_OPTS} --add-opens java.base/jdk.internal.misc=ALL-UNNAMED"
ENV JAVA_OPTS="${JAVA_OPTS} --add-opens java.base/java.nio=ALL-UNNAMED"
ENV JAVA_OPTS="${JAVA_OPTS} -Dio.netty.tryReflectionSetAccessible=true"
ENV JAVA_OPTS="${JAVA_OPTS} -XX:MaxRAMPercentage=65.0"

COPY  build/install/mininnboks-api/lib/  ./lib
COPY build/libs/mininnboks*.jar ./app.jar

