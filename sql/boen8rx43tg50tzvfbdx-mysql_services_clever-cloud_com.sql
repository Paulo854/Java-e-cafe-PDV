-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: boen8rx43tg50tzvfbdx-mysql.services.clever-cloud.com:3306
-- Tempo de geração: 23/04/2025 às 18:35
-- Versão do servidor: 8.4.2-2
-- Versão do PHP: 8.2.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `boen8rx43tg50tzvfbdx`
--
CREATE DATABASE IF NOT EXISTS `boen8rx43tg50tzvfbdx` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `boen8rx43tg50tzvfbdx`;

-- --------------------------------------------------------

--
-- Estrutura para tabela `caixas_abertos`
--

CREATE TABLE `caixas_abertos` (
  `id` int NOT NULL,
  `funcionario` varchar(255) NOT NULL,
  `filial` varchar(255) NOT NULL,
  `qtd_dinheiro` double NOT NULL,
  `pdv` int NOT NULL,
  `matricula` int NOT NULL,
  `matriculaGerencia` int NOT NULL,
  `ip` varchar(255) NOT NULL,
  `data_abertura` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `caixas_abertos`
--

INSERT INTO `caixas_abertos` (`id`, `funcionario`, `filial`, `qtd_dinheiro`, `pdv`, `matricula`, `matriculaGerencia`, `ip`, `data_abertura`) VALUES
(17, 'Murilo Pereira', '1001', 100, 1, 3, 3, '192.168.0.127', '2025-04-23'),
(18, 'Murilo Pereira', '1001', 1, 2, 3, 3, '192.168.0.127', '2025-04-23');

-- --------------------------------------------------------

--
-- Estrutura para tabela `cliente`
--

CREATE TABLE `cliente` (
  `id` int NOT NULL,
  `nome` varchar(255) NOT NULL,
  `cpf` varchar(255) NOT NULL,
  `dt_nascimento` date NOT NULL,
  `pontos` int NOT NULL,
  `qt_pedidos` int NOT NULL,
  `mb_fidelidade` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `controle estoque`
--

CREATE TABLE `controle estoque` (
  `id` int NOT NULL,
  `produto` varchar(255) NOT NULL,
  `quantidade` int NOT NULL,
  `fornecedor` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `funcionario`
--

CREATE TABLE `funcionario` (
  `id` int NOT NULL,
  `nome` varchar(255) NOT NULL,
  `cpf` varchar(255) NOT NULL,
  `dt_nascimento` date NOT NULL,
  `matricula` int NOT NULL,
  `cargo` text NOT NULL,
  `filial` int DEFAULT NULL,
  `senha` int NOT NULL,
  `gerencia` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `funcionario`
--

INSERT INTO `funcionario` (`id`, `nome`, `cpf`, `dt_nascimento`, `matricula`, `cargo`, `filial`, `senha`, `gerencia`) VALUES
(1, 'Paulo Victor', '000.000.000-00', '2005-04-26', 1, 'Desenvolvedor', 1002, 375834, 1),
(2, 'Geovanna Ferreira', '000.000.000-00', '2004-10-09', 2, 'Desenvolvedora', 1003, 497199, 1),
(3, 'Murilo Pereira', '000.000.000-00', '2004-09-20', 3, 'Desenvolvedor', 1001, 2345678, 1),
(4, 'Karine Peixoto', '000.000.000-00', '2006-07-10', 4, 'Pentester', 1001, 681299, 1);

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `caixas_abertos`
--
ALTER TABLE `caixas_abertos`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `controle estoque`
--
ALTER TABLE `controle estoque`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `funcionario`
--
ALTER TABLE `funcionario`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `caixas_abertos`
--
ALTER TABLE `caixas_abertos`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de tabela `cliente`
--
ALTER TABLE `cliente`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `controle estoque`
--
ALTER TABLE `controle estoque`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `funcionario`
--
ALTER TABLE `funcionario`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
