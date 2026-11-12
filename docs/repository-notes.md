# Notes sur la couche Repository — AutoLoc (Atelier 3)

## Choix de l'interface

Les neuf repositories étendent `JpaRepository<Entité, Long>` : CRUD complet, collections renvoyées en `List` (et non en `Iterable` comme avec `CrudRepository`), tri, pagination et méthodes propres à JPA (`flush`, `saveAndFlush`, `getReferenceById`). Spring Data génère l'implémentation au démarrage : `@Repository` n'est pas nécessaire. Les suppressions « en lot » (`deleteAllInBatch`) sont à éviter sur les entités avec cascade, car elles contournent le contexte de persistance (cascade et orphanRemoval non appliqués).

| Interface | Étend | Justification |
|---|---|---|
| IAgenceRepository | `JpaRepository<Agence, Long>` | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. |
| IEmployeRepository | `JpaRepository<Employe, Long>` | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. |
| IVehiculeRepository | `JpaRepository<Vehicule, Long>` | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. |
| IEquipementRepository | `JpaRepository<Equipement, Long>` | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. |
| IClientRepository | `JpaRepository<Client, Long>` | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. |
| IReservationRepository | `JpaRepository<Reservation, Long>` | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. |
| IContratRepository | `JpaRepository<Contrat, Long>` | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. Permet d'observer cascade et orphanRemoval. |
| IPaiementRepository | `JpaRepository<Paiement, Long>` | Lecture des paiements ; la création et le retrait passent par le Contrat (composition, cascade ALL + orphanRemoval). |
| IMaintenanceRepository | `JpaRepository<Maintenance, Long>` | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. |

## Anomalies relevées avec SonarQube for IDE

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| Import inutilisé dans `IVehiculeRepository` | `java:S1128` : les imports inutiles alourdissent le code. | Suppression de l'import `CrudRepository` après passage à `JpaRepository`. |
| `RuntimeException` générique levée dans `findById` du service | `java:S112` : les exceptions génériques ne doivent pas être levées. | Remplacée par une exception métier (`EntityNotFoundException`, puis `ResourceNotFoundException`). |
| à compléter avec ton propre relevé | à compléter | à compléter |
