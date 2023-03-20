package jerem.coopcycle.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import jerem.coopcycle.IntegrationTest;
import jerem.coopcycle.domain.Restaurateur;
import jerem.coopcycle.repository.EntityManager;
import jerem.coopcycle.repository.RestaurateurRepository;
import jerem.coopcycle.service.dto.RestaurateurDTO;
import jerem.coopcycle.service.mapper.RestaurateurMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RestaurateurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RestaurateurResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/restaurateurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RestaurateurRepository restaurateurRepository;

    @Autowired
    private RestaurateurMapper restaurateurMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Restaurateur restaurateur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurateur createEntity(EntityManager em) {
        Restaurateur restaurateur = new Restaurateur().firstName(DEFAULT_FIRST_NAME).lastName(DEFAULT_LAST_NAME);
        return restaurateur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurateur createUpdatedEntity(EntityManager em) {
        Restaurateur restaurateur = new Restaurateur().firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);
        return restaurateur;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Restaurateur.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        restaurateur = createEntity(em);
    }

    @Test
    void createRestaurateur() throws Exception {
        int databaseSizeBeforeCreate = restaurateurRepository.findAll().collectList().block().size();
        // Create the Restaurateur
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(restaurateur);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeCreate + 1);
        Restaurateur testRestaurateur = restaurateurList.get(restaurateurList.size() - 1);
        assertThat(testRestaurateur.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testRestaurateur.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    void createRestaurateurWithExistingId() throws Exception {
        // Create the Restaurateur with an existing ID
        restaurateur.setId(1L);
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(restaurateur);

        int databaseSizeBeforeCreate = restaurateurRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurateurRepository.findAll().collectList().block().size();
        // set the field null
        restaurateur.setFirstName(null);

        // Create the Restaurateur, which fails.
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(restaurateur);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurateurRepository.findAll().collectList().block().size();
        // set the field null
        restaurateur.setLastName(null);

        // Create the Restaurateur, which fails.
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(restaurateur);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRestaurateurs() {
        // Initialize the database
        restaurateurRepository.save(restaurateur).block();

        // Get all the restaurateurList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(restaurateur.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME));
    }

    @Test
    void getRestaurateur() {
        // Initialize the database
        restaurateurRepository.save(restaurateur).block();

        // Get the restaurateur
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, restaurateur.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(restaurateur.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME));
    }

    @Test
    void getNonExistingRestaurateur() {
        // Get the restaurateur
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRestaurateur() throws Exception {
        // Initialize the database
        restaurateurRepository.save(restaurateur).block();

        int databaseSizeBeforeUpdate = restaurateurRepository.findAll().collectList().block().size();

        // Update the restaurateur
        Restaurateur updatedRestaurateur = restaurateurRepository.findById(restaurateur.getId()).block();
        updatedRestaurateur.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(updatedRestaurateur);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurateurDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeUpdate);
        Restaurateur testRestaurateur = restaurateurList.get(restaurateurList.size() - 1);
        assertThat(testRestaurateur.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testRestaurateur.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    void putNonExistingRestaurateur() throws Exception {
        int databaseSizeBeforeUpdate = restaurateurRepository.findAll().collectList().block().size();
        restaurateur.setId(count.incrementAndGet());

        // Create the Restaurateur
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(restaurateur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurateurDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRestaurateur() throws Exception {
        int databaseSizeBeforeUpdate = restaurateurRepository.findAll().collectList().block().size();
        restaurateur.setId(count.incrementAndGet());

        // Create the Restaurateur
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(restaurateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRestaurateur() throws Exception {
        int databaseSizeBeforeUpdate = restaurateurRepository.findAll().collectList().block().size();
        restaurateur.setId(count.incrementAndGet());

        // Create the Restaurateur
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(restaurateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRestaurateurWithPatch() throws Exception {
        // Initialize the database
        restaurateurRepository.save(restaurateur).block();

        int databaseSizeBeforeUpdate = restaurateurRepository.findAll().collectList().block().size();

        // Update the restaurateur using partial update
        Restaurateur partialUpdatedRestaurateur = new Restaurateur();
        partialUpdatedRestaurateur.setId(restaurateur.getId());

        partialUpdatedRestaurateur.firstName(UPDATED_FIRST_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurateur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurateur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeUpdate);
        Restaurateur testRestaurateur = restaurateurList.get(restaurateurList.size() - 1);
        assertThat(testRestaurateur.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testRestaurateur.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    void fullUpdateRestaurateurWithPatch() throws Exception {
        // Initialize the database
        restaurateurRepository.save(restaurateur).block();

        int databaseSizeBeforeUpdate = restaurateurRepository.findAll().collectList().block().size();

        // Update the restaurateur using partial update
        Restaurateur partialUpdatedRestaurateur = new Restaurateur();
        partialUpdatedRestaurateur.setId(restaurateur.getId());

        partialUpdatedRestaurateur.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurateur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurateur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeUpdate);
        Restaurateur testRestaurateur = restaurateurList.get(restaurateurList.size() - 1);
        assertThat(testRestaurateur.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testRestaurateur.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    void patchNonExistingRestaurateur() throws Exception {
        int databaseSizeBeforeUpdate = restaurateurRepository.findAll().collectList().block().size();
        restaurateur.setId(count.incrementAndGet());

        // Create the Restaurateur
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(restaurateur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, restaurateurDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRestaurateur() throws Exception {
        int databaseSizeBeforeUpdate = restaurateurRepository.findAll().collectList().block().size();
        restaurateur.setId(count.incrementAndGet());

        // Create the Restaurateur
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(restaurateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRestaurateur() throws Exception {
        int databaseSizeBeforeUpdate = restaurateurRepository.findAll().collectList().block().size();
        restaurateur.setId(count.incrementAndGet());

        // Create the Restaurateur
        RestaurateurDTO restaurateurDTO = restaurateurMapper.toDto(restaurateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurateurDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Restaurateur in the database
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRestaurateur() {
        // Initialize the database
        restaurateurRepository.save(restaurateur).block();

        int databaseSizeBeforeDelete = restaurateurRepository.findAll().collectList().block().size();

        // Delete the restaurateur
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, restaurateur.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Restaurateur> restaurateurList = restaurateurRepository.findAll().collectList().block();
        assertThat(restaurateurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
