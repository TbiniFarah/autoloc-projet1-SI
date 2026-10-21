package tn.esprit.farahtbini4cce10.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "maintenance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaintenance;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = true)
    private LocalDate dateFin;

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicule vehicule;
}
