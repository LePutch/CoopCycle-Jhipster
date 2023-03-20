package jerem.coopcycle.repository;

import jerem.coopcycle.domain.Panier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Panier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PanierRepository extends ReactiveCrudRepository<Panier, Long>, PanierRepositoryInternal {
    Flux<Panier> findAllBy(Pageable pageable);

    @Query("SELECT * FROM panier entity WHERE entity.restaurant_id = :id")
    Flux<Panier> findByRestaurant(Long id);

    @Query("SELECT * FROM panier entity WHERE entity.restaurant_id IS NULL")
    Flux<Panier> findAllWhereRestaurantIsNull();

    @Override
    <S extends Panier> Mono<S> save(S entity);

    @Override
    Flux<Panier> findAll();

    @Override
    Mono<Panier> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PanierRepositoryInternal {
    <S extends Panier> Mono<S> save(S entity);

    Flux<Panier> findAllBy(Pageable pageable);

    Flux<Panier> findAll();

    Mono<Panier> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Panier> findAllBy(Pageable pageable, Criteria criteria);

}
