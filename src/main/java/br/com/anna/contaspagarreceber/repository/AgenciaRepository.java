package br.com.anna.contaspagarreceber.repository;

import br.com.anna.contaspagarreceber.model.Agencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgenciaRepository extends JpaRepository<Agencia, String> {
}
