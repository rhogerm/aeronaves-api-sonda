package com.sonda.aeronaves.mapper;

import com.sonda.aeronaves.dto.AeronaveRequest;
import com.sonda.aeronaves.dto.AeronaveResponse;
import com.sonda.aeronaves.model.Aeronave;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AeronaveMapper {

    Aeronave toEntity(AeronaveRequest request);

    AeronaveResponse toResponse(Aeronave aeronave);

    void atualizarEntidade(AeronaveRequest request, @MappingTarget Aeronave aeronave);
}
