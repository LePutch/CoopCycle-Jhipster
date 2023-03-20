package jerem.coopcycle.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import jerem.coopcycle.domain.Restaurateur;
import jerem.coopcycle.repository.rowmapper.CommandeRowMapper;
import jerem.coopcycle.repository.rowmapper.RestaurateurRowMapper;
import jerem.coopcycle.repository.rowmapper.SocietaireRowMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Restaurateur entity.
 */
@SuppressWarnings("unused")
class RestaurateurRepositoryInternalImpl extends SimpleR2dbcRepository<Restaurateur, Long> implements RestaurateurRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CommandeRowMapper commandeMapper;
    private final SocietaireRowMapper societaireMapper;
    private final RestaurateurRowMapper restaurateurMapper;

    private static final Table entityTable = Table.aliased("restaurateur", EntityManager.ENTITY_ALIAS);
    private static final Table commandeTable = Table.aliased("commande", "commande");
    private static final Table societaireTable = Table.aliased("societaire", "societaire");

    public RestaurateurRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CommandeRowMapper commandeMapper,
        SocietaireRowMapper societaireMapper,
        RestaurateurRowMapper restaurateurMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Restaurateur.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.commandeMapper = commandeMapper;
        this.societaireMapper = societaireMapper;
        this.restaurateurMapper = restaurateurMapper;
    }

    @Override
    public Flux<Restaurateur> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Restaurateur> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RestaurateurSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CommandeSqlHelper.getColumns(commandeTable, "commande"));
        columns.addAll(SocietaireSqlHelper.getColumns(societaireTable, "societaire"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(commandeTable)
            .on(Column.create("commande_id", entityTable))
            .equals(Column.create("id", commandeTable))
            .leftOuterJoin(societaireTable)
            .on(Column.create("societaire_id", entityTable))
            .equals(Column.create("id", societaireTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Restaurateur.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Restaurateur> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Restaurateur> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Restaurateur process(Row row, RowMetadata metadata) {
        Restaurateur entity = restaurateurMapper.apply(row, "e");
        entity.setCommande(commandeMapper.apply(row, "commande"));
        entity.setSocietaire(societaireMapper.apply(row, "societaire"));
        return entity;
    }

    @Override
    public <S extends Restaurateur> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
