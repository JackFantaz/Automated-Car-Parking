# FROM openjdk:12.0.2
FROM openjdk:8-jre-alpine
EXPOSE 8101
ADD ./build/distributions/*.tar /
WORKDIR /carparking.webgui-1.0/bin
CMD ["sh", "carparking.webgui"]

# docker build --rm -t client-carparking-webgui .
# docker save -o client_carparking_webgui.tar client-carparking-webgui
# docker load -i client_carparking_webgui.tar
# docker run --rm -p8100:8100  client-carparking-webgui
