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

    public StateEntity state(Long id) {
        assertCanViewStates(currentUserService.get());
        return findState(id);
    }

    public List<District> districts(Long stateId) {
        assertCanViewDistricts(currentUserService.get());
        return stateId == null ? districtRepository.findAll() : districtRepository.findByState_Id(stateId);
    }

    public District district(Long id) {
        assertCanViewDistricts(currentUserService.get());
        return findDistrict(id);
    }

    public List<Block> blocks(Long districtId) {
        assertCanViewBlocks(currentUserService.get());
        return districtId == null ? blockRepository.findAll() : blockRepository.findByDistrict_Id(districtId);
    }

    public Block block(Long id) {
        assertCanViewBlocks(currentUserService.get());
        return findBlock(id);
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
        String code = normalizeCode(request.code());
        stateRepository.findByCode(code).ifPresent(existing -> {
            throw new AppException(HttpStatus.CONFLICT, "State code already exists");
        });
        StateEntity state = new StateEntity();
        state.setName(request.name().trim());
        state.setCode(code);
        return stateRepository.save(state);
    }

    @Transactional
    public StateEntity updateState(Long id, StateRequest request) {
        assertCanCreateState(currentUserService.get());
        StateEntity state = findState(id);
        String code = normalizeCode(request.code());
        stateRepository.findByCode(code)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new AppException(HttpStatus.CONFLICT, "State code already exists");
                });
        state.setName(request.name().trim());
        state.setCode(code);
        return stateRepository.save(state);
    }

    @Transactional
    public void deleteState(Long id) {
        assertCanCreateState(currentUserService.get());
        boolean hasDistricts = districtRepository.existsByState_Id(id);
        boolean hasCenters = centerRepository.existsByState_Id(id);
        if (hasDistricts || hasCenters) {
            if (hasDistricts && hasCenters) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Cannot delete state with existing districts and centers");
            }
            if (hasDistricts) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Cannot delete state with existing districts");
            }
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot delete state with existing centers");
        }
        stateRepository.delete(findState(id));
    }

    @Transactional
    public District createDistrict(DistrictRequest request) {
        assertCanCreateDistrict(currentUserService.get());
        StateEntity state = findState(request.stateId());
        District district = new District();
        district.setName(request.name().trim());
        district.setState(state);
        return districtRepository.save(district);
    }

    @Transactional
    public District updateDistrict(Long id, DistrictRequest request) {
        assertCanCreateDistrict(currentUserService.get());
        District district = findDistrict(id);
        district.setName(request.name().trim());
        district.setState(findState(request.stateId()));
        return districtRepository.save(district);
    }

    @Transactional
    public void deleteDistrict(Long id) {
        assertCanCreateDistrict(currentUserService.get());
        boolean hasBlocks = blockRepository.existsByDistrict_Id(id);
        boolean hasCenters = centerRepository.existsByDistrict_Id(id);
        if (hasBlocks || hasCenters) {
            if (hasBlocks && hasCenters) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Cannot delete district with existing blocks and centers");
            }
            if (hasBlocks) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Cannot delete district with existing blocks");
            }
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot delete district with existing centers");
        }
        districtRepository.delete(findDistrict(id));
    }

    @Transactional
    public Block createBlock(BlockRequest request) {
        assertCanCreateBlock(currentUserService.get());
        District district = findDistrict(request.districtId());
        Block block = new Block();
        block.setName(request.name().trim());
        block.setDistrict(district);
        return blockRepository.save(block);
    }

    @Transactional
    public Block updateBlock(Long id, BlockRequest request) {
        assertCanCreateBlock(currentUserService.get());
        Block block = findBlock(id);
        block.setName(request.name().trim());
        block.setDistrict(findDistrict(request.districtId()));
        return blockRepository.save(block);
    }

    @Transactional
    public void deleteBlock(Long id) {
        assertCanCreateBlock(currentUserService.get());
        if (centerRepository.existsByBlock_Id(id)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Cannot delete block with centers");
        }
        blockRepository.delete(findBlock(id));
    }

    @Transactional
    public Center createCenter(CenterRequest request) {
        assertCanCreateCenter(currentUserService.get());
        Block block = findBlock(request.blockId());
        District district = findDistrict(request.districtId());
        StateEntity state = findState(request.stateId());
        validateHierarchy(state, district, block);

        Center center = new Center();
        center.setCode(generateCenterCode(state, district, block));
        center.setName(request.name().trim());
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
    public Center updateCenter(Long id, CenterRequest request) {
        assertCanCreateCenter(currentUserService.get());
        Center center = center(id);
        Block block = findBlock(request.blockId());
        District district = findDistrict(request.districtId());
        StateEntity state = findState(request.stateId());
        validateHierarchy(state, district, block);

        center.setName(request.name().trim());
        center.setAddress(request.address());
        center.setCity(request.city());
        center.setBlock(block);
        center.setDistrict(district);
        center.setState(state);
        center.setPhone(request.phone());
        center.setEmail(request.email());
        center.setPincode(request.pincode());
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

    private StateEntity findState(Long id) {
        return stateRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "State not found"));
    }

    private District findDistrict(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "District not found"));
    }

    private Block findBlock(Long id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Block not found"));
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private void validateHierarchy(StateEntity state, District district, Block block) {
        if (!block.getDistrict().getId().equals(district.getId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Block does not belong to the selected district");
        }
        if (!district.getState().getId().equals(state.getId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "District does not belong to the selected state");
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
