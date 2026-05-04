package com.fastariam.repository;

import com.fastariam.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    @Query("SELECT c FROM Cidade c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%',:nome,'%')) ORDER BY c.nome")
    List<Cidade> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    @Query("SELECT c FROM Cidade c WHERE LOWER(c.nome) = LOWER(:nome) AND UPPER(c.estado) = UPPER(:estado)")
    Optional<Cidade> findByNomeAndEstado(@Param("nome") String nome, @Param("estado") String estado);

    List<Cidade> findByEstadoOrderByNome(String estado);

    @Query("SELECT DISTINCT c.estado FROM Cidade c ORDER BY c.estado")
    List<String> findAllEstados();
}
