package jerem.coopcycle.service;

import jerem.coopcycle.domain.Restaurateur;
import jerem.coopcycle.repository.RestaurateurRepository;
import jerem.coopcycle.service.dto.RestaurateurDTO;
import jerem.coopcycle.service.mapper.RestaurateurMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Restaurateur}.
 */
@Service
@Transactional
public class RestaurateurService {

    private final Logger log = LoggerFactory.getLogger(RestaurateurService.class);

    private final RestaurateurRepository restaurateurRepository;

    private final RestaurateurMapper restaurateurMapper;

    public RestaurateurService(RestaurateurRepository restaurateurRepository, RestaurateurMapper restaurateurMapper) {
        this.restaurateurRepository = restaurateurRepository;
        this.restaurateurMapper = restaurateurMapper;
    }

    /**
     * Save a restaurateur.
     *
     * @param restaurateurDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RestaurateurDTO> save(RestaurateurDTO restaurateurDTO) {
        log.debug("Request to save Restaurateur : {}", restaurateurDTO);
        return restaurateurRepository.save(restaurateurMapper.toEntity(restaurateurDTO)).map(restaurateurMapper::toDto);
    }

    /**
     * Update a restaurateur.
     *
     * @param restaurateurDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RestaurateurDTO> update(RestaurateurDTO restaurateurDTO) {
        log.debug("Request to update Restaurateur : {}", restaurateurDTO);
        return restaurateurRepository.save(restaurateurMapper.toEntity(restaurateurDTO)).map(restaurateurMapper::toDto);
    }

    /**
     * Partially update a restaurateur.
     *
     * @param restaurateurDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RestaurateurDTO> partialUpdate(RestaurateurDTO restaurateurDTO) {
        log.debug("Request to partially update Restaurateur : {}", restaurateurDTO);

        return restaurateurRepository
            .findById(restaurateurDTO.getId())
            .map(existingRestaurateur -> {
                restaurateurMapper.partialUpdate(existingRestaurateur, restaurateurDTO);

                return existingRestaurateur;
            })
            .flatMap(restaurateurRepository::save)
            .map(restaurateurMapper::toDto);
    }

    /**
     * Get all the restaurateurs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RestaurateurDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Restaurateurs");
        return restaurateurRepository.findAllBy(pageable).map(restaurateurMapper::toDto);
    }

    /**
     * Returns the number of restaurateurs available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return restaurateurRepository.count();
    }

    /**
     * Get one restaurateur by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RestaurateurDTO> findOne(Long id) {
        log.debug("Request to get Restaurateur : {}", id);
        return restaurateurRepository.findById(id).map(restaurateurMapper::toDto);
    }

    /**
     * Delete the restaurateur by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Restaurateur : {}", id);
        return restaurateurRepository.deleteById(id);
    }
}
