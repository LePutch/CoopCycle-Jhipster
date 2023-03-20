package jerem.coopcycle.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jerem.coopcycle.repository.RestaurateurRepository;
import jerem.coopcycle.service.RestaurateurService;
import jerem.coopcycle.service.dto.RestaurateurDTO;
import jerem.coopcycle.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link jerem.coopcycle.domain.Restaurateur}.
 */
@RestController
@RequestMapping("/api")
public class RestaurateurResource {

    private final Logger log = LoggerFactory.getLogger(RestaurateurResource.class);

    private static final String ENTITY_NAME = "restaurateur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RestaurateurService restaurateurService;

    private final RestaurateurRepository restaurateurRepository;

    public RestaurateurResource(RestaurateurService restaurateurService, RestaurateurRepository restaurateurRepository) {
        this.restaurateurService = restaurateurService;
        this.restaurateurRepository = restaurateurRepository;
    }

    /**
     * {@code POST  /restaurateurs} : Create a new restaurateur.
     *
     * @param restaurateurDTO the restaurateurDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restaurateurDTO, or with status {@code 400 (Bad Request)} if the restaurateur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/restaurateurs")
    public Mono<ResponseEntity<RestaurateurDTO>> createRestaurateur(@Valid @RequestBody RestaurateurDTO restaurateurDTO)
        throws URISyntaxException {
        log.debug("REST request to save Restaurateur : {}", restaurateurDTO);
        if (restaurateurDTO.getId() != null) {
            throw new BadRequestAlertException("A new restaurateur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return restaurateurService
            .save(restaurateurDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/restaurateurs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /restaurateurs/:id} : Updates an existing restaurateur.
     *
     * @param id the id of the restaurateurDTO to save.
     * @param restaurateurDTO the restaurateurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurateurDTO,
     * or with status {@code 400 (Bad Request)} if the restaurateurDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the restaurateurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/restaurateurs/{id}")
    public Mono<ResponseEntity<RestaurateurDTO>> updateRestaurateur(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RestaurateurDTO restaurateurDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Restaurateur : {}, {}", id, restaurateurDTO);
        if (restaurateurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurateurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return restaurateurRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return restaurateurService
                    .update(restaurateurDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /restaurateurs/:id} : Partial updates given fields of an existing restaurateur, field will ignore if it is null
     *
     * @param id the id of the restaurateurDTO to save.
     * @param restaurateurDTO the restaurateurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurateurDTO,
     * or with status {@code 400 (Bad Request)} if the restaurateurDTO is not valid,
     * or with status {@code 404 (Not Found)} if the restaurateurDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the restaurateurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/restaurateurs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RestaurateurDTO>> partialUpdateRestaurateur(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RestaurateurDTO restaurateurDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Restaurateur partially : {}, {}", id, restaurateurDTO);
        if (restaurateurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurateurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return restaurateurRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RestaurateurDTO> result = restaurateurService.partialUpdate(restaurateurDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /restaurateurs} : get all the restaurateurs.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restaurateurs in body.
     */
    @GetMapping("/restaurateurs")
    public Mono<ResponseEntity<List<RestaurateurDTO>>> getAllRestaurateurs(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Restaurateurs");
        return restaurateurService
            .countAll()
            .zipWith(restaurateurService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /restaurateurs/:id} : get the "id" restaurateur.
     *
     * @param id the id of the restaurateurDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restaurateurDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/restaurateurs/{id}")
    public Mono<ResponseEntity<RestaurateurDTO>> getRestaurateur(@PathVariable Long id) {
        log.debug("REST request to get Restaurateur : {}", id);
        Mono<RestaurateurDTO> restaurateurDTO = restaurateurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(restaurateurDTO);
    }

    /**
     * {@code DELETE  /restaurateurs/:id} : delete the "id" restaurateur.
     *
     * @param id the id of the restaurateurDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/restaurateurs/{id}")
    public Mono<ResponseEntity<Void>> deleteRestaurateur(@PathVariable Long id) {
        log.debug("REST request to delete Restaurateur : {}", id);
        return restaurateurService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
