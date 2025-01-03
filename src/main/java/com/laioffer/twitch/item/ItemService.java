package com.laioffer.twitch.item;

import com.laioffer.twitch.external.TwitchService;
import com.laioffer.twitch.external.model.Clip;
import com.laioffer.twitch.external.model.Stream;
import com.laioffer.twitch.external.model.Video;
import com.laioffer.twitch.model.TypeGroupedItemList;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private static final int SEARCH_RESULT_SIZE = 20;

    private final TwitchService twitchService; // 依赖于twitchService

    public ItemService(TwitchService twitchService) {
        this.twitchService = twitchService;
    }

    // 从twitch中拿，调用twitchService，twitchService再调用TwitchApiClient；SEARCH_RESULT_SIZE就是那个first
    @Cacheable("items") // 可以在postman里面试，第二次会比第一次快很多，1分钟之后又变慢，因为过期了
    public TypeGroupedItemList getItems(String gameId) {
        List<Video> videos = twitchService.getVideos(gameId, SEARCH_RESULT_SIZE);
        List<Clip> clips = twitchService.getClips(gameId, SEARCH_RESULT_SIZE);
        List<Stream> streams = twitchService.getStreams(List.of(gameId), SEARCH_RESULT_SIZE);
        return new TypeGroupedItemList(gameId, streams, videos, clips); // 调用TypeGroupedItemList转换类型
    }
}
