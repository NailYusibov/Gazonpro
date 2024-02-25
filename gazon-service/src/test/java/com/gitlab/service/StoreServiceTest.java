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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

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
        List<StoreDto> expectedResult = List.of(generateStoreDto(1L), generateStoreDto(2L));

        when(storeRepository.findAll()).thenReturn(generateStores());
        when(storeMapper.toDtoList(generateStores())).thenReturn(expectedResult);

        List<StoreDto> actualResult = storeService.findAll();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_all_if_not_exist() {
        when(storeRepository.findAll()).thenReturn(Collections.emptyList());

        List<StoreDto> actualResult = storeService.findAll();

        assertEquals(new ArrayList<>(), actualResult);
    }

    @Test
    void should_find_storeDto_by_id_with_entity_status_is_ACTIVE() {
        long id = 1L;
        StoreDto expectedResult = generateStoreDto();
        Store store = generateStore(id);

        when(storeRepository.findById(id)).thenReturn(Optional.of(store));
        when(storeMapper.toDto(store)).thenReturn(generateStoreDto());

        Optional<StoreDto> actualResult = storeService.findById(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }

    @Test
    void should_find_storeDto_by_id_with_entity_status_is_DELETED() {
        long id = 1L;
        Optional<StoreDto> expectedResult = Optional.empty();
        Store store = generateStore(id);
        store.setEntityStatus(EntityStatus.DELETED);

        when(storeRepository.findById(id)).thenReturn(Optional.of(store));

        Optional<StoreDto> actualResult = storeService.findById(id);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_stores_with_pagination_of_not_null_fields() {
        Integer page = 0;
        Integer size = 2;

        List<Store> arrayList = new ArrayList<>();
        arrayList.add(generateStore(1L));
        arrayList.add(generateStore(2L));

        Page<Store> storePage = new PageImpl<>(arrayList);

        when(storeRepository.findAll(any(Pageable.class))).thenReturn(storePage);
        when(storeMapper.toDto(generateStore(1L))).thenReturn(generateStoreDto(1L));
        when(storeMapper.toDto(generateStore(2L))).thenReturn(generateStoreDto(2L));

        Page<StoreDto> result = storeService.getPage(page, size);

        verify(storeRepository, times(1)).findAll(any(Pageable.class));
        verify(storeMapper, times(arrayList.size())).toDto(any(Store.class));
    }

    @Test
    void should_find_stores_with_pagination_of_null_parameter_fields() {
        Page<StoreDto> expectedResult = new PageImpl<>(generateStoreDtos());

        when(storeService.findAll()).thenReturn(generateStoreDtos());
        Page<StoreDto> actualResult = storeService.getPage(null, null);

        assertEquals(expectedResult, actualResult);
    }
    @Test
    void should_find_stores_with_pagination_of_null_fields_when_products_not_found() {
        when(storeService.findAll()).thenReturn(Collections.emptyList());
        Page<StoreDto> actualResult = storeService.getPage(null, null);

        assertEquals(Page.empty(), actualResult);
    }

    @Test
    void should_find_stores_with_pagination_of_incorrect_parameter_fields() {
        Page<StoreDto> actualResult1 = storeService.getPage(0, 0);
        Page<StoreDto> actualResult2 = storeService.getPage(-1, 2);
        Page<StoreDto> actualResult3 = storeService.getPage(-1, 0);

        assertEquals(Page.empty(), actualResult1);
        assertEquals(Page.empty(), actualResult2);
        assertEquals(Page.empty(), actualResult3);
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

        storeService.delete(id);
        verify(storeRepository).save(deletedStore);
    }

    @Test
    void should_not_delete_store_when_entity_not_found() {
        long id = 1L;
        when(storeRepository.findById(id)).thenReturn(Optional.empty());

        storeService.delete(id);

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

        return storeDto;
    }

    private StoreDto generateStoreDto(Long id) {
        StoreDto storeDto = new StoreDto();
        storeDto.setId(id);
        storeDto.setOwnerId(1L);
        storeDto.setManagersId(new HashSet<>());

        return storeDto;
    }
}