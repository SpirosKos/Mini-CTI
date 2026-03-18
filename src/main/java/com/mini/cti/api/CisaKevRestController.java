package com.mini.cti.api;


import com.mini.cti.service.CisaKevService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/api/v1")
public class CisaKevRestController {

    private final CisaKevService cisaKevService;


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
