package jerem.coopcycle.service.mapper;

import jerem.coopcycle.domain.Societaire;
import jerem.coopcycle.service.dto.SocietaireDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Societaire} and its DTO {@link SocietaireDTO}.
 */
@Mapper(componentModel = "spring")
public interface SocietaireMapper extends EntityMapper<SocietaireDTO, Societaire> {}
