package com.shop.image.repository.shard3;

import com.shop.image.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageShard3Repository extends JpaRepository<Image, UUID> {
}
