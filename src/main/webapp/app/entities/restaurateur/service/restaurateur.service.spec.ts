import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRestaurateur } from '../restaurateur.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../restaurateur.test-samples';

import { RestaurateurService } from './restaurateur.service';

const requireRestSample: IRestaurateur = {
  ...sampleWithRequiredData,
};

describe('Restaurateur Service', () => {
  let service: RestaurateurService;
  let httpMock: HttpTestingController;
  let expectedResult: IRestaurateur | IRestaurateur[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RestaurateurService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Restaurateur', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const restaurateur = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(restaurateur).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Restaurateur', () => {
      const restaurateur = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(restaurateur).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Restaurateur', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Restaurateur', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Restaurateur', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRestaurateurToCollectionIfMissing', () => {
      it('should add a Restaurateur to an empty array', () => {
        const restaurateur: IRestaurateur = sampleWithRequiredData;
        expectedResult = service.addRestaurateurToCollectionIfMissing([], restaurateur);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(restaurateur);
      });

      it('should not add a Restaurateur to an array that contains it', () => {
        const restaurateur: IRestaurateur = sampleWithRequiredData;
        const restaurateurCollection: IRestaurateur[] = [
          {
            ...restaurateur,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRestaurateurToCollectionIfMissing(restaurateurCollection, restaurateur);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Restaurateur to an array that doesn't contain it", () => {
        const restaurateur: IRestaurateur = sampleWithRequiredData;
        const restaurateurCollection: IRestaurateur[] = [sampleWithPartialData];
        expectedResult = service.addRestaurateurToCollectionIfMissing(restaurateurCollection, restaurateur);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(restaurateur);
      });

      it('should add only unique Restaurateur to an array', () => {
        const restaurateurArray: IRestaurateur[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const restaurateurCollection: IRestaurateur[] = [sampleWithRequiredData];
        expectedResult = service.addRestaurateurToCollectionIfMissing(restaurateurCollection, ...restaurateurArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const restaurateur: IRestaurateur = sampleWithRequiredData;
        const restaurateur2: IRestaurateur = sampleWithPartialData;
        expectedResult = service.addRestaurateurToCollectionIfMissing([], restaurateur, restaurateur2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(restaurateur);
        expect(expectedResult).toContain(restaurateur2);
      });

      it('should accept null and undefined values', () => {
        const restaurateur: IRestaurateur = sampleWithRequiredData;
        expectedResult = service.addRestaurateurToCollectionIfMissing([], null, restaurateur, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(restaurateur);
      });

      it('should return initial array if no Restaurateur is added', () => {
        const restaurateurCollection: IRestaurateur[] = [sampleWithRequiredData];
        expectedResult = service.addRestaurateurToCollectionIfMissing(restaurateurCollection, undefined, null);
        expect(expectedResult).toEqual(restaurateurCollection);
      });
    });

    describe('compareRestaurateur', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRestaurateur(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRestaurateur(entity1, entity2);
        const compareResult2 = service.compareRestaurateur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRestaurateur(entity1, entity2);
        const compareResult2 = service.compareRestaurateur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRestaurateur(entity1, entity2);
        const compareResult2 = service.compareRestaurateur(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
