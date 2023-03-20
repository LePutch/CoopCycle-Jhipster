package jerem.coopcycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Restaurateur.
 */
@Table("restaurateur")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Restaurateur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 50)
    @Column("first_name")
    private String firstName;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 50)
    @Column("last_name")
    private String lastName;

    @Transient
    @JsonIgnoreProperties(value = { "panier", "paiement", "clients", "restaurateurs" }, allowSetters = true)
    private Commande commande;

    @Transient
    @JsonIgnoreProperties(value = { "restaurateurs" }, allowSetters = true)
    private Societaire societaire;

    @Transient
    @JsonIgnoreProperties(value = { "paniers", "restaurateur" }, allowSetters = true)
    private Set<Restaurant> restaurants = new HashSet<>();

    @Column("commande_id")
    private Long commandeId;

    @Column("societaire_id")
    private Long societaireId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Restaurateur id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Restaurateur firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Restaurateur lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Commande getCommande() {
        return this.commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
        this.commandeId = commande != null ? commande.getId() : null;
    }

    public Restaurateur commande(Commande commande) {
        this.setCommande(commande);
        return this;
    }

    public Societaire getSocietaire() {
        return this.societaire;
    }

    public void setSocietaire(Societaire societaire) {
        this.societaire = societaire;
        this.societaireId = societaire != null ? societaire.getId() : null;
    }

    public Restaurateur societaire(Societaire societaire) {
        this.setSocietaire(societaire);
        return this;
    }

    public Set<Restaurant> getRestaurants() {
        return this.restaurants;
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        if (this.restaurants != null) {
            this.restaurants.forEach(i -> i.setRestaurateur(null));
        }
        if (restaurants != null) {
            restaurants.forEach(i -> i.setRestaurateur(this));
        }
        this.restaurants = restaurants;
    }

    public Restaurateur restaurants(Set<Restaurant> restaurants) {
        this.setRestaurants(restaurants);
        return this;
    }

    public Restaurateur addRestaurant(Restaurant restaurant) {
        this.restaurants.add(restaurant);
        restaurant.setRestaurateur(this);
        return this;
    }

    public Restaurateur removeRestaurant(Restaurant restaurant) {
        this.restaurants.remove(restaurant);
        restaurant.setRestaurateur(null);
        return this;
    }

    public Long getCommandeId() {
        return this.commandeId;
    }

    public void setCommandeId(Long commande) {
        this.commandeId = commande;
    }

    public Long getSocietaireId() {
        return this.societaireId;
    }

    public void setSocietaireId(Long societaire) {
        this.societaireId = societaire;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Restaurateur)) {
            return false;
        }
        return id != null && id.equals(((Restaurateur) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Restaurateur{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            "}";
    }
}
