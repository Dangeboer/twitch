package com.laioffer.twitch.model;

import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.external.model.Clip;
import com.laioffer.twitch.external.model.Stream;
import com.laioffer.twitch.external.model.Video;

import java.util.ArrayList;
import java.util.List;

public record TypeGroupedItemList(
        List<ItemEntity> streams,
        List<ItemEntity> videos,
        List<ItemEntity> clips
) {

    // 过滤；数据库里是不分类的，拿到的直接是所有items
    public TypeGroupedItemList(List<ItemEntity> items) {
        this(
                filterForType(items, ItemType.STREAM),
                filterForType(items, ItemType.VIDEO),
                filterForType(items, ItemType.CLIP)
        );
    }

    // 这里是分好的，但是数据类型不对，如从streams转换为item entity
    public TypeGroupedItemList(String gameId, List<Stream> streams, List<Video> videos, List<Clip> clips) {
        this(
                fromStreams(streams),
                fromVideos(gameId, videos),
                fromClips(clips)
        );
    }

    private static List<ItemEntity> filterForType(List<ItemEntity> items, ItemType type) {
        List<ItemEntity> filtered = new ArrayList<>();
        for (ItemEntity item : items) {
            if (item.type() == type) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private static List<ItemEntity> fromStreams(List<Stream> streams) {
        List<ItemEntity> items = new ArrayList<>();
        for (Stream stream : streams) {
            items.add(new ItemEntity(stream));
        }
        return items;
    }

    private static List<ItemEntity> fromVideos(String gameId, List<Video> videos) {
        List<ItemEntity> items = new ArrayList<>();
        for (Video video : videos) {
            items.add(new ItemEntity(gameId, video));
        }
        return items;
    }

    private static List<ItemEntity> fromClips(List<Clip> clips) {
        List<ItemEntity> items = new ArrayList<>();
        for (Clip clip : clips) {
            items.add(new ItemEntity(clip));
        }
        return items;
    }
}
