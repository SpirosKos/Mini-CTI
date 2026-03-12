package com.mini.cti.api;



import com.mini.cti.dto.IpLookUpResponseDTO;
import com.mini.cti.model.User;
import com.mini.cti.repository.UserRepository;
import com.mini.cti.service.IpLookUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ip-lookup")
public class IpLookUpRestController {

    private final IpLookUpService ipLookUpService;
    private final UserRepository userRepository;

    @GetMapping("/{ipAddress}")
    public ResponseEntity<IpLookUpResponseDTO> responseDTO (@PathVariable String ipAddress, @AuthenticationPrincipal User user) {

        IpLookUpResponseDTO responseDTO = ipLookUpService.lookUpIp(ipAddress,user);

        return ResponseEntity.ok(responseDTO);
    }
}
