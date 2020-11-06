FROM navikt/java:11

# sett riktig tidssone
RUN ln -fs /usr/share/zoneinfo/Europe/Oslo /etc/localtime

COPY init-scripts /init-scripts

ENV APPD_ENABLED=true
ENV APP_NAME=mininnboks-api

# Netty bruker jdk.internal.misc.Unsafe så dette må åpnes opp for max ytelse
ENV JAVA_OPTS="${JAVA_OPTS} --add-opens java.base/jdk.internal.misc=ALL-UNNAMED"
ENV JAVA_OPTS="${JAVA_OPTS} --add-opens java.base/java.nio=ALL-UNNAMED"
ENV JAVA_OPTS="${JAVA_OPTS} -Dio.netty.tryReflectionSetAccessible=true"
ENV JAVA_OPTS="${JAVA_OPTS} -XX:MaxRAMPercentage=65.0"
ENV JAVA_OPTS="${JAVA_OPTS} --illegal-access=warn"

COPY  build/install/app/lib/  ./lib
COPY build/libs/mininnboks*.jar ./app.jar

