package com.laioffer.twitch.external;

import com.laioffer.twitch.external.model.ClipResponse;
import com.laioffer.twitch.external.model.GameResponse;
import com.laioffer.twitch.external.model.StreamResponse;
import com.laioffer.twitch.external.model.VideoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "twitch-api") // 对应yml文件中11行，名字是自己取的，但要保持一致
public interface TwitchApiClient {

    @GetMapping("/games")
    GameResponse getGames(@RequestParam("name") String name);

    @GetMapping("/games/top")
    GameResponse getTopGames();

    @GetMapping("/videos")
    VideoResponse getVideos(@RequestParam("game_id") String gameId, @RequestParam("first") int first);
    // first是指返回前xx个

    @GetMapping("/clips")
    ClipResponse getClips(@RequestParam("game_id") String gameId, @RequestParam("first") int first);

    @GetMapping("/streams")
    StreamResponse getStreams(@RequestParam("game_id") List<String> gameIds, @RequestParam("first") int first);
}
