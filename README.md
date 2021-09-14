# FinalProjectScala
 
### General task
As part of this project, it is necessary to develop a backend for the service of movie reviews. The backend should provide a REST API with the following capabilities for the foreseen frontend of the application and mobile clients.

### Technologies used
- scala 2.13.6
- Sbt
- Akka 2.6.16, Akka-Http
- PostgreSQL 13.3
- Slick
- OAuth2/Basic Auth
- Flyway
- Postman
- Docker

### Database schema (v2)
![db-schema](src/main/resources/db-schema.png)

## Possible requests

### CRUD for films and other entities
```
curl --location --request GET '127.0.0.1:8000/films/'
```

### Create film using name and release year via another API
```
curl --location --request POST '127.0.0.1:8000/help-create/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "Godzilla",
    "releaseDate":"2014"
}'
```
### Update list of genres via another API
```
127.0.0.1:8000/update-genres
```