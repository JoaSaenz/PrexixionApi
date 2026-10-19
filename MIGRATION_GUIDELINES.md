# Directrices de Migración: Backend Prexixion API

Este documento establece los patrones y arquitecturas estándar a seguir durante el desarrollo y mantenimiento del backend (Prexixion API).

---

## 1. Backend - Controladores (Spring Boot)
- **RESTful API Limpia**: Los controladores deben exponer endpoints usando los verbos HTTP correspondientes (`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`).
- **Respuestas Estandarizadas**:
  - Operaciones de consulta / listados / DataTables: retornar `ResponseEntity<T>` o `ResponseEntity<[Modulo]DataTablesResponse>`.
  - Operaciones de mutación (Creación, Edición, Eliminación): retornar `ResponseEntity<ApiResponse<T>>`.
- **Verbos para Búsqueda/Filtro**: Las operaciones de listado que incluyen filtros usan `@GetMapping`. En caso de usar un objeto de Request, Spring Boot lo mapea automáticamente como `@ModelAttribute` a partir de los query params.
- **Objetos de Transferencia (DTOs)**: Se deben recibir y retornar DTOs específicos con sufijo `DTO` (ej. `[Modulo]DTO`, `[Modulo]DataTablesRequest`, `[Modulo]DataTablesResponse`). No usar sufijos obsoletos como `*Projection`.

---

## 2. Backend - Servicios (Lógica de Negocio)
- **Orquestación**: Los servicios actúan como puente entre el controlador y el repositorio.
- **Optimización de Consultas (Batch Fetching)**: Para evitar el problema de "N+1 consultas" al listar registros con sub-entidades (listas hijas), se deben extraer primero los IDs principales y luego hacer una única consulta para traer los datos relacionados, agrupándolos en memoria (usando `Map`) antes de devolver la respuesta.

---

## 3. Backend - Repositorios (Data Access Layer)

### Regla Arquitectónica: 90% JDBC / 10% JPA
- **Consultas Complejas, Reportes y Listados (90%)**: Usar clases `@Repository` con `NamedParameterJdbcTemplate` o `JdbcTemplate` mapeando a DTOs con lambdas.
- **CRUDs Simples 1-a-1 de Entidad (10%)**: Usar interfaces `JpaRepository<Entity, ID>` únicamente para operaciones básicas (`save`, `findById`, `delete`).
- **Prohibido**: No usar interfaces Spring Data JPA con `@Query(nativeQuery = true)` acopladas a entidades artificiales/dummy (ej. `ClienteExcelEntity`) ni interfaces de proyección con sufijo `Projection`.

### Patrones Clave en Repositorios
1. **Consultas Autocontenidas**: Cada método debe declarar su propia consulta SQL explícita y aislada. No usar métodos compartidos (`getBaseSelect()`) ni constantes de cadenas (`SELECT_BASE`).
2. **Mapeo Tipado mediante Lambdas (`RowMapper`)**: Extraer directamente del `ResultSet` (`rs.getString(...)`, `rs.getInt(...)`, etc.) construyendo DTOs vía `@Builder` de Lombok.
3. **Transaccionalidad (CUD)**: Las operaciones de creación, actualización y eliminación que involucren múltiples tablas deben usar la anotación `@Transactional`.

### Plantilla de Repositorio Estándar (Gold Standard)
```java
package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.dto.MiModuloDTO;
import com.joa.prexixionapi.dto.MiModuloRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MiModuloRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<MiModuloDTO> list(MiModuloRequest req) {
        String sql = """
            SELECT c.ruc, c.razonSocial, c.idEstado, ce.descripcion as estado
            FROM cliente c
            INNER JOIN clientsEstados ce ON c.idEstado = ce.id
            WHERE c.idEstado IN (:estados)
            ORDER BY c.razonSocial
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("estados", req.getEstados());

        return jdbcTemplate.query(sql, params, this::mapRow);
    }

    private MiModuloDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MiModuloDTO.builder()
                .ruc(rs.getString("ruc"))
                .razonSocial(rs.getString("razonSocial"))
                .idEstado(rs.getInt("idEstado"))
                .estado(rs.getString("estado"))
                .build();
    }
}
```

---

## 4. Exportaciones y Descargas (Excel/PDF)
- **Generación en el API**: Los endpoints de exportación (ej. `/excel`) NO deben retornar listas de objetos JSON. Deben delegar a un servicio (ej. `[Modulo]ExcelService`) que genere el archivo binario (`byte[]`) utilizando `Apache POI` (y las utilidades base como `ExcelStyleManager`), retornando el archivo con las cabeceras HTTP correspondientes (`Content-Disposition: attachment; filename=...` y el tipo MIME).
