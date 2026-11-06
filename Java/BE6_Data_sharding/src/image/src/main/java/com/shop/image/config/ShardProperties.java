package com.shop.image.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "datasource")
@Getter @Setter
public class ShardProperties {
Shard shard0;
Shard shard1;
Shard shard2;
Shard shard3;

    @Getter @Setter
    @NoArgsConstructor
    static class Shard {
    String url;
    String username;
    String password;
    }
}
