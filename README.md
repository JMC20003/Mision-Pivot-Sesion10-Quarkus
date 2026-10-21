# Servicio de consulta de tipo de cambio

API imperativa en Quarkus para consultar el tipo de cambio diario de eApi Peru. Cada respuesta exitosa se registra en la base de datos y cada DNI puede realizar hasta 10 consultas por dia.

## Ejecucion local

Requiere JDK 21. En modo desarrollo usa H2 en memoria; no es necesario instalar PostgreSQL.

```powershell
.\mvnw.cmd quarkus:dev
```

La aplicacion queda disponible en `http://localhost:8082`. El esquema se actualiza al iniciar y las consultas no se eliminan durante reinicios en una base persistente.

El limite diario se configura con `exchange-rate.daily-query-limit` en `src/main/resources/application.properties` o con la variable de entorno `EXCHANGE_RATE_DAILY_QUERY_LIMIT`.

En produccion se configura PostgreSQL con `DB_USERNAME`, `DB_PASSWORD` y `DB_JDBC_URL`. Por ejemplo:

```text
DB_USERNAME=postgres
DB_PASSWORD=tu-clave
DB_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
```

## Endpoint

```http
GET http://localhost:8082/api/v1/tipo-cambio?dni=12345678
Accept: application/json
```

En Postman, crea una solicitud GET con la URL anterior y reemplaza el DNI por uno de 8 digitos.

Respuesta exitosa (`200 OK`):

```json
{
  "fecha": "2026-10-20",
  "sunat": 3.649,
  "compra": 3.647,
  "venta": 3.651
}
```

Errores:

| HTTP | Codigo | Motivo |
| --- | --- | --- |
| `400` | `INVALID_DNI` | DNI ausente o distinto de 8 digitos. |
| `429` | `DAILY_QUERY_LIMIT_EXCEEDED` | El DNI alcanzo el maximo diario configurado. |
| `502` | `EXCHANGE_RATE_PROVIDER_ERROR` | Error o respuesta vacia de la API externa. |
| `503` | `DATABASE_UNAVAILABLE` | No se pudo consultar o actualizar la base de datos. |

Solo se registra una consulta cuando la API externa responde correctamente. La verificacion del limite ocurre antes de invocar la API.

## Organizacion

- `controller`: endpoint HTTP y validacion de entrada.
- `service`: flujo de negocio y limite diario.
- `repository`: acceso a la base de datos y delegacion al cliente REST.
- `entity`: entidad JPA de auditoria de consultas.
- `proxy`: interfaz MicroProfile Rest Client y modelo recibido de eApi Peru.

Se mantiene una clase por cada paquete de la aplicación; `proxy` conserva sus dos clases de integración REST.
