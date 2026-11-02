
import { useState } from 'react'
import './App.css'
import Inventory from './Inventory'
import Billing from './Billing'
import Sales from './Sales'
import Reports from './Reports'
import Dashboard from './Dashboard'

function App() {
  const [page, setPage] = useState('dashboard')

  const menu = [
    ['', 'Dashboard', 'dashboard'],
    ['', 'Inventory', 'inventory'],
    ['', 'Billing & Invoices', 'billing'],
    ['', 'Sales History', 'sales'],
    ['', 'Reports & Analytics', 'reports'],
  ]

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-icon">U</div>
          <div>
            <h2>Universal</h2>
            <span>INVENTORY SYSTEM</span>
          </div>
        </div>

        <div className="menu-label">MAIN MENU</div>

        <nav>
          {menu.map(([icon, label, value]) => (
            <a
              key={value}
              href={`#${value}`}
              className={page === value ? 'active' : ''}
              onClick={() => setPage(value)}
            >
              {icon} <span>{label}</span>
            </a>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <div className="menu-label">SYSTEM</div>
          <a href="#settings" onClick={() => setPage('settings')}> <span>Settings</span></a>
          <div className="profile">
            <div className="avatar">A</div>
            <div>
              <strong>Administrator</strong>
              <span>Store Manager</span>
            </div>
          </div>
        </div>
      </aside>

      {page === 'inventory' ? (
        <Inventory />
      ) : page === 'billing' ? (
        <Billing />
      ) : page === 'sales' ? (
        <Sales />
      ) : page === 'reports' ? (
        <Reports />
      ) : page === 'dashboard' ? (
        <Dashboard onNavigate={setPage} />
      ) : (
        <main className="main-content">
          <header className="topbar">
            <div>
              <span className="breadcrumb">Workspace</span>
              <h1>{menu.find(item => item[2] === page)?.[1] || 'Settings'}</h1>
            </div>
          </header>
          <section className="panel" style={{ padding: 30, marginTop: 25 }}>
            <h3>This section is coming next.</h3>
            <p>We'll connect this page to the backend shortly.</p>
          </section>
        </main>
      )}
    </div>
  )
}

export default App
