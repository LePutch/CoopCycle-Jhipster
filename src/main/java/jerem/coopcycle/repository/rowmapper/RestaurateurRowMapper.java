package jerem.coopcycle.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import jerem.coopcycle.domain.Restaurateur;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Restaurateur}, with proper type conversions.
 */
@Service
public class RestaurateurRowMapper implements BiFunction<Row, String, Restaurateur> {

    private final ColumnConverter converter;

    public RestaurateurRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Restaurateur} stored in the database.
     */
    @Override
    public Restaurateur apply(Row row, String prefix) {
        Restaurateur entity = new Restaurateur();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setCommandeId(converter.fromRow(row, prefix + "_commande_id", Long.class));
        entity.setSocietaireId(converter.fromRow(row, prefix + "_societaire_id", Long.class));
        return entity;
    }
}
