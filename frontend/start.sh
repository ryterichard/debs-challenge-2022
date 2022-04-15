export KAFKA_HOSTNAME=$(ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{ print $2 }' | cut -f2 -d: | head -n1)
docker rm -vf $(docker-compose ps -a -q)
docker compose up -d
cd ./server/
npm ci
npm run server
