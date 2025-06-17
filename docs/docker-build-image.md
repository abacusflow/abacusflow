docker login

docker tag abacusflow-web:0.0.1 brucewang5638/abacusflow-web:0.0.1
docker push brucewang5638/abacusflow-web:0.0.1

docker tag abacusflow-server:0.0.1 brucewang5638/abacusflow-server:0.0.1
docker push brucewang5638/abacusflow-server:0.0.1


docker compose -f docker-compose-pord.yml up -d
docker compose -f docker-compose-pord.yml down xxx

docker compose -f docker-compose-prod.yml pull
docker compose -f docker-compose-prod.yml up -d --remove-orphans

