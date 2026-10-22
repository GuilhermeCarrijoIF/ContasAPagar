package br.com.anna.contaspagarreceber.repository;

import br.com.anna.contaspagarreceber.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BancoRepository extends JpaRepository<Banco, String> {
}
