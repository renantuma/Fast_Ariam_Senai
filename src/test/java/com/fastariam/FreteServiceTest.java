package com.fastariam;

import com.fastariam.service.FreteService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class FreteServiceTest {
    @Test
    void icmsPorEstado() {
        assertThat(FreteService.getIcmsEstado("PR")).isEqualTo(18.0);
        assertThat(FreteService.getIcmsEstado("SP")).isEqualTo(12.0);
        assertThat(FreteService.getIcmsEstado("BA")).isEqualTo(7.0);
        assertThat(FreteService.getIcmsEstado("pr")).isEqualTo(18.0);
        assertThat(FreteService.getIcmsEstado("XX")).isEqualTo(7.0);
    }
}
