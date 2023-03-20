package jerem.coopcycle.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import jerem.coopcycle.domain.Societaire;
import jerem.coopcycle.domain.enumeration.SocietaireType;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Societaire}, with proper type conversions.
 */
@Service
public class SocietaireRowMapper implements BiFunction<Row, String, Societaire> {

    private final ColumnConverter converter;

    public SocietaireRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Societaire} stored in the database.
     */
    @Override
    public Societaire apply(Row row, String prefix) {
        Societaire entity = new Societaire();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setType(converter.fromRow(row, prefix + "_type", SocietaireType.class));
        return entity;
    }
}
