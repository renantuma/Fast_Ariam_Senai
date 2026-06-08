package com.fastariam.repository;

import com.fastariam.model.Cidade;
import com.fastariam.model.TarifaFrete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifaFreteRepository extends JpaRepository<TarifaFrete, Long> {

    Optional<TarifaFrete> findByCidade(Cidade cidade);

    @Query("""
           SELECT t FROM TarifaFrete t
           WHERE LOWER(t.cidade.nome) = LOWER(:nome)
             AND UPPER(t.cidade.estado) = UPPER(:estado)
           """)
    Optional<TarifaFrete> buscarPorCidade(@Param("nome") String nome,
                                          @Param("estado") String estado);

    void deleteByCidade(Cidade cidade);
}
