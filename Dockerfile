FROM openjdk:11
LABEL maintainer="Matthew.Crowell@Smoothstack.com"
ADD target/loanservice-0.0.1-SNAPSHOT.jar loanservice.jar
EXPOSE 7776
ENTRYPOINT ["java", "-jar", "loanservice.jar", "--spring.profiles.active=dev"]