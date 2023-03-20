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

describe('Commande e2e test', () => {
  const commandePageUrl = '/commande';
  const commandePageUrlPattern = new RegExp('/commande(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const commandeSample = { dateTime: '2023-03-19T07:27:19.878Z', status: 'EN_COURS' };

  let commande;
  let panier;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/paniers',
      body: { description: 'Global', price: 34070 },
    }).then(({ body }) => {
      panier = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/commandes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/commandes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/commandes/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/paniers', {
      statusCode: 200,
      body: [panier],
    });

    cy.intercept('GET', '/api/paiements', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/clients', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/restaurateurs', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (commande) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/commandes/${commande.id}`,
      }).then(() => {
        commande = undefined;
      });
    }
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

  it('Commandes menu should load Commandes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('commande');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Commande').should('exist');
    cy.url().should('match', commandePageUrlPattern);
  });

  describe('Commande page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(commandePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Commande page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/commande/new$'));
        cy.getEntityCreateUpdateHeading('Commande');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', commandePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/commandes',
          body: {
            ...commandeSample,
            panier: panier,
          },
        }).then(({ body }) => {
          commande = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/commandes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/commandes?page=0&size=20>; rel="last",<http://localhost/api/commandes?page=0&size=20>; rel="first"',
              },
              body: [commande],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(commandePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Commande page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('commande');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', commandePageUrlPattern);
      });

      it('edit button click should load edit Commande page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Commande');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', commandePageUrlPattern);
      });

      it('edit button click should load edit Commande page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Commande');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', commandePageUrlPattern);
      });

      it('last delete button click should delete instance of Commande', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('commande').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', commandePageUrlPattern);

        commande = undefined;
      });
    });
  });

  describe('new Commande page', () => {
    beforeEach(() => {
      cy.visit(`${commandePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Commande');
    });

    it('should create an instance of Commande', () => {
      cy.get(`[data-cy="dateTime"]`).type('2023-03-19T04:00').blur().should('have.value', '2023-03-19T04:00');

      cy.get(`[data-cy="status"]`).select('LIVREE');

      cy.get(`[data-cy="panier"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        commande = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', commandePageUrlPattern);
    });
  });
});
