package jerem.coopcycle.repository;

import jerem.coopcycle.domain.Restaurateur;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Restaurateur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RestaurateurRepository extends ReactiveCrudRepository<Restaurateur, Long>, RestaurateurRepositoryInternal {
    Flux<Restaurateur> findAllBy(Pageable pageable);

    @Query("SELECT * FROM restaurateur entity WHERE entity.commande_id = :id")
    Flux<Restaurateur> findByCommande(Long id);

    @Query("SELECT * FROM restaurateur entity WHERE entity.commande_id IS NULL")
    Flux<Restaurateur> findAllWhereCommandeIsNull();

    @Query("SELECT * FROM restaurateur entity WHERE entity.societaire_id = :id")
    Flux<Restaurateur> findBySocietaire(Long id);

    @Query("SELECT * FROM restaurateur entity WHERE entity.societaire_id IS NULL")
    Flux<Restaurateur> findAllWhereSocietaireIsNull();

    @Override
    <S extends Restaurateur> Mono<S> save(S entity);

    @Override
    Flux<Restaurateur> findAll();

    @Override
    Mono<Restaurateur> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RestaurateurRepositoryInternal {
    <S extends Restaurateur> Mono<S> save(S entity);

    Flux<Restaurateur> findAllBy(Pageable pageable);

    Flux<Restaurateur> findAll();

    Mono<Restaurateur> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Restaurateur> findAllBy(Pageable pageable, Criteria criteria);

}
