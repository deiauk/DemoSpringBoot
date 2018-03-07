package com.example.lalala.demo.respository;

import com.example.lalala.demo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(nativeQuery = true, value = "SELECT *, 111.045 * DEGREES(ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS(:longitude)) + SIN(RADIANS(:latitude)) * SIN(RADIANS(lat)))) AS distance_in_km FROM Item ORDER BY distance_in_km LIMIT :beginAt, 5")
//    @SqlResultSetMapping(name="OrderResults",
//            entities={
//                    @EntityResult(entityClass = Item, fields = {
//                            @FieldResult(name = "id", column = "order_id"),
//                            @FieldResult(name = "quantity", column = "order_quantity"),
//                            @FieldResult(name = "item", column = "order_item")})},
//            columns={
//                    @ColumnResult(name="item_name")}
//    )

    List<Item> findNearest(@Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("beginAt") Integer beginAt);
}
