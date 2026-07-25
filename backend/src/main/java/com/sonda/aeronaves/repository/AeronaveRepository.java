package com.sonda.aeronaves.repository;

import com.sonda.aeronaves.model.Aeronave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AeronaveRepository extends JpaRepository<Aeronave, Long> {

    @Query("""
            select a from Aeronave a
            where lower(a.nome) like lower(concat('%', :termo, '%'))
               or lower(a.marca) like lower(concat('%', :termo, '%'))
               or str(a.id) = :termo
            order by a.id
            """)
    List<Aeronave> buscarPorTermo(@Param("termo") String termo);

    long countByVendidoFalse();

    List<Aeronave> findByCreatedGreaterThanEqualOrderByCreatedDesc(LocalDateTime desde);

    @Query("""
            select (a.ano / 10) * 10 as decada, count(a) as quantidade
            from Aeronave a
            group by (a.ano / 10) * 10
            order by decada
            """)
    List<Object[]> contarPorDecada();

    @Query("""
            select a.marca as marca, count(a) as quantidade
            from Aeronave a
            group by a.marca
            order by quantidade desc
            """)
    List<Object[]> contarPorFabricante();
}
