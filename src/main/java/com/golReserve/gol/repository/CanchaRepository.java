package com.golReserve.gol.repository;

import com.golReserve.gol.entity.Cancha;
import com.golReserve.gol.entity.Enums.EstadoCancha;
import com.golReserve.gol.entity.Enums.TipoCancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CanchaRepository extends JpaRepository<Cancha, Long> {

    Optional<Cancha> findById(Long id);
    Optional<Cancha> findByTipoCancha(TipoCancha tipoCancha);
    Optional<Cancha> findByPrecioHora(Double precioHora);
    List<Cancha> findByEstadoCancha(EstadoCancha estadoCancha);
    List<Cancha> findByEstablecimiento_Id(Long establecimientoId);

    // Método para contar canchas activas por establecimiento
    @Query("SELECT COUNT(c) FROM Cancha c WHERE c.establecimiento.id = :idEstablecimiento AND c.estadoCancha = :estado")
    int countByEstablecimientoIdAndEstadoCancha(@Param("idEstablecimiento") Long idEstablecimiento, @Param("estado") EstadoCancha estado);

}
