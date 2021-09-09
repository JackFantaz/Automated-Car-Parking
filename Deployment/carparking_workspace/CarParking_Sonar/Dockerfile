FROM hypriot/rpi-java
EXPOSE 60001

ADD ./build/distributions/*.tar /
WORKDIR /CarParking_Sonar-1.0/bin
COPY ./*.pl ./
COPY ./*.sh ./
COPY ./*.py ./
COPY ./*.c ./
COPY ./sonar ./

CMD ["sh", "CarParking_Sonar"]

