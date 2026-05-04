package com.fastariam.repository;

import com.fastariam.model.ConfiguracaoFrete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoFreteRepository extends JpaRepository<ConfiguracaoFrete, Long> {
    default ConfiguracaoFrete getConfiguracao() {
        return findAll().stream().findFirst().orElse(null);
    }
}
