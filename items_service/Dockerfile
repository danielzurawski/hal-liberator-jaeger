FROM clojure:openjdk-11 AS builder

WORKDIR /usr/src/app

ADD project.clj .

RUN ["lein", "deps"]

ADD . .

CMD ["lein", "repl", ":headless", ":host", "0.0.0.0", ":port", "17889"]

EXPOSE 17889
