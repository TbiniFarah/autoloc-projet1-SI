package tn.esprit.farahtbini4cce10.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.farahtbini4cce10.domain.Contrat;
import tn.esprit.farahtbini4cce10.service.IContratService;

@Tag(name = "Contrats", description = "Gestion des contrats")
@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
public class ContratController {

    private final IContratService contratService;

    @Operation(summary = "Lister les contrats")
    @ApiResponse(responseCode = "200", description = "Liste des contrats")
    @GetMapping
    public List<Contrat> findAll() {
        return contratService.findAll();
    }

    @Operation(summary = "Consulter un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultation réussie"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")})
    @GetMapping("/{id}")
    public Contrat findById(@Parameter(description = "Identifiant (contrat)") @PathVariable Long id) {
        return contratService.findById(id);
    }

    @Operation(summary = "Créer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Création réussie"),
            @ApiResponse(responseCode = "400", description = "Données invalides")})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contrat create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "dateSignature": "2026-11-02",
                              "montantTotal": 480.0,
                              "valide": true,
                              "paiements": [
                                {
                                  "montant": 150.0,
                                  "datePaiement": "2026-11-02",
                                  "modePaiement": "CARTE"
                                },
                                {
                                  "montant": 330.0,
                                  "datePaiement": "2026-11-06",
                                  "modePaiement": "VIREMENT"
                                }
                              ]
                            }
                            """)))
            @RequestBody Contrat contrat) {
        return contratService.create(contrat);
    }

    @Operation(summary = "Modifier un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modification réussie"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")})
    @PutMapping("/{id}")
    public Contrat update(@Parameter(description = "Identifiant (contrat)") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "dateSignature": "2026-11-02",
                              "montantTotal": 520.0,
                              "valide": true
                            }
                            """)))
            @RequestBody Contrat contrat) {
        return contratService.update(id, contrat);
    }

    @Operation(summary = "Supprimer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Suppression réussie"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant (contrat)") @PathVariable Long id) {
        contratService.deleteById(id);
    }

    @Operation(summary = "Créer un contrat avec ses paiements (cascade)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Création réussie"),
            @ApiResponse(responseCode = "400", description = "Données invalides")})
    @PostMapping("/with-paiements")
    @ResponseStatus(HttpStatus.CREATED)
    public Contrat addWithPaiements(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "dateSignature": "2026-11-12",
                              "montantTotal": 480.0,
                              "valide": true,
                              "paiements": [
                                {
                                  "montant": 150.0,
                                  "datePaiement": "2026-11-12",
                                  "modePaiement": "CARTE"
                                },
                                {
                                  "montant": 330.0,
                                  "datePaiement": "2026-11-20",
                                  "modePaiement": "VIREMENT"
                                }
                              ]
                            }
                            """)))
            @RequestBody Contrat contrat) {
        return contratService.addWithPaiements(contrat);
    }
}
