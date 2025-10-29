REM crea el volumen
docker volume create mysql-data

REM Crear el network
docker network create proyectoDAWII

REM Creamos un contenedor desde la imagen oficial de MySQL
docker run -d --name mysql-proyectoDAWII --network proyectoDAWII -p 3307:3306 -v mysql-data:/var/lib/mysql -v .\Docker\docker-files\util\script\script.sql:/docker-entrypoint-initdb.d/init.sql:ro -e MYSQL_ROOT_PASSWORD=sql mysql:latest