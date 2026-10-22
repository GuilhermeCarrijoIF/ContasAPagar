package br.com.anna.contaspagarreceber.repository;

import br.com.anna.contaspagarreceber.enums.StatusConta;
import br.com.anna.contaspagarreceber.model.Recebimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecebimentoRepository extends JpaRepository<Recebimento, String> {
    List<Recebimento> findByStatus(StatusConta status);
    List<Recebimento> findByVenctoBetween(LocalDate inicio, LocalDate fim);
    List<Recebimento> findByStatusAndVenctoBetween(StatusConta status, LocalDate inicio, LocalDate fim);
    List<Recebimento> findByStatusAndVenctoBefore(StatusConta status, LocalDate data);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select c from Recebimento c where c.titulo = :id")
    java.util.Optional<Recebimento> buscarComBloqueio(@org.springframework.data.repository.query.Param("id") String id);
}

