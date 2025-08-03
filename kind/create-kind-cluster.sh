echo "===Starting Kind Cluster==="

kind create cluster --name learntrad-microservices --config kind-config.yaml

echo "Loading Docker Images into Kind Cluster"

chmod +x ./kind-load.sh
./kind-load.sh

echo "Docker Images Loaded into Kind Cluster"

echo "===Kind Cluster Started==="