package br.com.anna.contaspagarreceber.repository;

import br.com.anna.contaspagarreceber.model.RemessaCartorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemessaCartorioRepository extends JpaRepository<RemessaCartorio, Long> {
}
