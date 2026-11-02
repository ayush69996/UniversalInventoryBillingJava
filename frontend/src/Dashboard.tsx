
import { useEffect, useState } from 'react'

interface DashboardData {
  totalProducts: number
  totalSales: number
  revenue: number
  grossProfit: number
  lowStockCount: number
  totalUnits: number
}

interface Product {
  id: number
  name: string
  sku: string
  category: string
  quantity: number
}

interface Sale {
  id: number
  date: string
  total: number
  items: { productName: string; quantity: number }[]
}

function Dashboard({ onNavigate }: { onNavigate: (page: string) => void }) {
  const [data, setData] = useState<DashboardData>({
    totalProducts: 0,
    totalSales: 0,
    revenue: 0,
    grossProfit: 0,
    lowStockCount: 0,
    totalUnits: 0,
  })
  const [products, setProducts] = useState<Product[]>([])
  const [sales, setSales] = useState<Sale[]>([])
  const [error, setError] = useState('')

  const loadDashboard = async () => {
    try {
      setError('')
      const [dashboardRes, inventoryRes, salesRes] = await Promise.all([
        fetch('/api/dashboard'),
        fetch('/api/reports/inventory'),
        fetch('/api/reports/sales'),
      ])

      if (!dashboardRes.ok || !inventoryRes.ok || !salesRes.ok) {
        throw new Error('Unable to load dashboard')
      }

      const dashboard = await dashboardRes.json()
      const inventory = await inventoryRes.json()
      const salesReport = await salesRes.json()

      setData(dashboard)
      setProducts(inventory.products || [])
      setSales((salesReport.sales || []).slice().reverse().slice(0, 5))
    } catch {
      setError('Unable to load dashboard data. Please check your backend.')
    }
  }

  useEffect(() => {
    loadDashboard()
  }, [])

  const money = (value: number) =>
    new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2,
    }).format(value || 0)

  const formatDate = (value: string) =>
    new Date(value).toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    })

  return (
    <main className="main-content">
      <header className="topbar">
        <div>
          <span className="breadcrumb">Workspace / Overview</span>
          <h1>Dashboard</h1>
        </div>
        <div className="top-actions">
          <span className="status"><i /> System Online</span>
          <div className="notification">!</div>
          <div className="top-avatar">A</div>
        </div>
      </header>

      <section className="welcome">
        <div>
          <h2>Welcome back, Administrator </h2>
          <p>Here's what's happening with your business today.</p>
        </div>
        <button className="date-button" onClick={loadDashboard}>Refresh</button>
      </section>

      {error && <p className="error-message">{error}</p>}

      <section className="stats-grid">
        <div className="stat-card">
          <div className="stat-top"><span>Total Revenue</span><span className="stat-icon purple">Rs. </span></div>
          <h2>{money(data.revenue)}</h2>
          <p className="muted">From all recorded sales</p>
        </div>
        <div className="stat-card">
          <div className="stat-top"><span>Total Products</span><span className="stat-icon blue"></span></div>
          <h2>{data.totalProducts}</h2>
          <p className="muted">Products in inventory</p>
        </div>
        <div className="stat-card">
          <div className="stat-top"><span>Total Sales</span><span className="stat-icon green"></span></div>
          <h2>{data.totalSales}</h2>
          <p className="muted">Completed transactions</p>
        </div>
        <div className="stat-card">
          <div className="stat-top"><span>Gross Profit</span><span className="stat-icon orange"></span></div>
          <h2>{money(data.grossProfit)}</h2>
          <p className="muted">Before tax and discounts</p>
        </div>
      </section>

      <section className="content-grid">
        <div className="panel inventory-panel">
          <div className="panel-heading">
            <div><h3>Inventory Overview</h3><p>Your current stock summary</p></div>
            <a href="#inventory" onClick={() => onNavigate('inventory')}>View inventory →</a>
          </div>
          <div className="inventory-summary">
            <div><span>Total stock units</span><strong>{data.totalUnits}</strong></div>
            <div><span>Low stock products</span><strong>{data.lowStockCount}</strong></div>
            <div><span>Inventory items</span><strong>{data.totalProducts}</strong></div>
          </div>
          {products.slice(0, 3).map(product => (
            <div className="stock-row" key={product.id}>
              <div><strong>{product.name}</strong><span>{product.sku} | {product.category}</span></div>
              <div className="stock-quantity"><strong>{product.quantity}</strong><span>Units available</span></div>
            </div>
          ))}
          {products.length === 0 && <p className="muted">No products in inventory.</p>}
        </div>

        <div className="panel quick-panel">
          <div className="panel-heading">
            <div><h3>Quick Actions</h3><p>Common tasks</p></div>
          </div>
          <button className="quick-action" onClick={() => onNavigate('inventory')}>
  <span><strong>Add Product</strong><small>Add a new inventory item</small></span>
  <b>→</b>
</button>
          <button className="quick-action" onClick={() => onNavigate('billing')}>
  <span><strong>Create New Bill</strong><small>Generate a customer invoice</small></span>
  <b>→</b>
</button>
          <button className="quick-action" onClick={() => onNavigate('reports')}>
  <span><strong>View Reports</strong><small>Analyze business performance</small></span>
  <b>→</b>
</button>
        </div>
      </section>

      <section className="panel recent-panel">
        <div className="panel-heading">
          <div><h3>Recent Transactions</h3><p>Your latest sales activity</p></div>
          <a href="#sales" onClick={() => onNavigate('sales')}>View all sales →</a>
        </div>
        <div className="table-wrap">
          <table>
            <thead>
              <tr><th>SALE ID</th><th>PRODUCT</th><th>QUANTITY</th><th>DATE</th><th>AMOUNT</th><th>STATUS</th></tr>
            </thead>
            <tbody>
              {sales.map(sale => (
                <tr key={sale.id}>
                  <td>#{String(sale.id).padStart(3, '0')}</td>
                  <td><strong>{sale.items?.map(item => item.productName).join(', ') || 'Sale'}</strong></td>
                  <td>{sale.items?.reduce((sum, item) => sum + item.quantity, 0) || 0} units</td>
                  <td>{formatDate(sale.date)}</td>
                  <td><strong>{money(sale.total)}</strong></td>
                  <td><span className="paid">Completed</span></td>
                </tr>
              ))}
              {sales.length === 0 && (
                <tr><td colSpan={6}>No transactions recorded yet.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </section>

      <footer>Universal Inventory & Billing System <span>|</span> Dashboard</footer>
    </main>
  )
}

export default Dashboard
