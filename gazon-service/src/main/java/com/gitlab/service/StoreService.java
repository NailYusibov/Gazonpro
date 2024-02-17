package com.gitlab.service;

import com.gitlab.dto.StoreDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.StoreMapper;
import com.gitlab.model.Store;
import com.gitlab.repository.StoreRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public @NonNull List<StoreDto> findAllDto() {
        return findAll()
                .stream()
                .map(storeMapper::toDto)
                .toList();
    }

    public Optional<Store> findById(Long id) {
        Optional<Store> optionalStore = storeRepository.findById(id);
        if (optionalStore.isPresent() && optionalStore.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return Optional.empty();
        }
        return optionalStore;
    }

    public Optional<StoreDto> findByIdDto(Long id) {
        Optional<Store> optionalStore = storeRepository.findById(id);
        if (optionalStore.isPresent() && optionalStore.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return Optional.empty();
        }
        return optionalStore.map(storeMapper::toDto);
    }

    public Page<Store> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var stores = findAll();
            if (stores.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(stores);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return storeRepository.findAll(pageRequest);
    }

    public Page<StoreDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var stores = findAllDto();
            if (stores.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(stores);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Store> storePage = storeRepository.findAll(pageRequest);
        return storePage.map(storeMapper::toDto);
    }

    @Transactional
    public StoreDto save(StoreDto storeDto) {
        Store store = storeMapper.toEntity(storeDto);
        store.setEntityStatus(EntityStatus.ACTIVE);
        Store savedStore = storeRepository.save(store);
        return storeMapper.toDto(savedStore);
    }

    @Transactional
    public Optional<StoreDto> update(Long id, StoreDto storeDto) {
        Optional<Store> optionalSavedStore = storeRepository.findById(id);
        if (optionalSavedStore.isEmpty()) {
            return Optional.empty();
        }
        Store savedStore = optionalSavedStore.get();
        savedStore.setEntityStatus(EntityStatus.ACTIVE);
        if (storeDto.getOwnerId() != null) {
            savedStore.setOwner(storeMapper.mapOwnerIdToUser(storeDto.getOwnerId()));
        }
        if (storeDto.getManagersId() != null) {
            savedStore.setManagers(storeMapper.mapUserToLong(storeDto.getManagersId()));
        }

        Store updatedStore = storeRepository.save(savedStore);
        updatedStore.setEntityStatus(EntityStatus.ACTIVE);

        return Optional.of(storeMapper.toDto(updatedStore));
    }

    @Transactional
    public Optional<StoreDto> deleteDto(Long id) {
        Optional<Store> optionalDeletedStore = storeRepository.findById(id);
        if (optionalDeletedStore.isEmpty() || optionalDeletedStore.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return Optional.empty();
        }
        Store deletedStore = optionalDeletedStore.get();
        deletedStore.setEntityStatus(EntityStatus.DELETED);
        deletedStore.setManagers(Collections.emptySet());

        storeRepository.save(deletedStore);

        return Optional.of(storeMapper.toDto(deletedStore));
    }

}