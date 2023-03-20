package jerem.coopcycle.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import jerem.coopcycle.domain.enumeration.SocietaireType;

/**
 * A DTO for the {@link jerem.coopcycle.domain.Societaire} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SocietaireDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 50)
    private String lastName;

    @NotNull(message = "must not be null")
    private SocietaireType type;

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

    public SocietaireType getType() {
        return type;
    }

    public void setType(SocietaireType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SocietaireDTO)) {
            return false;
        }

        SocietaireDTO societaireDTO = (SocietaireDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, societaireDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SocietaireDTO{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
