FROM debian:bullseye-slim
WORKDIR /server/
COPY server.c /server/
RUN apt update && \
    apt-get install -y nginx gcc libfcgi-dev spawn-fcgi libcap2-bin && \
    rm -rf /var/lib/apt/lists/* && \
    gcc server.c -lfcgi -o server && \
    useradd -m -s /bin/bash user1 && \
    setcap 'cap_net_bind_service=+ep' /usr/sbin/nginx && \
    chown -R user1:user1 /var/log/nginx /var/lib/nginx
COPY ./nginx/nginx.conf /etc/nginx/nginx.conf
USER user1
CMD spawn-fcgi -p 8080 ./server && nginx -g 'daemon off;'




# FROM nginx:latest
# WORKDIR /server/
# COPY server.c /server/
# COPY ./nginx/nginx.conf /etc/nginx/nginx.conf
# RUN apt update && \
#     apt-get install -y libfcgi-dev spawn-fcgi gcc && \
#     rm -rf /var/lib/apt/lists/* && \
#     gcc server.c -lfcgi -o server
# CMD spawn-fcgi -p 8080 ./server && nginx -g 'daemon off;'
