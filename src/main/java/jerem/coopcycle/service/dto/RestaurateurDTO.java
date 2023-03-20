package jerem.coopcycle.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link jerem.coopcycle.domain.Restaurateur} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurateurDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 50)
    private String lastName;

    private CommandeDTO commande;

    private SocietaireDTO societaire;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public CommandeDTO getCommande() {
        return commande;
    }

    public void setCommande(CommandeDTO commande) {
        this.commande = commande;
    }

    public SocietaireDTO getSocietaire() {
        return societaire;
    }

    public void setSocietaire(SocietaireDTO societaire) {
        this.societaire = societaire;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurateurDTO)) {
            return false;
        }

        RestaurateurDTO restaurateurDTO = (RestaurateurDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, restaurateurDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurateurDTO{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", commande=" + getCommande() +
            ", societaire=" + getSocietaire() +
            "}";
    }
}
