FROM ubuntu:12.04

RUN apt-get update
RUN apt-get install -y --no-install-recommends openjdk-7-jdk

RUN apt-get install -y curl
RUN curl https://raw.github.com/technomancy/leiningen/stable/bin/lein > /usr/bin/lein
RUN chmod +x /usr/bin/lein
RUN /usr/bin/lein upgrade

RUN mkdir -p /opt/openpassword
RUN mkdir -p /root/.m2/repository

ENTRYPOINT ["/usr/bin/lein"]
ENV LEIN_ROOT true
WORKDIR /opt/openpassword

