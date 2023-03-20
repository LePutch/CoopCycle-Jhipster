package jerem.coopcycle.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import jerem.coopcycle.domain.Commande;
import jerem.coopcycle.domain.enumeration.CommandeStatus;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Commande}, with proper type conversions.
 */
@Service
public class CommandeRowMapper implements BiFunction<Row, String, Commande> {

    private final ColumnConverter converter;

    public CommandeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Commande} stored in the database.
     */
    @Override
    public Commande apply(Row row, String prefix) {
        Commande entity = new Commande();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDateTime(converter.fromRow(row, prefix + "_date_time", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", CommandeStatus.class));
        entity.setPanierId(converter.fromRow(row, prefix + "_panier_id", Long.class));
        entity.setPaiementId(converter.fromRow(row, prefix + "_paiement_id", Long.class));
        return entity;
    }
}
