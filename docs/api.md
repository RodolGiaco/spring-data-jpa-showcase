# API reference

**English** · [Español](api.es.md) · [Back to README](../README.md)

Base URL: `http://localhost:8080/api`. Every endpoint consumes and produces JSON.

Each table has a **Spring Data** column that names the repository technique behind the endpoint, so you can go straight from a URL to the code that serves it.

## Contents

- [Status codes](#status-codes)
- [Pizzas](#pizzas)
- [Orders](#orders)
- [Customers](#customers)
- [Payloads](#payloads)
- [Examples](#examples)

---

## Status codes

| Code | When |
|---|---|
| `200 OK` | The request succeeded. List endpoints return `[]` when nothing matches. |
| `400 Bad Request` | A write targets a resource in the wrong state: creating an id that already exists, or updating/deleting one that does not. |
| `404 Not Found` | A single-resource lookup (by id, name, phone or summary) finds nothing. |

---

## Pizzas

| Method | Path | Parameters | Description | Spring Data | Codes |
|---|---|---|---|---|---|
| `GET` | `/pizzas` | `page` (default `0`), `element` (default `8`) | Page of all pizzas | `findAll(Pageable)` | 200 |
| `GET` | `/pizzas/{idPizza}` | | One pizza by id | `findById` | 200, 404 |
| `GET` | `/pizzas/available` | `page` (`0`), `element` (`3`), `sort` (`price`), `sortDirection` (`ASC`\|`DESC`) | Page of available pizzas, sorted by any property | Derived query + `Pageable` + `Sort` | 200 |
| `GET` | `/pizzas/name/{pizzaName}` | | First available pizza with that name, case-insensitive | `findFirstBy…NameIgnoreCase` | 200, 404 |
| `GET` | `/pizzas/with/{ingredient}` | | Available pizzas whose description contains the ingredient | `…DescriptionContainingIgnoreCase` | 200 |
| `GET` | `/pizzas/without/{ingredient}` | | Available pizzas whose description does not contain it | `…DescriptionNotContainingIgnoreCase` | 200 |
| `GET` | `/pizzas/vegan` | | Number of vegan pizzas in the catalog | `countByVeganTrue` | 200 |
| `GET` | `/pizzas/cheapest/{price}` | | Up to 3 available pizzas priced ≤ `price`, cheapest first | `findTop3By…PriceLessThanEqualOrderByPriceAsc` | 200 |
| `POST` | `/pizzas` | Body: [Pizza](#pizza) without `idPizza` | Create a pizza | `save` + auditing | 200, 400 |
| `PUT` | `/pizzas` | Body: [Pizza](#pizza) with an existing `idPizza` | Replace a pizza | `save` + auditing | 200, 400 |
| `PUT` | `/pizzas/price` | Body: [UpdatePizzaPrice](#updatepizzaprice) | Change only the price | `@Modifying` native query + SpEL | 200, 400 |
| `DELETE` | `/pizzas/{idPizza}` | | Delete a pizza | `deleteById` | 200, 400 |

## Orders

| Method | Path | Parameters | Description | Spring Data | Codes |
|---|---|---|---|---|---|
| `GET` | `/orders` | | All orders with their items | `findAll` | 200 |
| `GET` | `/orders/today` | | Orders placed since 00:00 today | `findByDateAfter` | 200 |
| `GET` | `/orders/outside` | | Delivery (`D`) and carryout (`C`) orders | `findAllByMethodIn` | 200 |
| `GET` | `/orders/customer/{id}` | | Orders of one customer | Native `@Query` | 200 |
| `GET` | `/orders/summary/{id}` | | Aggregated view of one order | Native `@Query` + [OrderSummary](#ordersummary) projection | 200, 404 |
| `PUT` | `/orders/update` | Body: [Order](#order) with an existing `idOrder` | Update an order header | `save` | 200, 400 |
| `POST` | `/orders/random` | Body: [RandomOrder](#randomorder) | Order one random available pizza at 20% off. Returns `true` if the order was placed, `false` if the procedure rolled back (e.g. unknown customer) | `@Procedure` | 200 |

## Customers

| Method | Path | Parameters | Description | Spring Data | Codes |
|---|---|---|---|---|---|
| `GET` | `/customers` | | All customers | JPQL `@Query` | 200 |
| `GET` | `/customers/phone/{phone}` | `phone` URL-encoded, e.g. `%28826%29%20607-2278` | Customer with that phone number | JPQL `@Query` + `@Param` | 200, 404 |

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

`created_date` and `modified_date` are stored in the database by JPA auditing and are not part of the JSON.

### UpdatePizzaPrice

```json
{ "pizzaId": 2, "newPrice": 19.0 }
```

### Order

Response shape of the order endpoints. `items` is sorted by price and each item embeds its pizza:

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

`method` is `D` (delivery), `C` (carryout) or `S` (on site). For `PUT /orders/update`, send the header fields (`idOrder`, `idCustomer`, `date`, `total`, `method`, `additionalNotes`).

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

## Examples

**Sorted, paged query**

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

**Derived query with `Top3` and `LessThanEqual`**

```bash
curl http://localhost:8080/api/pizzas/cheapest/19
```

```json
[
  { "idPizza": 2,  "name": "Margherita",        "price": 18.5,  "...": "..." },
  { "idPizza": 12, "name": "Spinach Artichoke", "price": 18.95, "...": "..." }
]
```

**Modifying query with SpEL**

```bash
curl -X PUT http://localhost:8080/api/pizzas/price \
     -H "Content-Type: application/json" \
     -d '{"pizzaId": 2, "newPrice": 19.0}'
# 200 with an empty body; 400 if the pizza does not exist
```

**Stored procedure**

```bash
curl -X POST http://localhost:8080/api/orders/random \
     -H "Content-Type: application/json" \
     -d '{"idCustomer": "863264988", "method": "D"}'
# true
```
