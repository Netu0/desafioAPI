import React from "react";

export default function Header({ current, setCurrent }) {
  return (
    <div style={{ display: "flex", gap: 16, padding: 12, background: "#eee" }}>
      <button
        onClick={() => setCurrent("conta")}
        style={{ fontWeight: current === "conta" ? "bold" : "normal" }}
      >
        Conta
      </button>
      <button
        onClick={() => setCurrent("extrato")}
        style={{ fontWeight: current === "extrato" ? "bold" : "normal" }}
      >
        Extrato
      </button>
    </div>
  );
}
