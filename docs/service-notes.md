# Notes sur la couche Service — AutoLoc (Atelier 4)

## A. Modes d'injection comparés

| Critère | Injection par constructeur (retenue) | Injection par attribut |
|---|---|---|
| Champ `final` possible ? | Oui | Non |
| Dépendance visible dans la signature de la classe ? | Oui (paramètre du constructeur) | Non (dépendance cachée) |
| Classe utilisable avec `new` hors conteneur sans dépendance `null` ? | Oui : le constructeur impose le repository | Non : le champ reste `null` |
| Signalé par SonarQube for IDE ? | Non | Oui (règle S6813, à confirmer dans le panneau) |

## B. Services

Tous les services sont des `@Service` annotés `@RequiredArgsConstructor`, sans état, avec un champ `final` typé par l'interface du repository.

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| AgenceServiceImpl | IAgenceRepository | Constructeur | CRUD ; refuse une agence sans nom, ville ou adresse. |
| EmployeServiceImpl | IEmployeRepository | Constructeur | CRUD ; refuse un employé sans nom, prénom ou rôle. |
| VehiculeServiceImpl | IVehiculeRepository | Constructeur | CRUD ; refuse un tarif journalier négatif ou absent. |
| EquipementServiceImpl | IEquipementRepository | Constructeur | CRUD ; refuse un équipement sans libellé. |
| ClientServiceImpl | IClientRepository | Constructeur | CRUD ; refuse un client sans nom, prénom, e-mail ou numéro de permis. |
| ReservationServiceImpl | IReservationRepository | Constructeur | CRUD ; refuse une date de fin antérieure à la date de début. |
| ContratServiceImpl | IContratRepository | Constructeur | CRUD ; refuse un montant total négatif ; la suppression d'un contrat supprime ses paiements (cascade ALL). |
| PaiementServiceImpl | IPaiementRepository | Constructeur | Lecture seule (`findById`, `findAll`) : un paiement est créé, modifié ou supprimé à travers son contrat (composition). |
| MaintenanceServiceImpl | IMaintenanceRepository | Constructeur | CRUD ; refuse une date de fin antérieure à la date de début. |

## C. Messages d'erreur du conteneur

**Message 1 — `IContratService` introuvable pour `ContratController`.**
Exception : `NoSuchBeanDefinitionException` (enveloppée dans `UnsatisfiedDependencyException`). Causes probables côté `ContratServiceImpl` : l'annotation `@Service` est oubliée, ou la classe est placée hors du package racine scanné (`tn.esprit.farahtbini4cce10`). Correction : ajouter `@Service` et vérifier le package.

**Message 2 — deux beans `emailNotificateur` et `smsNotificateur` pour `NotificationController`.**
Exception : `NoUniqueBeanDefinitionException`. Deux corrections possibles : (1) `@Primary` sur la classe du bean à choisir par défaut (par exemple `EmailNotificateur`) ; (2) `@Qualifier("emailNotificateur")` sur le paramètre du constructeur du contrôleur, pour un choix explicite à l'injection.

**Message 3 — cycle entre `clientServiceImpl` et `reservationServiceImpl`.**
Exception : `BeanCurrentlyInCreationException`. Avec l'injection par constructeur, chaque bean doit être entièrement construit avant d'être fourni à l'autre : aucun des deux ne peut être créé en premier, le cycle est donc impossible à contourner. Piste de conception : extraire la responsabilité commune dans un troisième service, ou faire dépendre un seul sens (par exemple `ReservationServiceImpl` dépend de `IClientRepository` et non de `IClientService`).

## D. Anomalies SonarQube for IDE

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| Injection par attribut (`@Autowired` sur un champ) dans `ContratServiceImpl` | S6813 : la dépendance est cachée, le champ n'est pas `final` et reste `null` hors conteneur. | Retour à l'injection par constructeur (`@RequiredArgsConstructor` et champ `final`). |
| `RuntimeException` levée dans `findById` | `java:S112` : les exceptions génériques ne doivent pas être levées. | Remplacée par l'exception métier `ResourceNotFoundException`. |
| Import inutilisé (`java.util.Map`) | `java:S1128` : les imports inutiles alourdissent le code. | Import supprimé. |

## E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?** Il n'est écrit nulle part : c'est le conteneur Spring qui instancie la classe au démarrage, après l'avoir détectée grâce à `@Service` et au scan, et qui lui fournit le proxy de `IContratRepository` par son constructeur.
2. **Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?** Pour dépendre d'une abstraction : on peut changer d'implémentation sans modifier le contrôleur, et le remplacer par un faux service dans les tests. Le couplage reste faible.
3. **Pourquoi un service singleton doit-il rester sans état ?** Une seule instance est partagée par tous les threads. Exemple de bug : un attribut `contratCourant` modifié par `update` ; deux requêtes simultanées s'écrasent et l'une modifie le contrat de l'autre.
4. **Pourquoi `update` charge-t-il l'entité existante avant `save` ?** Passer directement l'objet reçu à `save` (avec un identifiant) déclenche un `merge` qui écrase toutes les colonnes, y compris celles non envoyées ; avec `orphanRemoval`, une collection `paiements` vide ou `null` supprimerait tous les paiements du contrat. Le chargement vérifie aussi que l'entité existe.
5. **Différences.** `@Component` s'applique à une classe qu'on écrit et la fait détecter par le scan ; `@Bean` s'applique à une méthode d'une classe `@Configuration`, pour déclarer explicitement un bean (classe de bibliothèque, construction particulière). `@Primary` désigne un candidat par défaut, posé sur la classe ; `@Qualifier` choisit un bean précis, par son nom, au point d'injection. `IPaiementService` n'expose ni création, ni modification, ni suppression car un paiement n'existe que rattaché à son contrat (composition) : ces opérations passent par le `Contrat` (cascade ALL et orphanRemoval), ce qui préserve la cohérence.
