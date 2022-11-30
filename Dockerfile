FROM openjdk:11

# install yq - a YAML query command line tool
RUN curl -Lso yq https://github.com/mikefarah/yq/releases/download/2.2.1/yq_linux_amd64 && \
    chmod +x yq && \
    mv yq /usr/local/bin


RUN apt-get update && apt-get install -y unzip

# NEW RELIC
#RUN mkdir -p /opt/newrelic
#RUN curl -O https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip && unzip newrelic-java.zip && cp -Rp ./newrelic/* /opt/newrelic/

#ENV JAVA_OPTS="$JAVA_OPTS -javaagent:/opt/newrelic/newrelic.jar"

#DataDog
#RUN wget -O dd-java-agent.jar "https://dtdg.co/latest-java-tracer"
#ENV JAVA_OPTS="$JAVA_OPTS -javaagent:/dd-java-agent.jar"

# Copy app files
COPY config.yml /opt/scanPay/
COPY target/scanPay-1.0-SNAPSHOT.jar /opt/scanPay/app.jar

# Appdynamics
RUN wget -O AppServerAgent.zip "$(curl 'https://download.appdynamics.com/download/custom/v1/' \
	  -H 'authority: download.appdynamics.com' \
	  -H 'accept: application/json, text/plain, */*' \
	  -H 'accept-language: en-US,en;q=0.9,pt-BR;q=0.8,pt;q=0.7,es-419;q=0.6,es;q=0.5' \
	  -H 'content-type: application/x-www-form-urlencoded' \
	  -H 'origin: https://econocombrasil.saas.appdynamics.com' \
	  -H 'referer: https://econocombrasil.saas.appdynamics.com/' \
	  -H 'sec-ch-ua: "Google Chrome";v="107", "Chromium";v="107", "Not=A?Brand";v="24"' \
	  -H 'sec-ch-ua-mobile: ?0' \
	  -H 'sec-ch-ua-platform: "macOS"' \
	  -H 'sec-fetch-dest: empty' \
	  -H 'sec-fetch-mode: cors' \
	  -H 'sec-fetch-site: same-site' \
	  -H 'user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36' \
	  --data-raw 'type=java-jdk8&account-access-key=z3wz73pivywb&controller-host=econocombrasil.saas.appdynamics.com&controller-port=443&controller-ssl-enabled=true&account=econocombrasil&timestamp=1667851003594&agent-version=22.8.1.0&auto_naming=false&application-name=srm-demo-app&tier-name=backend-java&node-name=srm-node' \
	  --compressed)";mkdir -p /appdy ;unzip AppServerAgent.zip -d /appdy/appagent;ls -ltra
#COPY AppServerAgent-4.5.0.23604.tar.gz  /opt/scanPay/AppServerAgent-4.5.0.23604.tar.gz

# Error Tracking
RUN mkdir /opt/harness-et-agent && cd /opt/harness-et-agent && wget -qO- https://get.et.harness.io/releases/latest/nix/harness-et-agent.tar.gz | tar -xzv
#COPY harness-et-agent /opt/harness-et-agent
#ENV JAVA_TOOL_OPTIONS="-agentpath:/opt/harness-et-agent/lib/libETAgent.so"

#ENV ET_COLLECTOR_URL=https://app.harness.io/gratis/et-collector
#ENV ET_APPLICATION_NAME=FF_CV_DEMO
#ENV ET_DEPLOYMENT_NAME=1
#ENV ET_ENV_ID=gitflow
#ENV ET_ACCOUNT_ID=Io9SR1H7TtGBq9LVyJVB2w
#ENV ET_ORG_ID=default
#ENV ET_PROJECT_ID=FF_GITFLOW_CV
#RUN wget -qO- https://get.et.harness.io/releases/latest/nix/harness-et-agent.tar.gz | tar -xz
#COPY newrelic-java-5.3.0.tar.gz /opt/scanPay/

WORKDIR /opt/scanPay


CMD bash -c ' \
    if [[ "$ENABLE_APPDYNAMICS" == "true" ]]; then \
      node_name="-Dappdynamics.agent.nodeName=$(hostname)"; \
      JAVA_OPTS=$JAVA_OPTS" -javaagent:/appdy/appagent/javaagent.jar -Dappdynamics.jvm.shutdown.mark.node.as.historical=false"; \
      JAVA_OPTS="$JAVA_OPTS $node_name"; \
      echo "Using Appdynamics java agent"; \
    fi; \
    \
    if [[ "$ENABLE_NEWRELIC" == "true" ]]; then \
      tar -zxvf newrelic-java-5.3.0.tar.gz; \
      JAVA_OPTS=$JAVA_OPTS" -javaagent:/opt/scanPay/newrelic/newrelic.jar"; \
    fi; \
    \
    CONFIG_FILE=/opt/scanPay/config.yml; \
    #if [[ "" != "$ALLOWED_ORIGINS" ]]; then yq write -i $CONFIG_FILE allowedOrigins "$ALLOWED_ORIGINS"; fi; \
    #if [[ "" != "$ELK_URL" ]]; then yq write -i $CONFIG_FILE elkUrl "$ELK_URL"; fi; \
    #if [[ "" != "$ELK_INDEX" ]]; then yq write -i $CONFIG_FILE elkIndex "$ELK_INDEX"; fi; \
    #if [[ "" != "$ELK_PASS" ]]; then yq write -i $CONFIG_FILE elkPass "$ELK_PASS"; fi; \
    #if [[ "" != "$FF_API_KEY" ]]; then yq write -i $CONFIG_FILE ffApiKey "$FF_API_KEY"; fi; \
    #if [[ "" != "$FF_METRIC_KEY" ]]; then yq write -i $CONFIG_FILE ffMetricKey "$FF_METRIC_KEY"; fi; \
    #if [[ "" != "$FF_LOG_KEY" ]]; then yq write -i $CONFIG_FILE ffLogKey "$FF_LOG_KEY"; fi; \
    #if [[ "" != "$FF_TARGET" ]]; then yq write -i $CONFIG_FILE target "$FF_TARGET"; fi; \
    #if [[ "" != "$LOG_MSG" ]]; then yq write -i $CONFIG_FILE defaultConfig.logConfig.errorMessage "LOG_MSG"; fi; \


    java $JAVA_TOOL_OPTIONS $JAVA_OPTS -XX:-UseTypeSpeculation -Xshare:off -jar app.jar server config.yml'
