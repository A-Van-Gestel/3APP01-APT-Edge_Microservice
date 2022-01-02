# 3APP01 | APT | Edge Microservice

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=A-Van-Gestel_3APP01-APT-Edge_Microservice&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=A-Van-Gestel_3APP01-APT-Edge_Microservice)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=A-Van-Gestel_3APP01-APT-Edge_Microservice&metric=bugs)](https://sonarcloud.io/summary/new_code?id=A-Van-Gestel_3APP01-APT-Edge_Microservice)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=A-Van-Gestel_3APP01-APT-Edge_Microservice&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=A-Van-Gestel_3APP01-APT-Edge_Microservice)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=A-Van-Gestel_3APP01-APT-Edge_Microservice&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=A-Van-Gestel_3APP01-APT-Edge_Microservice)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=A-Van-Gestel_3APP01-APT-Edge_Microservice&metric=coverage)](https://sonarcloud.io/summary/new_code?id=A-Van-Gestel_3APP01-APT-Edge_Microservice)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=A-Van-Gestel_3APP01-APT-Edge_Microservice&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=A-Van-Gestel_3APP01-APT-Edge_Microservice)


### Teamleden
* [Sten Neyrinck](https://github.com/stenneyrinck)
* [Axel Van Gestel](https://github.com/A-Van-Gestel)


### Thema
We zijn van plan een tamagotchi te maken, hierbij worden dan de soorten tamagotchi en de gegevens van de tamagotchi van de speler met behulp van de microservices beheerd.

### Backend repo's
* [PlayerData Microservice (MongoDB)](https://github.com/A-Van-Gestel/3APP01-APT-Back_PlayerData_Microservice)
* [Types Tamagotchi Microservice (MariaDB)](https://github.com/stenneyrinck/3APP01-APT-Back_TypesTomagotchi_Microservice)

### Kubernetes Service URL's
Opent de Swagger-UI pagina van elke service, elke service draait in de cloud via Okteto.
* [Edge Microservice](https://edge-service-server-a-van-gestel.cloud.okteto.net/swagger-ui.html)
* [PlayerData Microservice (MongoDB)](https://playerdata-service-server-a-van-gestel.cloud.okteto.net/swagger-ui.html)
* [Types Tamagotchi Microservice (MariaDB)](https://type-tamagotchi-service-server-a-van-gestel.cloud.okteto.net/swagger-ui.html)

### Swagger-UI output Edge Microservice
![swagger-ui-edge](docs/img/swagger-ui-edge.png)
![swagger-ui-edge-output](docs/img/swagger-ui-edge-output.png)

### Diagram van de volledige microservices architectuur
![diagram](docs/img/Overview.png)