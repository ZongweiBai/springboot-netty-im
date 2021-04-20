FROM openjdk:16-jdk-alpine3.13

ENV WORKDIR="/usr/local/modules/netty-im"
ENV JAVA_OPTS="-XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -Xms1024m -Xmx1024m"
ENV JVM_OPTS="-Djava.security.egd=file:/dev/./urandom -DAPP_CONFIG=$WORKDIR/config -DLOG_PATH=$WORKDIR/log -Dspring.config.additional-location=$WORKDIR/config/"

VOLUME ["$WORKDIR/config", "$WORKDIR/log"]

RUN mkdir -p $WORKDIR/config \
     && mkdir -p $WORKDIR/log

WORKDIR $WORKDIR

ARG JAR_FILE
ADD $JAR_FILE netty-im.jar
#ADD logback.xml $WORKDIR/config/

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar -Dserver.port=8080 $JVM_OPTS netty-im.jar"]