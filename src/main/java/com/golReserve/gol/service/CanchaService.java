package com.golReserve.gol.service;

import com.golReserve.gol.entity.Cancha;
import com.golReserve.gol.entity.Enums.EstadoCancha;
import com.golReserve.gol.entity.Enums.EstadoUsuario;
import com.golReserve.gol.entity.Enums.TipoCancha;
import com.golReserve.gol.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface CanchaService {

    //Registra una nueva cancha
    Cancha registrarCancha(Cancha cancha);

    //Lista las canchas
    List<Cancha> listarCanchas();

    //Buscar una cancha por id
    Optional<Cancha> buscarCanchaPorId(Long idCancha);

    //Busca cancha por tipo
    Optional<Cancha> buscarCanchaPorTipo(TipoCancha tipoCancha);

    //Actualiza información de una cancha
    Cancha ActualizarCancha(Long idCancha,Cancha cancha);

    //Elimina cancha de un establecimiento cambiando su estado a inactiva
    void eliminarCancha(Long idCancha, EstadoCancha estadoCancha);

    //Buscar canchas con filtros avanzados
    List<Cancha> buscarCanchasConFiltros(TipoCancha tipo, Double precioMin, Double precioMax,
                                          EstadoCancha estado, Long establecimientoId);
}
