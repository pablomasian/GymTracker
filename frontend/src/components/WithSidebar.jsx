import Sidebar from './Sidebar';
import React from 'react';

/**
 * Contiene el layout que muestra la Sidebar y el área principal.
 * Úsalo para envolver páginas que necesitan navegación persistente.
 */
export default function WithSidebar({ children }) {
  return (
    <div className="app-shell">
      <Sidebar />
      <main className="main-content">
        {children}
      </main>
    </div>
  );
}
