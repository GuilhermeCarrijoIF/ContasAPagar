# Contas a Pagar e a Receber — Parte 1

API REST acadêmica em Java 17 e Spring Boot 3.5.5. Clientes, fornecedores, categorias e contas financeiras ficam em uma aplicação monolítica, com banco relacional e documentação Swagger.

## Executar para apresentar

Abra o PowerShell na pasta do projeto, com Java 17+ instalado. Na primeira execução após clonar ou baixar este repositório, gere o JAR:

```powershell
.\mvnw.cmd verify
```

Depois, inicie a aplicação:

```powershell
java -jar target/contas-pagar-receber-1.0.0.jar --spring.profiles.active=local
```

Se precisar usar diretamente o JDK configurado em `JAVA_HOME`:

```powershell
& "$env:JAVA_HOME\bin\java.exe" -jar target/contas-pagar-receber-1.0.0.jar --spring.profiles.active=local
```

Mantenha o terminal aberto e acesse [Swagger UI](http://localhost:8080/swagger-ui.html). O perfil `local` usa H2 em arquivo: os dados ficam em `data/contas.mv.db` e permanecem após reiniciar. Execute sempre da pasta do projeto.

## Compilar e testar

Se o JAR não existir ou se os fontes forem alterados:

```powershell
.\mvnw.cmd verify
```

O comando executa os testes e gera `target/contas-pagar-receber-1.0.0.jar`. A primeira compilação precisa de internet para baixar Maven e dependências. Linux/macOS: `./mvnw verify`.

## Material da apresentação

| Material | Uso |
|---|---|
| [Documentação do projeto](docs/documentacao-projeto.md) | Problema, requisitos, regras, arquitetura, API e matriz de atendimento |
| [Referência Delphi](docs/referencia-delphi.md) | Conceitos do livro adaptados para o projeto |
| [Diagramas](docs/diagramas/visualizacao.html) | Galeria com classes, DER, componentes, pacotes, casos de uso e sequências |
| [Testes e evidências](docs/testes.md) | Resultados e cenários que a equipe pode mostrar |
| [Coleção Postman](postman/contas-pagar-receber.postman_collection.json) | Fluxo de 24 requisições com IDs e datas automáticos |
| [Esquema MySQL](database/schema.sql) | Tabelas e chaves estrangeiras para explicar a persistência |

Para abrir a galeria, abra `docs/diagramas/visualizacao.html` no navegador. No Postman, importe a coleção e use `baseUrl = http://localhost:8080`. As requisições dependem dos cadastros anteriores: siga a ordem da coleção.

A documentação OpenAPI é gerada pela aplicação em [JSON](http://localhost:8080/v3/api-docs) e pode ser explorada no Swagger. A aplicação entrega uma API sem interface web própria.

## Executar com MySQL

Como alternativa ao H2 local, com Docker Desktop em execução:

```powershell
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Aguarde o banco aceitar conexões. `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `SERVER_PORT` substituem os padrões do perfil MySQL. O Compose usa MySQL 8.4; as evidências históricas de MySQL foram obtidas em servidor 8.0.46, sem validar a imagem Compose.

Para instalar o esquema em uma base nova, abra o cliente MySQL na pasta do projeto e execute `SOURCE database/schema.sql;`. O script cria a base, 13 tabelas e 16 chaves estrangeiras. O perfil H2 cria suas tabelas pelo Hibernate e dispensa esse script.

## Escopo

A Parte 1 inclui cadastros, consultas, filtros por situação e vencimento, pagamento, recebimento, cancelamento, validações, persistência e testes. A entrega da equipe exige vídeo de pelo menos 30 minutos. Microsserviços e a comparação de arquiteturas pertencem à Parte 2.
