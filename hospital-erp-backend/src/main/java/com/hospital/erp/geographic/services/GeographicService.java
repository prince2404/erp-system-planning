package com.hospital.erp.geographic.services;

import com.hospital.erp.common.AppException;
import com.hospital.erp.common.enums.Role;
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
        assertCanViewStates(currentUserService.get());
        return stateRepository.findAll();
    }

    public List<District> districts(Long stateId) {
        assertCanViewDistricts(currentUserService.get());
        return stateId == null ? districtRepository.findAll() : districtRepository.findByState_Id(stateId);
    }

    public List<Block> blocks(Long districtId) {
        assertCanViewBlocks(currentUserService.get());
        return districtId == null ? blockRepository.findAll() : blockRepository.findByDistrict_Id(districtId);
    }

    public List<Center> centers() {
        User user = currentUserService.get();
        assertCanViewCenters(user);
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
        assertCanViewCenters(currentUserService.get());
        return centerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Center not found"));
    }

    @Transactional
    public StateEntity createState(StateRequest request) {
        assertCanCreateState(currentUserService.get());
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
        assertCanCreateDistrict(currentUserService.get());
        StateEntity state = stateRepository.findById(request.stateId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "State not found"));
        District district = new District();
        district.setName(request.name());
        district.setState(state);
        return districtRepository.save(district);
    }

    @Transactional
    public Block createBlock(BlockRequest request) {
        assertCanCreateBlock(currentUserService.get());
        District district = districtRepository.findById(request.districtId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "District not found"));
        Block block = new Block();
        block.setName(request.name());
        block.setDistrict(district);
        return blockRepository.save(block);
    }

    @Transactional
    public Center createCenter(CenterRequest request) {
        assertCanCreateCenter(currentUserService.get());
        Block block = blockRepository.findById(request.blockId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Block not found"));
        District district = districtRepository.findById(request.districtId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "District not found"));
        StateEntity state = stateRepository.findById(request.stateId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "State not found"));
        if (!block.getDistrict().getId().equals(district.getId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Block does not belong to the selected district");
        }
        if (!district.getState().getId().equals(state.getId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "District does not belong to the selected state");
        }

        Center center = new Center();
        center.setCode(generateCenterCode(state, district, block));
        center.setName(request.name());
        center.setAddress(request.address());
        center.setCity(request.city());
        center.setBlock(block);
        center.setDistrict(district);
        center.setState(state);
        center.setPhone(request.phone());
        center.setEmail(request.email());
        center.setPincode(request.pincode());
        center.setActive(true);
        return centerRepository.save(center);
    }

    @Transactional
    public Center deactivateCenter(Long id) {
        assertCanCreateCenter(currentUserService.get());
        Center center = center(id);
        center.setActive(false);
        return centerRepository.save(center);
    }

    private void assertCanViewStates(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.STATE_MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot view states");
        }
    }

    private void assertCanViewDistricts(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.STATE_MANAGER
                && actor.getRole() != Role.DISTRICT_MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot view districts");
        }
    }

    private void assertCanViewBlocks(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.STATE_MANAGER
                && actor.getRole() != Role.DISTRICT_MANAGER
                && actor.getRole() != Role.BLOCK_MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot view blocks");
        }
    }

    private void assertCanViewCenters(User actor) {
        if (actor.getRole() == Role.PATIENT) {
            throw new AppException(HttpStatus.FORBIDDEN, "Patients cannot view centers");
        }
    }

    private void assertCanCreateState(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN && actor.getRole() != Role.ADMIN) {
            throw new AppException(HttpStatus.FORBIDDEN, "Only SUPER_ADMIN or ADMIN can create states");
        }
    }

    private void assertCanCreateDistrict(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.STATE_MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot create districts");
        }
    }

    private void assertCanCreateBlock(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.STATE_MANAGER
                && actor.getRole() != Role.DISTRICT_MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot create blocks");
        }
    }

    private void assertCanCreateCenter(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.STATE_MANAGER
                && actor.getRole() != Role.DISTRICT_MANAGER
                && actor.getRole() != Role.BLOCK_MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot manage centers");
        }
    }

    private String generateCenterCode(StateEntity state, District district, Block block) {
        String districtCode = district.getName().replaceAll("[^A-Za-z]", "").toUpperCase();
        if (districtCode.length() > 3) {
            districtCode = districtCode.substring(0, 3);
        }
        while (districtCode.length() < 3) {
            districtCode += "X";
        }

        long sequence = centerRepository.countByBlock_Id(block.getId()) + 1;
        String code = "ASK-%s-%s-%03d-%03d".formatted(state.getCode(), districtCode, block.getId(), sequence);
        int guard = 1;
        while (centerRepository.existsByCode(code)) {
            sequence += 1;
            guard += 1;
            code = "ASK-%s-%s-%03d-%03d".formatted(state.getCode(), districtCode, block.getId(), sequence + guard);
        }
        return code;
    }
}
