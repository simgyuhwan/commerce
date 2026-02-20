# Context Map Template

```mermaid
graph LR
    User[User Context<br/>Generic]
    Seller[Seller Context<br/>Core Domain]
    File[File Context<br/>Supporting]
    Payment[Payment Context<br/>Supporting]
    
    User -->|OHS| Seller
    Seller -->|ACL| File
    Seller -->|ACL| Payment
    
    style Seller fill:#f96,stroke:#333,stroke-width:3px
    style User fill:#ccc,stroke:#333,stroke-width:2px
    style File fill:#9cf,stroke:#333,stroke-width:2px
    style Payment fill:#9cf,stroke:#333,stroke-width:2px
```

## 관계 타입
- **OHS** (Open Host Service): 공개 API
- **ACL** (Anti-Corruption Layer): 방어 계층
- **CF** (Conformist): 순응
- **PS** (Published Language): 공통 언어

## 컨텍스트 타입
- **Core**: 핵심 (빨강)
- **Supporting**: 지원 (파랑)
- **Generic**: 범용 (회색)