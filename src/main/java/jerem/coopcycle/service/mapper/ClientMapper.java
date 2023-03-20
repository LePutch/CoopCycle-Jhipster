package jerem.coopcycle.service.mapper;

import jerem.coopcycle.domain.Client;
import jerem.coopcycle.domain.Commande;
import jerem.coopcycle.service.dto.ClientDTO;
import jerem.coopcycle.service.dto.CommandeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Client} and its DTO {@link ClientDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClientMapper extends EntityMapper<ClientDTO, Client> {
    @Mapping(target = "commande", source = "commande", qualifiedByName = "commandeId")
    ClientDTO toDto(Client s);

    @Named("commandeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommandeDTO toDtoCommandeId(Commande commande);
}
