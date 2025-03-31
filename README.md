# Digital Money House - Backend

Este backend corresponde al proyecto **Digital Money House**, una billetera virtual que permite transferencias, depósitos, consulta de saldos y gestión de cuentas y tarjetas de usuario. Desarrollado en Java con Spring Boot, siguiendo un modelo de arquitectura de microservicios.

---

## 🔧 Stack Tecnológico
- **Java (Spring Boot)**
- **Hibernate/JPA** (Persistencia)
- **MySQL** (Base de Datos)
- **Spring Cloud** (Microservicios):
    - Eureka (Service Discovery)
    - API Gateway
    - Feign Client (Comunicación interna)
- **JWT** (Autenticación)
- **Swagger** (Documentación API)

---

## 📂 Estructura del Proyecto
```
📁 config               → Seguridad, CORS, Swagger
📁 exception            → Excepciones globales
📁 controller           → Endpoints REST
📁 service              → Lógica de negocio
📁 repository           → Acceso a base de datos
📁 entity               → Entidades
📁 dto                  → Data Transfer Objects
```

---

## 🌐 Microservicios
- `user-service` → Gestión de usuarios y autenticación.
- `wallet-service` → Gestión de cuentas y saldos.
- `transaction-service` → Transferencias, depósitos y movimientos.
- `eureka-server` → Descubrimiento de servicios.
- `api-gateway` → Gateway en `http://localhost:8080/api`

---

## 🚀 Instrucciones para levantar el backend

### Requisitos
- Java 17 o superior
- Maven
- MySQL (local)
- Docker Desktop (opcional)

### Pasos

#### 1. Clonar el proyecto
```bash
git clone https://github.com/jgalviss1959/dp-backend.git
```

#### 2. Configuración
Editar `application.yml` o `.properties` y configurar:
- Conexión MySQL local
- Eureka server URL
- JWT secret y expiración

#### 3. Inicialización de Bases de Datos
Crea las siguientes bases de datos antes de ejecutar los microservicios:

```sql
CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS wallet_db;
CREATE DATABASE IF NOT EXISTS transaction_db;
```

Ejecútalo desde la terminal:

```bash
mysql -u root -p < crear_bases.sql
```

Introduce tu contraseña cuando se te solicite.

Esto creará automáticamente las bases necesarias para que Hibernate genere posteriormente las tablas correspondientes.

#### 4. Servicios adicionales (Docker)

**RabbitMQ (Message Broker)** para comunicación asíncrona:
```bash
docker run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
```
- Interfaz Web: [`http://localhost:15672`](http://localhost:15672) | Usuario/Contraseña: `guest`/`guest`

**Zipkin (Distributed Tracing)** para monitoreo:
```bash
docker run -d -p 9411:9411 openzipkin/zipkin
```
- Interfaz Web: [`http://localhost:9411`](http://localhost:9411)

#### 5. Iniciar Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```

#### 6. Iniciar Microservicios
Ejecutar en el siguiente orden:
```bash
cd api-gateway && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd wallet-service && mvn spring-boot:run
cd transaction-service && mvn spring-boot:run
```

#### 7. Comprobar
- Eureka → [`http://localhost:8761`](http://localhost:8761)
- API Gateway → [`http://localhost:8080/api`](http://localhost:8080/api)
- Swagger → [`http://localhost:8080/api/swagger-ui.html`](http://localhost:8080/api/swagger-ui.html)

---

## 📊 Diagrama de Arquitectura

```
[Frontend] ↔️ [API Gateway]
                     |
                     |→ [Eureka Server]
                     |
    ---------------------------------
    |                |                |
[user-service]   [wallet-service]  [transaction-service]
    |                |                |
[user_db]        [wallet_db]     [transaction_db]
    |                |                |
     ---------------------------------
                     |
        (Comunicación asincrónica)
                     |
                 [RabbitMQ]

[Todos los microservicios] → (Envían trazas) → [Zipkin]
```

---

## 🧪 Tests
Usa RestAssured para ejecutar tests automatizados:
```bash
mvn test
```

---

## ⚠️ Notas
- Se debe configurar bien Eureka para que todos los servicios se descubran.
- JWT es necesario para consumir endpoints protegidos.
- Asegura que el frontend apunta a `http://localhost:8080/api`.