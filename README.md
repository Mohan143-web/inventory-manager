# Inventory Manager

A Spring Boot full-stack inventory app: products, categories, stock levels,
search/filter with pagination, and low-stock alerts.

## 🚀 Live demo

**https://inventory-manager-k4ey.onrender.com**

Lands directly on the products list — pre-seeded with 7 products across 3
categories so the search, pagination, and low-stock features have data to
show. Hosted on Render's free tier — first request after 15 min of idle
takes ~30 s to wake the dyno. The H2 in-memory database is recreated on
each restart, so any edits revert on cold-start.

[![Deploy your own to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/Mohan143-web/inventory-manager)

## Tech stack
- Spring Boot 2.7.18 (Java 16)
- Spring Data JPA + H2 (in-memory)
- Thymeleaf + Bootstrap 5
- Bean Validation (`@Valid`, `@NotBlank`, `@Min`, `@DecimalMin`)
- Optimistic locking via `@Version`

## Features
- **Schema design**: `Category` ←one-to-many→ `Product`, unique SKU, indexes on `name` and `category_id`
- **Transactions**: `@Transactional` services for create/update/delete and stock adjustments,
  with `@Version` on `Product` for optimistic concurrency
- **Search/filter**: paginated product search by name/SKU, by category, low-stock-only toggle
- **Pagination**: server-side via Spring Data `Pageable` (`?page=1&size=10&sort=name,asc`)
- **Low-stock alerts**: `/alerts` view + `/api/products/low-stock` REST endpoint
- **REST API** with JSON exception handling (400 / 409 / 409 on stale write)
- **Seed data** loaded on startup so the UI is non-empty

## Run

```bash
mvn spring-boot:run
```

Open http://localhost:8081.

H2 console: http://localhost:8081/h2-console (JDBC URL `jdbc:h2:mem:inventory`).

## REST API

```bash
# Search (paginated)
curl 'http://localhost:8081/api/products?q=cable&size=5&page=0&sort=stockLevel,asc'

# Low stock
curl http://localhost:8081/api/products/low-stock

# Adjust stock (positive = receive, negative = sell)
curl -X POST http://localhost:8081/api/products/1/adjust-stock \
     -H 'Content-Type: application/json' -d '{"delta": -3}'

# Create
curl -X POST 'http://localhost:8081/api/products?categoryId=1' \
     -H 'Content-Type: application/json' \
     -d '{"name":"HDMI Cable","sku":"ELE-099","price":7.50,"stockLevel":50,"lowStockThreshold":10}'
```

## Build

```bash
mvn clean package
java -jar target/inventory-manager-0.0.1-SNAPSHOT.jar
```

## Project layout
```
src/main/java/com/mohan/inventory
├── InventoryManagerApplication.java
├── config/DataLoader.java
├── controller/{ProductWebController, CategoryWebController, ProductRestController, ApiExceptionHandler}.java
├── model/{Category, Product}.java
├── repository/{CategoryRepository, ProductRepository}.java
└── service/{CategoryService, ProductService}.java
```
