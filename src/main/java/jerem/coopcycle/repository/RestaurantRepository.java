package jerem.coopcycle.repository;

import jerem.coopcycle.domain.Restaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Restaurant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RestaurantRepository extends ReactiveCrudRepository<Restaurant, Long>, RestaurantRepositoryInternal {
    Flux<Restaurant> findAllBy(Pageable pageable);

    @Query("SELECT * FROM restaurant entity WHERE entity.restaurateur_id = :id")
    Flux<Restaurant> findByRestaurateur(Long id);

    @Query("SELECT * FROM restaurant entity WHERE entity.restaurateur_id IS NULL")
    Flux<Restaurant> findAllWhereRestaurateurIsNull();

    @Override
    <S extends Restaurant> Mono<S> save(S entity);

    @Override
    Flux<Restaurant> findAll();

    @Override
    Mono<Restaurant> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RestaurantRepositoryInternal {
    <S extends Restaurant> Mono<S> save(S entity);

    Flux<Restaurant> findAllBy(Pageable pageable);

    Flux<Restaurant> findAll();

    Mono<Restaurant> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Restaurant> findAllBy(Pageable pageable, Criteria criteria);

}
