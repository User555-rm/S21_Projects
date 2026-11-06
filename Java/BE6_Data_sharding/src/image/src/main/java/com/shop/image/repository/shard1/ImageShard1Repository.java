package com.shop.image.repository.shard1;

import com.shop.image.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageShard1Repository extends JpaRepository<Image, UUID> {
}
