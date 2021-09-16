FROM openjdk:11
MAINTAINER Matthew.Crowell@Smoothstack.com
RUN adduser --system --group discoveryservice
USER discoveryservice:discoveryservice
ADD target/loanservice-0.0.1-SNAPSHOT.jar loanservice.jar
EXPOSE 7776
ENTRYPOINT ["java", "-jar", "loanservice.jar", "--spring.profiles.active=dev"]