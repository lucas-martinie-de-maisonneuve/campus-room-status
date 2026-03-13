## Prérequis

- Java 17+
- Maven
- Un compte Google Workspace avec accès aux ressources Calendar
- Un projet Google Cloud Console

---

## Installation

### 1. Créer un OAuth Client ID sur Google Cloud Console

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

---

## Variables d'environnement

| Variable | Description | Obligatoire |
|----------|-------------|-------------|
| `GOOGLE_CLIENT_ID` | Client ID OAuth Google Cloud Console | Oui |
| `GOOGLE_CLIENT_SECRET` | Client Secret OAuth Google Cloud Console | Oui |
| `GOOGLE_REFRESH_TOKEN` | Refresh token obtenu via `/api/v1/get-token` | Oui (après premier démarrage) |

---

## Notes

- Le fichier `.env` ne doit jamais être commité — il est dans `.gitignore`
- Le refresh token est lié à votre compte Google — chaque utilisateur doit générer le sien
- Si le refresh token expire ou est révoqué, videz `GOOGLE_REFRESH_TOKEN` dans `.env` et relancez pour en générer un nouveau via `/api/v1/get-token`
