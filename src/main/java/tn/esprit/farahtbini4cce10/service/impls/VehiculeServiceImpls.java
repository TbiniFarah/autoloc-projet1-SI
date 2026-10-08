
package tn.esprit.farahtbini4cce10.service.impls;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.farahtbini4cce10.domain.Vehicule;
import tn.esprit.farahtbini4cce10.repository.IVehiculeRepository;
import tn.esprit.farahtbini4cce10.service.IVehiculeServices;

import java.util.List;
@Service
@RequiredArgsConstructor
class VehiculeServicesImlp implements IVehiculeServices {


    private final IVehiculeRepository vehiculeRepository;



    @Override
    public Vehicule create(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule findById(long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable avec l'id : " + id));
    }

    @Override
    public List<Vehicule> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(long id) {
        vehiculeRepository.deleteById(id);
    }

    @Override
    public Vehicule update(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }
}
