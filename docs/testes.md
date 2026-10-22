# Testes e evidências — Parte 1

## Resultados de referência

Em **21/10/2026**, a preparação para apresentação foi verificada com `.\mvnw.cmd -o verify` em Java 17.0.17: **32 testes aprovados, zero falhas e BUILD SUCCESS**. Os dois resumos TXT registram essa execução. As evidências HTTP, MySQL, persistência e SQL preservam a validação anterior de 03/10/2026; esses cenários externos não foram repetidos nesta limpeza.

| Verificação | Resultado | Evidência |
|---|---|---|
| API, HTTP, OpenAPI e relatórios | 26 testes aprovados | `evidencias/br.com.anna.contaspagarreceber.ApiEtapa1IntegrationTest.txt` |
| Módulos auxiliares e vínculos | 6 testes aprovados | `evidencias/br.com.anna.contaspagarreceber.SupportModulesIntegrationTest.txt` |
| HTTP com MySQL real | 36 requisições aprovadas | `evidencias/mysql-http.json` |
| Quitações concorrentes com formas diferentes | Uma resposta 200 e uma 409 em cada par; primeiro registro preservado | `evidencias/mysql-http.json` |
| Coleção de demonstração | 24 requisições e verificações HTTP aprovadas | `evidencias/colecao-http.json` |
| Persistência H2 local | Cadastro recuperado após reiniciar | `evidencias/h2-local-persistencia.json` |
| Esquema MySQL | 13 tabelas e 16 chaves estrangeiras em base nova | `evidencias/schema-validacao.txt` |

A imagem MySQL 8.4 do Docker Compose não foi validada nessas execuções. O servidor MySQL usado foi o 8.0.46 local, isolado na porta 3307. O vídeo de pelo menos 30 minutos é uma entrega da equipe.

## Como repetir

No PowerShell, na pasta do projeto:

```powershell
.\mvnw.cmd verify
```

Linux/macOS: `./mvnw verify`. Os testes usam H2 em memória, com dados isolados por cenário. A saída final mostra o total de testes e o resultado da compilação. Os relatórios da execução ficam em `target/surefire-reports/` e podem ser gerados novamente a qualquer momento.

Para a demonstração HTTP, importe `postman/contas-pagar-receber.postman_collection.json`. A coleção guarda os IDs e gera datas automaticamente; execute em ordem pelo Collection Runner. Respostas 400, 404 e 409 previstas fazem parte dos cenários.

## O que explicar na apresentação

1. Clientes, fornecedores e categorias precisam existir antes de criar as contas.
2. Campos obrigatórios, valores e datas são validados; dados inválidos retornam 400.
3. Contas podem ser consultadas por título, situação e intervalo inclusivo de vencimento; recurso inexistente retorna 404.
4. Somente contas abertas ou vencidas podem ser alteradas ou excluídas.
5. A quitação registra data, forma e valor final, considerando acréscimo e desconto válido.
6. Repetir a mesma quitação retorna sucesso; mudar os dados de uma quitação já registrada gera 409.
7. Contas quitadas ou canceladas preservam seu histórico.
8. Os vínculos de banco e o controle de concorrência impedem operações inconsistentes.
9. O perfil `local` conserva os dados em arquivo; o perfil `test` isola os testes em memória.

Use os dois resumos TXT para mostrar **26 + 6 = 32 testes, zero falhas** e os JSON para mostrar requisições e respostas. Se executar novamente, apresente os resultados efetivamente observados nessa nova execução.
