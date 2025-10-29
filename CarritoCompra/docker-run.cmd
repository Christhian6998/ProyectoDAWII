docker build -t proyectodawii/carritocompra:1.0 -f CarritoCompra\Dockerfile .

docker run -d --name carritocompra --network proyectoDAWII -p 6006:6005 proyectodawii/carritocompra:1.0
