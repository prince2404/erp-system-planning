package com.hospital.erp.geographic.services;

import com.hospital.erp.common.AppException;
import com.hospital.erp.common.enums.ScopeType;
import com.hospital.erp.geographic.dto.BlockRequest;
import com.hospital.erp.geographic.dto.CenterRequest;
import com.hospital.erp.geographic.dto.DistrictRequest;
import com.hospital.erp.geographic.dto.StateRequest;
import com.hospital.erp.geographic.entities.Block;
import com.hospital.erp.geographic.entities.Center;
import com.hospital.erp.geographic.entities.District;
import com.hospital.erp.geographic.entities.StateEntity;
import com.hospital.erp.geographic.repositories.BlockRepository;
import com.hospital.erp.geographic.repositories.CenterRepository;
import com.hospital.erp.geographic.repositories.DistrictRepository;
import com.hospital.erp.geographic.repositories.StateRepository;
import com.hospital.erp.user.CurrentUserService;
import com.hospital.erp.user.ScopeFilter;
import com.hospital.erp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeographicService {
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final BlockRepository blockRepository;
    private final CenterRepository centerRepository;
    private final CurrentUserService currentUserService;
    private final ScopeFilter scopeFilter;

    public List<StateEntity> states() {
        return stateRepository.findAll();
    }

    public List<District> districts(Long stateId) {
        return stateId == null ? districtRepository.findAll() : districtRepository.findByState_Id(stateId);
    }

    public List<Block> blocks(Long districtId) {
        return districtId == null ? blockRepository.findAll() : blockRepository.findByDistrict_Id(districtId);
    }

    public List<Center> centers() {
        User user = currentUserService.get();
        if (scopeFilter.isSystem(user)) {
            return centerRepository.findByActiveTrue();
        }
        if (user.getScopeType() == ScopeType.STATE) {
            return centerRepository.findByState_IdAndActiveTrue(user.getScopeId());
        }
        if (user.getScopeType() == ScopeType.DISTRICT) {
            return centerRepository.findByDistrict_IdAndActiveTrue(user.getScopeId());
        }
        if (user.getScopeType() == ScopeType.BLOCK) {
            return centerRepository.findByBlock_IdAndActiveTrue(user.getScopeId());
        }
        if (user.getScopeType() == ScopeType.CENTER) {
            return centerRepository.findById(user.getScopeId()).stream().toList();
        }
        return List.of();
    }

    public Center center(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Center not found"));
    }

    @Transactional
    public StateEntity createState(StateRequest request) {
        stateRepository.findByCode(request.code()).ifPresent(existing -> {
            throw new AppException(HttpStatus.CONFLICT, "State code already exists");
        });
        StateEntity state = new StateEntity();
        state.setName(request.name());
        state.setCode(request.code());
        return stateRepository.save(state);
    }

    @Transactional
    public District createDistrict(DistrictRequest request) {
        StateEntity state = stateRepository.findById(request.stateId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "State not found"));
        District district = new District();
        district.setName(request.name());
        district.setState(state);
        return districtRepository.save(district);
    }

    @Transactional
    public Block createBlock(BlockRequest request) {
        District district = districtRepository.findById(request.districtId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "District not found"));
        Block block = new Block();
        block.setName(request.name());
        block.setDistrict(district);
        return blockRepository.save(block);
    }

    @Transactional
    public Center createCenter(CenterRequest request) {
        Block block = blockRepository.findById(request.blockId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Block not found"));
        District district = districtRepository.findById(request.districtId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "District not found"));
        StateEntity state = stateRepository.findById(request.stateId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "State not found"));

        Center center = new Center();
        center.setName(request.name());
        center.setAddress(request.address());
        center.setCity(request.city());
        center.setBlock(block);
        center.setDistrict(district);
        center.setState(state);
        center.setPhone(request.phone());
        center.setEmail(request.email());
        center.setActive(true);
        return centerRepository.save(center);
    }

    @Transactional
    public Center deactivateCenter(Long id) {
        Center center = center(id);
        center.setActive(false);
        return centerRepository.save(center);
    }
}
