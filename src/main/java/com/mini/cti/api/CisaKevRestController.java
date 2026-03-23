package com.mini.cti.api;


import com.mini.cti.dto.CisaKevDTO;
import com.mini.cti.dto.CisaKevResponseDTO;
import com.mini.cti.dto.ErrorResponseDTO;
import com.mini.cti.dto.UpdateVulnerabilitiesDTO;
import com.mini.cti.model.CisaKev;
import com.mini.cti.service.CisaKevService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
@Tag(name = "CISA-KEV", description = "Import and Update CVE's from CISA-KEV")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cisa-kev")
public class CisaKevRestController {

    private final CisaKevService cisaKevService;


    @Operation(
            summary = "Vulnerabilities from Cisa-Kev",
            description = "Import-Update vulnerabilities from Cisa-Kev in JSON"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "CVE's imported successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CisaKevResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Not authenticated",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Internal Server error.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        )
    })
    @GetMapping
    public ResponseEntity<Page<CisaKev>> getAllVulnerabilitiesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateAdded, desc") String sort
    ){
        Page<CisaKev> vulnerabilities = cisaKevService.getAllVulnerabilitiesPaginated(page, size, sort);
        return ResponseEntity.ok(vulnerabilities);
    }




    // TODO future option for user to lookup for CVE by the ID
    @Operation(hidden = true)
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
    @Operation(
            summary = "Update CVE's",
            description = "Manual database update with newest CVE's from Admin"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "CVE's updated successfully in DB",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateVulnerabilitiesDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
        @ApiResponse(
        responseCode = "500",
        description = "Internal Server error.",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public ResponseEntity<UpdateVulnerabilitiesDTO> manualUpdate() {
            cisaKevService.updateDatabase();
            return ResponseEntity.ok(new UpdateVulnerabilitiesDTO("Success", "Database CVE's updated successfully"));
    }
}
