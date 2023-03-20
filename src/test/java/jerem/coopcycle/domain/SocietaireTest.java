package jerem.coopcycle.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jerem.coopcycle.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SocietaireTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Societaire.class);
        Societaire societaire1 = new Societaire();
        societaire1.setId(1L);
        Societaire societaire2 = new Societaire();
        societaire2.setId(societaire1.getId());
        assertThat(societaire1).isEqualTo(societaire2);
        societaire2.setId(2L);
        assertThat(societaire1).isNotEqualTo(societaire2);
        societaire1.setId(null);
        assertThat(societaire1).isNotEqualTo(societaire2);
    }
}
