FROM openjdk:8u212-jre

ADD target/tesla-springcloudgateway-sample-1.0.0.jar /root/app.jar
ADD bin/start.sh /root/
RUN chmod +x /root/*.sh;mkdir /root/logs
ENV APP_NAME tesla-springcloudgateway-sample
ENV JAVA_OPTS ""
ENV LANG C.UTF-8
WORKDIR /root
EXPOSE 9001
CMD ["./start.sh"]