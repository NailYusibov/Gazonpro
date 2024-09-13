package com.gitlab.service;


import com.gitlab.dto.PickupPointDto;
import com.gitlab.mapper.PickupPointMapper;
import com.gitlab.model.PickupPoint;
import com.gitlab.repository.PickupPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gitlab.util.ServiceUtils.updateFieldIfNotNull;

@Service
@Transactional
@RequiredArgsConstructor
public class PickupPointService {

    private final PickupPointRepository pickupPointRepository;

    private final PickupPointMapper pickupPointMapper;

    @Transactional(readOnly = true)
    public List<PickupPoint> findAll() {
        return pickupPointRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PickupPointDto> findAllDto() {
        List<PickupPoint> pickupPoints = pickupPointRepository.findAll();
        return pickupPoints.stream()
                .map(pickupPointMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PickupPoint> findById(Long id) {
        return pickupPointRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<PickupPointDto> findByIdDto(Long id) {
        return pickupPointRepository.findById(id)
                .map(pickupPointMapper::toDto);
    }

    public Page<PickupPoint> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var pickupPoints = findAll();
            if (pickupPoints.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(pickupPoints);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return pickupPointRepository.findAll(pageRequest);
    }

    public Page<PickupPointDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var pickupPoints = findAllDto();
            if (pickupPoints.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(pickupPoints);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PickupPoint> pickupPointPage = pickupPointRepository.findAll(pageRequest);
        return pickupPointPage.map(pickupPointMapper::toDto);
    }

    public PickupPoint save(PickupPoint pickupPoint) {
        return pickupPointRepository.save(pickupPoint);
    }

    public PickupPointDto saveDto(PickupPointDto pickupPointDto) {
        PickupPoint pickupPoint = pickupPointMapper.toEntity(pickupPointDto);
        PickupPoint savedPickupPoint = pickupPointRepository.save(pickupPoint);
        return pickupPointMapper.toDto(savedPickupPoint);
    }

    public PickupPointDto update(Long id, PickupPointDto pickupPointDto) {
        Optional<PickupPoint> optionalSavedPickupPoint = pickupPointRepository.findById(id);
        if (optionalSavedPickupPoint.isEmpty()) {
            throw new EntityNotFoundException("Пункт выдачи не найден");
        }

        PickupPoint savedPickupPoint = optionalSavedPickupPoint.get();

        updateFieldIfNotNull(savedPickupPoint::setDirections, pickupPointDto.getDirections());

        updateFieldIfNotNull(savedPickupPoint::setPickupPointFeatures, pickupPointDto.getPickupPointFeatures());

        updateFieldIfNotNull(savedPickupPoint::setAddress, pickupPointDto.getAddress());

        updateFieldIfNotNull(savedPickupPoint::setShelfLifeDays, pickupPointDto.getShelfLifeDays());

        return pickupPointMapper.toDto(pickupPointRepository.save(savedPickupPoint));
    }

    public Optional<PickupPoint> delete(Long id) {
        Optional<PickupPoint> optionalSavedPickupPoint = findById(id);
        if (optionalSavedPickupPoint.isPresent()) {
            pickupPointRepository.deleteById(id);
        }
        return optionalSavedPickupPoint;
    }

    public Optional<PickupPointDto> deleteDto(Long id) {
        Optional<PickupPointDto> optionalSavedPickupPointDto = findByIdDto(id);

        if (optionalSavedPickupPointDto.isPresent()) {
            pickupPointRepository.deleteById(id);
        }

        return optionalSavedPickupPointDto;
    }

}
