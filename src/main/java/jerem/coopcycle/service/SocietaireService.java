package jerem.coopcycle.service;

import jerem.coopcycle.domain.Societaire;
import jerem.coopcycle.repository.SocietaireRepository;
import jerem.coopcycle.service.dto.SocietaireDTO;
import jerem.coopcycle.service.mapper.SocietaireMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Societaire}.
 */
@Service
@Transactional
public class SocietaireService {

    private final Logger log = LoggerFactory.getLogger(SocietaireService.class);

    private final SocietaireRepository societaireRepository;

    private final SocietaireMapper societaireMapper;

    public SocietaireService(SocietaireRepository societaireRepository, SocietaireMapper societaireMapper) {
        this.societaireRepository = societaireRepository;
        this.societaireMapper = societaireMapper;
    }

    /**
     * Save a societaire.
     *
     * @param societaireDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<SocietaireDTO> save(SocietaireDTO societaireDTO) {
        log.debug("Request to save Societaire : {}", societaireDTO);
        return societaireRepository.save(societaireMapper.toEntity(societaireDTO)).map(societaireMapper::toDto);
    }

    /**
     * Update a societaire.
     *
     * @param societaireDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<SocietaireDTO> update(SocietaireDTO societaireDTO) {
        log.debug("Request to update Societaire : {}", societaireDTO);
        return societaireRepository.save(societaireMapper.toEntity(societaireDTO)).map(societaireMapper::toDto);
    }

    /**
     * Partially update a societaire.
     *
     * @param societaireDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<SocietaireDTO> partialUpdate(SocietaireDTO societaireDTO) {
        log.debug("Request to partially update Societaire : {}", societaireDTO);

        return societaireRepository
            .findById(societaireDTO.getId())
            .map(existingSocietaire -> {
                societaireMapper.partialUpdate(existingSocietaire, societaireDTO);

                return existingSocietaire;
            })
            .flatMap(societaireRepository::save)
            .map(societaireMapper::toDto);
    }

    /**
     * Get all the societaires.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<SocietaireDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Societaires");
        return societaireRepository.findAllBy(pageable).map(societaireMapper::toDto);
    }

    /**
     * Returns the number of societaires available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return societaireRepository.count();
    }

    /**
     * Get one societaire by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<SocietaireDTO> findOne(Long id) {
        log.debug("Request to get Societaire : {}", id);
        return societaireRepository.findById(id).map(societaireMapper::toDto);
    }

    /**
     * Delete the societaire by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Societaire : {}", id);
        return societaireRepository.deleteById(id);
    }
}
