package jerem.coopcycle.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import jerem.coopcycle.IntegrationTest;
import jerem.coopcycle.domain.Paiement;
import jerem.coopcycle.domain.enumeration.PaymentType;
import jerem.coopcycle.repository.EntityManager;
import jerem.coopcycle.repository.PaiementRepository;
import jerem.coopcycle.service.dto.PaiementDTO;
import jerem.coopcycle.service.mapper.PaiementMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link PaiementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PaiementResourceIT {

    private static final Float DEFAULT_AMOUNT = 0F;
    private static final Float UPDATED_AMOUNT = 1F;

    private static final PaymentType DEFAULT_PAYMENT_TYPE = PaymentType.CB;
    private static final PaymentType UPDATED_PAYMENT_TYPE = PaymentType.MASTERCARD;

    private static final String ENTITY_API_URL = "/api/paiements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private PaiementMapper paiementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Paiement paiement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paiement createEntity(EntityManager em) {
        Paiement paiement = new Paiement().amount(DEFAULT_AMOUNT).paymentType(DEFAULT_PAYMENT_TYPE);
        return paiement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paiement createUpdatedEntity(EntityManager em) {
        Paiement paiement = new Paiement().amount(UPDATED_AMOUNT).paymentType(UPDATED_PAYMENT_TYPE);
        return paiement;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Paiement.class).block();
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
        paiement = createEntity(em);
    }

    @Test
    void createPaiement() throws Exception {
        int databaseSizeBeforeCreate = paiementRepository.findAll().collectList().block().size();
        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeCreate + 1);
        Paiement testPaiement = paiementList.get(paiementList.size() - 1);
        assertThat(testPaiement.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPaiement.getPaymentType()).isEqualTo(DEFAULT_PAYMENT_TYPE);
    }

    @Test
    void createPaiementWithExistingId() throws Exception {
        // Create the Paiement with an existing ID
        paiement.setId(1L);
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        int databaseSizeBeforeCreate = paiementRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = paiementRepository.findAll().collectList().block().size();
        // set the field null
        paiement.setAmount(null);

        // Create the Paiement, which fails.
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPaymentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = paiementRepository.findAll().collectList().block().size();
        // set the field null
        paiement.setPaymentType(null);

        // Create the Paiement, which fails.
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPaiements() {
        // Initialize the database
        paiementRepository.save(paiement).block();

        // Get all the paiementList
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
            .value(hasItem(paiement.getId().intValue()))
            .jsonPath("$.[*].amount")
            .value(hasItem(DEFAULT_AMOUNT.doubleValue()))
            .jsonPath("$.[*].paymentType")
            .value(hasItem(DEFAULT_PAYMENT_TYPE.toString()));
    }

    @Test
    void getPaiement() {
        // Initialize the database
        paiementRepository.save(paiement).block();

        // Get the paiement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, paiement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(paiement.getId().intValue()))
            .jsonPath("$.amount")
            .value(is(DEFAULT_AMOUNT.doubleValue()))
            .jsonPath("$.paymentType")
            .value(is(DEFAULT_PAYMENT_TYPE.toString()));
    }

    @Test
    void getNonExistingPaiement() {
        // Get the paiement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPaiement() throws Exception {
        // Initialize the database
        paiementRepository.save(paiement).block();

        int databaseSizeBeforeUpdate = paiementRepository.findAll().collectList().block().size();

        // Update the paiement
        Paiement updatedPaiement = paiementRepository.findById(paiement.getId()).block();
        updatedPaiement.amount(UPDATED_AMOUNT).paymentType(UPDATED_PAYMENT_TYPE);
        PaiementDTO paiementDTO = paiementMapper.toDto(updatedPaiement);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paiementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);
        Paiement testPaiement = paiementList.get(paiementList.size() - 1);
        assertThat(testPaiement.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPaiement.getPaymentType()).isEqualTo(UPDATED_PAYMENT_TYPE);
    }

    @Test
    void putNonExistingPaiement() throws Exception {
        int databaseSizeBeforeUpdate = paiementRepository.findAll().collectList().block().size();
        paiement.setId(count.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paiementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPaiement() throws Exception {
        int databaseSizeBeforeUpdate = paiementRepository.findAll().collectList().block().size();
        paiement.setId(count.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPaiement() throws Exception {
        int databaseSizeBeforeUpdate = paiementRepository.findAll().collectList().block().size();
        paiement.setId(count.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePaiementWithPatch() throws Exception {
        // Initialize the database
        paiementRepository.save(paiement).block();

        int databaseSizeBeforeUpdate = paiementRepository.findAll().collectList().block().size();

        // Update the paiement using partial update
        Paiement partialUpdatedPaiement = new Paiement();
        partialUpdatedPaiement.setId(paiement.getId());

        partialUpdatedPaiement.paymentType(UPDATED_PAYMENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPaiement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPaiement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);
        Paiement testPaiement = paiementList.get(paiementList.size() - 1);
        assertThat(testPaiement.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPaiement.getPaymentType()).isEqualTo(UPDATED_PAYMENT_TYPE);
    }

    @Test
    void fullUpdatePaiementWithPatch() throws Exception {
        // Initialize the database
        paiementRepository.save(paiement).block();

        int databaseSizeBeforeUpdate = paiementRepository.findAll().collectList().block().size();

        // Update the paiement using partial update
        Paiement partialUpdatedPaiement = new Paiement();
        partialUpdatedPaiement.setId(paiement.getId());

        partialUpdatedPaiement.amount(UPDATED_AMOUNT).paymentType(UPDATED_PAYMENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPaiement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPaiement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);
        Paiement testPaiement = paiementList.get(paiementList.size() - 1);
        assertThat(testPaiement.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPaiement.getPaymentType()).isEqualTo(UPDATED_PAYMENT_TYPE);
    }

    @Test
    void patchNonExistingPaiement() throws Exception {
        int databaseSizeBeforeUpdate = paiementRepository.findAll().collectList().block().size();
        paiement.setId(count.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, paiementDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPaiement() throws Exception {
        int databaseSizeBeforeUpdate = paiementRepository.findAll().collectList().block().size();
        paiement.setId(count.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPaiement() throws Exception {
        int databaseSizeBeforeUpdate = paiementRepository.findAll().collectList().block().size();
        paiement.setId(count.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paiementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePaiement() {
        // Initialize the database
        paiementRepository.save(paiement).block();

        int databaseSizeBeforeDelete = paiementRepository.findAll().collectList().block().size();

        // Delete the paiement
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, paiement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Paiement> paiementList = paiementRepository.findAll().collectList().block();
        assertThat(paiementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
