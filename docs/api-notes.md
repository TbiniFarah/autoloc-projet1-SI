# Notes sur l'API REST — AutoLoc (Atelier 5)

## A. Endpoints

Le préfixe `/api` est défini une seule fois par `server.servlet.context-path=/api`. Les erreurs 404 viennent de `ResourceNotFoundException` et les 400 de `IllegalArgumentException`, toutes deux traduites par `GlobalExceptionHandler` (corps JSON : `statut`, `message`, `horodatage`). `PaiementController` est en lecture seule : un `POST /api/paiements` reçoit un 405.

| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| AgenceController | GET /api/agences | IAgenceService.findAll() | 200 | — |
| AgenceController | GET /api/agences/{id} | IAgenceService.findById(id) | 200 | 404 |
| AgenceController | POST /api/agences | IAgenceService.create(agence) | 201 | 400 |
| AgenceController | PUT /api/agences/{id} | IAgenceService.update(id, agence) | 200 | 404, 400 |
| AgenceController | DELETE /api/agences/{id} | IAgenceService.deleteById(id) | 204 | 404 |
| EmployeController | GET /api/employes | IEmployeService.findAll() | 200 | — |
| EmployeController | GET /api/employes/{id} | IEmployeService.findById(id) | 200 | 404 |
| EmployeController | POST /api/employes | IEmployeService.create(employe) | 201 | 400 |
| EmployeController | PUT /api/employes/{id} | IEmployeService.update(id, employe) | 200 | 404, 400 |
| EmployeController | DELETE /api/employes/{id} | IEmployeService.deleteById(id) | 204 | 404 |
| VehiculeController | GET /api/vehicules | IVehiculeService.findAll() | 200 | — |
| VehiculeController | GET /api/vehicules/{id} | IVehiculeService.findById(id) | 200 | 404 |
| VehiculeController | POST /api/vehicules | IVehiculeService.create(vehicule) | 201 | 400 |
| VehiculeController | PUT /api/vehicules/{id} | IVehiculeService.update(id, vehicule) | 200 | 404, 400 |
| VehiculeController | DELETE /api/vehicules/{id} | IVehiculeService.deleteById(id) | 204 | 404 |
| EquipementController | GET /api/equipements | IEquipementService.findAll() | 200 | — |
| EquipementController | GET /api/equipements/{id} | IEquipementService.findById(id) | 200 | 404 |
| EquipementController | POST /api/equipements | IEquipementService.create(equipement) | 201 | 400 |
| EquipementController | PUT /api/equipements/{id} | IEquipementService.update(id, equipement) | 200 | 404, 400 |
| EquipementController | DELETE /api/equipements/{id} | IEquipementService.deleteById(id) | 204 | 404 |
| ClientController | GET /api/clients | IClientService.findAll() | 200 | — |
| ClientController | GET /api/clients/{id} | IClientService.findById(id) | 200 | 404 |
| ClientController | POST /api/clients | IClientService.create(client) | 201 | 400 |
| ClientController | PUT /api/clients/{id} | IClientService.update(id, client) | 200 | 404, 400 |
| ClientController | DELETE /api/clients/{id} | IClientService.deleteById(id) | 204 | 404 |
| ReservationController | GET /api/reservations | IReservationService.findAll() | 200 | — |
| ReservationController | GET /api/reservations/{id} | IReservationService.findById(id) | 200 | 404 |
| ReservationController | POST /api/reservations | IReservationService.create(reservation) | 201 | 400 |
| ReservationController | PUT /api/reservations/{id} | IReservationService.update(id, reservation) | 200 | 404, 400 |
| ReservationController | DELETE /api/reservations/{id} | IReservationService.deleteById(id) | 204 | 404 |
| ContratController | GET /api/contrats | IContratService.findAll() | 200 | — |
| ContratController | GET /api/contrats/{id} | IContratService.findById(id) | 200 | 404 |
| ContratController | POST /api/contrats | IContratService.create(contrat) | 201 | 400 |
| ContratController | PUT /api/contrats/{id} | IContratService.update(id, contrat) | 200 | 404, 400 |
| ContratController | DELETE /api/contrats/{id} | IContratService.deleteById(id) | 204 | 404 |
| MaintenanceController | GET /api/maintenances | IMaintenanceService.findAll() | 200 | — |
| MaintenanceController | GET /api/maintenances/{id} | IMaintenanceService.findById(id) | 200 | 404 |
| MaintenanceController | POST /api/maintenances | IMaintenanceService.create(maintenance) | 201 | 400 |
| MaintenanceController | PUT /api/maintenances/{id} | IMaintenanceService.update(id, maintenance) | 200 | 404, 400 |
| MaintenanceController | DELETE /api/maintenances/{id} | IMaintenanceService.deleteById(id) | 204 | 404 |
| PaiementController | GET /api/paiements | IPaiementService.findAll() | 200 | — |
| PaiementController | GET /api/paiements/{id} | IPaiementService.findById(id) | 200 | 404 |

## B. Annotations Jackson

| Association | Annotation retenue | Justification |
|---|---|---|
| Contrat → Paiement (`Contrat.paiements`) | `@JsonManagedReference("contrat-paiements")` | Composition (cascade ALL) : les paiements se créent dans le JSON de leur contrat. |
| Paiement → Contrat (`Paiement.contrat`) | `@JsonBackReference("contrat-paiements")` | Côté parent de la paire : évite la boucle infinie, le contrat n'est pas répété dans chaque paiement. |
| Agence → Vehicule, Agence → Employe | `@JsonIgnore` sur les listes | Les enfants se créent seuls et désignent leur agence par un identifiant (`{"agence": {"idAgence": 1}}`). |
| Vehicule → Reservation, Vehicule → Maintenance | `@JsonIgnore` sur les listes | Même logique : côté inverse non sérialisé. |
| Client → Reservation | `@JsonIgnore` sur la liste | Une réservation désigne son client par un identifiant. |
| Equipement → Vehicule (ManyToMany inverse) | `@JsonIgnore` | Le côté propriétaire (`Vehicule.equipements`) reste sérialisé ; le côté inverse boucle sinon. |
| Reservation → Contrat (OneToOne inverse) | `@JsonIgnore` | Le côté propriétaire (`Contrat.reservation`) reste sérialisé. |

`@JsonBackReference` ignore la propriété à la sérialisation et la rattache automatiquement au parent à la désérialisation : elle convient à une composition. `@JsonIgnore` supprime simplement la propriété dans les deux sens : elle convient à un côté inverse que l'on ne veut jamais lire ni écrire. `@JsonBackReference` n'est pas utilisé sur `Reservation.client` : la propriété serait ignorée à la lecture et le POST créerait une réservation sans client.

La propriété provisoire `spring.jackson.serialization.fail-on-empty-beans=false` évite l'erreur sur les proxys LAZY (`ByteBuddyInterceptor`) ; le JSON contient alors `"hibernateLazyInitializer": {}`. Ces palliatifs disparaîtront avec les DTO (chapitre 10).

## C. Postman et Swagger

- **Postman** sert à tester : requêtes rejouables, assertions sur les réponses. Exemple : la requête « Consulter un contrat inexistant » (`GET /api/contrats/9999`) vérifie un 404 avec un corps JSON lisible ; le scénario de cascade crée un contrat avec deux paiements puis vérifie, par `GET /api/paiements`, qu'ils existent sans avoir été créés séparément.
- **Swagger UI** sert à documenter et explorer : le contrat de l'API est généré depuis le code par SpringDoc. Exemple : `POST /api/contrats` avec « Try it out » affiche la requête curl, le code 201 et le corps ; la section Schemas décrit `Contrat` à partir de l'entité.
- **Tests avec Swagger UI** : un scénario complet (création, lecture, 404, 400, modification, suppression, cascade) a aussi été exécuté depuis Swagger UI, avec des corps d'exemple valides préchargés par `@ExampleObject` ; il est décrit dans `docs/swagger-tests.md`.
- Ils sont complémentaires : Swagger décrit et essaie l'API, Postman conserve les scénarios de test.

## D. Anomalies SonarQube for IDE

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| Injection par attribut (`@Autowired` sur un champ) dans un contrôleur | S6813 : dépendance cachée, champ non final, `null` hors conteneur. | Retour à l'injection par constructeur (`@RequiredArgsConstructor`, champ `final`). |
| Import inutilisé | `java:S1128` : les imports inutiles alourdissent le code. | Suppression de l'import. |
| Affichage direct avec `System.out.println` | `java:S106` : utiliser un logger plutôt que la sortie standard. | Suppression de l'instruction. |
| Entité persistante en paramètre de `@RequestBody` | S4684 : exposer les entités couple l'API à la base de données. | Choix volontaire et provisoire, remplacé par des DTO au chapitre 10. |

## E. Questions de compréhension

1. **Quel composant reçoit toutes les requêtes ?** Le `DispatcherServlet`. Le `HandlerMapping` retrouve la méthode du contrôleur à partir du verbe HTTP et de l'URL (`@GetMapping`, `@PostMapping`, `@RequestMapping`…).
2. **Pourquoi `ContratController` dépend-il de `IContratService` ?** Un contrôleur mince ne contient aucune logique métier et ne s'adresse jamais au repository : il passe par le service. Il dépend de l'interface et non de `ContratServiceImpl` pour rester faiblement couplé et pouvoir changer d'implémentation.
3. **201, 204, 404 et pas 500.** Un POST qui crée une ressource renvoie 201 (Created) ; un DELETE réussi n'a rien à retourner, d'où 204 (No Content). Une ressource absente est une erreur du client, pas du serveur : `ResourceNotFoundException` est traduite en 404 par `GlobalExceptionHandler`, alors qu'une exception non gérée donnerait un 500.
4. **Pourquoi `PaiementController` n'expose-t-il que des GET ?** Un paiement n'existe que rattaché à son contrat (composition) : sa création, sa modification et sa suppression passent par le `Contrat` (cascade ALL et orphanRemoval). Un `POST /api/paiements` reçoit un 405 Method Not Allowed.
5. **Risques de l'exposition des entités.** Couplage de l'API au schéma de la base, champs internes visibles, boucles de sérialisation et proxys LAZY, modification involontaire de champs sensibles. `Contrat.paiements` utilise `@JsonManagedReference` car les paiements se créent dans le JSON de leur contrat (composition), alors que `Agence.vehicules` utilise `@JsonIgnore` car un véhicule se crée seul et désigne son agence par un identifiant.
