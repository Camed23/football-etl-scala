# Football ETL – Documentation Technique Complète

## 1. Présentation du projet

Ce projet consiste à développer un **pipeline ETL (Extract – Transform – Load)** en **Scala**, appliqué à l’analyse de données de joueurs de football.

À partir d’un fichier JSON contenant des données potentiellement incorrectes ou incomplètes, le système :

* charge les données,
* gère les erreurs de parsing,
* valide et nettoie les entrées,
* calcule des statistiques avancées,
* génère un **rapport d’analyse structuré**.

Le projet met l’accent sur :

* la **programmation fonctionnelle**,
* la **gestion explicite des erreurs**,
* la **modularité du code**,
* la **traçabilité des transformations de données**.

---

## 2. Dataset utilisé

Le dataset contient des informations sur des **joueurs de football professionnels**, incluant notamment :

* informations personnelles (âge, nationalité),
* club et ligue,
* statistiques sportives (buts, passes, matchs joués),
* discipline (cartons jaunes et rouges),
* données économiques (valeur marchande, salaire).

### Fichiers utilisés

* `data_clean.json` : données nettoyées
* `data_dirty.json` : données contenant des erreurs de parsing
* `data_large.json` : dataset volumineux utilisé pour mesurer les performances

Certaines valeurs peuvent être absentes (ex. salaire, valeur marchande), ce qui justifie l’utilisation de types optionnels.

---

## 3. Architecture globale du pipeline

Le projet est organisé selon une architecture modulaire claire :

```
DataLoader
   ↓
DataValidator
   ↓
StatsCalculator
   ↓
ReportGenerator
   ↓
Main
```

### Rôle des modules

| Module            | Rôle                                                       |
| ----------------- | ---------------------------------------------------------- |
| `DataLoader`      | Lecture du fichier JSON et gestion des erreurs de parsing  |
| `DataValidator`   | Validation métier et suppression des doublons              |
| `StatsCalculator` | Calcul des statistiques, agrégations, classements et bonus |
| `ReportGenerator` | Construction du rapport final et écriture des fichiers     |
| `Main`            | Orchestration du pipeline ETL                              |

---

## 4. Modèle de données

### 4.1 Données d’entrée

```scala
case class Player(
  id: Int,
  name: String,
  age: Int,
  nationality: String,
  position: String,
  club: String,
  league: String,
  goalsScored: Int,
  assists: Int,
  matchesPlayed: Int,
  yellowCards: Int,
  redCards: Int,
  marketValue: Option[Double],
  salary: Option[Double]
)
```

L’utilisation de `Option[Double]` permet de gérer les **valeurs manquantes** sans utiliser de `null`.

---

### 4.2 Rapport final

Toutes les analyses sont regroupées dans une structure unique :

```scala
case class AnalysisReport(
  playerStats: PlayerStats,
  topScorers: List[TopScorer],
  topAssisters: List[TopAssister],
  topMarketValues: List[TopMarketValue],
  topSalaries: List[TopSalary],
  aggregationReport: AggregationReport,
  disciplineStats: DisciplineStats,
  bonusReport: BonusReport
)
```

---

## 5. Fonctionnement du pipeline ETL

### 5.1 Extraction – DataLoader

* Lecture du fichier JSON
* Décodage sécurisé via Circe
* Gestion des erreurs avec `Either`
* Comptage des erreurs de parsing

### 5.2 Transformation – DataValidator

* Validation des règles métier :

  * âge entre 16 et 45 ans
  * matchs joués > 0
  * buts ≥ 0
  * poste valide
* Suppression des doublons basée sur l’identifiant `id`

### 5.3 Analyse – StatsCalculator

Calculs réalisés :

* statistiques de parsing,
* top 10 (buteurs, passeurs, valeurs marchandes, salaires),
* agrégations par ligue et par poste,
* statistiques de discipline,
* **bonus** :

  * efficacité offensive (buts / matchs),
  * rapport qualité/prix (buts / salaire),
  * statistiques par ligue (âge moyen, buts moyens, ligue la plus productive).

### 5.4 Génération du rapport – ReportGenerator

* Construction du `AnalysisReport`
* Écriture de :

  * `results.json`
  * `report.txt`
* Gestion des erreurs d’écriture avec `Try`

---

## 6. Compilation et exécution du projet

### Prérequis

* Java 11+
* sbt
* Scala 3 (ou Scala 2.13 selon configuration)

### Compilation

```bash
sbt compile
```

### Exécution

```bash
sbt run
```

### Exécution des tests unitaires

```bash
sbt test
```

---

## 7. Tests unitaires

Des **tests unitaires avec MUnit** ont été implémentés afin de valider la logique métier du module `StatsCalculator`.

Les tests couvrent notamment :

* l’efficacité offensive,
* le rapport qualité/prix,
* les statistiques par ligue,
* les filtres sur les données.

Ces tests garantissent la fiabilité des calculs indépendamment du pipeline ETL.

---

## 8. Performances

Des mesures ont été effectuées sur le fichier **`data_large.json`**.

### Résultats observés

* Temps d’exécution : **≈ 2.849 secondes**
* Débit : **≈ 3510 entrées/seconde**

Les métriques sont affichées dans la console et intégrées dans `report.txt`.

---

## 9. Choix techniques et justification

### Programmation fonctionnelle

* Utilisation de `map`, `flatMap`, `groupBy`, `fold`
* Données immuables
* Fonctions pures

### Gestion des erreurs

* `Either` pour les erreurs critiques (I/O, parsing)
* `Option` pour les valeurs facultatives
* Aucun `null` utilisé

### Organisation du code

* Séparation claire des responsabilités
* Réutilisabilité via `StatsCalculator`
* Point de sortie unique : `AnalysisReport`

---

## 10. Difficultés rencontrées et solutions

### Gestion des erreurs de parsing

**Problème :** données JSON invalides
**Solution :** décodage individuel et comptage des erreurs sans interrompre le pipeline

### Données manquantes

**Problème :** salaires et valeurs absentes
**Solution :** utilisation de `Option` et `flatMap`

### Doublons

**Problème :** joueurs dupliqués
**Solution :** déduplication basée sur l’identifiant

### Lisibilité du pipeline

**Problème :** logique complexe
**Solution :** séparation claire entre calcul, orchestration et génération de rapport

---

## 11. Conclusion

Ce projet met en œuvre un **pipeline ETL complet**, robuste et extensible, illustrant les principes fondamentaux de la programmation fonctionnelle en Scala.

La gestion explicite des erreurs, l’ajout de statistiques avancées et de tests unitaires, ainsi que la génération de rapports structurés permettent d’obtenir une solution fiable, lisible et adaptée à l’analyse de données réelles.
