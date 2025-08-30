import React, { useState } from "react";
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

function moedaBRL(v) {
  if (v === null || v === undefined) return "-";
  const n = typeof v === "string" ? Number(v) : v;
  if (Number.isNaN(n)) return String(v);
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(n);
}

export default function ContaDetalhe() {
  const [idInput, setIdInput] = useState("");
  const [conta, setConta] = useState(null);
  const [titular, setTitular] = useState(null);
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState(null);

  async function buscarConta() {
    setLoading(true);
    setErro(null);
    setConta(null);
    setTitular(null);

    try {

      const respConta = await api.get(`/contas/${idInput}`);
      const acc = respConta.data || {};

      const idConta = acc.idConta;
      const saldo = acc.saldo;
      const limite = acc.limiteSaqueDiario;
      const flagAtivo = acc.flagAtivo;
      const tipoConta = acc.tipoConta;
      const dataCriacao = acc.dataCriacao;

      let nomePessoa = acc?.pessoa?.nome;

      const idPessoa = acc.idPessoa;

      if (!nomePessoa && idPessoa) {
        try {
          const respPessoa = await api.get(`/pessoas/${idPessoa}`);
          const p = respPessoa.data || {};
          nomePessoa = p.nome ?? p.name;
          setTitular({
            idPessoa: idPessoa,
            nome: nomePessoa,
            cpf: p.cpf,
            dataNascimento: p.dataNascimento,
          });
        } catch {
        }
      } else if (nomePessoa || idPessoa) {
        setTitular({ idPessoa, nome: nomePessoa });
      }

      setConta({
        idConta,
        saldo,
        limite,
        flagAtivo,
        tipoConta,
        dataCriacao,
        bruto: acc,
      });
    } catch (e) {
      setErro("Conta não encontrada ou erro na API.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{ padding: 20, fontFamily: "Arial", maxWidth: 720 }}>
      <h2>Consulta de Conta</h2>

      <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
        <input
          type="number"
          placeholder="Digite o ID da conta"
          value={idInput}
          onChange={(e) => setIdInput(e.target.value)}
        />
        <button onClick={buscarConta} disabled={!idInput || loading}>
          {loading ? "Buscando..." : "Buscar"}
        </button>
      </div>

      {erro && <p style={{ color: "red", marginTop: 12 }}>{erro}</p>}

      {conta && (
        <div
          style={{
            marginTop: 20,
            border: "1px solid #ccc",
            padding: 12,
            borderRadius: 8,
          }}
        >
          <h3>Dados da Conta</h3>
          <p>
            <strong>ID da Conta:</strong> {conta.idConta ?? "-"}
          </p>
          <p>
            <strong>Nome do Titular:</strong> {titular?.nome ?? "-"}
          </p>
          {titular?.idPessoa && (
            <p>
              <strong>ID da Pessoa:</strong> {titular.idPessoa}
            </p>
          )}
          <p>
            <strong>Saldo:</strong> {moedaBRL(conta.saldo)}
          </p>
          {conta.limite !== undefined && (
            <p>
              <strong>Limite de Saque Diário:</strong>{" "}
              {moedaBRL(conta.limite)}
            </p>
          )}
          {conta.flagAtivo !== undefined && (
            <p>
              <strong>Ativo:</strong> {String(conta.flagAtivo)}
            </p>
          )}
          {conta.tipoConta && (
            <p>
              <strong>Tipo de Conta:</strong> {conta.tipoConta}
            </p>
          )}
          {conta.dataCriacao && (
            <p>
              <strong>Data de Criação:</strong> {String(conta.dataCriacao)}
            </p>
          )}

          {/* Debug rápido para ver o JSON verdadeiro que o back está mandando */}
          <details style={{ marginTop: 12 }}>
            <summary>Ver JSON bruto da conta</summary>
            <pre
              style={{
                overflow: "auto",
                background: "#f7f7f7",
                padding: 12,
              }}
            >
              {JSON.stringify(conta.bruto, null, 2)}
            </pre>
          </details>
        </div>
      )}
    </div>
  );
}
