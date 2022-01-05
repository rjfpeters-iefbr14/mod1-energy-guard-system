#FROM yggdrasill.tld/aarch64-alpine-openjdk:17-jdk-3.14-run
FROM openjdk:17-jdk-alpine3.14

WORKDIR /app

COPY maven/app /app/

EXPOSE 8888

COPY maven/entrypoint.sh /sbin/entrypoint.sh

ENTRYPOINT ["/sbin/entrypoint.sh"]
CMD ["/sbin/entrypoint.sh"]
