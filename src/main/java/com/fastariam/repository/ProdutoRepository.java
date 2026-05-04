package com.fastariam.repository;

import com.fastariam.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByCodigo(String codigo);

    List<Produto> findByAtivoTrue();

    List<Produto> findByTipoAndAtivoTrue(TipoProduto tipo);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND " +
           "(LOWER(p.codigo) LIKE LOWER(CONCAT('%',:termo,'%')) OR " +
           " LOWER(p.descricao) LIKE LOWER(CONCAT('%',:termo,'%')) OR " +
           " LOWER(p.nomeAntigo) LIKE LOWER(CONCAT('%',:termo,'%')))")
    List<Produto> buscarPorTermo(@Param("termo") String termo);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND " +
           "(LOWER(p.codigo) = LOWER(:codigo) OR LOWER(p.nomeAntigo) = LOWER(:codigo))")
    Optional<Produto> findByCodigoOrNomeAntigo(@Param("codigo") String codigo);
}
