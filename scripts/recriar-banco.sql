-- Recria o banco do zero para aplicar o novo schema (APAGA TODOS OS DADOS).
-- Execute no MySQL Workbench (conectado como root) ANTES de subir a aplicação.
-- Ao subir, o Hibernate recria as tabelas e o DataInitializer repopula os dados de exemplo.
--
-- Os privilégios (GRANT) dos usuários sobre `fastariam`.* são mantidos pelo MySQL
-- mesmo após o DROP DATABASE.

DROP DATABASE IF EXISTS fastariam;
CREATE DATABASE fastariam CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
