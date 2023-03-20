package jerem.coopcycle.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import jerem.coopcycle.IntegrationTest;
import jerem.coopcycle.domain.Panier;
import jerem.coopcycle.repository.EntityManager;
import jerem.coopcycle.repository.PanierRepository;
import jerem.coopcycle.service.dto.PanierDTO;
import jerem.coopcycle.service.mapper.PanierMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link PanierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PanierResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Float DEFAULT_PRICE = 0F;
    private static final Float UPDATED_PRICE = 1F;

    private static final String ENTITY_API_URL = "/api/paniers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private PanierMapper panierMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Panier panier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Panier createEntity(EntityManager em) {
        Panier panier = new Panier().description(DEFAULT_DESCRIPTION).price(DEFAULT_PRICE);
        return panier;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Panier createUpdatedEntity(EntityManager em) {
        Panier panier = new Panier().description(UPDATED_DESCRIPTION).price(UPDATED_PRICE);
        return panier;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Panier.class).block();
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
        panier = createEntity(em);
    }

    @Test
    void createPanier() throws Exception {
        int databaseSizeBeforeCreate = panierRepository.findAll().collectList().block().size();
        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(panierDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeCreate + 1);
        Panier testPanier = panierList.get(panierList.size() - 1);
        assertThat(testPanier.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPanier.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    void createPanierWithExistingId() throws Exception {
        // Create the Panier with an existing ID
        panier.setId(1L);
        PanierDTO panierDTO = panierMapper.toDto(panier);

        int databaseSizeBeforeCreate = panierRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(panierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = panierRepository.findAll().collectList().block().size();
        // set the field null
        panier.setPrice(null);

        // Create the Panier, which fails.
        PanierDTO panierDTO = panierMapper.toDto(panier);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(panierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPaniers() {
        // Initialize the database
        panierRepository.save(panier).block();

        // Get all the panierList
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
            .value(hasItem(panier.getId().intValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    void getPanier() {
        // Initialize the database
        panierRepository.save(panier).block();

        // Get the panier
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, panier.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(panier.getId().intValue()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.price")
            .value(is(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    void getNonExistingPanier() {
        // Get the panier
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPanier() throws Exception {
        // Initialize the database
        panierRepository.save(panier).block();

        int databaseSizeBeforeUpdate = panierRepository.findAll().collectList().block().size();

        // Update the panier
        Panier updatedPanier = panierRepository.findById(panier.getId()).block();
        updatedPanier.description(UPDATED_DESCRIPTION).price(UPDATED_PRICE);
        PanierDTO panierDTO = panierMapper.toDto(updatedPanier);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, panierDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(panierDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
        Panier testPanier = panierList.get(panierList.size() - 1);
        assertThat(testPanier.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPanier.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    void putNonExistingPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().collectList().block().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, panierDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(panierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().collectList().block().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(panierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().collectList().block().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(panierDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePanierWithPatch() throws Exception {
        // Initialize the database
        panierRepository.save(panier).block();

        int databaseSizeBeforeUpdate = panierRepository.findAll().collectList().block().size();

        // Update the panier using partial update
        Panier partialUpdatedPanier = new Panier();
        partialUpdatedPanier.setId(panier.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPanier.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPanier))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
        Panier testPanier = panierList.get(panierList.size() - 1);
        assertThat(testPanier.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPanier.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    void fullUpdatePanierWithPatch() throws Exception {
        // Initialize the database
        panierRepository.save(panier).block();

        int databaseSizeBeforeUpdate = panierRepository.findAll().collectList().block().size();

        // Update the panier using partial update
        Panier partialUpdatedPanier = new Panier();
        partialUpdatedPanier.setId(panier.getId());

        partialUpdatedPanier.description(UPDATED_DESCRIPTION).price(UPDATED_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPanier.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPanier))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
        Panier testPanier = panierList.get(panierList.size() - 1);
        assertThat(testPanier.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPanier.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    void patchNonExistingPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().collectList().block().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, panierDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(panierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().collectList().block().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(panierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().collectList().block().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(panierDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePanier() {
        // Initialize the database
        panierRepository.save(panier).block();

        int databaseSizeBeforeDelete = panierRepository.findAll().collectList().block().size();

        // Delete the panier
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, panier.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Panier> panierList = panierRepository.findAll().collectList().block();
        assertThat(panierList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
