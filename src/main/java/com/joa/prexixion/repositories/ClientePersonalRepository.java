package com.joa.prexixion.repositories;

import com.joa.prexixion.dto.ClientePersonalProjection;
import com.joa.prexixion.entities.ClienteExcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientePersonalRepository extends JpaRepository<ClienteExcelEntity, String> {

    @Query(nativeQuery = true, value = """
        SELECT
            c.idGrupoEconomico,
            ge.descripcion AS descGrupoEconomico,
            c.idEstado,
            ce.descripcion AS descEstado,
            c.ruc, c.y, c.razonSocial,
            cp.plIdTipoCoordinacionContable,
            t.descripcion AS descTipoCC,
            cp.plDni, cp.plApellido, cp.plNombre,
            cp.plIdPuesto,
            p.descripcion AS descPuesto,
            cp.plTelefono, cp.plCorreo, cp.plFNacimiento,
            cp.plTieneClaveSol, cp.plAdministracion
        FROM cliente c
        LEFT JOIN gruposEconomicos ge ON c.idGrupoEconomico = ge.id
        LEFT JOIN clientsEstados ce ON c.idEstado = ce.id
        LEFT JOIN clientePersonal cp ON c.ruc = cp.idCliente
        LEFT JOIN tipoCoordinacionContable t ON cp.plIdTipoCoordinacionContable = t.id
        LEFT JOIN puestos p ON cp.plIdPuesto = p.id
        WHERE ce.id IN (:estados) AND c.y IN (:grupos)
        ORDER BY c.y
        """)
    List<ClientePersonalProjection> getPersonalData(
            @Param("estados") List<Integer> estados,
            @Param("grupos") List<Integer> grupos
    );
}
