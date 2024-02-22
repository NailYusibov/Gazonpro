//package com.gitlab.mapper;
//
//import com.gitlab.controller.AbstractIntegrationTest;
//import com.gitlab.dto.StoreDto;
//import com.gitlab.model.Store;
//import com.gitlab.model.User;
//import org.jetbrains.annotations.NotNull;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//public class StoreMapperTest extends AbstractIntegrationTest {
//
//    @Autowired
//    private StoreMapper mapper;
//
//    @Test
//    void should_map_productImage_to_Dto() {
//        Store store = getStore(1L);
//
//        StoreDto dtoTwin = mapper.toDto(store);
//
//        assertNotNull(dtoTwin);
//        assertEquals(store.getId(), dtoTwin.getId());
//        assertEquals(store.getOwner().getId(), dtoTwin.getOwnerId());
//        assertEquals(store.getManagers().size(), dtoTwin.getManagersId().size());
//        assertEquals(store.getManagers().stream().map(User::getId).collect(Collectors.toSet()), dtoTwin.getManagersId());
//    }
//
//    @Test
//    void should_map_productImageDto_to_Entity() {
//        StoreDto storeDto = getStoreDto(1L);
//
//        Store entityTwin = mapper.toEntity(storeDto);
//
//        assertNotNull(entityTwin);
//        assertEquals(entityTwin.getId(), storeDto.getId());
//        assertEquals(entityTwin.getOwner().getId(), storeDto.getOwnerId());
//        assertEquals(entityTwin.getManagers().size(), storeDto.getManagersId().size());
//        assertEquals(entityTwin.getManagers().stream().map(User::getId).collect(Collectors.toSet()), storeDto.getManagersId());
//    }
//
//
//    @Test
//    void should_map_storeList_to_DtoList() {
//        List<Store> storeList = List.of(getStore(1L), getStore(2L), getStore(3L));
//
//        List<StoreDto> storeDtoList = mapper.toDtoList(storeList);
//
//        assertNotNull(storeDtoList);
//        assertEquals(storeList.size(), storeList.size());
//        for (int i = 0; i < storeDtoList.size(); i++) {
//            StoreDto dto = storeDtoList.get(i);
//            Store entity = storeList.get(i);
//            assertEquals(entity.getId(), dto.getId());
//            assertEquals(entity.getOwner().getId(), dto.getOwnerId());
//            assertEquals(entity.getManagers().size(), dto.getManagersId().size());
//            assertEquals(entity.getManagers().stream().map(User::getId).collect(Collectors.toSet()), dto.getManagersId());
//        }
//    }
//
//    @Test
//    void should_map_storeDtoList_to_EntityList() {
//        List<StoreDto> storeDtoList = List.of(getStoreDto(1L), getStoreDto(2L), getStoreDto(3L));
//
//        List<Store> storeList = mapper.toEntityList(storeDtoList);
//
//        assertNotNull(storeList);
//        assertEquals(storeList.size(), storeList.size());
//        for (int i = 0; i < storeList.size(); i++) {
//            StoreDto dto = storeDtoList.get(i);
//            Store entity = storeList.get(i);
//            assertEquals(entity.getId(), dto.getId());
//            assertEquals(entity.getOwner().getId(), dto.getOwnerId());
//            assertEquals(entity.getManagers().size(), dto.getManagersId().size());
//            assertEquals(entity.getManagers().stream().map(User::getId).collect(Collectors.toSet()), dto.getManagersId());
//        }
//    }
//
//    @NotNull
//    private Store getStore(Long id) {
//        Store store = new Store();
//        store.setId(id);
//        User owner = new User();
//        owner.setId(id);
//        store.setOwner(owner);
//        User manager = new User();
//        manager.setId(id + 10);
//        store.setManagers(Set.of(manager));
//        return store;
//    }
//
//    @NotNull
//    private StoreDto getStoreDto(Long id) {
//        StoreDto storeDto = new StoreDto();
//        storeDto.setId(id);
//        storeDto.setOwnerId(id);
//        storeDto.setManagersId(Set.of(id + 3));
//        storeDto.setProductsId(new HashSet<>());
//        return storeDto;
//    }
//}