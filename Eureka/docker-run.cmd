REM --- Construcción de la imagen Docker ---
docker build -t proyectoDAWII/eureka:1.0 -f Eureka/Dockerfile .

REM --- Ejecución del contenedor ---
docker run -d --name eureka --network proyectoDAWII -p 8762:8761 proyectoDAWII/eureka:1.0
