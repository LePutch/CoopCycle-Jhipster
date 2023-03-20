import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CommandeFormService } from './commande-form.service';
import { CommandeService } from '../service/commande.service';
import { ICommande } from '../commande.model';
import { IPanier } from 'app/entities/panier/panier.model';
import { PanierService } from 'app/entities/panier/service/panier.service';
import { IPaiement } from 'app/entities/paiement/paiement.model';
import { PaiementService } from 'app/entities/paiement/service/paiement.service';

import { CommandeUpdateComponent } from './commande-update.component';

describe('Commande Management Update Component', () => {
  let comp: CommandeUpdateComponent;
  let fixture: ComponentFixture<CommandeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let commandeFormService: CommandeFormService;
  let commandeService: CommandeService;
  let panierService: PanierService;
  let paiementService: PaiementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CommandeUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CommandeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CommandeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    commandeFormService = TestBed.inject(CommandeFormService);
    commandeService = TestBed.inject(CommandeService);
    panierService = TestBed.inject(PanierService);
    paiementService = TestBed.inject(PaiementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Panier query and add missing value', () => {
      const commande: ICommande = { id: 456 };
      const panier: IPanier = { id: 82318 };
      commande.panier = panier;

      const panierCollection: IPanier[] = [{ id: 64761 }];
      jest.spyOn(panierService, 'query').mockReturnValue(of(new HttpResponse({ body: panierCollection })));
      const additionalPaniers = [panier];
      const expectedCollection: IPanier[] = [...additionalPaniers, ...panierCollection];
      jest.spyOn(panierService, 'addPanierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      expect(panierService.query).toHaveBeenCalled();
      expect(panierService.addPanierToCollectionIfMissing).toHaveBeenCalledWith(
        panierCollection,
        ...additionalPaniers.map(expect.objectContaining)
      );
      expect(comp.paniersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Paiement query and add missing value', () => {
      const commande: ICommande = { id: 456 };
      const paiement: IPaiement = { id: 28554 };
      commande.paiement = paiement;

      const paiementCollection: IPaiement[] = [{ id: 22813 }];
      jest.spyOn(paiementService, 'query').mockReturnValue(of(new HttpResponse({ body: paiementCollection })));
      const additionalPaiements = [paiement];
      const expectedCollection: IPaiement[] = [...additionalPaiements, ...paiementCollection];
      jest.spyOn(paiementService, 'addPaiementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      expect(paiementService.query).toHaveBeenCalled();
      expect(paiementService.addPaiementToCollectionIfMissing).toHaveBeenCalledWith(
        paiementCollection,
        ...additionalPaiements.map(expect.objectContaining)
      );
      expect(comp.paiementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const commande: ICommande = { id: 456 };
      const panier: IPanier = { id: 63803 };
      commande.panier = panier;
      const paiement: IPaiement = { id: 5351 };
      commande.paiement = paiement;

      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      expect(comp.paniersSharedCollection).toContain(panier);
      expect(comp.paiementsSharedCollection).toContain(paiement);
      expect(comp.commande).toEqual(commande);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICommande>>();
      const commande = { id: 123 };
      jest.spyOn(commandeFormService, 'getCommande').mockReturnValue(commande);
      jest.spyOn(commandeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: commande }));
      saveSubject.complete();

      // THEN
      expect(commandeFormService.getCommande).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(commandeService.update).toHaveBeenCalledWith(expect.objectContaining(commande));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICommande>>();
      const commande = { id: 123 };
      jest.spyOn(commandeFormService, 'getCommande').mockReturnValue({ id: null });
      jest.spyOn(commandeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commande: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: commande }));
      saveSubject.complete();

      // THEN
      expect(commandeFormService.getCommande).toHaveBeenCalled();
      expect(commandeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICommande>>();
      const commande = { id: 123 };
      jest.spyOn(commandeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(commandeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePanier', () => {
      it('Should forward to panierService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(panierService, 'comparePanier');
        comp.comparePanier(entity, entity2);
        expect(panierService.comparePanier).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePaiement', () => {
      it('Should forward to paiementService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(paiementService, 'comparePaiement');
        comp.comparePaiement(entity, entity2);
        expect(paiementService.comparePaiement).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
