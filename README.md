# Post-It API

Post-It API est une API de gestion de notes personnelles. Elle est vouée à être utilisée par une web-app s'inspirant fortement de Google Keep. L'API a été réalisée dans le but d'être montrée dans le cadre d'une recherche d'emploi.

L'API permet de :

- Se connecter
- Ajouter / Supprimer / Modifier une note, sécurisé par un token d'authentification
- Récupérer toutes les notes d'un utilisateur grâce à l'utilisation d'un token d'authentification
- Créer un compte

## Status

L'API est dans un état fonctionnel même si certains points sont à améliorer parmi lesquels :

- Externalisation de la clé secrété utilisée pour la génération et validation des JWTs.
- Ecriture de tests end to end
- Mise en place de OAuth 2 avec les providers courants (Google, Facebook, etc..)
- Modification du mot de passe d'un utilisateur

## Technologies

Le projet est une application Spring Boot (2.4.5).

L'API a été défini via Spring MVC. La validation des données entrantes se fait grâce à [Hibernate validator](https://hibernate.org/validator/)
L'authentification se fait via JWT grâce au package [jjwt](https://github.com/jwtk/jjwt) et la mise en place d'un filtre Spring security validant les tokens.

Les intéractions avec la base de données se font grâce à Spring Data JPA. Les données sont persistées dans une base de données PostgreSQL.

## Installation

Pour lancer l'application en local, **Java 11** est au minimum nécessaire.

Grâce à Spring Boot, il suffit d'exécuter la méthode main (se trouvant dans PostItApiApplication) pour lancer l'application dans un Tomcat embarqué.

Un Datasource valide est attendu au lancement. Il peut être précisé via les propriétés prédéfinies `spring.datasource.url`, `spring.datasource.username` et `spring.datasource.password`.

Pour le moment, la clé secréte de génération et vérification des JWTs est généré automatiquement au lancement de l'application.

## DB schéma

![DB Schéma](https://i.ibb.co/hfdQJ8Y/db-diagram.png)

Le schéma de la base de données est minimaliste. Hibernate n'a pas été configuré pour récréer le schéma au lancement de l'application. La base de données indiquée en datasource doit donc être opérationnelle.
