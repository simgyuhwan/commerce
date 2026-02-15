# Architecture Layers Template

```mermaid
graph TB
    subgraph "Presentation Layer"
        Controller[SellerController<br/>REST API]
    end
    
    subgraph "Application Layer"
        UseCase[SellerUseCase<br/>Orchestration]
        Command[ApplySellerCommand]
        Query[GetSellerQuery]
    end
    
    subgraph "Domain Layer"
        Aggregate[Seller Aggregate]
        VO[Value Objects]
        Event[Domain Events]
        Repo[Repository<br/>Interface]
    end
    
    subgraph "Infrastructure Layer"
        RepoImpl[SellerRepositoryImpl<br/>JPA]
        External[External API<br/>Bank]
        EventBus[Event Publisher]
    end
    
    Controller --> UseCase
    UseCase --> Command
    UseCase --> Query
    Command --> Aggregate
    Query --> Repo
    Aggregate --> VO
    Aggregate --> Event
    Repo -.->|구현| RepoImpl
    UseCase --> External
    Event --> EventBus
    
    style Aggregate fill:#f96,stroke:#333,stroke-width:3px
    style VO fill:#bbf,stroke:#333,stroke-width:2px
    style Event fill:#9f9,stroke:#333,stroke-width:2px
```

## 레이어별 역할

### Presentation
- REST API
- Request/Response DTO
- Validation

### Application
- Use Case 조율
- 트랜잭션 경계
- Command/Query 분리

### Domain
- 비즈니스 로직
- Aggregate
- Domain Event
- Repository Interface

### Infrastructure
- DB 연동
- 외부 API
- Event Publishing