package com.golReserve.gol.service.impl;

import com.golReserve.gol.entity.Cancha;
import com.golReserve.gol.entity.Enums.EstadoCancha;
import com.golReserve.gol.entity.Enums.TipoCancha;
import com.golReserve.gol.repository.CanchaRepository;
import com.golReserve.gol.service.CanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CanchaServiceImpl implements CanchaService {

    @Autowired
    private CanchaRepository canchaRepository;

    @Override
    public Cancha registrarCancha(Cancha cancha) { return canchaRepository.save(cancha); }

    @Override
    public List<Cancha> listarCanchas() { return canchaRepository.findAll(); }

    @Override
    public Optional<Cancha> buscarCanchaPorId(Long idCancha) { return canchaRepository.findById(idCancha); }

    @Override
    public Optional<Cancha> buscarCanchaPorTipo(TipoCancha tipoCancha) { return canchaRepository.findByTipoCancha(tipoCancha);}

    @Override
    public Cancha ActualizarCancha(Long idCancha, Cancha cancha) {
        Cancha canchaExistente = canchaRepository.findById(idCancha)
                .orElseThrow(() -> new RuntimeException("Cancha con ID" + idCancha + "No encontrado"));
        canchaExistente.setPrecioHora(cancha.getPrecioHora());
        canchaExistente.setTipoCancha(cancha.getTipoCancha());

        return canchaRepository.save(canchaExistente);
    }

    @Override
    public void eliminarCancha(Long idCancha, EstadoCancha nuevoEstadoCancha) {
        Cancha canchaExistenteEliminar = canchaRepository.findById(idCancha)
                .orElseThrow(() -> new RuntimeException("Cancha con ID" + idCancha + "No encontrado"));
        canchaExistenteEliminar.setEstadoCancha(nuevoEstadoCancha);
        canchaRepository.save(canchaExistenteEliminar);
    }

}
