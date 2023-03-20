package jerem.coopcycle.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import jerem.coopcycle.IntegrationTest;
import jerem.coopcycle.domain.Societaire;
import jerem.coopcycle.domain.enumeration.SocietaireType;
import jerem.coopcycle.repository.EntityManager;
import jerem.coopcycle.repository.SocietaireRepository;
import jerem.coopcycle.service.dto.SocietaireDTO;
import jerem.coopcycle.service.mapper.SocietaireMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link SocietaireResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SocietaireResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final SocietaireType DEFAULT_TYPE = SocietaireType.LIVREUR;
    private static final SocietaireType UPDATED_TYPE = SocietaireType.RESTAURATEUR;

    private static final String ENTITY_API_URL = "/api/societaires";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SocietaireRepository societaireRepository;

    @Autowired
    private SocietaireMapper societaireMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Societaire societaire;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Societaire createEntity(EntityManager em) {
        Societaire societaire = new Societaire().firstName(DEFAULT_FIRST_NAME).lastName(DEFAULT_LAST_NAME).type(DEFAULT_TYPE);
        return societaire;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Societaire createUpdatedEntity(EntityManager em) {
        Societaire societaire = new Societaire().firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).type(UPDATED_TYPE);
        return societaire;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Societaire.class).block();
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
        societaire = createEntity(em);
    }

    @Test
    void createSocietaire() throws Exception {
        int databaseSizeBeforeCreate = societaireRepository.findAll().collectList().block().size();
        // Create the Societaire
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeCreate + 1);
        Societaire testSocietaire = societaireList.get(societaireList.size() - 1);
        assertThat(testSocietaire.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testSocietaire.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testSocietaire.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void createSocietaireWithExistingId() throws Exception {
        // Create the Societaire with an existing ID
        societaire.setId(1L);
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);

        int databaseSizeBeforeCreate = societaireRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = societaireRepository.findAll().collectList().block().size();
        // set the field null
        societaire.setFirstName(null);

        // Create the Societaire, which fails.
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = societaireRepository.findAll().collectList().block().size();
        // set the field null
        societaire.setLastName(null);

        // Create the Societaire, which fails.
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = societaireRepository.findAll().collectList().block().size();
        // set the field null
        societaire.setType(null);

        // Create the Societaire, which fails.
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSocietaires() {
        // Initialize the database
        societaireRepository.save(societaire).block();

        // Get all the societaireList
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
            .value(hasItem(societaire.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()));
    }

    @Test
    void getSocietaire() {
        // Initialize the database
        societaireRepository.save(societaire).block();

        // Get the societaire
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, societaire.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(societaire.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()));
    }

    @Test
    void getNonExistingSocietaire() {
        // Get the societaire
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSocietaire() throws Exception {
        // Initialize the database
        societaireRepository.save(societaire).block();

        int databaseSizeBeforeUpdate = societaireRepository.findAll().collectList().block().size();

        // Update the societaire
        Societaire updatedSocietaire = societaireRepository.findById(societaire.getId()).block();
        updatedSocietaire.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).type(UPDATED_TYPE);
        SocietaireDTO societaireDTO = societaireMapper.toDto(updatedSocietaire);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, societaireDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeUpdate);
        Societaire testSocietaire = societaireList.get(societaireList.size() - 1);
        assertThat(testSocietaire.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testSocietaire.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testSocietaire.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void putNonExistingSocietaire() throws Exception {
        int databaseSizeBeforeUpdate = societaireRepository.findAll().collectList().block().size();
        societaire.setId(count.incrementAndGet());

        // Create the Societaire
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, societaireDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSocietaire() throws Exception {
        int databaseSizeBeforeUpdate = societaireRepository.findAll().collectList().block().size();
        societaire.setId(count.incrementAndGet());

        // Create the Societaire
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSocietaire() throws Exception {
        int databaseSizeBeforeUpdate = societaireRepository.findAll().collectList().block().size();
        societaire.setId(count.incrementAndGet());

        // Create the Societaire
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSocietaireWithPatch() throws Exception {
        // Initialize the database
        societaireRepository.save(societaire).block();

        int databaseSizeBeforeUpdate = societaireRepository.findAll().collectList().block().size();

        // Update the societaire using partial update
        Societaire partialUpdatedSocietaire = new Societaire();
        partialUpdatedSocietaire.setId(societaire.getId());

        partialUpdatedSocietaire.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSocietaire.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSocietaire))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeUpdate);
        Societaire testSocietaire = societaireList.get(societaireList.size() - 1);
        assertThat(testSocietaire.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testSocietaire.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testSocietaire.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void fullUpdateSocietaireWithPatch() throws Exception {
        // Initialize the database
        societaireRepository.save(societaire).block();

        int databaseSizeBeforeUpdate = societaireRepository.findAll().collectList().block().size();

        // Update the societaire using partial update
        Societaire partialUpdatedSocietaire = new Societaire();
        partialUpdatedSocietaire.setId(societaire.getId());

        partialUpdatedSocietaire.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSocietaire.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSocietaire))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeUpdate);
        Societaire testSocietaire = societaireList.get(societaireList.size() - 1);
        assertThat(testSocietaire.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testSocietaire.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testSocietaire.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void patchNonExistingSocietaire() throws Exception {
        int databaseSizeBeforeUpdate = societaireRepository.findAll().collectList().block().size();
        societaire.setId(count.incrementAndGet());

        // Create the Societaire
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, societaireDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSocietaire() throws Exception {
        int databaseSizeBeforeUpdate = societaireRepository.findAll().collectList().block().size();
        societaire.setId(count.incrementAndGet());

        // Create the Societaire
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSocietaire() throws Exception {
        int databaseSizeBeforeUpdate = societaireRepository.findAll().collectList().block().size();
        societaire.setId(count.incrementAndGet());

        // Create the Societaire
        SocietaireDTO societaireDTO = societaireMapper.toDto(societaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(societaireDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Societaire in the database
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSocietaire() {
        // Initialize the database
        societaireRepository.save(societaire).block();

        int databaseSizeBeforeDelete = societaireRepository.findAll().collectList().block().size();

        // Delete the societaire
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, societaire.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Societaire> societaireList = societaireRepository.findAll().collectList().block();
        assertThat(societaireList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
