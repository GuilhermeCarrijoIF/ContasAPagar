package br.com.anna.contaspagarreceber.repository;

import br.com.anna.contaspagarreceber.enums.StatusConta;
import br.com.anna.contaspagarreceber.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, String> {
    List<Pagamento> findByStatus(StatusConta status);
    List<Pagamento> findByVenctoBetween(LocalDate inicio, LocalDate fim);
    List<Pagamento> findByStatusAndVenctoBetween(StatusConta status, LocalDate inicio, LocalDate fim);
    List<Pagamento> findByStatusAndVenctoBefore(StatusConta status, LocalDate data);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select c from Pagamento c where c.titulo = :id")
    java.util.Optional<Pagamento> buscarComBloqueio(@org.springframework.data.repository.query.Param("id") String id);
}

