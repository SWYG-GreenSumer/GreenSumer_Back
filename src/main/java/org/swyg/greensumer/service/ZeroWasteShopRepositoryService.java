package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.ZeroWasteShopEntity;
import org.swyg.greensumer.repository.ZeroWasteShopEntityRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZeroWasteShopRepositoryService {

    private final ZeroWasteShopEntityRepository zeroWasteShopEntityRepository;

    @Transactional
    public void updateAddress(Long id, String address) {
        ZeroWasteShopEntity entity = zeroWasteShopEntityRepository.findById(id).orElse(null);

        if(Objects.isNull(entity)) {
            log.error("[ZeroWasteShopEntityRepositoryService - updateAddress] not found id: {, id}");
            return;
        }

        entity.changeZeroWasteShopAddress(address);
    }

    @Transactional(readOnly = true)
    public List<ZeroWasteShopEntity> findAll() {
        return zeroWasteShopEntityRepository.findAll();
    }
}
