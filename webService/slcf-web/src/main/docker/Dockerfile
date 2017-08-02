FROM frolvlad/alpine-openjdk7
MAINTAINER 'Richard xie <xieqiang@shanlinjinrong.com>'
RUN apk update && \
    apk add --no-cache tzdata bash curl && \
    cp  /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    date && \
    rm -rf /var/lib/apk/* /var/cache/apk/* /etc/apk/cache/* /tmp/* /var/tmp/*
VOLUME /tmp
ADD slcfservice-1.0.0.war app.war
RUN bash -c 'touch /app.war'
ENTRYPOINT java ${JVM_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.war
