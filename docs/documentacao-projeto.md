# Documento do projeto — sistema monolítico (Parte 1)

## 1. Problema

Organizações precisam registrar obrigações com fornecedores e valores a receber de clientes, acompanhar datas de emissão e vencimento, reconhecer atrasos, registrar quitações e consultar compromissos financeiros. A solução substitui formulários de uma aplicação legada de desktop por uma API HTTP centralizada.

## 2. Objetivos

Construir uma aplicação monolítica com Spring Boot, API REST documentada, persistência relacional, validação e tratamento de erros. A Parte 1 também fornece base observável para a futura análise de decomposição em microsserviços; não implementa nem antecipa essa arquitetura.

## 3. Requisitos funcionais

- RF01 Manter clientes, fornecedores e categorias financeiras.
- RF02 Cadastrar, consultar, alterar e excluir contas a pagar e a receber, respeitando estado e integridade.
- RF03 Relacionar cada pagamento a `fornecedorId` e `categoriaId` obrigatórios e cada recebimento a `clienteId` e `categoriaId` obrigatórios.
- RF04 Filtrar contas por situação e intervalo de vencimento.
- RF05 Registrar pagamento ou recebimento com data e forma, calculando o valor quitado.
- RF06 Cancelar sem apagar o histórico e consultar resumo financeiro com totais monetários de contas abertas e quitadas.
- RF07 Disponibilizar OpenAPI/Swagger e módulos financeiros de apoio que vieram da referência legada.

## 4. Requisitos não funcionais

- RNF01 Java 17 ou superior, Spring Boot 3.5.5 e execução em um único processo.
- RNF02 Uma base/modelo relacional compartilhado via JPA, selecionado por configuração: MySQL padrão, H2 em arquivo no perfil `local` e H2 em memória no perfil `test`.
- RNF03 API REST em JSON, erros coerentes e documentação OpenAPI.
- RNF04 Valores monetários em decimal de precisão 15 e escala 2; datas de situação no fuso `America/Sao_Paulo`.
- RNF05 Integridade referencial e preservação do histórico das contas terminais.

## 5. Funcionalidades e regras de negócio

Uma conta exige título, descrição, valor, emissão, vencimento, identificadores de pessoa e categoria. Dinheiro deve ser maior que zero e caber em decimal(15,2). Descrição é obrigatória; observações são opcionais. A pessoa e a categoria devem existir; pagamento vincula fornecedor, recebimento vincula cliente. O POST cria conta aberta e não deixa o cliente sobrescrever campos de quitação.

Situações: `ABERTA`, `VENCIDA`, `PAGA`, `RECEBIDA` e `CANCELADA`. Vencida é derivada da data do vencimento corrente e fuso de São Paulo, sem persistir uma mudança na leitura. Entrada com `ABERTA` ou `VENCIDA` é normalizada na criação para estado inicial aberto. Emissão futura e vencimento anterior à emissão são inválidos.

Quitação aceita data entre emissão e hoje (inclusive), forma válida, e só ocorre de estado aberto/vencido. O valor é `valor + acrescimo - desconto`, usando desconto somente até a sua validade. Uma repetição com mesma data e forma é idempotente (200); dados diferentes conflitam (409). Cancelar é idempotente por PATCH sem corpo. Conta quitada ou cancelada não pode ser editada ou excluída; exclusão física fica limitada às abertas/vencidas. Operações concorrentes usam bloqueio e versão otimista para evitar sobrescrita silenciosa. Erros de integridade e concorrência são traduzidos para 409.

## 6. Modelo de domínio

Contas a pagar e a receber compartilham conceitos, mas mantêm entidades separadas e ligação a pessoa com papel adequado. `PlanoContas` representa categoria e tem hierarquia autorreferenciada pelo código `superior -> conta`. O modelo também inclui Banco, Agência, ContaCorrente, Cartorio, ChequePre, Lancamento, Despesa e RemessaCartorio. A referência de negócio é o livro *Delphi 4: Contas a Pagar e a Receber*, de Kazuhiro Shiraishi e Pedro Luiz Côrtes (1998); método, páginas consultadas e limitações de OCR estão em `referencia-delphi.md`.

## 7. Persistência

JPA/Hibernate mapeia entidades relacionais. A aplicação usa um modelo/base compartilhado; os perfis selecionam bancos alternativos: MySQL padrão via `docker compose up -d`, H2 em arquivo (`local`) ou H2 memória (`test`). No perfil padrão, a URL, usuário e senha aceitam `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. O perfil `local` usa a configuração H2 de `application-local.properties`; `SERVER_PORT` altera a porta HTTP em ambos. Filtros atualmente operam em memória sobre os registros recuperados, solução suficiente para volume acadêmico, sujeita a substituição por consultas paginadas/filtradas no banco se a escala exigir.

## 8. Arquitetura monolítica

O sistema reúne as funcionalidades em um único projeto Maven e processo JVM, com banco compartilhado. A separação em pacotes organiza responsabilidades internas. Os módulos compartilham implantação, dependências e acesso ao banco; não há comunicação de rede entre serviços independentes.

### Camadas e fluxo de uma operação

| Camada ou componente | Responsabilidade | Exemplo |
|---|---|---|
| `controller` | Receber requisições HTTP e devolver respostas | `PagamentoController` recebe o pedido de quitação |
| `service` | Aplicar regras, validar estado e calcular valores | `PagamentoService` verifica a conta e calcula a quitação |
| `repository` | Consultar e persistir dados por Spring Data JPA | `PagamentoRepository` acessa o registro no banco |
| `model` | Representar entidades, campos e relacionamentos | `Pagamento`, `Recebimento`, `Cliente` e `Fornecedor` |
| `dto` | Definir os dados de entrada de uma operação | `QuitacaoDTO` contém data e forma de pagamento |
| `exception` | Traduzir falhas em respostas HTTP coerentes | `ApiExceptionHandler` trata validações e conflitos |
| `config` e `enums` | Configurar a API e representar valores permitidos | OpenAPI, situações e formas de pagamento |

O fluxo principal é **cliente HTTP → controller → service → repository → banco**. Na quitação, o serviço verifica o estado e a data, calcula o valor final e só então grava o resultado. Transações e controle de concorrência protegem os dados financeiros.

A galeria [Diagramas](diagramas/visualizacao.html) reúne casos de uso, componentes, pacotes, classes, sequência de pagamento, sequência de conflito e DER. Ela pode ser aberta diretamente no navegador, com imagens SVG ampliáveis.

### Decisões e limites

O monólito permite executar e apresentar o sistema como uma única aplicação. Como todos os módulos compartilham processo e banco, a implantação e a escala são conjuntas; uma falha de inicialização ou indisponibilidade do banco pode afetar o sistema inteiro. Os filtros em memória atendem ao volume acadêmico e podem evoluir para consultas filtradas e paginadas no banco.

MySQL, H2 em arquivo e H2 em memória são alternativas de configuração desse mesmo monólito. A aplicação oferece uma API sem interface web própria; Swagger e Postman permitem interagir com suas operações. O escopo acadêmico não inclui autenticação. A futura divisão em microsserviços pertence à Parte 2.

## 9. APIs

Base local: `http://localhost:8080`. A API recebe e devolve JSON; datas seguem `yyyy-MM-dd` e enums são escritos em maiúsculas. A documentação interativa fica em [Swagger UI](http://localhost:8080/swagger-ui.html), e o contrato OpenAPI é gerado em [API OpenAPI](http://localhost:8080/v3/api-docs) enquanto a aplicação está em execução.

### Rotas das contas

As operações abaixo existem tanto para `/api/pagamentos` quanto para `/api/recebimentos`. Nas rotas das contas, `{id}` corresponde ao título do registro.

| Método | Rota a partir do recurso | Resultado |
|---|---|---|
| GET | `/` | Lista contas, com filtros opcionais |
| GET | `/{id}` | Consulta uma conta pelo título |
| POST | `/` | Cria uma conta e retorna 201 |
| PUT | `/{id}` | Atualiza uma conta aberta ou vencida |
| PATCH | `/{id}/quitar` | Registra pagamento ou recebimento |
| PATCH | `/{id}/cancelar` | Cancela sem corpo, preservando o registro |
| DELETE | `/{id}` | Exclui uma conta aberta ou vencida e retorna 204 |

Por exemplo, a rota de listagem de pagamentos é `GET /api/pagamentos`, e a de quitação é `PATCH /api/pagamentos/{id}/quitar`.

### Consultas e filtros

`GET /api/pagamentos?status=VENCIDA&inicio=2026-10-01&fim=2026-10-31` filtra por situação e intervalo de vencimento, incluindo as duas datas. O mesmo padrão vale para recebimentos. `inicio` e `fim` devem ser informados juntos, e a data final não pode ser anterior à inicial.

Situações possíveis: `ABERTA`, `VENCIDA`, `PAGA`, `RECEBIDA` e `CANCELADA`, conforme o tipo e o estado da conta. `VENCIDA` é calculada quando o vencimento passou, considerando `America/Sao_Paulo`, sem gravar uma mudança durante a consulta.

### Cadastro e alteração

Pagamento exige `titulo`, `fornecedorId`, `categoriaId`, `descricao`, `valor`, `emissao` e `vencto`. Recebimento usa `clienteId` em lugar de `fornecedorId`. A pessoa e a categoria precisam existir. `observacoes` é opcional. O campo opcional `cgc`, quando enviado, deve corresponder à pessoa vinculada.

O título é uma string de até 20 caracteres, a descrição aceita até 255 e as observações até 2000. Valor deve ser positivo, com precisão decimal 15 e escala 2. Exemplo de cadastro de pagamento:

```json
{
  "titulo": "FAT-2026-001",
  "fornecedorId": 1,
  "categoriaId": 1,
  "descricao": "Serviço mensal",
  "observacoes": "Contrato 42",
  "valor": 1250.00,
  "emissao": "2026-10-01",
  "vencto": "2026-10-15"
}
```

Os IDs e as datas do exemplo devem ser ajustados aos cadastros e ao dia da demonstração. A coleção Postman fornecida gera IDs e datas para seu fluxo automaticamente.

POST cria a conta aberta, rejeita título duplicado e impede impor dados de quitação no cadastro. Situação recebida como `ABERTA` ou `VENCIDA` é normalizada para o estado inicial aberto; situação terminal é rejeitada. No PUT, o título do JSON deve coincidir com o da URL. Contas quitadas ou canceladas têm seu histórico protegido contra alteração e exclusão.

### Quitação e cancelamento

`PATCH /api/pagamentos/{id}/quitar` e `PATCH /api/recebimentos/{id}/quitar` recebem data e forma de pagamento:

```json
{"data":"2026-10-10","formaPagamento":"PIX"}
```

Formas permitidas: `DINHEIRO`, `PIX`, `BOLETO`, `CARTAO`, `CHEQUE`, `TRANSFERENCIA` e `OUTRA`. A data precisa estar entre a emissão e o dia corrente no fuso de São Paulo. O valor quitado é **valor original + acréscimo − desconto válido na data**; depois de `validadeDesc`, o desconto não é aplicado.

A operação registra data, forma, valor quitado e situação `PAGA` ou `RECEBIDA`. Repetir a quitação com a mesma data e forma retorna 200 sem alterar o resultado; dados diferentes retornam 409. Cancelamento repetido também retorna sucesso. Quitar conta cancelada, cancelar conta quitada ou editar/excluir conta terminal gera conflito.

### Cadastros de apoio e resumo financeiro

Outros recursos são `/api/clientes`, `/api/fornecedores`, `/api/plano-contas`, `/api/bancos`, `/api/agencias`, `/api/cartorios`, `/api/lancamentos`, `/api/despesas`, `/api/conta-corrente`, `/api/cheques-pre` e `/api/remessas-cartorio`. Os schemas e verbos de cada recurso estão no Swagger.

`GET /api/relatorios/resumo` apresenta contagens e os totais `valorAPagarEmAberto` e `valorAReceberEmAberto`, somando o valor original das contas abertas ou vencidas. `valorPago` e `valorRecebido` somam `valorQuitado` das contas pagas ou recebidas. Contas canceladas ficam fora dos totais monetários.

## 10. Falhas e respostas

400: JSON, campo, enum, data, período ou regra inválida. 404: identificador não localizado. 409: duplicidade, FK/integridade, operação de estado incompatível ou concorrência. 500: erro inesperado, logado no servidor. O corpo de erro contém `dataHora`, `status`, `erro` e `mensagem` ou detalhes dos erros de campo. API não requer autenticação no escopo acadêmico; clientes acessam via HTTP.

## 11. Execução

Pré-requisitos: JDK 17+, Docker Compose para MySQL ou perfil local. Com Maven Wrapper: `./mvnw test`, `./mvnw package`, `./mvnw spring-boot:run "-Dspring-boot.run.profiles=local"`. Em PowerShell use `.\mvnw.cmd`. Para artefato, `java -jar target/contas-pagar-receber-1.0.0.jar --spring.profiles.active=local`. MySQL padrão: `docker compose up -d`. Endereços e configurações: `README.md`.

## 12. Testes

32 testes automatizados de API com H2 passaram sem falhas, além de 36 requisições HTTP reais que passaram contra MySQL 8.0.46 local isolado na porta 3307. No teste concorrente da quitação repetida, observaram-se uma resposta 200 e uma 409. A imagem MySQL do Docker Compose não foi validada. Os cenários automatizados incluem regras de cadastro, vínculo, datas, filtros, situação vencida, idempotência, conflitos, preservação de histórico e erros HTTP. O estado das evidências está em `testes.md`; o roteiro de demonstração não é laudo de teste.

## 13. Dificuldades técnicas observadas

O legado usa terminologia e estruturas próprias de desktop e banco antigo; foi necessário distinguir conceitos de negócio que ainda se aplicam (pessoa vinculada, datas, valor, consulta, quitação) de telas/infraestrutura Delphi que não fazem sentido como requisito web. A regra de vencimento muda com o calendário, por isso precisa ser calculada no fuso de negócio e não gravada por uma consulta. Requisições repetidas e concorrentes exigem idempotência, bloqueio e conflito HTTP para proteger o histórico. Converter um PDF escaneado por OCR também limita a leitura: houve extração OCR integral, com inspeção visual focal das páginas relevantes, não conferência visual linha a linha de todas as 369 páginas. A separação de banco local/teste/desenvolvimento exige perfis explícitos para que persistência local não dependa de servidor e testes permaneçam isolados.

## 14. Escopo e entregas ainda humanas

A aplicação é API sem front-end, uma escolha compatível com o requisito de API REST. O enunciado solicita apresentação gravada de no mínimo 30 minutos do monólito; gravação, demonstração e domínio oral são responsabilidades da equipe. Microsserviços, justificativa de divisão, comunicação entre serviços, comparação arquitetural, vídeo correspondente e conclusão final são entregas da Parte 2.

## 15. Matriz de requisitos da Parte 1

A matriz relaciona os requisitos da Parte 1 com a implementação e os materiais que a equipe pode mostrar na apresentação. Os resultados de testes correspondem às evidências identificadas em `testes.md`.

| Requisito | Situação | Implementação ou evidência |
|---|---|---|
| Clientes e fornecedores | Atendido | Cadastros com CRUD e vínculo obrigatório nas contas |
| Categorias financeiras | Atendido | `PlanoContas` com CRUD, hierarquia validada e vínculo nas contas |
| Contas a pagar e receber com situação, datas, valores, descrição, observações e formas de quitação | Atendido | Entidades `Pagamento` e `Recebimento`, `QuitacaoDTO` e validações |
| Aplicação Spring Boot monolítica em camadas | Atendido | Um projeto Maven, um processo e banco compartilhado; seção 8 e galeria de diagramas |
| API REST com cadastro, consulta, alteração e exclusão quando permitidas | Atendido | Controllers e Swagger; histórico de contas quitadas e canceladas protegido |
| Registro de pagamento e recebimento | Atendido | PATCH de quitação com cálculo e gravação de valor, data e forma |
| Consultas por período e situação | Atendido | Filtros por vencimento com limites inclusivos e situação calculada na consulta |
| Persistência e integridade dos dados | Atendido | JPA, MySQL/H2, 13 tabelas, 16 chaves estrangeiras, transações e controle de concorrência |
| Validação e tratamento de erros | Atendido | Validação de campos e regras de negócio; respostas HTTP estruturadas |
| Documentação do sistema e instruções de execução | Atendido | Este documento, `referencia-delphi.md`, `testes.md` e README |
| Diagramas de domínio, arquitetura, classes, componentes, pacotes, casos de uso, sequência e DER | Atendido | `diagramas/visualizacao.html` e imagens SVG |
| Script de banco | Atendido | `database/schema.sql`, que cria a base, tabelas e relacionamentos MySQL |
| Testes e evidências | Atendido | 32 testes automatizados, 36 verificações HTTP MySQL, 24 requisições da coleção e persistência após reinício; ver `testes.md` |
| Vídeo de pelo menos 30 minutos | Responsabilidade da equipe | Gravação com apresentação do monólito e demonstração das operações |
| Microsserviços, comunicação distribuída e comparação de arquiteturas | Fora da Parte 1 | Reservados para a segunda etapa |

