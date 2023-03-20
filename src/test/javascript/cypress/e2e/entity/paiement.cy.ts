import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Paiement e2e test', () => {
  const paiementPageUrl = '/paiement';
  const paiementPageUrlPattern = new RegExp('/paiement(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const paiementSample = { amount: 53967, paymentType: 'GOOGLE_PAY' };

  let paiement;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/paiements+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/paiements').as('postEntityRequest');
    cy.intercept('DELETE', '/api/paiements/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (paiement) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/paiements/${paiement.id}`,
      }).then(() => {
        paiement = undefined;
      });
    }
  });

  it('Paiements menu should load Paiements page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('paiement');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Paiement').should('exist');
    cy.url().should('match', paiementPageUrlPattern);
  });

  describe('Paiement page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(paiementPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Paiement page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/paiement/new$'));
        cy.getEntityCreateUpdateHeading('Paiement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', paiementPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/paiements',
          body: paiementSample,
        }).then(({ body }) => {
          paiement = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/paiements+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/paiements?page=0&size=20>; rel="last",<http://localhost/api/paiements?page=0&size=20>; rel="first"',
              },
              body: [paiement],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(paiementPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Paiement page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('paiement');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', paiementPageUrlPattern);
      });

      it('edit button click should load edit Paiement page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Paiement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', paiementPageUrlPattern);
      });

      it('edit button click should load edit Paiement page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Paiement');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', paiementPageUrlPattern);
      });

      it('last delete button click should delete instance of Paiement', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('paiement').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', paiementPageUrlPattern);

        paiement = undefined;
      });
    });
  });

  describe('new Paiement page', () => {
    beforeEach(() => {
      cy.visit(`${paiementPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Paiement');
    });

    it('should create an instance of Paiement', () => {
      cy.get(`[data-cy="amount"]`).type('1431').should('have.value', '1431');

      cy.get(`[data-cy="paymentType"]`).select('APPLE_PAY');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        paiement = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', paiementPageUrlPattern);
    });
  });
});
