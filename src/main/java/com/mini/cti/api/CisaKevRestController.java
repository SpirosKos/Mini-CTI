package com.mini.cti.api;


import com.mini.cti.core.exceptions.CisaApiException;
import com.mini.cti.dto.ErrorResponseDTO;
import com.mini.cti.dto.UpdateVulnerabilitiesDTO;
import com.mini.cti.model.CisaKev;
import com.mini.cti.service.CisaKevService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            summary = "List all vulnerabilities (paginated)",
            description = "Returns a paginated list of CVEs from the CISA KEV feed."
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Results returned successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CisaKev.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid pagination parameters",
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

            @Parameter(description = "Page number, zero based", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of results per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort field and direction", example = "dateAdded,desc")
            @RequestParam(defaultValue = "dateAdded,desc") String sort
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
     * @throws CisaApiException if the CISA API is unavailable
     */
    @Operation(
            summary = "Manually trigger CVE database update",
            description = "Forces an immediate sync with the CISA KEV feed. Requires ADMIN role."
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
            responseCode = "403",
            description = "Admin role required.",
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
