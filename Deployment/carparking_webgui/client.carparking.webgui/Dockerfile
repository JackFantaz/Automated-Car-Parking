# docker build --rm -t client-carparking-webgui .
# docker run --rm -p8100:8100  client-carparking-webgui

FROM openjdk:12.0.2
EXPOSE 8100
ADD ./build/distributions/*.tar /
WORKDIR /carparking.webgui-1.0/bin
CMD ["bash", "carparking.webgui"]
