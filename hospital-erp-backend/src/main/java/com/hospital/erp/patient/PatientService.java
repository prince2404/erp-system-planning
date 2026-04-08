package com.hospital.erp.patient;

import com.hospital.erp.billing.BillingService;
import com.hospital.erp.common.AppException;
import com.hospital.erp.common.PageResponse;
import com.hospital.erp.common.enums.AdmissionStatus;
import com.hospital.erp.common.enums.Role;
import com.hospital.erp.geographic.entities.Center;
import com.hospital.erp.geographic.repositories.CenterRepository;
import com.hospital.erp.patient.dto.BedRequest;
import com.hospital.erp.patient.dto.DischargeRequest;
import com.hospital.erp.patient.dto.IpdAdmissionRequest;
import com.hospital.erp.patient.dto.OpdStatusRequest;
import com.hospital.erp.patient.dto.OpdVisitRequest;
import com.hospital.erp.patient.dto.PatientRequest;
import com.hospital.erp.user.CurrentUserService;
import com.hospital.erp.user.User;
import com.hospital.erp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final OpdVisitRepository opdVisitRepository;
    private final IpdAdmissionRepository ipdAdmissionRepository;
    private final BedRepository bedRepository;
    private final CenterRepository centerRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    @Lazy
    private final BillingService billingService;

    @Transactional
    public Patient register(PatientRequest request) {
        User actor = currentUserService.get();
        assertCanRegisterPatients(actor);
        Patient patient = new Patient();
        patient.setUhid(generateUhid());
        patient.setName(request.name());
        patient.setAge(request.age());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setBloodGroup(request.bloodGroup());
        patient.setPhone(request.phone());
        patient.setEmail(request.email());
        patient.setAddress(request.address());
        patient.setEmergencyContact(request.emergencyContact());
        patient.setEmergencyName(request.emergencyName());
        patient.setAllergies(request.allergies());
        patient.setCenter(findCenter(request.centerId()));
        patient.setRegisteredBy(actor);
        return patientRepository.save(patient);
    }

    public PageResponse<Patient> search(String q, Pageable pageable) {
        assertCanViewPatients(currentUserService.get());
        if (q == null || q.isBlank()) {
            return PageResponse.from(patientRepository.findAll(pageable));
        }
        return PageResponse.from(patientRepository.findByNameContainingIgnoreCaseOrPhoneContainingOrUhidContainingIgnoreCase(q, q, q, pageable));
    }

    public Patient profile(String uhid) {
        assertCanViewPatients(currentUserService.get());
        return patientRepository.findByUhid(uhid)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    @Transactional
    public Patient update(Long id, PatientRequest request) {
        assertCanRegisterPatients(currentUserService.get());
        Patient patient = findPatient(id);
        patient.setName(request.name());
        patient.setAge(request.age());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setBloodGroup(request.bloodGroup());
        patient.setPhone(request.phone());
        patient.setEmail(request.email());
        patient.setAddress(request.address());
        patient.setEmergencyContact(request.emergencyContact());
        patient.setEmergencyName(request.emergencyName());
        patient.setAllergies(request.allergies());
        patient.setCenter(findCenter(request.centerId()));
        return patientRepository.save(patient);
    }

    @Transactional
    public OpdVisit createOpdVisit(OpdVisitRequest request) {
        assertCanCreateOpd(currentUserService.get());
        LocalDate date = request.visitDate() != null ? request.visitDate() : LocalDate.now();
        OpdVisit visit = new OpdVisit();
        visit.setPatient(findPatient(request.patientId()));
        visit.setDoctor(request.doctorId() != null ? findUser(request.doctorId()) : null);
        visit.setCenter(findCenter(request.centerId()));
        visit.setVisitDate(date);
        visit.setTokenNumber(opdVisitRepository.maxToken(request.centerId(), date) + 1);
        visit.setSymptoms(request.symptoms());
        visit.setFee(request.fee());
        return opdVisitRepository.save(visit);
    }

    public List<OpdVisit> queue(Long centerId, LocalDate date) {
        assertCanViewOpd(currentUserService.get());
        return opdVisitRepository.findByCenter_IdAndVisitDateOrderByTokenNumber(centerId, date != null ? date : LocalDate.now());
    }

    @Transactional
    public OpdVisit updateOpdStatus(Long id, OpdStatusRequest request) {
        assertCanUpdateOpd(currentUserService.get());
        OpdVisit visit = opdVisitRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "OPD visit not found"));
        visit.setStatus(request.status());
        visit.setDiagnosis(request.diagnosis());
        visit.setPrescriptionNotes(request.prescriptionNotes());
        return opdVisitRepository.save(visit);
    }

    public List<OpdVisit> opdHistory(Long patientId) {
        assertCanViewOpd(currentUserService.get());
        return opdVisitRepository.findByPatient_IdOrderByVisitDateDesc(patientId);
    }

    @Transactional
    public Bed createBed(BedRequest request) {
        assertCanManageBeds(currentUserService.get());
        Bed bed = new Bed();
        bed.setCenter(findCenter(request.centerId()));
        bed.setWard(request.ward());
        bed.setBedNumber(request.bedNumber());
        bed.setOccupied(false);
        return bedRepository.save(bed);
    }

    public List<Bed> availableBeds(Long centerId, String ward) {
        assertCanViewIpd(currentUserService.get());
        if (ward == null || ward.isBlank()) {
            return bedRepository.findByCenter_IdAndOccupiedFalse(centerId);
        }
        return bedRepository.findByCenter_IdAndWardAndOccupiedFalse(centerId, ward);
    }

    @Transactional
    public IpdAdmission admit(IpdAdmissionRequest request) {
        assertCanManageIpd(currentUserService.get());
        Bed bed = bedRepository.findById(request.bedId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Bed not found"));
        if (Boolean.TRUE.equals(bed.getOccupied())) {
            throw new AppException(HttpStatus.CONFLICT, "Bed is already occupied");
        }
        bed.setOccupied(true);
        bedRepository.save(bed);

        IpdAdmission admission = new IpdAdmission();
        admission.setPatient(findPatient(request.patientId()));
        admission.setDoctor(request.doctorId() != null ? findUser(request.doctorId()) : null);
        admission.setCenter(findCenter(request.centerId()));
        admission.setBed(bed);
        admission.setAdmissionDate(LocalDateTime.now());
        admission.setDiagnosis(request.diagnosis());
        admission.setTreatmentNotes(request.treatmentNotes());
        admission.setDailyCharge(request.dailyCharge());
        admission.setStatus(AdmissionStatus.ADMITTED);
        return ipdAdmissionRepository.save(admission);
    }

    public List<IpdAdmission> activeAdmissions(Long centerId) {
        assertCanViewIpd(currentUserService.get());
        return ipdAdmissionRepository.findByCenter_IdAndStatus(centerId, AdmissionStatus.ADMITTED);
    }

    @Transactional
    public IpdAdmission discharge(Long id, DischargeRequest request) {
        assertCanManageIpd(currentUserService.get());
        IpdAdmission admission = ipdAdmissionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "IPD admission not found"));
        admission.setDischargeDate(LocalDateTime.now());
        admission.setDischargedBy(currentUserService.get());
        admission.setStatus(AdmissionStatus.DISCHARGED);
        if (request != null && request.treatmentNotes() != null) {
            admission.setTreatmentNotes(request.treatmentNotes());
        }
        if (admission.getBed() != null) {
            admission.getBed().setOccupied(false);
            bedRepository.save(admission.getBed());
        }
        IpdAdmission saved = ipdAdmissionRepository.save(admission);
        billingService.createIpdDraft(saved);
        return saved;
    }

    private String generateUhid() {
        int year = LocalDate.now().getYear();
        String prefix = "HOSP-" + year + "-";
        long next = patientRepository.countByUhidStartingWith(prefix) + 1;
        return prefix + String.format("%06d", next);
    }

    private Patient findPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    private Center findCenter(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Center not found"));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void assertCanRegisterPatients(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.STATE_MANAGER
                && actor.getRole() != Role.DISTRICT_MANAGER
                && actor.getRole() != Role.BLOCK_MANAGER
                && actor.getRole() != Role.DOCTOR
                && actor.getRole() != Role.RECEPTIONIST) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot register or update patients");
        }
    }

    private void assertCanViewPatients(User actor) {
        if (actor.getRole() == Role.PATIENT) {
            throw new AppException(HttpStatus.FORBIDDEN, "Patients cannot browse patient records");
        }
    }

    private void assertCanCreateOpd(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.DOCTOR
                && actor.getRole() != Role.RECEPTIONIST) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot create OPD visits");
        }
    }

    private void assertCanViewOpd(User actor) {
        if (actor.getRole() == Role.PATIENT) {
            throw new AppException(HttpStatus.FORBIDDEN, "Patients cannot view OPD records");
        }
    }

    private void assertCanUpdateOpd(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.DOCTOR) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot update OPD status");
        }
    }

    private void assertCanManageBeds(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.BLOCK_MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot manage beds");
        }
    }

    private void assertCanViewIpd(User actor) {
        if (actor.getRole() == Role.PATIENT || actor.getRole() == Role.STATE_MANAGER || actor.getRole() == Role.DISTRICT_MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot view IPD records");
        }
    }

    private void assertCanManageIpd(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.DOCTOR) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot manage IPD admissions");
        }
    }
}
