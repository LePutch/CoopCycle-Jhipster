package jerem.coopcycle.service.mapper;

import jerem.coopcycle.domain.Restaurant;
import jerem.coopcycle.domain.Restaurateur;
import jerem.coopcycle.service.dto.RestaurantDTO;
import jerem.coopcycle.service.dto.RestaurateurDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Restaurant} and its DTO {@link RestaurantDTO}.
 */
@Mapper(componentModel = "spring")
public interface RestaurantMapper extends EntityMapper<RestaurantDTO, Restaurant> {
    @Mapping(target = "restaurateur", source = "restaurateur", qualifiedByName = "restaurateurId")
    RestaurantDTO toDto(Restaurant s);

    @Named("restaurateurId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurateurDTO toDtoRestaurateurId(Restaurateur restaurateur);
}
