package br.com.anna.contaspagarreceber.repository;

import br.com.anna.contaspagarreceber.model.ChequePre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChequePreRepository extends JpaRepository<ChequePre, Long> {
}
