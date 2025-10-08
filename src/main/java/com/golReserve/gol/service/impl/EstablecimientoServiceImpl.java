package com.golReserve.gol.service.impl;

import com.golReserve.gol.entity.Establecimiento;
import com.golReserve.gol.repository.EstablecimientoRepository;
import com.golReserve.gol.service.EstablecimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstablecimientoServiceImpl implements EstablecimientoService {

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Override
    public Establecimiento registrarEstablecimiento(Establecimiento establecimiento) {
        return establecimientoRepository.save(establecimiento);
    }

    @Override
    public List<Establecimiento> listarEstablecimientos() {
        return establecimientoRepository.findAll();
    }

    @Override
    public Optional<Establecimiento> buscarEstablecimientoPorId(Long id) {
        return establecimientoRepository.findById(id);
    }

    @Override
    public Optional<Establecimiento> buscarEstablecimientoPorNombre(String nombre) {
        return establecimientoRepository.findByNombre(nombre);
    }

    @Override
    public Establecimiento actualizarEstablecimiento(Long id, Establecimiento establecimiento) {
        Establecimiento existente = establecimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento con ID " + id + " no encontrado"));
        existente.setNombre(establecimiento.getNombre());
        existente.setDireccion(establecimiento.getDireccion());
        return establecimientoRepository.save(existente);
    }

    @Override
    public void eliminarEstablecimiento(Long id) {
        establecimientoRepository.deleteById(id);
    }
}
