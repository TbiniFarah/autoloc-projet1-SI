# Tests de l'API avec Swagger UI — AutoLoc (Atelier 5)

Swagger UI (`http://localhost:8080/api/swagger-ui.html`) permet d'explorer et d'essayer chaque endpoint sans outil externe. La description OpenAPI brute est disponible à `http://localhost:8080/api/v3/api-docs`. Pour chaque requête : ouvrir l'endpoint, cliquer sur **Try it out**, saisir les paramètres ou le corps (un exemple valide est préchargé), puis **Execute** ; lire le *Code* et le *Response body* sous « Responses ».

Les identifiants utilisés ci-dessous (1) supposent une base vide au départ ; sinon, utiliser les identifiants renvoyés par chaque création.

## Scénario

| N° | Groupe | Endpoint | Corps / paramètre | Résultat attendu |
|---|---|---|---|---|
| 1 | Agences | `GET /agences` | — | 200, tableau JSON (vide) |
| 2 | Agences | `POST /agences` | exemple préchargé | 201, `idAgence` généré |
| 3 | Equipements | `POST /equipements` | exemple préchargé | 201, `idEquipement` généré |
| 4 | Clients | `POST /clients` | exemple préchargé | 201, `idClient` généré |
| 5 | Vehicules | `POST /vehicules` | exemple préchargé (agence 1, équipement 1) | 201 |
| 6 | Employes | `POST /employes` | exemple préchargé (agence 1) | 201 |
| 7 | Reservations | `POST /reservations` | exemple préchargé (client 1, véhicule 1) | 201 |
| 8 | Contrats | `POST /contrats` | exemple préchargé (contrat avec deux paiements) | 201, `paiements` contient 2 éléments |
| 9 | Paiements | `GET /paiements` | — | 200, les deux paiements créés par cascade |
| 10 | Contrats | `GET /contrats/{id}` | `id` = 9999 | 404, corps JSON (`statut`, `message`, `horodatage`) |
| 11 | Contrats | `POST /contrats` | ajouter `"idContrat": 5` au corps | 400 (un nouveau contrat ne doit pas avoir d'identifiant) |
| 12 | Contrats | `POST /contrats` | `"montantTotal": -5` | 400 |
| 13 | Vehicules | `POST /vehicules` | `"tarifJournalier": -10` | 400 |
| 14 | Contrats | `PUT /contrats/{id}` | exemple de modification (montant 520) | 200, montant modifié, paiements conservés |
| 15 | Paiements | `GET /paiements` | — | 200, les paiements sont toujours là |
| 16 | Contrats | `DELETE /contrats/{id}` | `id` du contrat | 204, sans corps |
| 17 | Paiements | `GET /paiements/{id}` | `id` d'un paiement du contrat supprimé | 404 (suppression en cascade) |

## Observations

- Le groupe **Paiements** n'affiche que deux endpoints `GET` : la lecture seule est visible directement dans la documentation. Un `POST /api/paiements` renvoie 405 ; ce cas se teste avec Postman, Swagger ne proposant pas l'opération.
- Les corps d'exemple des `POST` et `PUT` sont définis par `@ExampleObject` : sans eux, Swagger génère un exemple contenant `"idContrat": 0`, que le service refuse (400), car un nouveau contrat ne doit pas avoir d'identifiant.
- La section **Schemas** décrit les entités (par exemple `Contrat`) à partir du code.
