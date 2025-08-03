docker pull redis:alpine
docker pull postgres:13-alpine
docker pull quay.io/keycloak/keycloak:24.0.1
docker pull grafana/loki:main
docker pull prom/prometheus:v2.46.0
docker pull grafana/tempo:2.2.2
docker pull grafana/grafana:10.1.0

docker pull timescale/timescaledb:latest-pg15

docker pull confluentinc/cp-zookeeper:7.5.0
docker pull confluentinc/cp-kafka:7.5.0
docker pull provectuslabs/kafka-ui:latest

docker pull reksaalamsyah/api-gateway:latest
docker pull reksaalamsyah/auth-service:latest
docker pull reksaalamsyah/customer-service:latest
docker pull reksaalamsyah/marketdata-service:latest
docker pull reksaalamsyah/marketdata-init-image:latest
docker pull reksaalamsyah/marketrealtime-service:latest
docker pull reksaalamsyah/notification-service:latest
docker pull reksaalamsyah/topup-service:latest
docker pull reksaalamsyah/trade-service:latest
docker pull reksaalamsyah/tradeprocessor-service:latest

kind load docker-image -n learntrad-microservices redis:alpine
kind load docker-image -n learntrad-microservices postgres:13-alpine
kind load docker-image -n learntrad-microservices quay.io/keycloak/keycloak:24.0.1
kind load docker-image -n learntrad-microservices grafana/loki:main
kind load docker-image -n learntrad-microservices prom/prometheus:v2.46.0
kind load docker-image -n learntrad-microservices grafana/tempo:2.2.2
kind load docker-image -n learntrad-microservices grafana/grafana:10.1.0

kind load docker-image -n learntrad-microservices timescale/timescaledb:latest-pg15

kind load docker-image -n learntrad-microservices confluentinc/cp-zookeeper:7.5.0
kind load docker-image -n learntrad-microservices confluentinc/cp-kafka:7.5.0
kind load docker-image -n learntrad-microservices provectuslabs/kafka-ui:latest

kind load docker-image -n learntrad-microservices reksaalamsyah/api-gateway:latest
kind load docker-image -n learntrad-microservices reksaalamsyah/auth-service:latest
kind load docker-image -n learntrad-microservices reksaalamsyah/customer-service:latest
kind load docker-image -n learntrad-microservices reksaalamsyah/marketdata-service:latest
kind load docker-image -n learntrad-microservices reksaalamsyah/marketdata-init-image:latest
kind load docker-image -n learntrad-microservices reksaalamsyah/marketrealtime-service:latest
kind load docker-image -n learntrad-microservices reksaalamsyah/notification-service:latest
kind load docker-image -n learntrad-microservices reksaalamsyah/topup-service:latest
kind load docker-image -n learntrad-microservices reksaalamsyah/trade-service:latest
kind load docker-image -n learntrad-microservices reksaalamsyah/tradeprocessor-service:latest