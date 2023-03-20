package jerem.coopcycle.repository;

import jerem.coopcycle.domain.Commande;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Commande entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandeRepository extends ReactiveCrudRepository<Commande, Long>, CommandeRepositoryInternal {
    Flux<Commande> findAllBy(Pageable pageable);

    @Query("SELECT * FROM commande entity WHERE entity.panier_id = :id")
    Flux<Commande> findByPanier(Long id);

    @Query("SELECT * FROM commande entity WHERE entity.panier_id IS NULL")
    Flux<Commande> findAllWherePanierIsNull();

    @Query("SELECT * FROM commande entity WHERE entity.paiement_id = :id")
    Flux<Commande> findByPaiement(Long id);

    @Query("SELECT * FROM commande entity WHERE entity.paiement_id IS NULL")
    Flux<Commande> findAllWherePaiementIsNull();

    @Override
    <S extends Commande> Mono<S> save(S entity);

    @Override
    Flux<Commande> findAll();

    @Override
    Mono<Commande> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CommandeRepositoryInternal {
    <S extends Commande> Mono<S> save(S entity);

    Flux<Commande> findAllBy(Pageable pageable);

    Flux<Commande> findAll();

    Mono<Commande> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Commande> findAllBy(Pageable pageable, Criteria criteria);

}
