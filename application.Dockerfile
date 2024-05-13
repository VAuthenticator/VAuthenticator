FROM amazoncorretto:21-al2023

ADD target/vauthenticator.jar /usr/local/vauthenticator/

VOLUME /var/log/onlyone-portal

WORKDIR /usr/local/vauthenticator/

USER application

CMD ["java", "-jar", "vauthenticator.jar"]