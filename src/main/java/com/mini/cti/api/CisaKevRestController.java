package com.mini.cti.api;


import com.mini.cti.service.CisaKevService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CisaKevRestController {

    private final CisaKevService cisaKevService;

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
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
