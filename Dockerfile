FROM navikt/java:11

# sett riktig tidssone
ENV TZ Europe/Oslo
RUN ln -fs /usr/share/zoneinfo/Europe/Oslo /etc/localtime

COPY java-debug.sh /init-scripts/08-java-debug.sh
COPY init.sh /init-scripts/init.sh

#COPY init.sh /init-scripts/init.sh
#RUN chmod +x /init-scripts/init.sh
#CMD ./init-scripts/init.sh

ENV APPD_ENABLED=true
ENV APP_NAME=mininnboks-api

# Netty bruker jdk.internal.misc.Unsafe så dette må åpnes opp for max ytelse
ENV JAVA_OPTS="${JAVA_OPTS} --add-opens java.base/jdk.internal.misc=ALL-UNNAMED"
ENV JAVA_OPTS="${JAVA_OPTS} --add-opens java.base/java.nio=ALL-UNNAMED"
ENV JAVA_OPTS="${JAVA_OPTS} -Dio.netty.tryReflectionSetAccessible=true"
#ENV JAVA_OPTS="${JAVA_OPTS} --illegal-access=deny"
ENV JAVA_OPTS="${JAVA_OPTS} -XX:MaxRAMPercentage=65.0"
ENV JAVA_OPTS="${JAVA_OPTS} -XX:+IgnoreUnrecognizedVMOptions"

COPY build/libs/app*.jar ./app.jar
