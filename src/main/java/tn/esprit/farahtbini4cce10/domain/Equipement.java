package tn.esprit.farahtbini4cce10.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "equipement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipement;

    @Column(nullable = false, length = 100)
    private String libelle;

    @JsonIgnore
    @ManyToMany(mappedBy = "equipements", fetch = FetchType.LAZY)
    private Set<Vehicule> vehicules = new HashSet<>();
}
