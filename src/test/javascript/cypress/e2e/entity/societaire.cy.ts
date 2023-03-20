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

describe('Societaire e2e test', () => {
  const societairePageUrl = '/societaire';
  const societairePageUrlPattern = new RegExp('/societaire(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const societaireSample = { firstName: 'Herbert', lastName: 'Mann', type: 'RESTAURATEUR' };

  let societaire;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/societaires+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/societaires').as('postEntityRequest');
    cy.intercept('DELETE', '/api/societaires/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (societaire) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/societaires/${societaire.id}`,
      }).then(() => {
        societaire = undefined;
      });
    }
  });

  it('Societaires menu should load Societaires page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('societaire');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Societaire').should('exist');
    cy.url().should('match', societairePageUrlPattern);
  });

  describe('Societaire page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(societairePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Societaire page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/societaire/new$'));
        cy.getEntityCreateUpdateHeading('Societaire');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', societairePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/societaires',
          body: societaireSample,
        }).then(({ body }) => {
          societaire = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/societaires+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/societaires?page=0&size=20>; rel="last",<http://localhost/api/societaires?page=0&size=20>; rel="first"',
              },
              body: [societaire],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(societairePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Societaire page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('societaire');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', societairePageUrlPattern);
      });

      it('edit button click should load edit Societaire page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Societaire');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', societairePageUrlPattern);
      });

      it('edit button click should load edit Societaire page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Societaire');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', societairePageUrlPattern);
      });

      it('last delete button click should delete instance of Societaire', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('societaire').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', societairePageUrlPattern);

        societaire = undefined;
      });
    });
  });

  describe('new Societaire page', () => {
    beforeEach(() => {
      cy.visit(`${societairePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Societaire');
    });

    it('should create an instance of Societaire', () => {
      cy.get(`[data-cy="firstName"]`).type('Trinity').should('have.value', 'Trinity');

      cy.get(`[data-cy="lastName"]`).type('Hartmann').should('have.value', 'Hartmann');

      cy.get(`[data-cy="type"]`).select('LIVREUR');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        societaire = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', societairePageUrlPattern);
    });
  });
});
