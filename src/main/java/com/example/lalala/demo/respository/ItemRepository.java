package com.example.lalala.demo.respository;

import com.example.lalala.demo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(nativeQuery = true, value = "SELECT *, 111.045 * " +
            "DEGREES(ACOS(COS(RADIANS(:latitude)) " +
            "* COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS(:longitude)) " +
            "+ SIN(RADIANS(:latitude)) * SIN(RADIANS(lat)))) " +
            "AS distance_in_km FROM Item WHERE user_id != :userId AND id NOT IN (:alreadySeenIds) " +
            "ORDER BY distance_in_km LIMIT 10")
    List<Item> findNearest(@Param("userId") Long userId,
                           @Param("latitude") Double latitude,
                           @Param("longitude") Double longitude,
                           @Param("alreadySeenIds") List<Long> alreadySeenIds);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM candidate WHERE candidate_item_id IN (:candidateItemIds)")
    void deleteFromCandidatesByItemIds(@Param("candidateItemIds") List<Long> candidateItemIds);


    @Query(nativeQuery = true, value = "SELECT * FROM item iz INNER JOIN user_items_history uz " +
            "ON iz.id = uz.item_id WHERE uz.user_id = :userId ORDER BY time DESC LIMIT :startFrom, :perPage")
    List<Item> findUserHistoryItems(@Param("userId") Long userId, @Param("startFrom") Long startFrom, @Param("perPage") Long perPage);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM user_items_history WHERE user_id = :userId AND item_id IN (:ids)")
    void deleteHistoryItems(@Param("userId") Long userId, @Param("ids") List<Long> ids);
}
