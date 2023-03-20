package jerem.coopcycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import jerem.coopcycle.domain.enumeration.SocietaireType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Societaire.
 */
@Table("societaire")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Societaire implements Serializable {

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

    @NotNull(message = "must not be null")
    @Column("type")
    private SocietaireType type;

    @Transient
    @JsonIgnoreProperties(value = { "commande", "societaire", "restaurants" }, allowSetters = true)
    private Set<Restaurateur> restaurateurs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Societaire id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Societaire firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Societaire lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public SocietaireType getType() {
        return this.type;
    }

    public Societaire type(SocietaireType type) {
        this.setType(type);
        return this;
    }

    public void setType(SocietaireType type) {
        this.type = type;
    }

    public Set<Restaurateur> getRestaurateurs() {
        return this.restaurateurs;
    }

    public void setRestaurateurs(Set<Restaurateur> restaurateurs) {
        if (this.restaurateurs != null) {
            this.restaurateurs.forEach(i -> i.setSocietaire(null));
        }
        if (restaurateurs != null) {
            restaurateurs.forEach(i -> i.setSocietaire(this));
        }
        this.restaurateurs = restaurateurs;
    }

    public Societaire restaurateurs(Set<Restaurateur> restaurateurs) {
        this.setRestaurateurs(restaurateurs);
        return this;
    }

    public Societaire addRestaurateur(Restaurateur restaurateur) {
        this.restaurateurs.add(restaurateur);
        restaurateur.setSocietaire(this);
        return this;
    }

    public Societaire removeRestaurateur(Restaurateur restaurateur) {
        this.restaurateurs.remove(restaurateur);
        restaurateur.setSocietaire(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Societaire)) {
            return false;
        }
        return id != null && id.equals(((Societaire) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Societaire{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
