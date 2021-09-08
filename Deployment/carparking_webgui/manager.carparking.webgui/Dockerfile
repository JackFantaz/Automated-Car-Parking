# docker build --rm -t manager-carparking-webgui .
# docker run --rm -p8101:8101  manager-carparking-webgui

FROM openjdk:12.0.2
EXPOSE 8101
ADD ./build/distributions/*.tar /
WORKDIR /carparking.webgui-1.0/bin
CMD ["bash", "carparking.webgui"]
