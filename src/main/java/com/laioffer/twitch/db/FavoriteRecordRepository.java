package com.laioffer.twitch.db;

import com.laioffer.twitch.db.entity.FavoriteRecordEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface FavoriteRecordRepository extends ListCrudRepository<FavoriteRecordEntity, Long> {
    // 同理
    List<FavoriteRecordEntity> findAllByUserId(Long userId);
    // 看看有没有这个数据
    boolean existsByUserIdAndItemId(Long userId, Long itemId);

    // 返回的是long，没法自动根据函数名决定了
    // 这个时候函数名就可以乱写了
    @Query("SELECT item_id FROM favorite_records WHERE user_id = :userId")
    List<Long> findFavoriteItemIdsByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM favorite_records WHERE user_id = :userId AND item_id = :itemId")
    void delete(Long userId, Long itemId);
}
