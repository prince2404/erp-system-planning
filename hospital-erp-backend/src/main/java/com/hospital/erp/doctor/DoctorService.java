package com.hospital.erp.doctor;

import com.hospital.erp.appointment.Appointment;
import com.hospital.erp.appointment.AppointmentRepository;
import com.hospital.erp.appointment.dto.AppointmentRequest;
import com.hospital.erp.appointment.dto.AppointmentStatusRequest;
import com.hospital.erp.appointment.dto.SlotResponse;
import com.hospital.erp.common.AppException;
import com.hospital.erp.common.enums.AppointmentStatus;
import com.hospital.erp.common.enums.AppointmentType;
import com.hospital.erp.common.enums.DayOfWeekCode;
import com.hospital.erp.doctor.dto.DoctorProfileRequest;
import com.hospital.erp.doctor.dto.DoctorScheduleRequest;
import com.hospital.erp.geographic.entities.Center;
import com.hospital.erp.geographic.repositories.CenterRepository;
import com.hospital.erp.patient.Patient;
import com.hospital.erp.patient.PatientRepository;
import com.hospital.erp.user.CurrentUserService;
import com.hospital.erp.user.User;
import com.hospital.erp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final PatientRepository patientRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public DoctorProfile createProfile(DoctorProfileRequest request) {
        DoctorProfile profile = new DoctorProfile();
        profile.setUser(findUser(request.userId()));
        profile.setSpecialization(request.specialization());
        profile.setQualification(request.qualification());
        profile.setExperienceYears(request.experienceYears());
        profile.setConsultationFee(request.consultationFee());
        profile.setCenter(request.centerId() != null ? findCenter(request.centerId()) : null);
        profile.setAvailable(request.available() == null || request.available());
        return doctorProfileRepository.save(profile);
    }

    public List<DoctorProfile> doctors(Long centerId, String specialization) {
        if (centerId != null) {
            return doctorProfileRepository.findByCenter_IdAndAvailableTrue(centerId);
        }
        if (specialization != null && !specialization.isBlank()) {
            return doctorProfileRepository.findBySpecializationContainingIgnoreCaseAndAvailableTrue(specialization);
        }
        return doctorProfileRepository.findByAvailableTrue();
    }

    public List<DoctorSchedule> schedule(Long doctorId) {
        return doctorScheduleRepository.findByDoctor_IdOrderByDayOfWeek(doctorId);
    }

    @Transactional
    public List<DoctorSchedule> updateSchedule(Long doctorId, List<DoctorScheduleRequest> requests) {
        DoctorProfile doctor = findDoctor(doctorId);
        doctorScheduleRepository.deleteByDoctor_Id(doctorId);
        return requests.stream().map(request -> {
            DoctorSchedule schedule = new DoctorSchedule();
            schedule.setDoctor(doctor);
            schedule.setDayOfWeek(request.dayOfWeek());
            schedule.setStartTime(request.startTime());
            schedule.setEndTime(request.endTime());
            schedule.setSlotDurationMins(request.slotDurationMins() != null ? request.slotDurationMins() : 15);
            schedule.setMaxPatients(request.maxPatients());
            schedule.setActive(request.active() == null || request.active());
            return doctorScheduleRepository.save(schedule);
        }).toList();
    }

    public List<SlotResponse> availableSlots(Long doctorId, LocalDate date) {
        DayOfWeekCode day = DayOfWeekCode.from(date.getDayOfWeek());
        DoctorSchedule schedule = doctorScheduleRepository.findByDoctor_IdAndDayOfWeekAndActiveTrue(doctorId, day)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Doctor has no active schedule for this day"));
        Set<LocalTime> bookedSlots = new HashSet<>(appointmentRepository.findByDoctor_IdAndAppointmentDate(doctorId, date).stream()
                .filter(appointment -> appointment.getStatus() != AppointmentStatus.CANCELLED)
                .map(Appointment::getSlotTime)
                .toList());
        List<LocalTime> times = new java.util.ArrayList<>();
        for (LocalTime time = schedule.getStartTime(); time.isBefore(schedule.getEndTime()); time = time.plusMinutes(schedule.getSlotDurationMins())) {
            times.add(time);
        }
        return times.stream().map(time -> new SlotResponse(time, !bookedSlots.contains(time))).toList();
    }

    @Transactional
    public Appointment book(AppointmentRequest request) {
        ensureSlotAvailable(request.doctorId(), request.appointmentDate(), request.slotTime());
        DoctorProfile doctor = findDoctor(request.doctorId());
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Patient not found"));

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setCenter(request.centerId() != null ? findCenter(request.centerId()) : doctor.getCenter());
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setSlotTime(request.slotTime());
        appointment.setType(request.type() != null ? request.type() : AppointmentType.OPD);
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setBookingFee(request.bookingFee());
        appointment.setNotes(request.notes());
        appointment.setBookedBy(currentUserService.get());
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> appointments(Long doctorId, LocalDate date) {
        return appointmentRepository.findByDoctor_IdAndAppointmentDate(doctorId, date != null ? date : LocalDate.now());
    }

    @Transactional
    public Appointment updateAppointmentStatus(Long id, AppointmentStatusRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Appointment not found"));
        appointment.setStatus(request.status());
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> patientHistory(Long patientId) {
        return appointmentRepository.findByPatient_IdOrderByAppointmentDateDesc(patientId);
    }

    private void ensureSlotAvailable(Long doctorId, LocalDate date, LocalTime time) {
        boolean unavailable = appointmentRepository.findByDoctor_IdAndAppointmentDateAndSlotTime(doctorId, date, time)
                .filter(appointment -> appointment.getStatus() != AppointmentStatus.CANCELLED)
                .isPresent();
        if (unavailable) {
            throw new AppException(HttpStatus.CONFLICT, "Appointment slot is already booked");
        }
        availableSlots(doctorId, date).stream()
                .filter(slot -> slot.time().equals(time) && slot.available())
                .findFirst()
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Slot is outside the doctor's available schedule"));
    }

    private DoctorProfile findDoctor(Long id) {
        return doctorProfileRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Doctor profile not found"));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Center findCenter(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Center not found"));
    }
}
