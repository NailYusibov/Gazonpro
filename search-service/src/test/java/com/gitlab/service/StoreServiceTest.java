package com.gitlab.service;

import com.gitlab.dto.StoreDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.StoreMapper;
import com.gitlab.model.Store;
import com.gitlab.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private StoreDto generateStoreDto() {
        StoreDto storeDto = new StoreDto();
        storeDto.setId(1L);
        storeDto.setOwnerId(1L);
        storeDto.setManagersId(new HashSet<>());

        return storeDto;
    }
}