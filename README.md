# Saga Pattern — Orchestration vs Choreography

A hands-on implementation of both Saga variants side-by-side using Spring Boot, Axon Framework, and Apache Kafka.
Domain: **Order Processing** (Place → Pay → Reserve Inventory → Ship), including compensating transactions on failure.

---

## Table of Contents

- [Overview](#overview)
- [Patterns Compared](#patterns-compared)
- [Technical Stack](#technical-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Infrastructure](#infrastructure)
- [Getting Started](#getting-started)
- [REST API](#rest-api)
- [Failure Scenarios](#failure-scenarios)

---

## Overview

The **Saga pattern** solves distributed transaction management across services without 2PC (two-phase commit).
Each step in the saga has a corresponding **compensating transaction** that undoes its effect if a later step fails.

This project implements the same Order Processing saga using both approaches so you can compare them directly.

---

## Patterns Compared

| Aspect | Orchestration (Axon) | Choreography (Kafka) |
|---|---|---|
| Coordinator | Central Saga class issues commands | None — each service reacts to events |
| Coupling | Services know about the orchestrator | Services only know about events |
| Visibility | Full saga state in one place | Distributed — harder to trace |
| Failure handling | Saga explicitly sends compensating commands | Each service emits a failure event |
| Complexity | Higher initial setup | Grows complex at scale |
| Best for | Complex flows, strict ordering | Loose coupling, independent teams |

---

## Technical Stack

### Core

| Technology | Version | Role |
|---|---|---|
| Java | 21 | Language (Virtual Threads enabled) |
| Spring Boot | 4.0.6 | Application framework |
| Spring Web MVC | via Boot | REST endpoints |
| Spring Data JPA | via Boot | Persistence |
| H2 Database | via Boot | In-memory DB (dev) |

### Orchestrator

| Technology | Version | Role |
|---|---|---|
| Axon Framework | 4.10.x | Saga orchestration engine |
| Axon Spring Boot Starter | 4.10.x | Auto-configuration |
| Axon Server | 4.10.x | Event store + message routing (Docker) |

### Choreography

| Technology | Version | Role |
|---|---|---|
| Apache Kafka | 3.7.x | Distributed event bus |
| Spring Kafka | via Boot | Kafka producer/consumer integration |
| Apache Zookeeper | 3.8.x | Kafka cluster coordination (Docker) |

### Infrastructure

| Technology | Role |
|---|---|
| Docker | Container runtime |
| Docker Compose | Local dev orchestration (Axon Server + Kafka + Zookeeper) |

### Testing

| Technology | Role |
|---|---|
| JUnit 5 | Unit and integration tests |
| Axon Test Fixtures | Saga + Aggregate BDD-style testing |
| Embedded Kafka | Choreography integration tests |

---

## Architecture

### Orchestration Flow (Axon)

```
POST /api/orchestrator/orders
        │
        ▼
  OrderController
        │ PlaceOrderCommand
        ▼
  OrderAggregate (Axon)
        │ OrderPlacedEvent
        ▼
  OrderSaga (@Saga)
        │ ProcessPaymentCommand
        ▼
  PaymentAggregate
        │ PaymentProcessedEvent / PaymentFailedEvent
        ▼
  OrderSaga
        │ ReserveInventoryCommand / CancelPaymentCommand (compensation)
        ▼
  InventoryAggregate
        │ InventoryReservedEvent / InventoryFailedEvent
        ▼
  OrderSaga
        │ ShipOrderCommand / CancelPaymentCommand + CancelOrderCommand (compensation)
        ▼
  ShipmentAggregate
        │ OrderShippedEvent
        ▼
  OrderSaga → marks saga complete
```

### Choreography Flow (Kafka)

```
POST /api/choreography/orders
        │
        ▼
  OrderController
        │ publishes → orders.placed (Kafka)
        ▼
  PaymentService (consumer: orders.placed)
        │ publishes → payment.processed OR payment.failed
        ▼
  InventoryService (consumer: payment.processed)
        │ publishes → inventory.reserved OR inventory.failed
        ▼
  ShipmentService (consumer: inventory.reserved)
        │ publishes → order.shipped

  On failure:
  InventoryService publishes inventory.failed
        │
  PaymentService (consumer: inventory.failed) → compensates → publishes payment.cancelled
        │
  OrderService (consumer: payment.cancelled) → marks order CANCELLED
```

---

## Project Structure

```
src/main/java/io/housedevinci/sagapattern/
├── api/
│   ├── OrchestratorOrderController.java   # POST /api/orchestrator/orders
│   └── ChoreographyOrderController.java   # POST /api/choreography/orders
│
├── orchestrator/
│   ├── command/                           # PlaceOrderCommand, ProcessPaymentCommand, ...
│   ├── event/                             # OrderPlacedEvent, PaymentProcessedEvent, ...
│   ├── aggregate/                         # OrderAggregate, PaymentAggregate, InventoryAggregate, ShipmentAggregate
│   └── saga/
│       └── OrderSaga.java                 # @Saga — central orchestrator
│
└── choreography/
    ├── event/                             # Plain POJOs published to Kafka topics
    ├── producer/
    │   └── OrderEventProducer.java
    └── consumer/
        ├── PaymentEventConsumer.java
        ├── InventoryEventConsumer.java
        └── ShipmentEventConsumer.java
```

---

## Infrastructure

`docker-compose.yml` starts:

| Service | Port | Purpose |
|---|---|---|
| `axon-server` | 8024 (UI), 8124 (gRPC) | Orchestrator event store + message bus |
| `zookeeper` | 2181 | Kafka coordination |
| `kafka` | 9092 | Choreography event bus |

---

## Getting Started

### Prerequisites

- Java 21
- Docker + Docker Compose

### Run infrastructure

```bash
docker-compose up -d
```

Axon Server UI: http://localhost:8024

### Run the application

```bash
./gradlew bootRun
```

---

## REST API

### Trigger Orchestrator Saga

```http
POST /api/orchestrator/orders
Content-Type: application/json

{
  "customerId": "customer-1",
  "productId": "product-42",
  "quantity": 2,
  "amount": 99.99
}
```

### Trigger Choreography Saga

```http
POST /api/choreography/orders
Content-Type: application/json

{
  "customerId": "customer-1",
  "productId": "product-42",
  "quantity": 2,
  "amount": 99.99
}
```

---

## Failure Scenarios

Pass `"simulateFailure": "PAYMENT"` or `"simulateFailure": "INVENTORY"` in the request body to trigger compensating transactions and observe rollback behavior in both patterns.

```json
{
  "customerId": "customer-1",
  "productId": "product-42",
  "quantity": 2,
  "amount": 99.99,
  "simulateFailure": "INVENTORY"
}
```

---

## Key Concepts

**Compensating Transaction** — the undo operation for a saga step. Not a rollback; it's a new forward action that reverses the effect.

**Axon Saga** — a stateful event listener managed by Axon. Survives restarts because its state is persisted in the event store.

**Idempotency** — Kafka consumers must be idempotent. Each consumer checks if it already processed an event before acting.
