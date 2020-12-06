FROM openjdk:8-jre
ADD target/eventlistener-server.jar eventlistener-server.jar
ADD docker-scripts/start-eventlistener.sh start-eventlistener.sh
ENV CONF ""
EXPOSE 8060
CMD chmod +x start-eventlistener.sh && ./start-eventlistener.sh