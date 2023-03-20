package jerem.coopcycle.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import jerem.coopcycle.domain.Paiement;
import jerem.coopcycle.domain.enumeration.PaymentType;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Paiement}, with proper type conversions.
 */
@Service
public class PaiementRowMapper implements BiFunction<Row, String, Paiement> {

    private final ColumnConverter converter;

    public PaiementRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Paiement} stored in the database.
     */
    @Override
    public Paiement apply(Row row, String prefix) {
        Paiement entity = new Paiement();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", Float.class));
        entity.setPaymentType(converter.fromRow(row, prefix + "_payment_type", PaymentType.class));
        return entity;
    }
}
