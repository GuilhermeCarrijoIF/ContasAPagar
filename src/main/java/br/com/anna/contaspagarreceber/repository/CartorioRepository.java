package br.com.anna.contaspagarreceber.repository;

import br.com.anna.contaspagarreceber.model.Cartorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartorioRepository extends JpaRepository<Cartorio, Long> {
}
