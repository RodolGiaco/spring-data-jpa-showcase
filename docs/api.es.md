# Referencia de la API

[English](api.md) · **Español** · [Volver al README](../README.es.md)

URL base: `http://localhost:8080/api`. Todos los endpoints reciben y devuelven JSON.

Cada tabla tiene una columna **Spring Data** que indica la técnica de repositorio detrás de cada endpoint, así se puede ir directo de una URL al código que la resuelve.

## Contenido

- [Códigos de estado](#códigos-de-estado)
- [Pizzas](#pizzas)
- [Pedidos](#pedidos)
- [Clientes](#clientes)
- [Payloads](#payloads)
- [Ejemplos](#ejemplos)

---

## Códigos de estado

| Código | Cuándo |
|---|---|
| `200 OK` | La operación fue exitosa. Los endpoints que devuelven listas responden `[]` si no hay coincidencias. |
| `400 Bad Request` | Una escritura apunta a un recurso en un estado inválido: crear un id que ya existe, o actualizar/borrar uno que no existe. |
| `404 Not Found` | Una búsqueda de un único recurso (por id, nombre, teléfono o resumen) no encuentra nada. |

---

## Pizzas

| Método | Ruta | Parámetros | Descripción | Spring Data | Códigos |
|---|---|---|---|---|---|
| `GET` | `/pizzas` | `page` (default `0`), `element` (default `8`) | Página con todas las pizzas | `findAll(Pageable)` | 200 |
| `GET` | `/pizzas/{idPizza}` | | Una pizza por id | `findById` | 200, 404 |
| `GET` | `/pizzas/available` | `page` (`0`), `element` (`3`), `sort` (`price`), `sortDirection` (`ASC`\|`DESC`) | Página de pizzas disponibles, ordenada por cualquier propiedad | Consulta derivada + `Pageable` + `Sort` | 200 |
| `GET` | `/pizzas/name/{pizzaName}` | | Primera pizza disponible con ese nombre, sin distinguir mayúsculas | `findFirstBy…NameIgnoreCase` | 200, 404 |
| `GET` | `/pizzas/with/{ingredient}` | | Pizzas disponibles cuya descripción contiene el ingrediente | `…DescriptionContainingIgnoreCase` | 200 |
| `GET` | `/pizzas/without/{ingredient}` | | Pizzas disponibles cuya descripción no lo contiene | `…DescriptionNotContainingIgnoreCase` | 200 |
| `GET` | `/pizzas/vegan` | | Cantidad de pizzas veganas del catálogo | `countByVeganTrue` | 200 |
| `GET` | `/pizzas/cheapest/{price}` | | Hasta 3 pizzas disponibles con precio ≤ `price`, de menor a mayor | `findTop3By…PriceLessThanEqualOrderByPriceAsc` | 200 |
| `POST` | `/pizzas` | Body: [Pizza](#pizza) sin `idPizza` | Crea una pizza | `save` + auditoría | 200, 400 |
| `PUT` | `/pizzas` | Body: [Pizza](#pizza) con un `idPizza` existente | Reemplaza una pizza | `save` + auditoría | 200, 400 |
| `PUT` | `/pizzas/price` | Body: [UpdatePizzaPrice](#updatepizzaprice) | Cambia solo el precio | Consulta nativa `@Modifying` + SpEL | 200, 400 |
| `DELETE` | `/pizzas/{idPizza}` | | Elimina una pizza | `deleteById` | 200, 400 |

## Pedidos

| Método | Ruta | Parámetros | Descripción | Spring Data | Códigos |
|---|---|---|---|---|---|
| `GET` | `/orders` | | Todos los pedidos con sus ítems | `findAll` | 200 |
| `GET` | `/orders/today` | | Pedidos realizados desde las 00:00 de hoy | `findByDateAfter` | 200 |
| `GET` | `/orders/outside` | | Pedidos para delivery (`D`) y para llevar (`C`) | `findAllByMethodIn` | 200 |
| `GET` | `/orders/customer/{id}` | | Pedidos de un cliente | `@Query` nativa | 200 |
| `GET` | `/orders/summary/{id}` | | Vista agregada de un pedido | `@Query` nativa + proyección [OrderSummary](#ordersummary) | 200, 404 |
| `PUT` | `/orders/update` | Body: [Order](#order) con un `idOrder` existente | Actualiza la cabecera de un pedido | `save` | 200, 400 |
| `POST` | `/orders/random` | Body: [RandomOrder](#randomorder) | Pide una pizza disponible al azar con 20% de descuento. Devuelve `true` si el pedido se registró, `false` si el procedure hizo rollback (por ejemplo, cliente inexistente) | `@Procedure` | 200 |

## Clientes

| Método | Ruta | Parámetros | Descripción | Spring Data | Códigos |
|---|---|---|---|---|---|
| `GET` | `/customers` | | Todos los clientes | `@Query` JPQL | 200 |
| `GET` | `/customers/phone/{phone}` | `phone` codificado para URL, p. ej. `%28826%29%20607-2278` | Cliente con ese teléfono | `@Query` JPQL + `@Param` | 200, 404 |

---

## Payloads

### Pizza

```json
{
  "idPizza": 2,
  "name": "Margherita",
  "description": "Fior de Latte, Homemade Tomato Sauce, Extra Virgin Olive Oil & Basil.",
  "price": 18.5,
  "vegetarian": true,
  "vegan": false,
  "available": true
}
```

`created_date` y `modified_date` los guarda en la base la auditoría JPA y no forman parte del JSON.

### UpdatePizzaPrice

```json
{ "pizzaId": 2, "newPrice": 19.0 }
```

### Order

Forma de la respuesta de los endpoints de pedidos. `items` viene ordenado por precio y cada ítem incluye su pizza:

```json
{
  "idOrder": 1,
  "idCustomer": "192758012",
  "date": "2026-09-18T12:37:17",
  "total": 42.95,
  "method": "D",
  "additionalNotes": "Don't be late pls.",
  "items": [
    {
      "idOrder": 1,
      "idItem": 2,
      "idPizza": 4,
      "quantity": 1.0,
      "price": 19.95,
      "pizza": { "idPizza": 4, "name": "Avocado Festival", "price": 19.95, "...": "..." }
    }
  ]
}
```

`method` es `D` (delivery), `C` (para llevar) o `S` (en el local). Para `PUT /orders/update` se envían los campos de cabecera (`idOrder`, `idCustomer`, `date`, `total`, `method`, `additionalNotes`).

### OrderSummary

```json
{
  "orderId": 1,
  "customerName": "Drew Watson",
  "orderDate": "2026-09-18T12:37:17",
  "orderTotal": 42.95,
  "pizzaNames": "Pepperoni,Avocado Festival"
}
```

### RandomOrder

```json
{ "idCustomer": "863264988", "method": "D" }
```

---

## Ejemplos

**Consulta paginada y ordenada**

```bash
curl "http://localhost:8080/api/pizzas/available?page=0&element=2&sort=price&sortDirection=DESC"
```

```json
{
  "content": [
    { "idPizza": 6, "name": "Goat Chesse", "price": 24.0, "...": "..." },
    { "idPizza": 1, "name": "Pepperoni",   "price": 23.0, "...": "..." }
  ],
  "number": 0,
  "size": 2,
  "numberOfElements": 2,
  "first": true,
  "last": false,
  "sort": { "sorted": true, "unsorted": false, "empty": false },
  "...": "totalElements, totalPages, pageable"
}
```

**Consulta derivada con `Top3` y `LessThanEqual`**

```bash
curl http://localhost:8080/api/pizzas/cheapest/19
```

```json
[
  { "idPizza": 2,  "name": "Margherita",        "price": 18.5,  "...": "..." },
  { "idPizza": 12, "name": "Spinach Artichoke", "price": 18.95, "...": "..." }
]
```

**Consulta de modificación con SpEL**

```bash
curl -X PUT http://localhost:8080/api/pizzas/price \
     -H "Content-Type: application/json" \
     -d '{"pizzaId": 2, "newPrice": 19.0}'
# 200 con cuerpo vacío; 400 si la pizza no existe
```

**Stored procedure**

```bash
curl -X POST http://localhost:8080/api/orders/random \
     -H "Content-Type: application/json" \
     -d '{"idCustomer": "863264988", "method": "D"}'
# true
```
