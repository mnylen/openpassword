FROM ubuntu:12.04

RUN apt-get update
RUN apt-get install -y ruby1.9.3
RUN gem install sass --no-ri --no-rdoc

RUN apt-get install -y build-essential
RUN gem install rb-inotify --no-ri --no-rdoc

RUN mkdir /opt/project
ENTRYPOINT ["/usr/local/bin/sass"]
WORKDIR /opt/project

