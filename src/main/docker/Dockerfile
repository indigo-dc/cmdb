FROM frolvlad/alpine-oraclejdk8:slim

ENV CMDB_TARGET_URL=http://indigo.cloud.plgrid.pl/cmdb
ENV CMDB_CRUD_TARGET_URL=http://couch.cloud.plgrid.pl/indigo-cmdb-v2
ENV CMDB_CRUD_USERNAME=FIXME
ENV CMDB_CRUD_PASSWORD=FIXME
ENV CMDB_CRUD_ADMIN_GROUP=cmdb-dev-admins
ENV OIDC_USERINFO=https://iam-test.indigo-datacloud.eu/userinfo

VOLUME /tmp
ADD cmdb.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --proxy.cmdb.target_url=$CMDB_TARGET_URL --proxy.cmdb-crud.target_url=$CMDB_CRUD_TARGET_URL --proxy.cmdb-crud.username=$CMDB_CRUD_USERNAME --proxy.cmdb-crud.password=$CMDB_CRUD_PASSWORD --proxy.cmdb-crud.admin_group=$CMDB_CRUD_ADMIN_GROUP --oidc.userinfo=$OIDC_USERINFO" ]