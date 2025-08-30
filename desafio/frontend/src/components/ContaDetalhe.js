// frontend/src/components/ContaDetalhe.js
import React, { useState } from "react";
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  // se preferir usar "proxy" no package.json, troque para baseURL: "/"
});

function moedaBRL(v) {
  if (v === null || v === undefined) return "-";
  const n = typeof v === "string" ? Number(v) : v;
  if (Number.isNaN(n)) return String(v);
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(n);
}

export default function ContaDetalhe() {
  const [idInput, setIdInput] = useState("");
  const [conta, setConta] = useState(null);
  const [titular, setTitular] = useState(null);
  const [transacoes, setTransacoes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState(null);

  async function buscarConta() {
    setLoading(true);
    setErro(null);
    setConta(null);
    setTitular(null);
    setTransacoes([]);

    try {
      // 1) Conta
      const respConta = await api.get(`/contas/${idInput}`);
      const acc = respConta.data || {};

      // Normaliza campos
      const idConta =
        acc.idConta ?? acc.id ?? acc.idconta ?? acc.contaId ?? acc.id_conta;
      const saldo = acc.saldo ?? acc.valor ?? acc.balance;
      const limite = acc.limiteSaqueDiario ?? acc.limite ?? acc.limite_saque_diario;
      const flagAtivo = acc.flagAtivo ?? acc.ativo ?? acc.isAtivo ?? acc.status;
      const tipoConta = acc.tipoConta ?? acc.tipo ?? acc.accountType;
      const dataCriacao = acc.dataCriacao ?? acc.criadoEm ?? acc.createdAt;

      // 2) Descobre o titular
      // tenta primeiro dentro da própria conta
      let nomePessoa =
        acc?.pessoa?.nome ??
        acc?.titular?.nome ??
        acc?.nomePessoa ??
        acc?.nome;

      // se não veio, tenta via idPessoa
      const idPessoa =
        acc.idPessoa ??
        acc.pessoaId ??
        acc?.pessoa?.idPessoa ??
        acc?.titular?.idPessoa;

      if (!nomePessoa && idPessoa) {
        try {
          const respPessoa = await api.get(`/pessoas/${idPessoa}`);
          const p = respPessoa.data || {};
          nomePessoa = p.nome ?? p.name;
          setTitular({ idPessoa: idPessoa, nome: nomePessoa, cpf: p.cpf, dataNascimento: p.dataNascimento });
        } catch {
          // silencia: se não tiver endpoint de pessoas, seguimos sem nome
        }
      } else if (nomePessoa || idPessoa) {
        setTitular({ idPessoa, nome: nomePessoa });
      }

      // 3) Transações (opcional)
      // tenta alguns caminhos comuns
      async function tentaCarregarTransacoes() {
        const caminhos = [
          `/contas/${idConta}/transacoes`,
          `/transacoes/conta/${idConta}`,
          `/transacoes?contaId=${idConta}`,
        ];
        for (const path of caminhos) {
          try {
            const r = await api.get(path);
            if (Array.isArray(r.data) && r.data.length >= 0) {
              return r.data;
            }
          } catch {
            // tenta o próximo
          }
        }
        return [];
      }
      const txs = idConta ? await tentaCarregarTransacoes() : [];

      setConta({ idConta, saldo, limite, flagAtivo, tipoConta, dataCriacao, bruto: acc });
      setTransacoes(txs);
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
        <div style={{ marginTop: 20, border: "1px solid #ccc", padding: 12, borderRadius: 8 }}>
          <h3>Dados da Conta</h3>
          <p><strong>ID da Conta:</strong> {conta.idConta ?? "-"}</p>
          <p><strong>Nome do Titular:</strong> {titular?.nome ?? "-"}</p>
          {titular?.idPessoa && <p><strong>ID da Pessoa:</strong> {titular.idPessoa}</p>}
          <p><strong>Saldo:</strong> {moedaBRL(conta.saldo)}</p>
          {conta.limite !== undefined && <p><strong>Limite de Saque Diário:</strong> {moedaBRL(conta.limite)}</p>}
          {conta.flagAtivo !== undefined && <p><strong>Ativo:</strong> {String(conta.flagAtivo)}</p>}
          {conta.tipoConta && <p><strong>Tipo de Conta:</strong> {conta.tipoConta}</p>}
          {conta.dataCriacao && <p><strong>Data de Criação:</strong> {String(conta.dataCriacao)}</p>}

          {/* Debug rápido para ver o JSON verdadeiro que o back está mandando */}
          <details style={{ marginTop: 12 }}>
            <summary>Ver JSON bruto da conta</summary>
            <pre style={{ overflow: "auto", background: "#f7f7f7", padding: 12 }}>
{JSON.stringify(conta.bruto, null, 2)}
            </pre>
          </details>
        </div>
      )}

      {Array.isArray(transacoes) && transacoes.length > 0 && (
        <div style={{ marginTop: 20, border: "1px solid #ccc", padding: 12, borderRadius: 8 }}>
          <h3>Transações</h3>
          <ul style={{ paddingLeft: 18 }}>
            {transacoes.map((t, i) => {
              const idTransacao = t.idTransacao ?? t.id ?? t.transacaoId;
              const valor = t.valor ?? t.amount;
              const data = t.dataTransacao ?? t.criadoEm ?? t.createdAt;
              return (
                <li key={idTransacao ?? i} style={{ marginBottom: 6 }}>
                  <div><strong>ID:</strong> {idTransacao ?? "-"}</div>
                  <div><strong>Valor:</strong> {moedaBRL(valor)}</div>
                  <div><strong>Data:</strong> {String(data ?? "-")}</div>
                </li>
              );
            })}
          </ul>
        </div>
      )}
    </div>
  );
}
