-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: vetmarket
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `carrito`
--

CREATE DATABASE IF NOT EXISTS vetmarket;
use vetmarket;

DROP TABLE IF EXISTS `carrito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrito` (
  `id_carrito` int NOT NULL AUTO_INCREMENT,
  `cantidad` int DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `id_producto` int DEFAULT NULL,
  `id_usuario` bigint DEFAULT NULL,
  `nombre_producto` varchar(255) DEFAULT NULL,
  `precio` double DEFAULT NULL,
  `total` double DEFAULT NULL,
  PRIMARY KEY (`id_carrito`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrito`
--

LOCK TABLES `carrito` WRITE;
/*!40000 ALTER TABLE `carrito` DISABLE KEYS */;
/*!40000 ALTER TABLE `carrito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_orden`
--

DROP TABLE IF EXISTS `detalle_orden`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_orden` (
  `tipo_detalle` varchar(31) NOT NULL,
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `comision` double NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `precio` double NOT NULL,
  `total` double NOT NULL,
  `cantidad` int DEFAULT NULL,
  `fecha_cita` datetime(6) DEFAULT NULL,
  `orden_numero_orden` int DEFAULT NULL,
  `producto_id_producto` int DEFAULT NULL,
  `mascota_id_mascota` bigint DEFAULT NULL,
  `servicio_id_servicio` int DEFAULT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `FKsi19og7hi4cx5bpivk44gfnux` (`orden_numero_orden`),
  KEY `FKruye0wtgosd3gp2mkybr23tkk` (`producto_id_producto`),
  KEY `FK14ugn6m6enx2u6vgdsbf1v3qk` (`mascota_id_mascota`),
  KEY `FKqjh5jl3yp583qv1yuntrfh1j0` (`servicio_id_servicio`),
  CONSTRAINT `FK14ugn6m6enx2u6vgdsbf1v3qk` FOREIGN KEY (`mascota_id_mascota`) REFERENCES `mascotas` (`id_mascota`),
  CONSTRAINT `FKqjh5jl3yp583qv1yuntrfh1j0` FOREIGN KEY (`servicio_id_servicio`) REFERENCES `servicio` (`id_servicio`),
  CONSTRAINT `FKruye0wtgosd3gp2mkybr23tkk` FOREIGN KEY (`producto_id_producto`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `FKsi19og7hi4cx5bpivk44gfnux` FOREIGN KEY (`orden_numero_orden`) REFERENCES `orden` (`numero_orden`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_orden`
--

LOCK TABLES `detalle_orden` WRITE;
/*!40000 ALTER TABLE `detalle_orden` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalle_orden` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mascotas`
--

DROP TABLE IF EXISTS `mascotas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mascotas` (
  `id_mascota` bigint NOT NULL AUTO_INCREMENT,
  `edad` int DEFAULT NULL,
  `especie` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `peso` double DEFAULT NULL,
  `raza` varchar(255) DEFAULT NULL,
  `usuario_id` bigint NOT NULL,
  PRIMARY KEY (`id_mascota`),
  KEY `FKsw9lt998nfwh4x6jdti0nurme` (`usuario_id`),
  CONSTRAINT `FKsw9lt998nfwh4x6jdti0nurme` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mascotas`
--

LOCK TABLES `mascotas` WRITE;
/*!40000 ALTER TABLE `mascotas` DISABLE KEYS */;
INSERT INTO `mascotas` VALUES (1,3,'Perro','Firulais',12.3,'Labrador',3),(2,2,'Gato','Mishi',4,'Siames',3);
/*!40000 ALTER TABLE `mascotas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orden`
--

DROP TABLE IF EXISTS `orden`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orden` (
  `numero_orden` int NOT NULL AUTO_INCREMENT,
  `comision_total` double NOT NULL,
  `fecha` datetime(6) DEFAULT NULL,
  `total` double NOT NULL,
  `usuario_id_usuario` bigint DEFAULT NULL,
  PRIMARY KEY (`numero_orden`),
  KEY `FKcoqegn1fly11c2bfr8q70bele` (`usuario_id_usuario`),
  CONSTRAINT `FKcoqegn1fly11c2bfr8q70bele` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orden`
--

LOCK TABLES `orden` WRITE;
/*!40000 ALTER TABLE `orden` DISABLE KEYS */;
/*!40000 ALTER TABLE `orden` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  `img` longblob,
  `nombre` varchar(255) DEFAULT NULL,
  `precio` double DEFAULT NULL,
  `stock` int DEFAULT NULL,
  `usuario_id_usuario` bigint DEFAULT NULL,
  PRIMARY KEY (`id_producto`),
  KEY `FKdsamcuwk0lwat0k98l6exwiu0` (`usuario_id_usuario`),
  CONSTRAINT `FKdsamcuwk0lwat0k98l6exwiu0` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'Alimento seco premium para perros',NULL,'Dog Chow Adulto',85.5,100,2),(2,'Juguete de goma resistente para masticar',NULL,'Hueso de Goma',15,100,2),(3,'Collar ajustable para gatos',NULL,'Collar Safety Break',22,100,5),(4,'Vitaminas para articulaciones',NULL,'VetriFlex Suplemento',45.9,100,5),(5,'Shampoo hipoalergÃ©nico',NULL,'Shampoo Hypo+',35,100,2);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id_rol` bigint NOT NULL AUTO_INCREMENT,
  `nombre_rol` varchar(255) NOT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `UKl0qdsam7tunbtmxcmeeyfcifk` (`nombre_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES (1,'ADMIN'),(3,'CLIENTE'),(2,'VETERINARIO');
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicio`
--

DROP TABLE IF EXISTS `servicio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicio` (
  `id_servicio` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  `img` longblob,
  `nombre` varchar(255) DEFAULT NULL,
  `precio` double NOT NULL,
  `usuario_id_usuario` bigint DEFAULT NULL,
  PRIMARY KEY (`id_servicio`),
  KEY `FK7lga4r87kvvr29rylp959oqld` (`usuario_id_usuario`),
  CONSTRAINT `FK7lga4r87kvvr29rylp959oqld` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicio`
--

LOCK TABLES `servicio` WRITE;
/*!40000 ALTER TABLE `servicio` DISABLE KEYS */;
INSERT INTO `servicio` VALUES (1,'Atendemos a tu mascota con una revisión completa para evaluar su estado de salud, detectar posibles enfermedades y brindarte las mejores recomendaciones para su cuidado y bienestar.',NULL,'Consulta General',60,2),(2,'Protege la salud de tu mascota y de tu familia con la vacuna antirrábica. Aplicamos dosis seguras y certificadas para prevenir esta enfermedad viral mortal.',NULL,'Vacuna AntirrÃ¡bica',45,5),(3,'Mantén a tu mascota limpia, fresca y con un aspecto saludable. Ofrecemos cortes de pelo según la raza y un baño con productos especializados que cuidan su piel y pelaje.',NULL,'Corte de Pelo y BaÃ±o',80,2),(4,'Realizamos análisis clínicos completos para diagnosticar enfermedades, controlar tratamientos y monitorear la salud general de tu mascota con resultados confiables.',NULL,'AnÃ¡lisis de Sangre Completo',120,5),(5,'Cuidamos la higiene bucal de tu mascota con limpiezas dentales profesionales que previenen el sarro, el mal aliento y enfermedades periodontales.',NULL,'Limpieza Dental',150,2);
/*!40000 ALTER TABLE `servicio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `apellido` varchar(255) DEFAULT NULL,
  `correo` varchar(255) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `numero_documento` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `ruc` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `id_rol` bigint DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `UK863n1y3x0jalatoir4325ehal` (`username`),
  KEY `FKmyv3138vvci6kaq3y5kt4cntu` (`id_rol`),
  CONSTRAINT `FKmyv3138vvci6kaq3y5kt4cntu` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'GarcÃ­a','admin@mail.com','Av. Central 100','Laura','70000000','$2a$10$NfDi.G1mLK/bqkgpu6j9seBPl2sGCk903c7wlf0YC814/eyhfmceW','123456789','900000001','admin',1),(2,'PÃ©rez','vet@mail.com','Calle Vet 20','Carlos','71111111','$2a$10$MIEpsKvT8OhAWD25cU4pI.nGlq8E2i6MAyTHBuZi3M10YhMoMvFHa','20123456789','900000002','veterinario',2),(3,'Rojas','cliente@mail.com','Jr. Lima 30','SofÃ­a','72222222','$2a$10$4CHXlkd9aUzjEJNxChlU9OjN3HhPWAfSt7kS298p/sszAMiDdIo3O',NULL,'900000003','cliente',3),(4,'Perez','carlos@mail.com','Av. Siempre Viva 123','Carlos','12345678','$2a$10$e4VA1jlYKBW4WxFBOkrU/uP1TSoOdRO0rzrQav6qAeE6PqSV9PBOm','','987654321','carlosp',3),(5,'Gonzales','manuel@mail.com','Av. Siempre Viva 123','Manuel','12345679','$2a$10$iGOhRq0xryEcggo4uyJH9Oc3xKR.1vE85K.QXVht3TNT0k2qCDZ96','10235145265','987654321','manuelg',2);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-28  2:50:13
