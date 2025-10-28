package org.cibertec.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.cibertec.dto.ProductoRequestDTO;
import org.cibertec.dto.ProductoResponseDTO;
import org.cibertec.entity.Producto;
import org.cibertec.mapper.ProductoMapper;
import org.cibertec.repository.IProductoRepository;
import org.cibertec.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private IProductoRepository productoRepository;
    private IUsuarioRepository usuarioRepository;

    private final ProductoMapper productoMapper;
    private final KafkaPublisherService kafkaPublisherService;

    @Autowired
    public ProductoService(IProductoRepository productoRepository,
                           IUsuarioRepository usuarioRepository,
                           ProductoMapper productoMapper,
                           KafkaPublisherService kafkaPublisherService) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoMapper = productoMapper;
        this.kafkaPublisherService = kafkaPublisherService;
    }

    @CircuitBreaker(name = "productoGeneral", fallbackMethod = "fallbackGuardar")
    @Retry(name = "productoGeneral")
    public ResponseEntity<ProductoResponseDTO> guardar(ProductoRequestDTO productoRequestDTO, MultipartFile imagen) {
        if (!usuarioRepository.existsById(productoRequestDTO.getIdUsuario())) {
            return ResponseEntity.notFound().build();
        }

        Producto productoEntidad = productoMapper.toEntitySaved(productoRequestDTO);

        try {
            if (imagen == null || imagen.isEmpty())
                productoEntidad.setImg(null);
            else {
                productoEntidad.setImg(imagen.getBytes());
            }
            productoRepository.save(productoEntidad);
            kafkaPublisherService.send(productoRequestDTO);

        } catch (Exception e) {
            System.out.println("Hubo un error al obtener el archivo de imagen: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(productoMapper.toDto(productoEntidad));
    }

    @CircuitBreaker(name = "productoGeneral", fallbackMethod = "fallbackBuscarProductoPorId")
    @Retry(name = "productoGeneral")
    public ResponseEntity<ProductoResponseDTO> buscarProductoPorID(int id) {
        return ResponseEntity.ok(productoRepository.findById(id)
                .map(productoMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("No existe el producto con el id: " + id)));
    }

    @CircuitBreaker(name = "productoList", fallbackMethod = "fallbackObtenerProductos")
    @Retry(name = "productoList")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductos() {
        return ResponseEntity.ok(productoRepository.findAll()
                .stream()
                .map(productoMapper::toDto)
                .toList());
    }

    @CircuitBreaker(name = "productoList", fallbackMethod = "fallbackBuscarProductoPorNombre")
    @Retry(name = "productoList")
    public ResponseEntity<List<ProductoResponseDTO>> buscarProductoPorNombre(String nombre) {
        Optional<List<Producto>> listaProductos = productoRepository.findAllByNombreIgnoreCaseContaining(nombre);
        return listaProductos.map(productos -> ResponseEntity.ok(productos
                .stream()
                .map(productoMapper::toDto)
                .toList())).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @CircuitBreaker(name = "productoList", fallbackMethod = "fallbackObtenerProductosPorVeterinario")
    @Retry(name = "productoList")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductosPorVeterinario(Long idVeterinario) {
        Optional<List<Producto>> listaProductos = productoRepository.findAllByUsuario_IdUsuario(idVeterinario);
        return listaProductos.map(productos -> ResponseEntity.ok(productos
                .stream()
                .map(productoMapper::toDto)
                .toList())).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @CircuitBreaker(name = "productoGeneral", fallbackMethod = "fallbackActualizarProductoPorId")
    @Retry(name = "productoGeneral")
    public ResponseEntity<String> actualizarProductoPorId(int id,
                                                          ProductoRequestDTO producto,
                                                          MultipartFile imagen) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            Producto productoHelper = productoRepository.findById(id).get();

            Producto productoEntidad = productoMapper.toEntitySaved(producto);
            productoEntidad.setIdProducto(id);

            if (imagen != null)
                productoEntidad.setImg(imagen.getBytes());
            else {
                productoEntidad.setImg(productoHelper.getImg());
            }

            productoRepository.save(productoEntidad);
            return ResponseEntity.ok("Producto con ID : " + productoEntidad.getIdProducto() + "actualizado con exito");

        } catch (IOException e) {
            System.out.println("Hubo un error al actualizar el archivo de imagen: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @CircuitBreaker(name = "productoGeneral", fallbackMethod = "fallbackRestarStockProducto")
    @Retry(name = "productoGeneral")
    public ResponseEntity<String> restarStockProducto(int idProducto,
                                                      int stock) {
        Producto producto = productoRepository.findById(idProducto).get();
        producto.setStock(producto.getStock() - stock);
        if (producto.getStock() < 0) {
            return ResponseEntity.badRequest().body("No hay suficiente stock del producto");
        }
        productoRepository.save(producto);
        return ResponseEntity.ok(
                "Stock del Producto con ID : " +
                        producto.getIdProducto() +
                        " actualizado con exito");
    }

    @CircuitBreaker(name = "productoGeneral", fallbackMethod = "fallbackEliminarProductoPorId")
    @Retry(name = "productoGeneral")
    public ResponseEntity<String> eliminarProductoPorId(int id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return ResponseEntity.ok("Producto eliminado con exito");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CircuitBreaker(name = "productoGeneral", fallbackMethod = "fallbackObtenerImagen")
    @Retry(name = "productoGeneral")
    public ResponseEntity<byte[]> obtenerImagen(int id) {
        Optional<Producto> producto = productoRepository.findById(id);

        if (producto.isEmpty() || producto.get().getImg() == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().
                    header("Content-Type", "image/jpeg").
                    body(producto.get().getImg());
        }

    }

    public ResponseEntity<ProductoResponseDTO> fallbackGuardar(ProductoRequestDTO productoRequestDTO,
                                                               MultipartFile imagen,
                                                               Throwable throwable) {
        System.out.println("FALLBACK: Error al guardar el producto: " + throwable.getMessage());
        return ResponseEntity.badRequest().body(new ProductoResponseDTO());
    }

    public ResponseEntity<ProductoResponseDTO> fallbackBuscarProductoPorId(int id, Throwable throwable) {
        System.out.println("FALLBACK: Error al obtener el producto con id: " + id + " " + throwable.getMessage());
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<List<ProductoResponseDTO>> fallbackObtenerProductos(Throwable throwable) {
        ProductoResponseDTO productoResponseDTO = new ProductoResponseDTO(
                0,
                0.0,
                0,
                "ERROR",
                "No se pudo obtener la lista de productos, intentelo de nuevo mas tarde",
                Long.valueOf("0"),
                "",
                "",
                "",
                "",
                ""
        );
        System.out.println("FALLBACK: Error al obtener la lista de productos: " + throwable.getMessage());
        return ResponseEntity.ok(List.of(productoResponseDTO));
    }

    public ResponseEntity<List<ProductoResponseDTO>> fallbackBuscarProductoPorNombre(String nombre,
                                                                                     Throwable throwable) {
        ProductoResponseDTO productoResponseDTO = new ProductoResponseDTO(
                0,
                0.0,
                0,
                "ERROR",
                "No se pudo buscar en la lista de productos, intentelo de nuevo mas tarde",
                Long.valueOf("0"),
                "",
                "",
                "",
                "",
                ""
        );
        System.out.println("FALLBACK: Error al intentar buscar la lista de productos: " + throwable.getMessage());
        return ResponseEntity.ok(List.of(productoResponseDTO));
    }

    public ResponseEntity<List<ProductoResponseDTO>> fallbackObtenerProductosPorVeterinario(Long idVeterinario,
                                                                                            Throwable throwable) {
        ProductoResponseDTO productoResponseDTO = new ProductoResponseDTO(
                0,
                0.0,
                0,
                "ERROR",
                "No se pudo obtener en la lista de productos del veterinario, intentelo de nuevo mas tarde",
                Long.valueOf("0"),
                "",
                "",
                "",
                "",
                ""
        );
        System.out.println("Fallback: Error al intentar obtener la lista de productos del Veterinario con ID : " + idVeterinario + throwable.getMessage());
        return ResponseEntity.ok(List.of(productoResponseDTO));
    }

    public ResponseEntity<String> fallbackActualizarProductoPorId(int id,
                                                                  ProductoRequestDTO producto,
                                                                  MultipartFile imagen,
                                                                  Throwable throwable) {
        System.out.println("FALLBACK: Error al actualizar el producto con ID : " + id + " " + throwable.getMessage());
        return ResponseEntity.badRequest().body("Ocurrio un error al intentar actualizar el producto, intentelo de nuevo mas tarde");
    }

    public ResponseEntity<String> fallbackRestarStockProducto(int idProducto,
                                                              int stock,
                                                              Throwable throwable) {
        System.out.println("FALLBACK: Error al restar " + stock + " del stock del Producto con ID : " +
                idProducto + " " + throwable.getMessage());
        return ResponseEntity.badRequest().body("Ocurrio un error al actualizar el stock del producto, intentelo de nuevo mas tarde");
    }

    public ResponseEntity<String> fallbackEliminarProductoPorId(int id,
                                                                Throwable throwable) {
        System.out.println("FALLBACK: Error al eliminar el producto con ID : " +
                id + " " + throwable.getMessage());
        return ResponseEntity.badRequest().body("Ocurrio un error al intentar eliminar el producto, intentelo de nuevo mas tarde");
    }

    public ResponseEntity<byte[]> fallbackObtenerImagen(int id,
                                                        Throwable throwable) {
        System.out.println("FALLBACK: Error al obtener la imagen del producto con ID : " +
                id + " " + throwable.getMessage());
        return ResponseEntity.notFound().build();
    }

}