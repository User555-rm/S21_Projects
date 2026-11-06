package com.shop.image.repository.shard2;

import com.shop.image.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageShard2Repository extends JpaRepository<Image, UUID> {
}
