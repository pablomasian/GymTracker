// Punto de entrada del frontend: monta React, Router y el AuthProvider
import React from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";
import "./index.css";
import { AuthProvider } from "./context/AuthContext";

// Renderiza la aplicación en #root
createRoot(document.getElementById("root")).render(
  <AuthProvider>
    <BrowserRouter basename="/gymtracker">
      <App />
    </BrowserRouter>
  </AuthProvider>
);
