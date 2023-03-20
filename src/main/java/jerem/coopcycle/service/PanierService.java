package jerem.coopcycle.service;

import jerem.coopcycle.domain.Panier;
import jerem.coopcycle.repository.PanierRepository;
import jerem.coopcycle.service.dto.PanierDTO;
import jerem.coopcycle.service.mapper.PanierMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Panier}.
 */
@Service
@Transactional
public class PanierService {

    private final Logger log = LoggerFactory.getLogger(PanierService.class);

    private final PanierRepository panierRepository;

    private final PanierMapper panierMapper;

    public PanierService(PanierRepository panierRepository, PanierMapper panierMapper) {
        this.panierRepository = panierRepository;
        this.panierMapper = panierMapper;
    }

    /**
     * Save a panier.
     *
     * @param panierDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PanierDTO> save(PanierDTO panierDTO) {
        log.debug("Request to save Panier : {}", panierDTO);
        return panierRepository.save(panierMapper.toEntity(panierDTO)).map(panierMapper::toDto);
    }

    /**
     * Update a panier.
     *
     * @param panierDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PanierDTO> update(PanierDTO panierDTO) {
        log.debug("Request to update Panier : {}", panierDTO);
        return panierRepository.save(panierMapper.toEntity(panierDTO)).map(panierMapper::toDto);
    }

    /**
     * Partially update a panier.
     *
     * @param panierDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PanierDTO> partialUpdate(PanierDTO panierDTO) {
        log.debug("Request to partially update Panier : {}", panierDTO);

        return panierRepository
            .findById(panierDTO.getId())
            .map(existingPanier -> {
                panierMapper.partialUpdate(existingPanier, panierDTO);

                return existingPanier;
            })
            .flatMap(panierRepository::save)
            .map(panierMapper::toDto);
    }

    /**
     * Get all the paniers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PanierDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Paniers");
        return panierRepository.findAllBy(pageable).map(panierMapper::toDto);
    }

    /**
     * Returns the number of paniers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return panierRepository.count();
    }

    /**
     * Get one panier by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PanierDTO> findOne(Long id) {
        log.debug("Request to get Panier : {}", id);
        return panierRepository.findById(id).map(panierMapper::toDto);
    }

    /**
     * Delete the panier by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Panier : {}", id);
        return panierRepository.deleteById(id);
    }
}
