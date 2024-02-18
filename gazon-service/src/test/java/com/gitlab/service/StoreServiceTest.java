package com.gitlab.service;

import com.gitlab.dto.StoreDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.StoreMapper;
import com.gitlab.model.Store;
import com.gitlab.model.User;
import com.gitlab.repository.StoreRepository;
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
    void should_find_allDto() {
        StoreDto storeDto1 = generateStoreDto(1L);
        StoreDto storeDto2 = generateStoreDto(2L);
        StoreDto storeDto3 = generateStoreDto(3L);
        StoreDto storeDto4 = generateStoreDto(4L);
        StoreDto storeDto5 = generateStoreDto(5L);

        List<StoreDto> dtoList = List.of(storeDto1, storeDto2, storeDto3, storeDto4, storeDto5);

        List<StoreDto> expectedResult = dtoList;

        when(storeService.findAll()).thenReturn(generateStores());

        when(storeMapper.toDto(generateStore(1L))).thenReturn(storeDto1);
        when(storeMapper.toDto(generateStore(2L))).thenReturn(storeDto2);
        when(storeMapper.toDto(generateStore(3L))).thenReturn(storeDto3);
        when(storeMapper.toDto(generateStore(4L))).thenReturn(storeDto4);
        when(storeMapper.toDto(generateStore(5L))).thenReturn(storeDto5);

        List<StoreDto> actualResult = storeService.findAllDto();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_storeDto_by_id_with_entity_status_is_ACTIVE() {
        long id = 1L;
        StoreDto expectedResult = generateStoreDto();
        Store store = generateStore(id);

        when(storeRepository.findById(id)).thenReturn(Optional.of(store));
        when(storeMapper.toDto(store)).thenReturn(generateStoreDto());

        Optional<StoreDto> actualResult = storeService.findByIdDto(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }

    @Test
    void should_find_storeDto_by_id_with_entity_status_is_DELETED() {
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
    void should_not_update_when_storeDto_have_nullable_fields() {
        Long id = 4L;
        User user = new User();
        user.setId(4L);
        Store expectedStore = generateStore(4L);
        expectedStore.setManagers(Set.of(user));

        StoreDto storeDtoWithNullFields = generateStoreDto(4L);
        storeDtoWithNullFields.setManagersId(null);
        storeDtoWithNullFields.setOwnerId(null);

        StoreDto expectedStoreDto = generateStoreDto(4L);
        expectedStoreDto.setManagersId(Set.of(user.getId()));

        when(storeRepository.findById(id)).thenReturn(Optional.of(expectedStore));
        when(storeRepository.save(expectedStore)).thenReturn(expectedStore);
        when(storeMapper.toDto(any())).thenReturn(expectedStoreDto);

        Optional<StoreDto> actualResult = storeService.update(id, storeDtoWithNullFields);

        verify(storeMapper, times(0)).mapUserToLong(any());
        verify(storeMapper, times(0)).mapOwnerIdToUser(any());
        assertEquals(Optional.of(expectedStoreDto), actualResult);

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