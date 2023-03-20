package jerem.coopcycle.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jerem.coopcycle.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SocietaireDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SocietaireDTO.class);
        SocietaireDTO societaireDTO1 = new SocietaireDTO();
        societaireDTO1.setId(1L);
        SocietaireDTO societaireDTO2 = new SocietaireDTO();
        assertThat(societaireDTO1).isNotEqualTo(societaireDTO2);
        societaireDTO2.setId(societaireDTO1.getId());
        assertThat(societaireDTO1).isEqualTo(societaireDTO2);
        societaireDTO2.setId(2L);
        assertThat(societaireDTO1).isNotEqualTo(societaireDTO2);
        societaireDTO1.setId(null);
        assertThat(societaireDTO1).isNotEqualTo(societaireDTO2);
    }
}
