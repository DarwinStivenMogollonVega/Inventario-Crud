package com.pragma.Inventario.producto.infrastructure.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.pragma.Inventario.producto.application.ports.out.ProductoRepositoryPort;
import com.pragma.Inventario.producto.domain.model.Producto;
import com.pragma.Inventario.shared.audit.AuditService;

@Component
public class JpaProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final SpringDataProductoRepository springDataProductoRepository;
    private final AuditService auditService;

    public JpaProductoRepositoryAdapter(SpringDataProductoRepository springDataProductoRepository, AuditService auditService) {
        this.springDataProductoRepository = springDataProductoRepository;
        this.auditService = auditService;
    }

    @Override
    public List<Producto> findAllOrderedByName() {
        return springDataProductoRepository.findAll(Sort.by(Sort.Direction.ASC, "nombre"))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return springDataProductoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Producto save(Producto producto) {
        boolean isCreate = producto.getId() == null;
        ProductoEntity savedEntity = springDataProductoRepository.save(toEntity(producto));
        // record audit
        String details = String.format("nombre=%s,cantidad=%d,precio=%s,categoria=%s",
                savedEntity.getNombre(), savedEntity.getCantidad(), savedEntity.getPrecio(), savedEntity.getCategoria());
        auditService.record("Producto", savedEntity.getId(), isCreate ? "CREATE" : "UPDATE", details);
        return toDomain(savedEntity);
    }

    @Override
    public void delete(Producto producto) {
        ProductoEntity entity = toEntity(producto);
        springDataProductoRepository.delete(entity);
        String details = String.format("nombre=%s,cantidad=%d,precio=%s,categoria=%s",
                entity.getNombre(), entity.getCantidad(), entity.getPrecio(), entity.getCategoria());
        auditService.record("Producto", producto.getId(), "DELETE", details);
    }

    private Producto toDomain(ProductoEntity entity) {
        return new Producto(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getCantidad(),
                entity.getPrecio(),
                entity.getCategoria());
    }

    private ProductoEntity toEntity(Producto producto) {
        ProductoEntity entity = new ProductoEntity();
        entity.setId(producto.getId());
        entity.setNombre(producto.getNombre());
        entity.setDescripcion(producto.getDescripcion());
        entity.setCantidad(producto.getCantidad());
        entity.setPrecio(producto.getPrecio());
        entity.setCategoria(producto.getCategoria());
        return entity;
    }
}
