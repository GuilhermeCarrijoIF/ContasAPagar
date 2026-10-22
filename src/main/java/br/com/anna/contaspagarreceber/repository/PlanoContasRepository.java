package br.com.anna.contaspagarreceber.repository;

import br.com.anna.contaspagarreceber.model.PlanoContas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoContasRepository extends JpaRepository<PlanoContas, Long> {
}
