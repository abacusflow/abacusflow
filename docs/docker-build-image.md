docker compose -f docker-compose-prod.yml down

docker volume rm abacusflow_postgres_data

docker compose -f docker-compose-prod.yml pull
docker compose -f docker-compose-prod.yml up -d --remove-orphans

docker compose  up -d