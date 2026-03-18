package com.mini.cti.api;


import com.mini.cti.model.CisaKev;
import com.mini.cti.service.CisaKevService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * REST controller for administrative operations on CISA KEV data.
 *
 * <p>Provides endpoints for admin users to manually trigger updates
 * and retrieve vulnerability statistics. All endpoints require ADMIN role.</p>
 *
 * @author Mini-CTI Team
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cisa-kev")
public class CisaKevRestController {

    private final CisaKevService cisaKevService;


    @GetMapping
    public ResponseEntity<Page<CisaKev>> getAllVulnerabilitiesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateAdded, desc") String sort
    ){
        Page<CisaKev> vulnerabilities = cisaKevService.getAllVulnerabilitiesPaginated(page, size, sort);
        return ResponseEntity.ok(vulnerabilities);
    }


    @GetMapping("/{cveID}")
    public ResponseEntity<CisaKev> getVulnerabilityByCveId(@PathVariable String cveID) {
        CisaKev vulnerability = cisaKevService.getVulnerabilityByCveId(cveID);
        return ResponseEntity.ok(vulnerability);
    }


    /**
     * Manually triggers a CISA KEV database update.
     *
     * <p>This endpoint allows administrators to force an immediate update
     * of the vulnerability database without waiting for the scheduled task.</p>
     *
     * <p><strong>Use cases:</strong></p>
     * <ul>
     *   <li>Testing the update mechanism</li>
     *   <li>Emergency updates when a critical vulnerability is announced</li>
     *   <li>Initial database population</li>
     * </ul>
     *
     * @return ResponseEntity with success/error message
     * @throws com.mini.cti.core.exceptions.CisaApiException if the CISA API is unavailable
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> manualUpdate() {

        try {
            cisaKevService.updateDatabase();
            return ResponseEntity.ok(Map.of(
                    "status","Success",
                    "message", "CISA KEV update triggered successfully"
            ));
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "Update" + e.getMessage()
            ));
        }
    }
}
