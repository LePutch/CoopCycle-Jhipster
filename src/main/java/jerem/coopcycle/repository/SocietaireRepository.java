package jerem.coopcycle.repository;

import jerem.coopcycle.domain.Societaire;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Societaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SocietaireRepository extends ReactiveCrudRepository<Societaire, Long>, SocietaireRepositoryInternal {
    Flux<Societaire> findAllBy(Pageable pageable);

    @Override
    <S extends Societaire> Mono<S> save(S entity);

    @Override
    Flux<Societaire> findAll();

    @Override
    Mono<Societaire> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SocietaireRepositoryInternal {
    <S extends Societaire> Mono<S> save(S entity);

    Flux<Societaire> findAllBy(Pageable pageable);

    Flux<Societaire> findAll();

    Mono<Societaire> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Societaire> findAllBy(Pageable pageable, Criteria criteria);

}
