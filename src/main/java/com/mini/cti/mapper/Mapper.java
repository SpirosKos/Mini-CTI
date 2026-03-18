package com.mini.cti.mapper;

import com.mini.cti.dto.*;
import com.mini.cti.model.CisaKev;
import com.mini.cti.model.IpCache;
import com.mini.cti.model.User;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class Mapper {

    public User mapToUserEntity(UserRequestDTO userRequestDTO){
        User user = new User();
        user.setEmail(userRequestDTO.email());
        user.setUsername(userRequestDTO.email());
        user.setPassword(userRequestDTO.password());
        return user;
//        return new User(userRequestDTO.email(), userRequestDTO.password());
    }

    public UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(user.getEmail(), user.getRole(), user.getUuid());
    }

    public IpCache mapToIpCacheEntity(VirusTotalResponseDTO virusTotalResponseDTO) {
        IpCache ipCache = new IpCache();
        ipCache.setIpAddress(virusTotalResponseDTO.data().id());
        ipCache.setCountry(virusTotalResponseDTO.data().attributes().country());
        ipCache.setAsOwner(virusTotalResponseDTO.data().attributes().as_owner());
        ipCache.setReputation(virusTotalResponseDTO.data().attributes().reputation());
        ipCache.setMalicious(virusTotalResponseDTO.data().attributes().last_analysis_stats().malicious());
        ipCache.setSuspicious(virusTotalResponseDTO.data().attributes().last_analysis_stats().suspicious());
        ipCache.setHarmless(virusTotalResponseDTO.data().attributes().last_analysis_stats().harmless());
        ipCache.setUndetected(virusTotalResponseDTO.data().attributes().last_analysis_stats().undetected());
        Long timestamp = virusTotalResponseDTO.data().attributes().last_analysis_date();
        ipCache.setLastAnalysisDate(Instant.ofEpochSecond(timestamp));
        ipCache.setLastUpdate(Instant.now());

        return ipCache;
    }

    public IpLookUpResponseDTO mapToIpLookUpResponseDTO(IpCache ipCache) {

        return new IpLookUpResponseDTO(
                ipCache.getIpAddress(),
                ipCache.getCountry(),
                ipCache.getAsOwner(),
                ipCache.getReputation(),
                ipCache.getMalicious(),
                ipCache.getSuspicious(),
                ipCache.getHarmless(),
                ipCache.getUndetected(),
                ipCache.getLastAnalysisDate(),
                ipCache.getLastUpdate()
        );
    }

    public CisaKev mapToCisaKevEntity(CisaKevDTO cisaKevDTO) {
        CisaKev cisaKev = new CisaKev();

        cisaKev.setCveID(cisaKevDTO.cveID());
        cisaKev.setVendorProject(cisaKevDTO.vendorProject());
        cisaKev.setProduct(cisaKevDTO.product());
        cisaKev.setVulnerabilityName(cisaKevDTO.vulnerabilityName());
        cisaKev.setDateAdded(cisaKevDTO.dateAdded());
        cisaKev.setShortDescription(cisaKevDTO.shortDescription());
        cisaKev.setRequiredAction(cisaKevDTO.requiredAction());
        cisaKev.setDueDate(cisaKevDTO.dueDate());
        cisaKev.setKnownRansomwareCampaignUse(cisaKevDTO.knownRansomwareCampaignUse());
        cisaKev.setNotes(cisaKevDTO.notes());
        cisaKev.setCwes(cisaKevDTO.cwes());

        return cisaKev;
    }

    public void updateCisaKevEntity(CisaKevDTO dto, CisaKev entity) {
        entity.setVendorProject(dto.vendorProject());
        entity.setProduct(dto.product());
        entity.setVulnerabilityName(dto.vulnerabilityName());
        entity.setDateAdded(dto.dateAdded());
        entity.setShortDescription(dto.shortDescription());
        entity.setRequiredAction(dto.requiredAction());
        entity.setDueDate(dto.dueDate());
        entity.setKnownRansomwareCampaignUse(dto.knownRansomwareCampaignUse());
        entity.setNotes(dto.notes());
        entity.setCwes(dto.cwes());
    }

}
