package com.mini.cti.mapper;

import com.mini.cti.dto.CisaKevDTO;
import com.mini.cti.model.CisaKev;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapperEntityToDTO {
    CisaKevDTO toDTO(CisaKev cisaKev);
    CisaKev toEntity(CisaKevDTO cisaKevDTO);
}
