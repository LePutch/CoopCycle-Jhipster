package jerem.coopcycle.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import jerem.coopcycle.domain.Panier;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Panier}, with proper type conversions.
 */
@Service
public class PanierRowMapper implements BiFunction<Row, String, Panier> {

    private final ColumnConverter converter;

    public PanierRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Panier} stored in the database.
     */
    @Override
    public Panier apply(Row row, String prefix) {
        Panier entity = new Panier();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Float.class));
        entity.setRestaurantId(converter.fromRow(row, prefix + "_restaurant_id", Long.class));
        return entity;
    }
}
