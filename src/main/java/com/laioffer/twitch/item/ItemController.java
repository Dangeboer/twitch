package com.laioffer.twitch.item;

import com.laioffer.twitch.model.TypeGroupedItemList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {

    private final ItemService itemService; // 需要用到itemService

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/search")
    public TypeGroupedItemList search(@RequestParam("game_id") String gameId) { // 需要提供game id
        return itemService.getItems(gameId);
    }
}
