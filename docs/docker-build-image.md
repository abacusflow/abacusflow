docker login

docker tag abacusflow-website:latest brucewang5638/abacusflow-website:latest
docker push brucewang5638/abacusflow-website:latest

docker tag abacusflow-webapp:0.0.1 brucewang5638/abacusflow-webapp:0.0.1
docker push brucewang5638/abacusflow-webapp:0.0.1

docker tag abacusflow-server:0.0.1 brucewang5638/abacusflow-server:0.0.1
docker push brucewang5638/abacusflow-server:0.0.1

docker compose -f docker-compose-prod.yml down
docker compose -f docker-compose-prod.yml down xxx xxx

docker volume rm abacusflow_postgres_data

docker compose -f docker-compose-prod.yml pull
docker compose -f docker-compose-prod.yml up -d --remove-orphans

docker compose  up -d

docker build -f ci.Dockerfile -t abacusflow .
docker push ghcr.io/abacusflow/abacusflow-ci:latest