package com.joa.prexixionapi.controllers;

import com.joa.prexixionapi.dto.AttendanceDTO;
import com.joa.prexixionapi.dto.AttendanceStatsDTO;
import com.joa.prexixionapi.dto.IdNameDTO;
import com.joa.prexixionapi.services.AttendanceExcelService;
import com.joa.prexixionapi.services.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceExcelService excelService;

    @GetMapping("/daily")
    public List<AttendanceDTO> getDailyAttendance(
            @RequestParam String fecha,
            @RequestParam(required = false) List<Integer> statuses,
            @RequestParam(required = false) List<Integer> types) {
        log.info("Request daily attendance for date: {}, statuses: {}, types: {}", fecha, statuses, types);
        return attendanceService.getDailyAttendance(fecha, statuses, types);
    }

    @GetMapping("/export-daily")
    public ResponseEntity<Resource> exportDaily(
            @RequestParam String fecha,
            @RequestParam(required = false) List<Integer> statuses,
            @RequestParam(required = false) List<Integer> types) throws IOException {
        log.info("Exporting daily attendance for date: {}, filters: true", fecha);
        List<AttendanceDTO> data = attendanceService.getDailyAttendance(fecha, statuses, types);
        byte[] excelContent = excelService.generateDailyExcel(fecha, data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asistencia_" + fecha + ".xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(excelContent));
    }

    @GetMapping("/stats")
    public AttendanceStatsDTO getStats(@RequestParam String fechaI, @RequestParam String fechaF) {
        log.info("Request attendance stats from {} to {}", fechaI, fechaF);
        return attendanceService.getStats(fechaI, fechaF);
    }

    @GetMapping("/statuses")
    public List<IdNameDTO> getStatuses() {
        return attendanceService.getPersonalStatuses();
    }

    @GetMapping("/types")
    public List<IdNameDTO> getTypes() {
        return attendanceService.getPersonalTypes();
    }

    @GetMapping("/logs")
    public List<Map<String, Object>> getRawLogs(@RequestParam String dni, @RequestParam String fecha) {
        log.info("Request raw logs for DNI: {} on date: {}", dni, fecha);
        return attendanceService.getRawLogs(dni, fecha);
    }

    @GetMapping("/monthly")
    public List<AttendanceDTO> getMonthly(@RequestParam String dni, @RequestParam String fechaI, @RequestParam String fechaF) {
        return attendanceService.getMonthlyAttendance(dni, fechaI, fechaF);
    }

    @GetMapping("/search-personal")
    public List<com.joa.prexixionapi.dto.PersonDTO> searchPersonal(@RequestParam String query) {
        return attendanceService.searchPersonal(query);
    }
}
