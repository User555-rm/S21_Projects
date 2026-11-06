package com.shop.image.repository.shard0;

import com.shop.image.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageShard0Repository extends JpaRepository<Image, UUID> {
}
