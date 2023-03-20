package jerem.coopcycle.service.mapper;

import jerem.coopcycle.domain.Commande;
import jerem.coopcycle.domain.Paiement;
import jerem.coopcycle.domain.Panier;
import jerem.coopcycle.service.dto.CommandeDTO;
import jerem.coopcycle.service.dto.PaiementDTO;
import jerem.coopcycle.service.dto.PanierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Commande} and its DTO {@link CommandeDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommandeMapper extends EntityMapper<CommandeDTO, Commande> {
    @Mapping(target = "panier", source = "panier", qualifiedByName = "panierId")
    @Mapping(target = "paiement", source = "paiement", qualifiedByName = "paiementId")
    CommandeDTO toDto(Commande s);

    @Named("panierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PanierDTO toDtoPanierId(Panier panier);

    @Named("paiementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaiementDTO toDtoPaiementId(Paiement paiement);
}
