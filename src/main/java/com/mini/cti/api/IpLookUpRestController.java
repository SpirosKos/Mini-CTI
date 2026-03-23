package com.mini.cti.api;



import com.mini.cti.dto.AuthResponseDTO;
import com.mini.cti.dto.ErrorResponseDTO;
import com.mini.cti.dto.IpLookUpResponseDTO;
import com.mini.cti.dto.ValidationErrorResponseDTO;
import com.mini.cti.model.User;
import com.mini.cti.repository.UserRepository;
import com.mini.cti.service.IpLookUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "IpLookUp")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ip-lookup")
public class IpLookUpRestController {

    private final IpLookUpService ipLookUpService;
    private final UserRepository userRepository;


    @Operation(
            summary = "Lookup information's for an IP through VirusTotal API.",
            description ="An authenticate user looking up for an IP.")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully lookup.Returns json with IP information's ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IpLookUpResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid IP address format.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable.",
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
    @GetMapping("/{ipAddress}")
    public ResponseEntity<IpLookUpResponseDTO> responseDTO (
            @Parameter(description = "IPv4 address to look up", example = "0.0.0.0")
            @PathVariable String ipAddress,

            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {

        IpLookUpResponseDTO responseDTO = ipLookUpService.lookUpIp(ipAddress,user);

        return ResponseEntity.ok(responseDTO);
    }
}
