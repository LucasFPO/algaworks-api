/* Lembrando que a partir do momento que se usa o Flyway, você não pode mais alterar a estrutura do banco
de dados (criar tabelas, colunas...) manualmente direto no banco de dados (MySQL Workbench).
Tudo que for alterar na estrutura do banco de dados, é necessário que seja feito a partir de migrations.*/

CREATE TABLE categoria (
	codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO categoria (nome) values ('Lazer');
INSERT INTO categoria (nome) values ('Alimentação');
INSERT INTO categoria (nome) values ('Supermercado');
INSERT INTO categoria (nome) values ('Farmácia');
INSERT INTO categoria (nome) values ('Outros');