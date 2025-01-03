package com.laioffer.twitch.recommendation;

import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.external.TwitchService;
import com.laioffer.twitch.external.model.Clip;
import com.laioffer.twitch.external.model.Stream;
import com.laioffer.twitch.external.model.Video;
import com.laioffer.twitch.favorite.FavoriteService;
import com.laioffer.twitch.model.TypeGroupedItemList;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecommendationService {

    private static final int MAX_GAME_SEED = 3; // 所有点赞过的游戏里取3个
    private static final int PER_PAGE_ITEM_SIZE = 20; // 每个种类最多推荐20个

    private final TwitchService twitchService;
    private final FavoriteService favoriteService;

    public RecommendationService(TwitchService twitchService, FavoriteService favoriteService) {
        this.twitchService = twitchService;
        this.favoriteService = favoriteService;
    }

    @Cacheable("recommend_items") // 如果取消收藏，1分钟之后才能拿到正确的推荐，这样是有问题的，要及时更新，在FavoriteService里面加上CacheEvict
    public TypeGroupedItemList recommendItems(UserEntity userEntity) { // 提供用户
        List<String> gameIds;
        Set<String> exclusions = new HashSet<>();
        if (userEntity == null) {
            gameIds  = twitchService.getTopGameIds(); // 用户未知就推荐最热门的（一般是未注册的时候）
        } else {
            List<ItemEntity> items = favoriteService.getFavoriteItems(userEntity); // 获取点赞的视频
            if (items.isEmpty()) { // 如果没有点赞的视频，推荐热门的
                gameIds = twitchService.getTopGameIds();
            } else {
                Set<String> uniqueGameIds = new HashSet<>();
                for (ItemEntity item : items) { // 遍历一遍item，取得相对应的游戏
                    uniqueGameIds.add(item.gameId()); // 用set是为了避免加入重复的游戏
                    exclusions.add(item.twitchId()); // 避免推荐已点赞过的视频
                }
                gameIds = new ArrayList<>(uniqueGameIds);
            }
        }

        int gameSize = Math.min(gameIds.size(), MAX_GAME_SEED);
        int perGameListSize = PER_PAGE_ITEM_SIZE / gameSize; // 每个game推荐多少个

        List<ItemEntity> streams = recommendStreams(gameIds, exclusions);
        List<ItemEntity> clips = recommendClips(gameIds.subList(0, gameSize), perGameListSize, exclusions);
        List<ItemEntity> videos = recommendVideos(gameIds.subList(0, gameSize), perGameListSize, exclusions);

        return new TypeGroupedItemList(streams, videos, clips);
    }

    private List<ItemEntity> recommendStreams(List<String> gameIds, Set<String> exclusions) {
        List<Stream> streams = twitchService.getStreams(gameIds, PER_PAGE_ITEM_SIZE); // 这里不分配每个游戏，因为twitch这个api自带这个功能，接受多个查询参数，不一定是均匀分布
        List<ItemEntity> resultItems = new ArrayList<>();
        for (Stream stream: streams) {
            if (!exclusions.contains(stream.id())) {
                resultItems.add(new ItemEntity(stream));
            }
        }
        return resultItems;
    }

    private List<ItemEntity> recommendVideos(List<String> gameIds, int perGameListSize, Set<String> exclusions) {
        List<ItemEntity> resultItems = new ArrayList<>();
        for (String gameId : gameIds) {
            List<Video> listPerGame = twitchService.getVideos(gameId, perGameListSize);
            for (Video video : listPerGame) {
                if (!exclusions.contains(video.id())) {
                    resultItems.add(new ItemEntity(gameId, video));
                }
            }
        }
        return resultItems;
    }

    private List<ItemEntity> recommendClips(List<String> gameIds, int perGameListSize, Set<String> exclusions) {
        List<ItemEntity> resultItem = new ArrayList<>();
        for (String gameId : gameIds) {
            List<Clip> listPerGame = twitchService.getClips(gameId, perGameListSize);
            for (Clip clip : listPerGame) {
                if (!exclusions.contains(clip.id())) {
                    resultItem.add(new ItemEntity(clip));
                }
            }
        }
        return resultItem;
    }
}
