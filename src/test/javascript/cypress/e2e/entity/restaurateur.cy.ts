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

describe('Restaurateur e2e test', () => {
  const restaurateurPageUrl = '/restaurateur';
  const restaurateurPageUrlPattern = new RegExp('/restaurateur(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const restaurateurSample = { firstName: 'Moriah', lastName: 'Cruickshank' };

  let restaurateur;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/restaurateurs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/restaurateurs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/restaurateurs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (restaurateur) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/restaurateurs/${restaurateur.id}`,
      }).then(() => {
        restaurateur = undefined;
      });
    }
  });

  it('Restaurateurs menu should load Restaurateurs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('restaurateur');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Restaurateur').should('exist');
    cy.url().should('match', restaurateurPageUrlPattern);
  });

  describe('Restaurateur page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(restaurateurPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Restaurateur page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/restaurateur/new$'));
        cy.getEntityCreateUpdateHeading('Restaurateur');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurateurPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/restaurateurs',
          body: restaurateurSample,
        }).then(({ body }) => {
          restaurateur = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/restaurateurs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/restaurateurs?page=0&size=20>; rel="last",<http://localhost/api/restaurateurs?page=0&size=20>; rel="first"',
              },
              body: [restaurateur],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(restaurateurPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Restaurateur page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('restaurateur');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurateurPageUrlPattern);
      });

      it('edit button click should load edit Restaurateur page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Restaurateur');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurateurPageUrlPattern);
      });

      it('edit button click should load edit Restaurateur page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Restaurateur');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurateurPageUrlPattern);
      });

      it('last delete button click should delete instance of Restaurateur', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('restaurateur').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurateurPageUrlPattern);

        restaurateur = undefined;
      });
    });
  });

  describe('new Restaurateur page', () => {
    beforeEach(() => {
      cy.visit(`${restaurateurPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Restaurateur');
    });

    it('should create an instance of Restaurateur', () => {
      cy.get(`[data-cy="firstName"]`).type('Wilburn').should('have.value', 'Wilburn');

      cy.get(`[data-cy="lastName"]`).type('Botsford').should('have.value', 'Botsford');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        restaurateur = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', restaurateurPageUrlPattern);
    });
  });
});
