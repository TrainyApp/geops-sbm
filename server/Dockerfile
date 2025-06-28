FROM --platform=$TARGETOS/$TARGETARCH eclipse-temurin:24-jre-alpine

RUN apk add gcompat

WORKDIR /usr/app
COPY build/install/server .

ENV LD_PRELOAD=/lib/libgcompat.so.0

ENTRYPOINT ["/usr/app/bin/server"]
