package jerem.coopcycle.service.mapper;

import jerem.coopcycle.domain.Panier;
import jerem.coopcycle.domain.Restaurant;
import jerem.coopcycle.service.dto.PanierDTO;
import jerem.coopcycle.service.dto.RestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Panier} and its DTO {@link PanierDTO}.
 */
@Mapper(componentModel = "spring")
public interface PanierMapper extends EntityMapper<PanierDTO, Panier> {
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    PanierDTO toDto(Panier s);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);
}
