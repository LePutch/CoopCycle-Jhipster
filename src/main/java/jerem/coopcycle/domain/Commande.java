package jerem.coopcycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import jerem.coopcycle.domain.enumeration.CommandeStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Commande.
 */
@Table("commande")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Commande implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("date_time")
    private Instant dateTime;

    @NotNull(message = "must not be null")
    @Column("status")
    private CommandeStatus status;

    @Transient
    private Panier panier;

    @Transient
    private Paiement paiement;

    @Transient
    @JsonIgnoreProperties(value = { "commande" }, allowSetters = true)
    private Set<Client> clients = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "commande", "societaire", "restaurants" }, allowSetters = true)
    private Set<Restaurateur> restaurateurs = new HashSet<>();

    @Column("panier_id")
    private Long panierId;

    @Column("paiement_id")
    private Long paiementId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Commande id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateTime() {
        return this.dateTime;
    }

    public Commande dateTime(Instant dateTime) {
        this.setDateTime(dateTime);
        return this;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public CommandeStatus getStatus() {
        return this.status;
    }

    public Commande status(CommandeStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CommandeStatus status) {
        this.status = status;
    }

    public Panier getPanier() {
        return this.panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
        this.panierId = panier != null ? panier.getId() : null;
    }

    public Commande panier(Panier panier) {
        this.setPanier(panier);
        return this;
    }

    public Paiement getPaiement() {
        return this.paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
        this.paiementId = paiement != null ? paiement.getId() : null;
    }

    public Commande paiement(Paiement paiement) {
        this.setPaiement(paiement);
        return this;
    }

    public Set<Client> getClients() {
        return this.clients;
    }

    public void setClients(Set<Client> clients) {
        if (this.clients != null) {
            this.clients.forEach(i -> i.setCommande(null));
        }
        if (clients != null) {
            clients.forEach(i -> i.setCommande(this));
        }
        this.clients = clients;
    }

    public Commande clients(Set<Client> clients) {
        this.setClients(clients);
        return this;
    }

    public Commande addClient(Client client) {
        this.clients.add(client);
        client.setCommande(this);
        return this;
    }

    public Commande removeClient(Client client) {
        this.clients.remove(client);
        client.setCommande(null);
        return this;
    }

    public Set<Restaurateur> getRestaurateurs() {
        return this.restaurateurs;
    }

    public void setRestaurateurs(Set<Restaurateur> restaurateurs) {
        if (this.restaurateurs != null) {
            this.restaurateurs.forEach(i -> i.setCommande(null));
        }
        if (restaurateurs != null) {
            restaurateurs.forEach(i -> i.setCommande(this));
        }
        this.restaurateurs = restaurateurs;
    }

    public Commande restaurateurs(Set<Restaurateur> restaurateurs) {
        this.setRestaurateurs(restaurateurs);
        return this;
    }

    public Commande addRestaurateur(Restaurateur restaurateur) {
        this.restaurateurs.add(restaurateur);
        restaurateur.setCommande(this);
        return this;
    }

    public Commande removeRestaurateur(Restaurateur restaurateur) {
        this.restaurateurs.remove(restaurateur);
        restaurateur.setCommande(null);
        return this;
    }

    public Long getPanierId() {
        return this.panierId;
    }

    public void setPanierId(Long panier) {
        this.panierId = panier;
    }

    public Long getPaiementId() {
        return this.paiementId;
    }

    public void setPaiementId(Long paiement) {
        this.paiementId = paiement;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Commande)) {
            return false;
        }
        return id != null && id.equals(((Commande) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Commande{" +
            "id=" + getId() +
            ", dateTime='" + getDateTime() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
