# SkinsSHOP
This program is an imitation of an online store for the sale / purchase / exchange of skins.
## Details
What You Need to Know Before Executing:

## Installation Options
On local machine:

Install docker
- git clone https://github.com/WAYKE4/skinsShop
- docker compose up -d
- go to http://localhost:8080/swagger-ui/index.html#/
- go to security/token endpoint and get your JWT token(The application has a pre-created superAdminTest (as username and password))
- insert token on swagger page in form

### Database
If you want to run applications without docker , use DDL file to set up the database correctly
### Swagger
Some endpoints are protected with JWT security and you'll not be able to reach them without a token
### Technologies used
- PostgreSQL as Database
- Spring Framework
- Spring Boot
- Spring Security JWT
- Spring AOP
- Spring DATA JPA
- Slf4g for logging
- Swagger
- Docker
