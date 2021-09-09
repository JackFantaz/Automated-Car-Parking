# FROM openjdk:12.0.2
FROM openjdk:8-jre-alpine
EXPOSE 8101
ADD ./build/distributions/*.tar /
WORKDIR /carparking.webgui-1.0/bin
CMD ["sh", "carparking.webgui"]

# docker build --rm -t manager-carparking-webgui .
# docker save -o manager_carparking_webgui.tar manager-carparking-webgui
# docker load -i manager_carparking_webgui.tar
# docker run --rm -p8101:8101  manager-carparking-webgui
