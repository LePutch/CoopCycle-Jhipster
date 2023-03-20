import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISocietaire } from '../societaire.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../societaire.test-samples';

import { SocietaireService } from './societaire.service';

const requireRestSample: ISocietaire = {
  ...sampleWithRequiredData,
};

describe('Societaire Service', () => {
  let service: SocietaireService;
  let httpMock: HttpTestingController;
  let expectedResult: ISocietaire | ISocietaire[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SocietaireService);
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

    it('should create a Societaire', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const societaire = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(societaire).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Societaire', () => {
      const societaire = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(societaire).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Societaire', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Societaire', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Societaire', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSocietaireToCollectionIfMissing', () => {
      it('should add a Societaire to an empty array', () => {
        const societaire: ISocietaire = sampleWithRequiredData;
        expectedResult = service.addSocietaireToCollectionIfMissing([], societaire);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(societaire);
      });

      it('should not add a Societaire to an array that contains it', () => {
        const societaire: ISocietaire = sampleWithRequiredData;
        const societaireCollection: ISocietaire[] = [
          {
            ...societaire,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSocietaireToCollectionIfMissing(societaireCollection, societaire);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Societaire to an array that doesn't contain it", () => {
        const societaire: ISocietaire = sampleWithRequiredData;
        const societaireCollection: ISocietaire[] = [sampleWithPartialData];
        expectedResult = service.addSocietaireToCollectionIfMissing(societaireCollection, societaire);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(societaire);
      });

      it('should add only unique Societaire to an array', () => {
        const societaireArray: ISocietaire[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const societaireCollection: ISocietaire[] = [sampleWithRequiredData];
        expectedResult = service.addSocietaireToCollectionIfMissing(societaireCollection, ...societaireArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const societaire: ISocietaire = sampleWithRequiredData;
        const societaire2: ISocietaire = sampleWithPartialData;
        expectedResult = service.addSocietaireToCollectionIfMissing([], societaire, societaire2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(societaire);
        expect(expectedResult).toContain(societaire2);
      });

      it('should accept null and undefined values', () => {
        const societaire: ISocietaire = sampleWithRequiredData;
        expectedResult = service.addSocietaireToCollectionIfMissing([], null, societaire, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(societaire);
      });

      it('should return initial array if no Societaire is added', () => {
        const societaireCollection: ISocietaire[] = [sampleWithRequiredData];
        expectedResult = service.addSocietaireToCollectionIfMissing(societaireCollection, undefined, null);
        expect(expectedResult).toEqual(societaireCollection);
      });
    });

    describe('compareSocietaire', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSocietaire(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSocietaire(entity1, entity2);
        const compareResult2 = service.compareSocietaire(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSocietaire(entity1, entity2);
        const compareResult2 = service.compareSocietaire(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSocietaire(entity1, entity2);
        const compareResult2 = service.compareSocietaire(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
