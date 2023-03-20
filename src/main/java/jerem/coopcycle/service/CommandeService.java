package jerem.coopcycle.service;

import jerem.coopcycle.domain.Commande;
import jerem.coopcycle.repository.CommandeRepository;
import jerem.coopcycle.service.dto.CommandeDTO;
import jerem.coopcycle.service.mapper.CommandeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Commande}.
 */
@Service
@Transactional
public class CommandeService {

    private final Logger log = LoggerFactory.getLogger(CommandeService.class);

    private final CommandeRepository commandeRepository;

    private final CommandeMapper commandeMapper;

    public CommandeService(CommandeRepository commandeRepository, CommandeMapper commandeMapper) {
        this.commandeRepository = commandeRepository;
        this.commandeMapper = commandeMapper;
    }

    /**
     * Save a commande.
     *
     * @param commandeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CommandeDTO> save(CommandeDTO commandeDTO) {
        log.debug("Request to save Commande : {}", commandeDTO);
        return commandeRepository.save(commandeMapper.toEntity(commandeDTO)).map(commandeMapper::toDto);
    }

    /**
     * Update a commande.
     *
     * @param commandeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CommandeDTO> update(CommandeDTO commandeDTO) {
        log.debug("Request to update Commande : {}", commandeDTO);
        return commandeRepository.save(commandeMapper.toEntity(commandeDTO)).map(commandeMapper::toDto);
    }

    /**
     * Partially update a commande.
     *
     * @param commandeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CommandeDTO> partialUpdate(CommandeDTO commandeDTO) {
        log.debug("Request to partially update Commande : {}", commandeDTO);

        return commandeRepository
            .findById(commandeDTO.getId())
            .map(existingCommande -> {
                commandeMapper.partialUpdate(existingCommande, commandeDTO);

                return existingCommande;
            })
            .flatMap(commandeRepository::save)
            .map(commandeMapper::toDto);
    }

    /**
     * Get all the commandes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CommandeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Commandes");
        return commandeRepository.findAllBy(pageable).map(commandeMapper::toDto);
    }

    /**
     * Returns the number of commandes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return commandeRepository.count();
    }

    /**
     * Get one commande by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CommandeDTO> findOne(Long id) {
        log.debug("Request to get Commande : {}", id);
        return commandeRepository.findById(id).map(commandeMapper::toDto);
    }

    /**
     * Delete the commande by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Commande : {}", id);
        return commandeRepository.deleteById(id);
    }
}
