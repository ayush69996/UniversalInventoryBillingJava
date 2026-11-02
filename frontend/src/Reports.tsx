
import { useEffect, useState } from 'react'

interface InventoryReport {
  totalProducts: number
  totalUnits: number
  lowStockCount: number
  stockCostValue: number
  stockSellingValue: number
  products: {
    id: number
    name: string
    sku: string
    quantity: number
    costPrice: number
    sellingPrice: number
  }[]
}

interface SalesReport {
  totalSales: number
  revenue: number
  taxCollected: number
  discounts: number
  grossProfitBeforeTaxAndDiscount: number
  sales: {
    id: number
    date: string
    total: number
    items: {
      productName: string
      quantity: number
      lineTotal: number
    }[]
  }[]
}

function Reports() {
  const [inventory, setInventory] = useState<InventoryReport | null>(null)
  const [sales, setSales] = useState<SalesReport | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function loadReports() {
    try {
      setLoading(true)
      const [inventoryResponse, salesResponse] = await Promise.all([
        fetch('/api/reports/inventory'),
        fetch('/api/reports/sales')
      ])

      if (!inventoryResponse.ok || !salesResponse.ok) {
        throw new Error('Unable to load reports.')
      }

      const inventoryData = await inventoryResponse.json()
      const salesData = await salesResponse.json()

      setInventory(inventoryData)
      setSales(salesData)
      setError('')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Something went wrong.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadReports()
  }, [])

  const currency = (amount: number) =>
    new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 2
    }).format(amount || 0)

  if (loading) {
    return <main className="main-content"><p>Loading reports...</p></main>
  }

  return (
    <main className="main-content">
      <header className="topbar">
        <div>
          <span className="breadcrumb">Workspace / Business Insights</span>
          <h1>Reports & Analytics</h1>
        </div>
        <button className="date-button" onClick={loadReports}>↻ Refresh</button>
      </header>

      {error && <p className="error-message">{error}</p>}

      {inventory && sales && (
        <section className="reports-page">
          <div className="reports-heading">
            <div>
              <h2>Business Overview</h2>
              <p>Your inventory and sales performance at a glance.</p>
            </div>
            <button className="primary-button" onClick={() => window.print()}>
              Print Report
            </button>
          </div>

          <div className="reports-grid">
            <div className="stat-card">
              <span>Total Revenue</span>
              <h2>{currency(sales.revenue)}</h2>
              <p className="muted">Revenue from completed sales</p>
            </div>
            <div className="stat-card">
              <span>Gross Profit</span>
              <h2>{currency(sales.grossProfitBeforeTaxAndDiscount)}</h2>
              <p className="muted">Before tax and discounts</p>
            </div>
            <div className="stat-card">
              <span>Total Sales</span>
              <h2>{sales.totalSales}</h2>
              <p className="muted">Completed transactions</p>
            </div>
            <div className="stat-card">
              <span>Total Products</span>
              <h2>{inventory.totalProducts}</h2>
              <p className="muted">Products in inventory</p>
            </div>
          </div>

          <div className="reports-grid">
            <div className="panel report-card">
              <h3>Inventory Valuation</h3>
              <div className="report-line">
                <span>Total Stock Units</span>
                <strong>{inventory.totalUnits}</strong>
              </div>
              <div className="report-line">
                <span>Stock Cost Value</span>
                <strong>{currency(inventory.stockCostValue)}</strong>
              </div>
              <div className="report-line">
                <span>Stock Selling Value</span>
                <strong>{currency(inventory.stockSellingValue)}</strong>
              </div>
              <div className="report-line">
                <span>Low Stock Products</span>
                <strong className="warning-text">{inventory.lowStockCount}</strong>
              </div>
            </div>

            <div className="panel report-card">
              <h3>Sales Summary</h3>
              <div className="report-line">
                <span>Total Revenue</span>
                <strong>{currency(sales.revenue)}</strong>
              </div>
              <div className="report-line">
                <span>Tax Collected</span>
                <strong>{currency(sales.taxCollected)}</strong>
              </div>
              <div className="report-line">
                <span>Total Discounts</span>
                <strong>{currency(sales.discounts)}</strong>
              </div>
              <div className="report-line">
                <span>Gross Profit</span>
                <strong className="profit-text">
                  {currency(sales.grossProfitBeforeTaxAndDiscount)}
                </strong>
              </div>
            </div>
          </div>

          <div className="panel report-table">
            <div className="panel-heading">
              <div>
                <h3>Inventory Stock Report</h3>
                <p>Product-wise stock valuation</p>
              </div>
            </div>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>PRODUCT</th>
                    <th>SKU</th>
                    <th>QUANTITY</th>
                    <th>COST PRICE</th>
                    <th>SELLING PRICE</th>
                    <th>STOCK VALUE</th>
                  </tr>
                </thead>
                <tbody>
                  {inventory.products?.map(product => (
                    <tr key={product.id}>
                      <td><strong>{product.name}</strong></td>
                      <td>{product.sku}</td>
                      <td>{product.quantity}</td>
                      <td>{currency(product.costPrice)}</td>
                      <td>{currency(product.sellingPrice)}</td>
                      <td>{currency(product.quantity * product.costPrice)}</td>
                    </tr>
                  ))}
                  {!inventory.products?.length && (
                    <tr><td colSpan={6}>No inventory data available.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </section>
      )}
    </main>
  )
}

export default Reports
