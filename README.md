# Software Engineering Project
TU Wien, Autumn 2017

## Dominion

.. is a deck-building multiplayer game.

## authors

Name: Ulrike, Schaefer\

Name: Johannes, Trippl\

Name: Sabrina, Kall\

Name: Alex, Nachname\

Name: Paul, Pinterits\

Name: Sahab, Nachname\

## how to

#### Build and run
```bash
cd path/to/proj_ws17_sepm_qse_04
./mvnw clean compile test install

```

#### Start server
```bash
java -jar server/target/Dominion-Server-0.0.1-SNAPSHOT.jar

```
#### Start client
```bash
java -jar client/target/Dominion-Client-0.0.1-SNAPSHOT-jar-with-dependencies.jar

```

#### REST - UserPersistence
##### GET /user/all
returns all users saved in db
```bash
curl --request GET \
  --url http://localhost:8080/user/all

```
##### GET /user/byid
returns a user with id=1
```bash
curl --request GET \
  --url 'http://localhost:8080/user/byid?id=1'
```

##### POST /user/add
add a user called Saruman
```bash
curl --request POST \
  --url http://localhost:8080/user/add \
  --header 'content-type: application/json' \
  --data '{"username":"Boromir","password":"pwpwpw"}'
```

##### POST /auth
authenticate against the server
```bash
curl --request POST \
  --url 'http://localhost:8080/auth' \
  --header 'content-type: application/json' \
  --data '{"username":"Boromir","password":"pwpwpw"}'
```

# KNOWN PROBLEMS
## Deployment issues
start server and client via scripts! running compile and install server throws an error (test @ client) - ignore it for now  - server works, client tests do too!
