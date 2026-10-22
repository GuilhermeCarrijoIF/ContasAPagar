-- Estrutura final da Parte 1. Execute em uma base nova.
CREATE DATABASE IF NOT EXISTS contas_pagar_receber CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE contas_pagar_receber;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_agencia` (
  `agencia` varchar(10) NOT NULL,
  `banco` varchar(4) NOT NULL,
  `data_hora` datetime(6) DEFAULT NULL,
  `endereco` varchar(50) DEFAULT NULL,
  `fax` varchar(15) DEFAULT NULL,
  `gerente` varchar(20) DEFAULT NULL,
  `internet` varchar(20) DEFAULT NULL,
  `nome` varchar(30) NOT NULL,
  `ramal` varchar(4) DEFAULT NULL,
  `telefone` varchar(13) DEFAULT NULL,
  PRIMARY KEY (`agencia`),
  KEY `FK4er1i0pt0v2ue418xvdol5eyo` (`banco`),
  CONSTRAINT `FK4er1i0pt0v2ue418xvdol5eyo` FOREIGN KEY (`banco`) REFERENCES `tb_banco` (`banco`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_banco` (
  `banco` varchar(4) NOT NULL,
  `nome` varchar(30) NOT NULL,
  PRIMARY KEY (`banco`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_cartorio` (
  `registro` bigint NOT NULL AUTO_INCREMENT,
  `contato` varchar(30) DEFAULT NULL,
  `nome` varchar(30) NOT NULL,
  `telefone` varchar(13) DEFAULT NULL,
  PRIMARY KEY (`registro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_cheque_pre` (
  `registro` bigint NOT NULL AUTO_INCREMENT,
  `agencia` varchar(10) DEFAULT NULL,
  `banco` varchar(4) DEFAULT NULL,
  `conta_corrente` varchar(15) DEFAULT NULL,
  `cpf_cgc` varchar(18) DEFAULT NULL,
  `deposito` date DEFAULT NULL,
  `descricao` varchar(50) DEFAULT NULL,
  `emissao` date DEFAULT NULL,
  `lancamento` date DEFAULT NULL,
  `num_cheque` varchar(10) NOT NULL,
  `valor` decimal(15,2) NOT NULL,
  PRIMARY KEY (`registro`),
  KEY `FK20v9452ix88bid5hhx6shfjc0` (`agencia`),
  KEY `FKg1wn994eql8k3xqo5kwj1tsor` (`banco`),
  KEY `FKh046mlavck856rf7btteokaff` (`conta_corrente`),
  CONSTRAINT `FK20v9452ix88bid5hhx6shfjc0` FOREIGN KEY (`agencia`) REFERENCES `tb_agencia` (`agencia`),
  CONSTRAINT `FKg1wn994eql8k3xqo5kwj1tsor` FOREIGN KEY (`banco`) REFERENCES `tb_banco` (`banco`),
  CONSTRAINT `FKh046mlavck856rf7btteokaff` FOREIGN KEY (`conta_corrente`) REFERENCES `tb_conta_corrente` (`conta_corrente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_cliente` (
  `registro` bigint NOT NULL AUTO_INCREMENT,
  `cep` varchar(9) DEFAULT NULL,
  `cgc` varchar(18) DEFAULT NULL,
  `cidade` varchar(20) DEFAULT NULL,
  `cliente` varchar(50) NOT NULL,
  `contato` varchar(15) DEFAULT NULL,
  `data_hora` datetime(6) DEFAULT NULL,
  `endereco` varchar(70) DEFAULT NULL,
  `estado` varchar(2) DEFAULT NULL,
  `inscricao_estadual` varchar(15) DEFAULT NULL,
  `internet` varchar(20) DEFAULT NULL,
  `ramal` varchar(4) DEFAULT NULL,
  `telefone` varchar(13) DEFAULT NULL,
  PRIMARY KEY (`registro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_conta_corrente` (
  `conta_corrente` varchar(15) NOT NULL,
  `agencia` varchar(10) DEFAULT NULL,
  `banco` varchar(4) DEFAULT NULL,
  `limite` decimal(15,2) DEFAULT NULL,
  `tipo` int DEFAULT NULL,
  PRIMARY KEY (`conta_corrente`),
  KEY `FKnqkhhlql25984l6mncpt4or5l` (`agencia`),
  KEY `FKpqcdh17jp2ja1rgsl9xa17ks` (`banco`),
  CONSTRAINT `FKnqkhhlql25984l6mncpt4or5l` FOREIGN KEY (`agencia`) REFERENCES `tb_agencia` (`agencia`),
  CONSTRAINT `FKpqcdh17jp2ja1rgsl9xa17ks` FOREIGN KEY (`banco`) REFERENCES `tb_banco` (`banco`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_despesa` (
  `despesa` bigint NOT NULL AUTO_INCREMENT,
  `data_lanc` date NOT NULL,
  `descricao` varchar(80) DEFAULT NULL,
  `lancamento` bigint DEFAULT NULL,
  `prev_pagto` date DEFAULT NULL,
  `valor_previsto` decimal(15,2) NOT NULL,
  PRIMARY KEY (`despesa`),
  KEY `FKeapy019gibnohs3dl81i7gbdy` (`lancamento`),
  CONSTRAINT `FKeapy019gibnohs3dl81i7gbdy` FOREIGN KEY (`lancamento`) REFERENCES `tb_lancamento` (`lancamento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_fornecedor` (
  `registro` bigint NOT NULL AUTO_INCREMENT,
  `cep` varchar(9) DEFAULT NULL,
  `cgc` varchar(18) DEFAULT NULL,
  `cidade` varchar(20) DEFAULT NULL,
  `data_hora` datetime(6) DEFAULT NULL,
  `desconto` varchar(20) DEFAULT NULL,
  `endereco` varchar(70) DEFAULT NULL,
  `entrega` int DEFAULT NULL,
  `estado` varchar(2) DEFAULT NULL,
  `fornecedor` varchar(50) NOT NULL,
  `inscricao_estadual` varchar(15) DEFAULT NULL,
  `internet` varchar(20) DEFAULT NULL,
  `pagamento` varchar(20) DEFAULT NULL,
  `ramal` varchar(4) DEFAULT NULL,
  `telefone` varchar(13) DEFAULT NULL,
  `vendedor` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`registro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lancamento` (
  `lancamento` bigint NOT NULL AUTO_INCREMENT,
  `conta_credito` bigint DEFAULT NULL,
  `conta_debito` bigint DEFAULT NULL,
  `data` date NOT NULL,
  `descricao` varchar(50) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `valor` decimal(15,2) NOT NULL,
  PRIMARY KEY (`lancamento`),
  KEY `FK1vwy01qsrbxj3jh9rg60iders` (`conta_credito`),
  KEY `FKf74xfo83mgosf5i7hhqwwri81` (`conta_debito`),
  CONSTRAINT `FK1vwy01qsrbxj3jh9rg60iders` FOREIGN KEY (`conta_credito`) REFERENCES `tb_plano_contas` (`plano_contas`),
  CONSTRAINT `FKf74xfo83mgosf5i7hhqwwri81` FOREIGN KEY (`conta_debito`) REFERENCES `tb_plano_contas` (`plano_contas`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_pagamento` (
  `titulo` varchar(20) NOT NULL,
  `acrescimo` decimal(15,2) DEFAULT NULL,
  `categoria_id` bigint NOT NULL,
  `cgc` varchar(18) DEFAULT NULL,
  `desconto` decimal(15,2) DEFAULT NULL,
  `descricao` varchar(255) NOT NULL,
  `emissao` date NOT NULL,
  `forma_pagamento` enum('BOLETO','CARTAO','CHEQUE','DINHEIRO','OUTRA','PIX','TRANSFERENCIA') DEFAULT NULL,
  `fornecedor_id` bigint NOT NULL,
  `lancamento` date DEFAULT NULL,
  `observacoes` varchar(2000) DEFAULT NULL,
  `pagto` date DEFAULT NULL,
  `status` enum('ABERTA','CANCELADA','PAGA','RECEBIDA','VENCIDA') DEFAULT NULL,
  `tipo` varchar(16) DEFAULT NULL,
  `validade_desc` date DEFAULT NULL,
  `valor` decimal(15,2) NOT NULL,
  `valor_quitado` decimal(15,2) DEFAULT NULL,
  `vencto` date NOT NULL,
  `versao` bigint DEFAULT NULL,
  PRIMARY KEY (`titulo`),
  KEY `FK1cp3f33w82kyb68fajlr9x21v` (`categoria_id`),
  KEY `FKoo4yr4nkcc3vgx722n85uqwo8` (`fornecedor_id`),
  CONSTRAINT `FK1cp3f33w82kyb68fajlr9x21v` FOREIGN KEY (`categoria_id`) REFERENCES `tb_plano_contas` (`plano_contas`),
  CONSTRAINT `FKoo4yr4nkcc3vgx722n85uqwo8` FOREIGN KEY (`fornecedor_id`) REFERENCES `tb_fornecedor` (`registro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_plano_contas` (
  `plano_contas` bigint NOT NULL AUTO_INCREMENT,
  `consolidacao` bit(1) DEFAULT NULL,
  `conta` varchar(18) NOT NULL,
  `descricao` varchar(60) DEFAULT NULL,
  `superior` varchar(18) DEFAULT NULL,
  PRIMARY KEY (`plano_contas`),
  UNIQUE KEY `UK7ec2lsfk4rh8da7tvyi5kg8v9` (`conta`),
  KEY `FK1e7osq57sovng9xk8xru89e5l` (`superior`),
  CONSTRAINT `FK1e7osq57sovng9xk8xru89e5l` FOREIGN KEY (`superior`) REFERENCES `tb_plano_contas` (`conta`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_recebimento` (
  `titulo` varchar(20) NOT NULL,
  `acrescimo` decimal(15,2) DEFAULT NULL,
  `categoria_id` bigint NOT NULL,
  `cgc` varchar(18) DEFAULT NULL,
  `cliente_id` bigint NOT NULL,
  `desconto` decimal(15,2) DEFAULT NULL,
  `descricao` varchar(255) NOT NULL,
  `emissao` date NOT NULL,
  `forma_pagamento` enum('BOLETO','CARTAO','CHEQUE','DINHEIRO','OUTRA','PIX','TRANSFERENCIA') DEFAULT NULL,
  `lancamento` date DEFAULT NULL,
  `observacoes` varchar(2000) DEFAULT NULL,
  `pagto` date DEFAULT NULL,
  `status` enum('ABERTA','CANCELADA','PAGA','RECEBIDA','VENCIDA') DEFAULT NULL,
  `tipo` varchar(16) DEFAULT NULL,
  `validade_desc` date DEFAULT NULL,
  `valor` decimal(15,2) NOT NULL,
  `valor_quitado` decimal(15,2) DEFAULT NULL,
  `vencto` date NOT NULL,
  `versao` bigint DEFAULT NULL,
  PRIMARY KEY (`titulo`),
  KEY `FKpy8qmy4fc0v1ta5cc5kaw4lf2` (`categoria_id`),
  KEY `FK6a74j3a2yvokf4tp1h1awr25k` (`cliente_id`),
  CONSTRAINT `FK6a74j3a2yvokf4tp1h1awr25k` FOREIGN KEY (`cliente_id`) REFERENCES `tb_cliente` (`registro`),
  CONSTRAINT `FKpy8qmy4fc0v1ta5cc5kaw4lf2` FOREIGN KEY (`categoria_id`) REFERENCES `tb_plano_contas` (`plano_contas`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_remessa_cartorio` (
  `id_remessa` bigint NOT NULL AUTO_INCREMENT,
  `baixa` date DEFAULT NULL,
  `protesto` date DEFAULT NULL,
  `registro` bigint DEFAULT NULL,
  `remessa` date DEFAULT NULL,
  `titulo` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_remessa`),
  KEY `FKrm9u12627rkru58da6sicqmg5` (`registro`),
  KEY `FK1udg824l7jkkcvdv3pwdo9nwv` (`titulo`),
  CONSTRAINT `FK1udg824l7jkkcvdv3pwdo9nwv` FOREIGN KEY (`titulo`) REFERENCES `tb_recebimento` (`titulo`),
  CONSTRAINT `FKrm9u12627rkru58da6sicqmg5` FOREIGN KEY (`registro`) REFERENCES `tb_cartorio` (`registro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

