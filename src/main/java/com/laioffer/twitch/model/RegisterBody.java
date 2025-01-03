package com.laioffer.twitch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterBody( // 前端获取的
        String username,
        String password,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName
) {
}
