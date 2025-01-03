package com.laioffer.twitch.favorite;

import com.laioffer.twitch.db.FavoriteRecordRepository;
import com.laioffer.twitch.db.ItemRepository;
import com.laioffer.twitch.db.entity.FavoriteRecordEntity;
import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.model.TypeGroupedItemList;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class FavoriteService {

    private final ItemRepository itemRepository;
    private final FavoriteRecordRepository favoriteRecordRepository;

    public FavoriteService(ItemRepository itemRepository,
                           FavoriteRecordRepository favoriteRecordRepository) {
        this.itemRepository = itemRepository;
        this.favoriteRecordRepository = favoriteRecordRepository;
    }

    @CacheEvict(cacheNames = "recommend_items", key = "#user") // 清理缓存，清理"recommend_items"这个缓存，清理当前"#user"的
    @Transactional // 一系列的操作。比如银行转钱，A向B转，B出现了异常，要抛出来，此时A扣钱的指令也要撤回掉，这个叫做回滚。能实现这种回滚的就是transaction。一般有多个写操作就要（save）
    public void setFavoriteItem(UserEntity user, ItemEntity item) {
        ItemEntity persistedItem = itemRepository.findByTwitchId(item.twitchId()); // 看看存不存在，因为只有有人点过赞才会从twitch里存进数据库里
        if (persistedItem == null) { // 没有就存一下，相当于是第一个人赞了
            persistedItem = itemRepository.save(item);
        }
        if (favoriteRecordRepository.existsByUserIdAndItemId(user.id(), persistedItem.id())) { // 如果有人已经赞过了，数据库里已经有了，就会有id
            throw new DuplicateFavoriteException(); // 如果这个点赞关系已经存在，即这个用户已经点赞过，就抛一个exception，函数会终止，返回上一层
        }
        FavoriteRecordEntity favoriteRecord = new FavoriteRecordEntity(null, user.id(), persistedItem.id(), Instant.now()); // 创建一个新的点赞关系
        favoriteRecordRepository.save(favoriteRecord);
    }

    @CacheEvict(cacheNames = "recommend_items", key = "#user")
    public void unsetFavoriteItem(UserEntity user, String twitchId) {
        ItemEntity item = itemRepository.findByTwitchId(twitchId);
        if (item != null) { // item确实存在，再删掉
            favoriteRecordRepository.delete(user.id(), item.id());
        }
    }

    public List<ItemEntity> getFavoriteItems(UserEntity user) {
        List<Long> favoriteItemIds = favoriteRecordRepository.findFavoriteItemIdsByUserId(user.id());
        return itemRepository.findAllById(favoriteItemIds); // 读一个user所有点赞的item
    }

    public TypeGroupedItemList getGroupedFavoriteItems(UserEntity user) {
        List<ItemEntity> items = getFavoriteItems(user);
        return new TypeGroupedItemList(items); // 整理一下，分类
    }
}
