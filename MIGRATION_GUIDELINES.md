# Directrices de Migración: Prexixion WebApp a Prexixion API

Este documento establece los patrones y arquitecturas a seguir durante la migración de los módulos de la aplicación web antigua (Prexixion) hacia el nuevo backend (Prexixion API).

## 1. Frontend (Javascript / JSP)
- **DataTables sin Server-Side**: La configuración de DataTables debe usar `serverSide: false` y `paging: false`. Toda la lógica de paginación, ordenación y búsqueda se maneja del lado del cliente.
- **Rutas y Autenticación**: Las llamadas AJAX deben apuntar a la base URL del API (`api-proxy`).
- **Seguridad**: Es indispensable inyectar el token JWT en las cabeceras de todas las peticiones AJAX (`Authorization: Bearer <token>`).

## 2. Backend - Controladores (Spring Boot)
- **RESTful API Limpia**: Los controladores deben exponer endpoints usando los verbos HTTP correspondientes (`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`).
- **Objetos de Transferencia (DTOs)**: Se deben recibir y retornar DTOs específicos en lugar de entidades directas (ej. `[Modulo]DTO`, `[Modulo]DataTablesRequest`, `[Modulo]DataTablesResponse`).

## 3. Backend - Servicios (Lógica de Negocio)
- **Orquestación**: Los servicios actúan como puente entre el controlador y el repositorio.
- **Optimización de Consultas (Batch Fetching)**: Para evitar el problema de "N+1 consultas" al listar registros con sub-entidades (listas hijas), se deben extraer primero los IDs principales y luego hacer una única consulta para traer los datos relacionados, agrupándolos en memoria (usando `Map`) antes de devolver la respuesta.

## 4. Backend - Repositorios (Data Access)
- **SQL Nativo**: Se debe utilizar `NamedParameterJdbcTemplate` para las operaciones en base de datos, priorizando el uso de SQL puro, claro y estructurado (en lugar de ORMs pesados como JPA/Hibernate).
- **Transaccionalidad (CUD)**: Las operaciones de creación, actualización y eliminación que involucren múltiples tablas (padre e hijo) deben usar la anotación `@Transactional`.
- **Manejo de Sub-entidades en Updates**: El patrón a seguir en las actualizaciones complejas es eliminar primero los registros hijos asociados (`DELETE FROM tabla_hija WHERE idPadre = :id`) y volver a insertarlos con la nueva información, manteniendo así la consistencia dentro de la transacción.
