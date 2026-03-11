# 🏫 Campus Room Status

## 📚 Sommaire

-   [🚀 Présentation](#-présentation)
-   [🧱 Architecture](#-architecture)
-   [⚙️ Technologies](#️-technologies)
-   [📋 Prérequis](#-prérequis)
-   [📁 Structure du projet](#-structure-du-projet)
-   [🔄 Migrations](#-migrations)
-   [🐳 Lancement avec Docker](#-lancement-avec-docker)
-   [🧪 Tests](#-tests)
-   [📄 Variables d'environnement](#-variables-denvironnement)
-   [📦 Déploiement](#-déploiement)
-   [🤝 Contribution](#-contribution)
    - [Clone](#clone)
    - [Création de branche](#création-de-branche)
    - [Convention des commits](#convention-des-commits)
    - [Pull Request](#pull-request)

## 🚀 Présentation

Pour ce projet, il s’agit de développer une API permettant de consulter les disponibilités des salles. Cette API servira à moderniser la gestion des espaces du nouveau campus. Les informations seront affichées sur des écrans géants dans les couloirs, à la manière des panneaux d’aéroports ou de gares.

## 🧱 Architecture

Le projet suit une architecture **client-serveur moderne** :

- **Frontend React** : interface utilisateur.
- **Backend Spring Boot** : API REST sécurisée et gestion des données.
- **Base de données** : stockage des informations des salles.

## ⚙️ Technologies

- **Java / Spring Boot** : serveur et API REST.
- **MySQL / PostgreSQL** : base de données relationnelle.

## 📋 Prérequis

Avant de lancer le projet, s'assurer d'avoir :
- Installer **Git Emoji** pour rendre les messages de commit plus visuels. Voici le site officiel / documentation : [gitmoji.dev](https://gitmoji.dev)


## 📁 Structure du projet

campus-room-status/
├─ backend/ # API Spring Boot
├─ frontend/ # Application React
├─ docker-compose.yml
└─ README.md

## 🔄 Migrations

Les migrations permettent de créer ou mettre à jour la base de données.

## 🐳 Lancement avec Docker

Pour lancer tous les services avec Docker :

``
docker-compose up --build
``

## ▶️ Lancement en mode développement

Backend: mvn spring-boot:run\
Frontend: npm start

## 🧪 Tests

• Backend
• Frontend

## 📄 Variables d'environnement

DB_HOST : adresse de la base de données
DB_USER : utilisateur de la base
DB_PASSWORD : mot de passe
JWT_SECRET : clé secrète pour l’authentification

## 📦 Déploiement

Procédure de déploiement.

## 🤝 Contribution

Règles de contribution à suivre

### Clone

1. Clonez le projet localement:

```bash
git clone https://github.com/lucas-martinie-de-maisonneuve/campus-room-status.git
```

###  >  Création de branche

Le format est : **type/CRS-XXX**

- `type` : le type de travail 
- `CRS-XXX` : numéro du ticket Jira (ex : CRS-12, CRS-45)

Exemple:
    - Nouvelle fonctionnalité: feature/CRS-23
    - Correction d’un bug: bugfix/CRS-12
    - Refactorisation: refactor/CRS-90
    - Mise à jour de la documentation: docs/CRS-80

###  >  Convention des commits

Le format est : **emoji CRS-XXX type (scope) :  description courte**

- `emoji` : symbole représentant le type de commit
- `type` : type et zone impactée
- `scope` : type et zone impactée
- `CRS-XXX` : numéro du ticket Jira
- `Description courte` : résumé sythétique

Exemple:

✨ [CRS-23] feat(frontend): ajouter formulaire login
🐛 [CRS-12] fix(backend): corriger endpoint patients
📝 [CRS-80] docs(readme): mise à jour section contribution
♻️ [CRS-90] refactor(api): simplifier controller utilisateur
🚑 [CRS-45] hotfix(backend): corriger crash DB

###  >  Pull Request

Le format est : CRS-XXX - Description courte
• Tâche effectuée 1
• Tâche effectuée 2
• Tâche effectuée 3

Intégration d'une image si necessaire

- `CRS-XXX` : numéro du ticket Jira pour la traçabilité.
- `Description courte` : description courte qui doit résumer la PR en une phrase.
- `Liste des tâches` : chaque tâche effectuée dans cette PR (bullet points).
- `Image` (optionnelle) : ajouter une image si ça aide à la compréhension.

Exemples :

CRS-23 - Ajouter formulaire de login sécurisé
• Création du formulaire React avec validation des champs
• Ajout de l’endpoint backend /login
• Génération du token JWT pour l’authentification

Règles à suivre:
1. Créer toujours la PR vers la branche principale (develop / main).
2. Faire des PR petites et ciblées, une fonctionnalité ou un bug par PR.
3. Vérifiez que tous les tests passent avant de créer la PR.
4. Respectez les conventions de commits et Git Emoji.
