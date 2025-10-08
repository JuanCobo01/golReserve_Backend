package com.golReserve.gol.repository;

import com.golReserve.gol.entity.Cancha;
import com.golReserve.gol.entity.Enums.TipoCancha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CanchaRepository extends JpaRepository<Cancha, Long> {

    Optional<Cancha> findById(Long id);
    Optional<Cancha> findByTipoCancha(TipoCancha tipoCancha);
    Optional<Cancha> findByPrecioHora(Double precioHora);

}
