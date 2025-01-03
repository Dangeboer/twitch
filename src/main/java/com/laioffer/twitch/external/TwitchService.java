package com.laioffer.twitch.external;

import com.laioffer.twitch.external.model.Clip;
import com.laioffer.twitch.external.model.Game;
import com.laioffer.twitch.external.model.Stream;
import com.laioffer.twitch.external.model.Video;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TwitchService {

    private final TwitchApiClient twitchApiClient;

    public TwitchService(TwitchApiClient twitchApiClient) {
        this.twitchApiClient = twitchApiClient;
    }

    @Cacheable("top_games") // 第一次从twitch拿，后面只要没过期，都可以从cache里拿；"top_games"是自己取的
    public List<Game> getTopGames() {
        return twitchApiClient.getTopGames().data();
    }

    @Cacheable("games_by_name")
    public List<Game> getGames(String name) {
        return twitchApiClient.getGames(name).data();
    }

    public List<Stream> getStreams(List<String> gameIds, int first) {
        return twitchApiClient.getStreams(gameIds, first).data();
    }

    public List<Video> getVideos(String gameId, int first) {
        return twitchApiClient.getVideos(gameId, first).data();
    }

    public List<Clip> getClips(String gameId, int first) {
        return twitchApiClient.getClips(gameId, first).data();
    }

    public List<String> getTopGameIds() {
        List<String> topGameIds = new ArrayList<>();
        for (Game game : getTopGames()) {
            topGameIds.add(game.id());
        }
        return topGameIds;
    }
}
