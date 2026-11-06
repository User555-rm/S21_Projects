package com.shop.image.service.impl;

import com.shop.image.config.ShardRouter;
import com.shop.image.dto.ImageDto;
import com.shop.image.exception.ResourceNotFoundException;
import com.shop.image.mapper.ImageMapper;
import com.shop.image.model.Image;
import com.shop.image.repository.shard0.ImageShard0Repository;
import com.shop.image.repository.shard1.ImageShard1Repository;
import com.shop.image.repository.shard2.ImageShard2Repository;
import com.shop.image.repository.shard3.ImageShard3Repository;
import com.shop.image.service.ImageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    private final ShardRouter router;
    private final ImageMapper imageMapper;
    private final ImageShard0Repository shard0Repository;
    private final ImageShard1Repository shard1Repository;
    private final ImageShard2Repository shard2Repository;
    private final ImageShard3Repository shard3Repository;
    private final TransactionTemplate[] transactionTemplates;

    public ImageServiceImpl(ShardRouter router, ImageMapper imageMapper,
                            ImageShard0Repository shard0Repository,
                            ImageShard1Repository shard1Repository,
                            ImageShard2Repository shard2Repository,
                            ImageShard3Repository shard3Repository,
                            @Qualifier("shard0TransactionManager") PlatformTransactionManager tm0,
                            @Qualifier("shard1TransactionManager") PlatformTransactionManager tm1,
                            @Qualifier("shard2TransactionManager") PlatformTransactionManager tm2,
                            @Qualifier("shard3TransactionManager") PlatformTransactionManager tm3
    ) {
        this.router = router;
        this.imageMapper = imageMapper;
        this.shard0Repository = shard0Repository;
        this.shard1Repository = shard1Repository;
        this.shard2Repository = shard2Repository;
        this.shard3Repository = shard3Repository;
        this.transactionTemplates = new TransactionTemplate[]{
                new TransactionTemplate(tm0),
                new TransactionTemplate(tm1),
                new TransactionTemplate(tm2),
                new TransactionTemplate(tm3)
        };
    }

    @Override
    public ImageDto create(byte[] bytes) {
        UUID id = UUID.randomUUID();
        int shard = router.resolveShard(id);
        return transactionTemplates[shard].execute(status -> {
            Image image = new Image();
            image.setId(id);
            image.setImage(bytes);
            shardRepositoryFor(shard).save(image);
            return imageMapper.toDto(image);
        });
    }

    @Override
    public ImageDto update(UUID id, byte[] bytes) {
        int shard = router.resolveShard(id);
        return transactionTemplates[shard].execute(status -> {
            Image image = shardRepositoryFor(shard).findById(id)
                    .orElseThrow(ResourceNotFoundException::new);
            image.setImage(bytes);
            shardRepositoryFor(shard).save(image);
            return imageMapper.toDto(image);
        });
    }

    @Override
    public void deleteById(UUID id) {
        int shard = router.resolveShard(id);
        transactionTemplates[shard].executeWithoutResult(status -> {
            if (shardRepositoryFor(shard).findById(id).isEmpty()) {
                throw new ResourceNotFoundException();
            }
            shardRepositoryFor(shard).deleteById(id);
        });
    }

    @Override
    public byte[] getById(UUID id) {
        int shard = router.resolveShard(id);
        Image image = shardRepositoryFor(shard).findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return image.getImage();
    }

    private JpaRepository<Image, UUID> shardRepositoryFor(int shardNum) {
        return switch (shardNum) {
            case 0 -> shard0Repository;
            case 1 -> shard1Repository;
            case 2 -> shard2Repository;
            case 3 -> shard3Repository;
            default -> throw new IllegalArgumentException("shard не найден");
        };
    }
}
