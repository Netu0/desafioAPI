INSERT INTO TB_PESSOA (idPessoa, nome, cpf, dataNascimento)
VALUES
(1, 'João da Silva', '123.456.789-00', '1985-03-12'),
(2, 'Maria Oliveira', '987.654.321-00', '1990-07-25'),
(3, 'Carlos Souza', '111.222.333-44', '1978-11-02'),
(4, 'Ana Pereira', '555.666.777-88', '2000-01-15');


INSERT INTO TB_CONTA (idConta, idPessoa, saldo, limiteSaqueDiario, flagAtivo, tipoConta, dataCriacao)
VALUES
(1001, 1, 2500.00, 1000.00, 'S', 1, '2023-05-10'),
(1002, 2, 5000.00, 2000.00, 'S', 2, '2023-06-20'),
(1003, 3, 150.50, 500.00, 'N', 1, '2022-12-01'),
(1004, 4, 780.00, 800.00, 'S', 1, '2024-01-05');


INSERT INTO TB_TRANSACAO (idTransacao, idConta, valor, dataTransacao)
VALUES
(9001, 1001, -200.00, '2024-08-10'),
(9002, 1001, 500.00, '2024-08-15'),
(9003, 1002, -1500.00, '2024-08-18'),
(9004, 1003, 200.00, '2024-07-22'),
(9005, 1004, -50.00, '2024-08-01'),
(9006, 1004, 300.00, '2024-08-05');
