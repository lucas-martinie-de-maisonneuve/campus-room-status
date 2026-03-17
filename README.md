# 🏫 Campus Room Status

## 📚 Sommaire

-   [🚀 Présentation](#-présentation)
-   [🧱 Architecture](#-architecture)
-   [⚙️ Technologies](#️-technologies)
-   [📋 Prérequis](#-prérequis)
-   [📋 Installation](#-installation)
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
- Un projet Google Cloud Console
- Java 17+
- Maven
- Un compte Google Workspace avec accès aux ressources Calendar
- Git Emoji pour rendre les messages de commit plus visuels. Voici le site officiel / documentation : [gitmoji.dev](https://gitmoji.dev)

## 📋 Installation

## 1. Créer un OAuth Client ID sur Google Cloud Console

1. Rendez-vous sur [console.cloud.google.com](https://console.cloud.google.com)
2. Sélectionnez ou créez un projet
3. Activez les APIs suivantes :
   - **Admin SDK API**
   - **Google Calendar API**
4. Allez dans **APIs & Services** → **Credentials** → **Create Credentials** → **OAuth client ID**
5. Type : **Web application**
6. Ajoutez l'URI de redirection autorisée :
   ```
   http://localhost:8080/login/oauth2/code/google
   ```
7. Notez le **Client ID** et le **Client Secret**

### 2. Configurer le fichier `.env`

Créez un fichier `.env` à la racine du projet (même niveau que `pom.xml`) :

```
GOOGLE_CLIENT_ID=votre_client_id
GOOGLE_CLIENT_SECRET=votre_client_secret
GOOGLE_REFRESH_TOKEN=
```

Laissez `GOOGLE_REFRESH_TOKEN` vide pour l'instant.

### 3. Compiler le projet

```bash
mvn clean install
```

---

## Obtenir un Refresh Token

Le refresh token permet à l'application de s'authentifier automatiquement sans interaction à chaque démarrage.

### Étape 1 — Lancer l'application

```bash
mvn spring-boot:run
```

### Étape 2 — Se connecter avec Google

Ouvrez votre navigateur et rendez-vous sur :

```
http://localhost:8080/api/v1/get-token
```

Connectez-vous avec votre compte Google LaPlateforme. L'application affichera votre refresh token dans la réponse JSON et dans la console.

### Étape 3 — Renseigner le Refresh Token

Copiez la valeur du `refreshToken` et ajoutez-la dans votre `.env` :

```
GOOGLE_REFRESH_TOKEN=votre_refresh_token
```

### Étape 4 — Relancer l'application

```bash
mvn spring-boot:run
```

L'application démarre sans redirection vers Google et est prête à utiliser.

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

| Variable | Description | Obligatoire |
|----------|-------------|-------------|
| `GOOGLE_CLIENT_ID` | Client ID OAuth Google Cloud Console | Oui |
| `GOOGLE_CLIENT_SECRET` | Client Secret OAuth Google Cloud Console | Oui |
| `GOOGLE_REFRESH_TOKEN` | Refresh token obtenu via `/api/v1/get-token` | Oui (après premier démarrage) |

- Le fichier `.env` ne doit jamais être commité — il est dans `.gitignore`
- Le refresh token est lié à votre compte Google — chaque utilisateur doit générer le sien
- Si le refresh token expire ou est révoqué, videz `GOOGLE_REFRESH_TOKEN` dans `.env` et relancez pour en générer un nouveau via `/api/v1/get-token`


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
