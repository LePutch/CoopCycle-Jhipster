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
 * A Restaurant.
 */
@Table("restaurant")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    @Column("address")
    private String address;

    @Size(max = 500)
    @Column("menu")
    private String menu;

    @Transient
    @JsonIgnoreProperties(value = { "restaurant" }, allowSetters = true)
    private Set<Panier> paniers = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "commande", "societaire", "restaurants" }, allowSetters = true)
    private Restaurateur restaurateur;

    @Column("restaurateur_id")
    private Long restaurateurId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Restaurant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Restaurant name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public Restaurant address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMenu() {
        return this.menu;
    }

    public Restaurant menu(String menu) {
        this.setMenu(menu);
        return this;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public Set<Panier> getPaniers() {
        return this.paniers;
    }

    public void setPaniers(Set<Panier> paniers) {
        if (this.paniers != null) {
            this.paniers.forEach(i -> i.setRestaurant(null));
        }
        if (paniers != null) {
            paniers.forEach(i -> i.setRestaurant(this));
        }
        this.paniers = paniers;
    }

    public Restaurant paniers(Set<Panier> paniers) {
        this.setPaniers(paniers);
        return this;
    }

    public Restaurant addPanier(Panier panier) {
        this.paniers.add(panier);
        panier.setRestaurant(this);
        return this;
    }

    public Restaurant removePanier(Panier panier) {
        this.paniers.remove(panier);
        panier.setRestaurant(null);
        return this;
    }

    public Restaurateur getRestaurateur() {
        return this.restaurateur;
    }

    public void setRestaurateur(Restaurateur restaurateur) {
        this.restaurateur = restaurateur;
        this.restaurateurId = restaurateur != null ? restaurateur.getId() : null;
    }

    public Restaurant restaurateur(Restaurateur restaurateur) {
        this.setRestaurateur(restaurateur);
        return this;
    }

    public Long getRestaurateurId() {
        return this.restaurateurId;
    }

    public void setRestaurateurId(Long restaurateur) {
        this.restaurateurId = restaurateur;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Restaurant)) {
            return false;
        }
        return id != null && id.equals(((Restaurant) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Restaurant{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", menu='" + getMenu() + "'" +
            "}";
    }
}
