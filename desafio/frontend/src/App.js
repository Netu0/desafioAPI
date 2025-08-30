// frontend/src/App.js
import React, { useState } from "react";
import Header from "./components/Header";
import ContaDetalhe from "./components/ContaDetalhe";
import Extrato from "./components/Extrato";

function App() {
  const [current, setCurrent] = useState("conta");

  return (
    <div>
      <h1>Desafio API - Frontend</h1>
      <Header current={current} setCurrent={setCurrent} />

      {current === "conta" && <ContaDetalhe />}
      {current === "extrato" && <Extrato />}
    </div>
  );
}

export default App;
