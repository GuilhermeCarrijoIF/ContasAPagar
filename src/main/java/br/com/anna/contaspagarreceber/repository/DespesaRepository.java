package br.com.anna.contaspagarreceber.repository;

import br.com.anna.contaspagarreceber.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> {
}
