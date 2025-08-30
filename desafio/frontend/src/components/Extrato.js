import React, { useState } from "react";
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

function moedaBRL(v) {
  if (v === null || v === undefined) return "-";
  const n = typeof v === "string" ? Number(v) : v;
  if (Number.isNaN(n)) return String(v);
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(n);
}

export default function Extrato() {
  const [idConta, setIdConta] = useState("");
  const [start, setStart] = useState("");
  const [end, setEnd] = useState("");
  const [transacoes, setTransacoes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState(null);

  async function buscarExtrato() {
    setLoading(true);
    setErro(null);
    setTransacoes([]);

    try {
      const params = {};
      if (start) params.start = start;
      if (end) params.end = end;

      const resp = await api.get(`/transacoes/${idConta}/extrato`, { params });
      setTransacoes(resp.data || []);
    } catch (e) {
      setErro("Erro ao buscar extrato.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{ padding: 20, fontFamily: "Arial", maxWidth: 720 }}>
      <h2>Extrato de Conta</h2>

      <div style={{ display: "flex", gap: 8, flexWrap: "wrap", alignItems: "center" }}>
        <input
          type="number"
          placeholder="ID da conta"
          value={idConta}
          onChange={(e) => setIdConta(e.target.value)}
        />
        <input
          type="date"
          value={start}
          onChange={(e) => setStart(e.target.value)}
        />
        <input
          type="date"
          value={end}
          onChange={(e) => setEnd(e.target.value)}
        />
        <button onClick={buscarExtrato} disabled={!idConta || loading}>
          {loading ? "Buscando..." : "Buscar Extrato"}
        </button>
      </div>

      {erro && <p style={{ color: "red", marginTop: 12 }}>{erro}</p>}

      {Array.isArray(transacoes) && transacoes.length > 0 && (
        <div style={{ marginTop: 20 }}>
          <h3>Movimentações</h3>
          <table style={{ borderCollapse: "collapse", width: "100%" }}>
            <thead>
              <tr style={{ background: "#f0f0f0" }}>
                <th style={{ border: "1px solid #ccc", padding: 8 }}>ID</th>
                <th style={{ border: "1px solid #ccc", padding: 8 }}>Data</th>
                <th style={{ border: "1px solid #ccc", padding: 8 }}>Valor</th>
                <th style={{ border: "1px solid #ccc", padding: 8 }}>Tipo</th>
              </tr>
            </thead>
            <tbody>
              {transacoes.map((t, i) => (
                <tr key={t.idTransacao ?? i}>
                  <td style={{ border: "1px solid #ccc", padding: 8 }}>
                    {t.idTransacao ?? "-"}
                  </td>
                  <td style={{ border: "1px solid #ccc", padding: 8 }}>
                    {t.dataTransacao ?? "-"}
                  </td>
                  <td style={{ border: "1px solid #ccc", padding: 8 }}>
                    {moedaBRL(t.valor)}
                  </td>
                  <td style={{ border: "1px solid #ccc", padding: 8 }}>
                    {t.tipo ?? (t.valor >= 0 ? "Depósito" : "Saque")}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
