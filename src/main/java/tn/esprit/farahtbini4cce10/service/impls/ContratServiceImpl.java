package tn.esprit.farahtbini4cce10.service.impls;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.farahtbini4cce10.domain.Contrat;
import tn.esprit.farahtbini4cce10.domain.Paiement;
import tn.esprit.farahtbini4cce10.exception.ResourceNotFoundException;
import tn.esprit.farahtbini4cce10.repository.IContratRepository;
import tn.esprit.farahtbini4cce10.service.IContratService;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;

    @Override
    public Contrat create(Contrat contrat) {
        if (contrat.getIdContrat() != null) {
            throw new IllegalArgumentException(
                    "Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        valider(contrat);
        return contratRepository.save(contrat);
    }

    @Override
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    @Override
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    public Contrat update(Long id, Contrat contrat) {
        Contrat existant = findById(id);
        valider(contrat);
        existant.setDateSignature(contrat.getDateSignature());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setValide(contrat.isValide());
        return contratRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        contratRepository.deleteById(id);
    }

    private void valider(Contrat contrat) {
        if (contrat.getMontantTotal() == null || contrat.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total doit être renseigné et positif ou nul");
        }
    }

    @Override
    @Transactional
    public Contrat addWithPaiements(Contrat contrat) {
        if (contrat.getIdContrat() != null) {
            throw new IllegalArgumentException(
                    "Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        valider(contrat);

        for (Paiement paiement : contrat.getPaiements()) {
            if (paiement.getIdPaiement() != null) {
                throw new IllegalArgumentException(
                        "Un nouveau paiement ne doit pas avoir d'identifiant");
            }
            if (paiement.getMontant() == null || paiement.getMontant().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Le montant de chaque paiement doit être renseigné et positif ou nul");
            }
            if (paiement.getDatePaiement() == null) {
                throw new IllegalArgumentException(
                        "La date de chaque paiement est obligatoire");
            }
            if (paiement.getModePaiement() == null) {
                throw new IllegalArgumentException(
                        "Le mode de paiement est obligatoire");
            }
            // lier le paiement à ce contrat
            paiement.setContrat(contrat);
        }

        return contratRepository.save(contrat);
    }
}