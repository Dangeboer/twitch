package com.laioffer.twitch.model;

import com.laioffer.twitch.db.entity.ItemEntity;

public record FavoriteRequestBody(
        ItemEntity favorite
) {}

// 这个class是为了在发请求时，如果http里body内容过长，就转移到这里
// 就是postman里body