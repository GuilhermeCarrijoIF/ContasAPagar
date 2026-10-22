package br.com.anna.contaspagarreceber.repository;

import br.com.anna.contaspagarreceber.model.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, String> {
}
