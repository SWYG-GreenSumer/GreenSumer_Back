package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.domain.ZeroWasteShopEntity;
import org.swyg.greensumer.dto.ZeroWasteShop;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZeroWasterShopEntitySearchService {

    private final ZeroWasteShopRepositoryService zeroWasteShopRepositoryService;
    private final ZeroWasteShopRedisTemplateService zeroWasteShopRedisTemplateService;

    public List<ZeroWasteShop> searchShopDtoList() {
        // redis
        List<ZeroWasteShop> zeroWasteShopList = zeroWasteShopRedisTemplateService.findAll();

        if(!zeroWasteShopList.isEmpty()) {
            log.info("redis findAll success!");;
            return zeroWasteShopList;
        }

        // db
        return zeroWasteShopRepositoryService.findAll()
                .stream()
                .map(this::convertToZeroWasteShop)
                .collect(Collectors.toList());
    }

    private ZeroWasteShop convertToZeroWasteShop(ZeroWasteShopEntity entity) {
        return ZeroWasteShop.builder()
                .id(entity.getId())
                .shopName(entity.getShopName())
                .shopAddress(entity.getShopAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .build();
    }
}
