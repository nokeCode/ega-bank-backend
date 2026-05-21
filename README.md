# 🏦 EGA Bank - Backend API

<div align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

**API REST robuste pour la gestion bancaire moderne**

[Documentation API](#-documentation-api) • [Installation](#-installation) • [Fonctionnalités](#-fonctionnalités) • [Contribuer](#-contribuer)

</div>

---

## 📋 Table des matières

- [À propos](#-à-propos)
- [Fonctionnalités](#-fonctionnalités)
- [Technologies](#-technologies)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [Structure du projet](#-structure-du-projet)
- [Documentation API](#-documentation-api)
- [Tests](#-tests)
- [Sécurité](#-sécurité)
- [Contribuer](#-contribuer)
- [Licence](#-licence)
- [Contact](#-contact)

---

## 🎯 À propos

**EGA Bank Backend** est une API REST complète développée avec Spring Boot pour gérer l'ensemble des opérations d'une banque moderne. Cette solution offre une architecture robuste, sécurisée et scalable pour la gestion des clients, comptes bancaires, transactions et statistiques financières.

### ✨ Points forts

- 🔐 **Authentification JWT** - Sécurité renforcée avec JSON Web Tokens
- 📊 **Statistiques en temps réel** - Dashboard analytique avec métriques quotidiennes
- 💰 **Gestion des transactions** - Dépôts, retraits et virements instantanés
- 👥 **Multi-utilisateurs** - Gestion des clients et comptes multiples
- 🚀 **API RESTful** - Architecture moderne et standardisée
- 📈 **Traçabilité complète** - Historique détaillé de toutes les opérations

---

## 🚀 Fonctionnalités

### 👤 Gestion des Clients
- ✅ Création et modification de profils clients
- ✅ Recherche avancée (nom, email, téléphone)
- ✅ Consultation de l'historique complet
- ✅ Validation des données (email, téléphone)
- ✅ Support multi-nationalités

### 💳 Gestion des Comptes
- ✅ Création de comptes (Courant, Épargne, Entreprise)
- ✅ Génération automatique de numéros de compte
- ✅ Consultation des soldes en temps réel
- ✅ Gestion multi-comptes par client
- ✅ Historique des transactions par compte

### 💸 Transactions Bancaires
- ✅ **Dépôts** - Alimenter un compte
- ✅ **Retraits** - Retirer des fonds (avec vérification de solde)
- ✅ **Virements** - Transferts entre comptes
- ✅ Validation des montants et soldes
- ✅ Historique complet et traçabilité

### 📊 Statistiques & Analytics
- ✅ Dashboard avec KPI en temps réel
- ✅ Statistiques quotidiennes automatisées
- ✅ Analyse hebdomadaire des performances
- ✅ Comparaisons période sur période
- ✅ Rapports financiers détaillés

### 🔐 Sécurité
- ✅ Authentification JWT
- ✅ Autorisation basée sur les rôles (ADMIN, USER)
- ✅ Chiffrement des mots de passe (BCrypt)
- ✅ Protection CORS configurée
- ✅ Validation des données entrantes

---

## 🛠️ Technologies

### Core
- **Java 21** - Langage de programmation
- **Spring Boot 3.5.9** - Framework principal
- **Spring Security** - Authentification et autorisation
- **Spring Data JPA** - Couche de persistance
- **Hibernate** - ORM

### Base de données
- **PostgreSQL 16** - Base de données relationnelle robuste
- **Flyway** - Migration de base de données

### Sécurité
- **JWT (JSON Web Tokens)** - Authentification stateless
- **BCrypt** - Hashing des mots de passe
- **Spring Security** - Sécurité applicative

### Outils
- **Maven** - Gestion des dépendances
- **Lombok** - Réduction du code boilerplate
- **Jackson** - Sérialisation JSON
- **Validation API** - Validation des données

---

## 📦 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- ☑️ **Java 21** ou supérieur ([Télécharger](https://www.oracle.com/java/technologies/downloads/))
- ☑️ **Maven 3.9+** ([Télécharger](https://maven.apache.org/download.cgi))
- ☑️ **PostgreSQL 14+** ([Télécharger](https://www.postgresql.org/download/))
- ☑️ **Git** ([Télécharger](https://git-scm.com/downloads))

### Vérification des versions

```bash
java -version       # Java 21 requis
mvn -version        # Maven 3.9+
psql --version      # PostgreSQL 14+
```

---

## 🔧 Installation

### 1️⃣ Cloner le repository

```bash
git clone https://github.com/votre-username/ega-bank-backend.git
cd ega-bank-backend
```

### 2️⃣ Créer la base de données

```bash
# Se connecter à PostgreSQL
psql -U postgres

# Ou sur Linux/Mac
sudo -u postgres psql
```

```sql
-- Créer la base de données
CREATE DATABASE ega_bank;

-- Créer un utilisateur (optionnel mais recommandé)
CREATE USER ega_user WITH PASSWORD 'votre_password';

-- Accorder les privilèges
GRANT ALL PRIVILEGES ON DATABASE ega_bank TO ega_user;

-- Se connecter à la base de données
\c ega_bank

-- Accorder les privilèges sur le schéma
GRANT ALL ON SCHEMA public TO ega_user;

-- Quitter psql
\q
```

### 3️⃣ Configurer l'application

Copiez le fichier de configuration et adaptez-le :

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Modifiez `application.properties` :

```properties
# Configuration PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/ega_bank
spring.datasource.username=ega_user
spring.datasource.password=votre_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuration JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=votre_secret_key_super_securisee_et_longue
jwt.expiration=86400000

# Port du serveur
server.port=8080

# Encodage
spring.datasource.sql-script-encoding=UTF-8
```

### 4️⃣ Installer les dépendances

```bash
./mvnw clean install
```

### 5️⃣ Lancer l'application

```bash
./mvnw spring-boot:run
```

L'API sera accessible sur : **http://localhost:8080**

---

## ⚙️ Configuration

### Variables d'environnement (Production)

Pour la production, utilisez des variables d'environnement :

```bash
export DB_URL=jdbc:postgresql://your-db-host:5432/ega_bank
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_super_secret_key
```

### Configuration PostgreSQL avancée

Pour optimiser les performances en production :

```properties
# Pool de connexions
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000

# Optimisations PostgreSQL
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
```

### Profils Spring

Le projet supporte plusieurs profils :

```bash
# Développement
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Tests
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

---

## 🎮 Utilisation

### Démarrage rapide

#### 1. Créer un utilisateur

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@egabank.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

#### 2. Se connecter

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "SecurePass123!"
  }'
```

Vous recevrez un token JWT à utiliser pour les requêtes suivantes.

#### 3. Créer un client

```bash
curl -X POST http://localhost:8080/api/clients \
  -H "Authorization: Bearer VOTRE_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Diallo",
    "prenom": "Mamadou",
    "dateNaissance": "1990-05-15",
    "sexe": "M",
    "adresse": "Lomé, Hédzranawoé",
    "numeroTelephone": "+228 90 12 34 56",
    "courriel": "mamadou.diallo@email.tg",
    "nationalite": "Togolaise"
  }'
```

---

## 📁 Structure du projet

```
ega-bank-backend/
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 java/com/ega/bank/egabankbackend/
│   │   │   ├── 📂 config/          # Configuration (Security, CORS, JWT)
│   │   │   ├── 📂 controller/      # Contrôleurs REST
│   │   │   ├── 📂 dto/             # Data Transfer Objects
│   │   │   ├── 📂 entity/          # Entités JPA
│   │   │   ├── 📂 repository/      # Repositories Spring Data
│   │   │   ├── 📂 service/         # Logique métier
│   │   │   ├── 📂 security/        # Sécurité JWT
│   │   │   ├── 📂 exception/       # Gestion des exceptions
│   │   │   └── 📄 EgaBankBackendApplication.java
│   │   └── 📂 resources/
│   │       ├── 📄 application.properties
│   │       └── 📄 data.sql         # Données de test
│   └── 📂 test/                    # Tests unitaires et d'intégration
├── 📄 pom.xml                      # Configuration Maven
├── 📄 README.md                    # Documentation
└── 📄 .gitignore
```

---

## 📚 Documentation API

### Endpoints principaux

#### 🔐 Authentification

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/auth/register` | Créer un compte utilisateur |
| `POST` | `/api/auth/login` | Se connecter et obtenir un JWT |

#### 👥 Clients

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/clients` | Liste tous les clients |
| `GET` | `/api/clients/{id}` | Détails d'un client |
| `POST` | `/api/clients` | Créer un client |
| `PUT` | `/api/clients/{id}` | Modifier un client |
| `DELETE` | `/api/clients/{id}` | Supprimer un client |
| `GET` | `/api/clients/search` | Rechercher des clients |

#### 💳 Comptes

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/accounts` | Liste tous les comptes |
| `GET` | `/api/accounts/{id}` | Détails d'un compte |
| `POST` | `/api/accounts` | Créer un compte |
| `PUT` | `/api/accounts/{id}` | Modifier un compte |
| `DELETE` | `/api/accounts/{id}` | Supprimer un compte |
| `GET` | `/api/accounts/client/{clientId}` | Comptes d'un client |

#### 💸 Transactions

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/transactions` | Liste toutes les transactions |
| `GET` | `/api/transactions/{id}` | Détails d'une transaction |
| `POST` | `/api/transactions/depot` | Effectuer un dépôt |
| `POST` | `/api/transactions/retrait` | Effectuer un retrait |
| `POST` | `/api/transactions/virement` | Effectuer un virement |
| `GET` | `/api/transactions/account/{accountId}` | Transactions d'un compte |
| `GET` | `/api/transactions/recent` | Transactions récentes |

#### 📊 Statistiques

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/stats/daily` | Statistiques du jour |
| `GET` | `/api/stats/weekly` | Statistiques de la semaine |
| `GET` | `/api/stats/dashboard` | Données du dashboard |

### Exemples de requêtes

#### Créer un compte

```bash
POST /api/accounts
Authorization: Bearer {token}
Content-Type: application/json

{
  "proprietaireId": 1,
  "typeCompte": "COURANT",
  "soldeInitial": 50000
}
```

#### Effectuer un virement

```bash
POST /api/transactions/virement
Authorization: Bearer {token}
Content-Type: application/json

{
  "compteSourceId": 1,
  "compteDestinationId": 2,
  "montant": 10000,
  "description": "Virement pour facture"
}
```

### Documentation Swagger

Une fois l'application lancée, accédez à la documentation interactive :

**http://localhost:8080/swagger-ui.html**

---

## 🧪 Tests

### Lancer tous les tests

```bash
./mvnw test
```

### Tests unitaires uniquement

```bash
./mvnw test -Dtest=*UnitTest
```

### Tests d'intégration

```bash
./mvnw test -Dtest=*IntegrationTest
```

### Couverture de code

```bash
./mvnw jacoco:report
```

Le rapport sera disponible dans `target/site/jacoco/index.html`

---

## 🔒 Sécurité

### Bonnes pratiques implémentées

- ✅ **JWT avec expiration** - Tokens avec durée de vie limitée
- ✅ **Passwords hashés** - BCrypt avec salt automatique
- ✅ **CORS configuré** - Protection contre les attaques cross-origin
- ✅ **Validation des entrées** - Protection contre les injections SQL
- ✅ **HTTPS recommandé** - Chiffrement en production
- ✅ **Rate limiting** - Protection contre les attaques par force brute

### Configuration de production recommandée

```properties
# HTTPS obligatoire
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_PASSWORD}

# JWT fort
jwt.secret=${JWT_SECRET:minimum-32-caracteres-aleatoires}
jwt.expiration=3600000

# Sessions sécurisées
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true

# PostgreSQL SSL
spring.datasource.url=jdbc:postgresql://localhost:5432/ega_bank?sslmode=require
```

---

## 🤝 Contribuer

Les contributions sont les bienvenues ! Voici comment participer :

### 1️⃣ Fork le projet

```bash
# Cliquez sur "Fork" en haut de la page GitHub
```

### 2️⃣ Créer une branche

```bash
git checkout -b feature/AmazingFeature
```

### 3️⃣ Commit vos changements

```bash
git commit -m "✨ Add: Amazing new feature"
```

### 4️⃣ Push vers la branche

```bash
git push origin feature/AmazingFeature
```

### 5️⃣ Ouvrir une Pull Request

Allez sur GitHub et créez une Pull Request avec une description détaillée.

### 📝 Conventions de commit

Utilisez les préfixes suivants :

- ✨ `feat:` Nouvelle fonctionnalité
- 🐛 `fix:` Correction de bug
- 📚 `docs:` Documentation
- 🎨 `style:` Formatage, style
- ♻️ `refactor:` Refactorisation
- ⚡ `perf:` Performance
- ✅ `test:` Tests
- 🔧 `chore:` Maintenance

---

## 📄 Licence

Ce projet est sous licence **MIT**. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

```
MIT License

Copyright (c) 2025 EGA Bank

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

---

## 📞 Contact

### 👨‍💻 Auteur

**Votre Nom**
- GitHub: [@votre-username](https://github.com/votre-username)
- Email: votre.email@example.com
- LinkedIn: [Votre Profil](https://linkedin.com/in/votre-profil)

### 🐛 Signaler un bug

Trouvé un bug ? [Créez une issue](https://github.com/votre-username/ega-bank-backend/issues/new)

### 💡 Proposer une fonctionnalité

Vous avez une idée ? [Ouvrez une discussion](https://github.com/votre-username/ega-bank-backend/discussions)

---

## 🌟 Remerciements

- [Spring Boot](https://spring.io/projects/spring-boot) - Framework principal
- [PostgreSQL](https://www.postgresql.org/) - Base de données relationnelle
- [JWT](https://jwt.io/) - Authentification
- Tous les [contributeurs](https://github.com/votre-username/ega-bank-backend/graphs/contributors) !

---

<div align="center">

**⭐ Si ce projet vous a été utile, n'hésitez pas à lui donner une étoile ! ⭐**

Made with IAI by [Germain AHOBLI](https://github.com/nokeCode)

[⬆ Retour en haut](#-ega-bank---backend-api)

</div>