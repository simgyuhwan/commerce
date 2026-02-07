# JPA Optimization

## N+1 해결
@EntityGraph(attributePaths = {"items", "items.product"})
List<Order> findAll();

## Bulk 연산
@Modifying
@Query("UPDATE Order o SET o.status = :status WHERE o.id IN :ids")
void bulkUpdate(@Param("status") Status s, @Param("ids") List<Long> ids);

## 읽기 전용
@Transactional(readOnly = true)