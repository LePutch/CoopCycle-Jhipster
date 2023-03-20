package jerem.coopcycle.service.mapper;

import jerem.coopcycle.domain.Commande;
import jerem.coopcycle.domain.Restaurateur;
import jerem.coopcycle.domain.Societaire;
import jerem.coopcycle.service.dto.CommandeDTO;
import jerem.coopcycle.service.dto.RestaurateurDTO;
import jerem.coopcycle.service.dto.SocietaireDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Restaurateur} and its DTO {@link RestaurateurDTO}.
 */
@Mapper(componentModel = "spring")
public interface RestaurateurMapper extends EntityMapper<RestaurateurDTO, Restaurateur> {
    @Mapping(target = "commande", source = "commande", qualifiedByName = "commandeId")
    @Mapping(target = "societaire", source = "societaire", qualifiedByName = "societaireId")
    RestaurateurDTO toDto(Restaurateur s);

    @Named("commandeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommandeDTO toDtoCommandeId(Commande commande);

    @Named("societaireId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SocietaireDTO toDtoSocietaireId(Societaire societaire);
}
