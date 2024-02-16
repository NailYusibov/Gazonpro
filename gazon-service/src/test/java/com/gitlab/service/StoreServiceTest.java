package com.gitlab.service;

import com.gitlab.dto.StoreDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.StoreMapper;
import com.gitlab.model.Store;
import com.gitlab.repository.StoreRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreMapper storeMapper;

    @InjectMocks
    private StoreService storeService;

    @Test
    void should_find_all() {
        List<Store> expectedResult = generateStores();
        when(storeRepository.findAll()).thenReturn(generateStores());

        List<Store> actualResult = storeService.findAll();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_store_by_id_with_entity_status_is_ACTIVE() {
        long id = 1L;
        StoreDto expectedResult = generateStoreDto();
        Store store = generateStore(id);

        when(storeRepository.findById(id)).thenReturn(Optional.of(store));
        when(storeMapper.toDto(store)).thenReturn(generateStoreDto());

        Optional<StoreDto> actualResult = storeService.findByIdDto(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }
    @Test
    void should_find_store_by_id_with_entity_status_is_DELETED() {
        long id = 1L;
        Optional<StoreDto> expectedResult = Optional.empty();
        Store store = generateStore(id);
        store.setEntityStatus(EntityStatus.DELETED);

        when(storeRepository.findById(id)).thenReturn(Optional.of(store));

        Optional<StoreDto> actualResult = storeService.findByIdDto(id);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_save_store() {
        Store expectedResult = generateStore();
        when(storeRepository.save(expectedResult)).thenReturn(expectedResult);

        Store actualResult = storeRepository.save(expectedResult);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_update_store_with_entity_status_is_ACTIVE() {
        long id = 1L;
        StoreDto storeToUpdate = generateStoreDto();

        Store storeBeforeUpdate = generateStoreBefore();

        StoreDto updatedStore = generateStoreDto();

        when(storeRepository.findById(id)).thenReturn(Optional.of(storeBeforeUpdate));// ok
        when(storeRepository.save(any())).thenReturn(storeBeforeUpdate);
        when(storeMapper.toDto(storeBeforeUpdate)).thenReturn(updatedStore);

        Optional<StoreDto> actualResult = storeService.update(id, storeToUpdate);

        assertEquals(Optional.of(updatedStore), actualResult);
    }

    @Test
    void should_update_store_with_entity_status_is_DELETED() {
        long id = 1L;
        StoreDto storeToUpdate = generateStoreDto();

        Store storeBeforeUpdate = generateStoreBefore();
        storeBeforeUpdate.setEntityStatus(EntityStatus.DELETED);

        StoreDto updatedStore = generateStoreDto();

        when(storeRepository.findById(id)).thenReturn(Optional.of(storeBeforeUpdate));// ok
        when(storeRepository.save(any())).thenReturn(storeBeforeUpdate);
        when(storeMapper.toDto(storeBeforeUpdate)).thenReturn(updatedStore);

        Optional<StoreDto> actualResult = storeService.update(id, storeToUpdate);

        assertEquals(Optional.of(updatedStore), actualResult);
    }

    @Test
    void should_not_update_store_when_entity_not_found() {
        long id = 4L;
        StoreDto storeToUpdateWith = generateStoreDto();

        when(storeRepository.findById(id)).thenReturn(Optional.empty());

        Optional<StoreDto> actualResult = storeService.update(id, storeToUpdateWith);

        verify(storeRepository, never()).save(any());
        assertNull(actualResult.orElse(null));
    }

    @Test
    void should_delete_store() {
        long id = 1L;
        Store deletedStore = generateStore(id);
        deletedStore.setEntityStatus(EntityStatus.DELETED);

        when(storeRepository.findById(id)).thenReturn(Optional.of(generateStore(id)));
        when(storeRepository.save(any())).thenReturn(generateStore(id));
        when(storeMapper.toDto(any())).thenReturn(generateStoreDto());

        storeService.deleteDto(id);
        verify(storeRepository).save(deletedStore);
    }

    @Test
    void should_not_delete_store_when_entity_not_found() {
        long id = 1L;
        when(storeRepository.findById(id)).thenReturn(Optional.empty());

        storeService.deleteDto(id);

        verify(storeRepository, never()).deleteById(anyLong());
    }

    private List<Store> generateStores() {
        return List.of(
                generateStore(1L),
                generateStore(2L),
                generateStore(3L),
                generateStore(4L),
                generateStore(5L));
    }
    private List<StoreDto> generateStoreDtos() {
        return List.of(
                generateStoreDto(1L),
                generateStoreDto(2L),
                generateStoreDto(3L),
                generateStoreDto(4L),
                generateStoreDto(5L));
    }

    private Store generateStore(Long id) {
        Store store = generateStore();
        store.setId(id);
        store.setEntityStatus(EntityStatus.ACTIVE);
        store.setProducts(new HashSet<>());
        store.setManagers(new HashSet<>());
        return store;
    }

    private Store generateStore() {
        Store store = new Store();
        store.setId(1L);
        store.setEntityStatus(EntityStatus.ACTIVE);
        return store;
    }

    private Store generateStoreBefore() {
        Store store = new Store();
        store.setId(1L);
        store.setEntityStatus(EntityStatus.ACTIVE);
        return store;
    }

    private StoreDto generateStoreDto() {
        StoreDto storeDto = new StoreDto();
        storeDto.setId(1L);
        storeDto.setOwnerId(1L);
        storeDto.setManagersId(new HashSet<>());
        storeDto.setProductsId(new HashSet<>());

        return storeDto;
    }
    private StoreDto generateStoreDto(Long id) {
        StoreDto storeDto = new StoreDto();
        storeDto.setId(id);
        storeDto.setOwnerId(1L);
        storeDto.setManagersId(new HashSet<>());
        storeDto.setProductsId(new HashSet<>());

        return storeDto;
    }
}