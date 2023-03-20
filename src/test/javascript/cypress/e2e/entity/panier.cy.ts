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

describe('Panier e2e test', () => {
  const panierPageUrl = '/panier';
  const panierPageUrlPattern = new RegExp('/panier(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const panierSample = { price: 27138 };

  let panier;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/paniers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/paniers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/paniers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (panier) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/paniers/${panier.id}`,
      }).then(() => {
        panier = undefined;
      });
    }
  });

  it('Paniers menu should load Paniers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('panier');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Panier').should('exist');
    cy.url().should('match', panierPageUrlPattern);
  });

  describe('Panier page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(panierPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Panier page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/panier/new$'));
        cy.getEntityCreateUpdateHeading('Panier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', panierPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/paniers',
          body: panierSample,
        }).then(({ body }) => {
          panier = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/paniers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/paniers?page=0&size=20>; rel="last",<http://localhost/api/paniers?page=0&size=20>; rel="first"',
              },
              body: [panier],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(panierPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Panier page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('panier');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', panierPageUrlPattern);
      });

      it('edit button click should load edit Panier page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Panier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', panierPageUrlPattern);
      });

      it('edit button click should load edit Panier page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Panier');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', panierPageUrlPattern);
      });

      it('last delete button click should delete instance of Panier', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('panier').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', panierPageUrlPattern);

        panier = undefined;
      });
    });
  });

  describe('new Panier page', () => {
    beforeEach(() => {
      cy.visit(`${panierPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Panier');
    });

    it('should create an instance of Panier', () => {
      cy.get(`[data-cy="description"]`).type('Analyst target Visionary').should('have.value', 'Analyst target Visionary');

      cy.get(`[data-cy="price"]`).type('99083').should('have.value', '99083');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        panier = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', panierPageUrlPattern);
    });
  });
});
