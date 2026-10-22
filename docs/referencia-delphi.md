# Leitura da referência Delphi e delimitação da Parte 1

## Fonte e método

Foi examinado o PDF fornecido `DELPHI 4 - Contas a Pagar e a Receber.pdf`, de Kazuhiro Shiraishi e Pedro Luiz Côrtes, Editora Érica, 1998. O arquivo contém 369 páginas digitalizadas. A extração direta encontrou páginas praticamente sem texto; por isso foram renderizadas imagens com Poppler e empregado OCR local do Windows. O OCR é auxiliar e pode trocar caracteres de código, números, acentos ou datas. As conclusões de negócio abaixo foram confirmadas pela leitura visual das páginas relevantes, não somente pelo OCR.

O OCR abrangeu as 369 páginas. A leitura visual concentrou-se nas páginas relacionadas ao domínio; a extração integral não significa conferência manual de cada linha do livro.

A numeração impressa do livro inicia na página 16 do PDF: página impressa 1 = página do PDF 16. Até o manual examinado, a diferença é de 15 páginas. Ela não é constante no restante da digitalização: por exemplo, a página impressa 252 está na página 262 do PDF. Por isso as referências abaixo informam separadamente a página impressa e a página física do PDF, conferidas nas imagens.

## Páginas visualmente examinadas

- Identificação bibliográfica: PDF 5; apresentação e arquivos do sistema: PDF 12–13.
- Sumário completo: PDF 14–15.
- Contexto e menu: p. 1 e 6 (PDF 16 e 21).
- Manual de contas a pagar: p. 51–53 e 55–58 (PDF 66–68 e 70–73).
- Manual de contas a receber: p. 58–60 (PDF 73–75).
- Implementação e persistência de pagamentos: p. 246–247 e 252 (PDF 256–257 e 262).
- Implementação de recebimentos: p. 253 e 262 (PDF 263 e 272).
- Banco de dados e estruturas de clientes, pagamentos, recebimentos e plano de contas: p. 70–71 e 74–75 (PDF 84–85 e 88–89).
- Integridade referencial e impedimento de exclusão de banco com agências: p. 165 (PDF 177).

## Conceitos confirmados na referência

| Conceito | Evidência no livro | Adaptação para a aplicação acadêmica |
|---|---|---|
| Separação entre despesas e receitas | Menu distingue contas a pagar e contas a receber: p. 51 e 58 | Entidades Pagamento e Recebimento e endpoints próprios de quitação |
| Pessoa previamente cadastrada | Fornecedor escolhido de cadastro existente: p. 56–57; cliente: p. 60 | Relacionamento obrigatório com pessoa existente, com papel compatível |
| Datas financeiras | Formulários possuem emissão, vencimento e pagamento: p. 57 e 60 | Emissão e vencimento no lançamento; data da quitação no pagamento/recebimento |
| Valor e identificação da obrigação | Formulários de títulos: p. 57 e 60 | Valor positivo e descrição obrigatória; categoria e observações conforme enunciado |
| Consulta de registros | Listagens de títulos e edição: p. 56 e 59 | Consulta por ID/listagem e filtros de período e situação |
| Inclusão e alteração | p. 52–57 e 59–60 | Operações REST de cadastro e alteração, preservando consistência |
| Previsão versus obrigação efetiva | Despesas previstas e pagamentos com valor exato: p. 51–55 | O mínimo acadêmico modela diretamente a obrigação efetiva, com valor e vencimento definidos |
| Quitação acompanhável | Data de pagamento visível no formulário e listagem: p. 57–60 | Operação explícita para pagar/receber, mantendo data, forma e valor da quitação |

A implementação examinada também confirma persistência dos títulos em tabela de pagamento com cadastro de fornecedor separado (p. 246–247), gravação do registro e data de lançamento (p. 252) e consulta conjunta às tabelas de cliente e recebimento (p. 262). Esses detalhes justificam os relacionamentos persistidos da adaptação, sem transportar componentes Delphi para Java.

As estruturas das tabelas de pagamentos e recebimentos (p. 74–75) confirmam valor monetário e datas em campos próprios. O livro também destaca a necessidade de integridade referencial: um banco com agências associadas não pode ser excluído (p. 165). Impedir exclusão de pessoas/categorias referenciadas na adaptação aplica esse mesmo princípio ao domínio mínimo; é uma decisão explícita de projeto.

## Funcionalidades adicionais do livro

O livro contém cadastros de bancos, agências, contas correntes e cartórios, plano contábil hierárquico, lançamentos contábeis, despesas previstas, tipos de título (duplicata, nota promissória e outros), desconto com validade, acréscimos, cheques pré-datados e remessa de títulos para protesto. Esses itens aparecem no sumário (PDF 14–15) e nos formulários de p. 57–60.

O enunciado acadêmico define o domínio mínimo e permite ampliações. Ele pede que os conceitos do livro sejam adaptados para Java/Spring Boot; não exige reproduzir todos os módulos, telas ou infraestrutura Delphi/BDE. Portanto os recursos adicionais acima não são tratados como requisitos obrigatórios da Parte 1. Categorias financeiras adaptam a classificação do domínio sem implementar contabilidade completa.

## Escopo da Parte 1

A Parte 1 exige aplicação monolítica Spring Boot, API REST documentada, persistência, cadastro/consulta/alteração/exclusão quando aplicáveis, quitação de pagar e receber, filtros por período e situação, validação, erros, instruções de execução, testes/evidências, documentação e diagramas do monólito. Microsserviços, comunicação pela rede entre serviços, divisão dos serviços e comparação final ficam para a Parte 2.

O vídeo de pelo menos 30 minutos apresentando o monólito também é uma entrega expressamente exigida. Código e documentação não substituem essa gravação; a equipe precisa produzi-la e demonstrar domínio do trabalho.

## Limites das conclusões

A referência apresenta um aplicativo de 1998. Suas telas ajudam a identificar o negócio; não constituem especificação automática de segurança, transações ou validações de uma API atual. Regras como impedir quitação duplicada, preservar histórico de contas quitadas, impedir exclusão de cadastros referenciados e rejeitar intervalos de datas invertidos são decisões de consistência da adaptação, que precisam ser descritas e testadas. Não se deve atribuí-las ao livro sem evidência específica.

Não se afirma equivalência linha a linha com todo o código Delphi. A revisão usa o livro como referência de negócio e o enunciado como lista de requisitos da entrega monolítica.

