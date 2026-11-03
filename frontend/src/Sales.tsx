
import { useEffect, useState } from 'react'

interface SaleItem {
  id: number
  productName: string
  sku: string
  quantity: number
  sellingPrice: number
  lineTotal: number
}

interface Sale {
  id: number
  date: string
  subtotal: number
  taxRate: number
  taxAmount: number
  discount: number
  total: number
  items: SaleItem[]
}

function Sales() {
  const [sales, setSales] = useState<Sale[]>([])
  const [search, setSearch] = useState('')
  const [selectedSale, setSelectedSale] = useState<Sale | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function loadSales() {
    try {
      setLoading(true)
      const response = await fetch('/api/sales')
      if (!response.ok) throw new Error('Unable to load sales.')

      const data = await response.json()
      setSales(Array.isArray(data) ? data : [])
      setError('')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Something went wrong.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadSales()
  }, [])

  const currency = (amount: number) =>
    new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount || 0)

  const filteredSales = sales.filter(sale =>
    String(sale.id).includes(search) ||
    sale.items?.some(item =>
      item.productName?.toLowerCase().includes(search.toLowerCase())
    )
  )

  return (
    <main className="main-content">
      <header className="topbar">
        <div>
          <span className="breadcrumb">Workspace / Transactions</span>
          <h1>Sales History</h1>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button className="date-button" onClick={() => window.history.back()}>
            ← Back
          </button>
          <button className="date-button" onClick={loadSales}>
            ↻ Refresh
          </button>
        </div>
      </header>

      <section className="sales-page">
        <div className="sales-toolbar">
          <div>
            <h2>All Transactions</h2>
            <p>Review your completed sales and invoices.</p>
          </div>
          <span>{sales.length} total sales</span>
        </div>

        <input
          className="sales-search"
          placeholder="Search by sale ID or product name..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />

        {error && <p className="error-message">{error}</p>}
        {loading && <p>Loading sales...</p>}

        {!loading && (
          <div className="panel sales-table-panel">
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>SALE ID</th>
                    <th>DATE</th>
                    <th>PRODUCTS</th>
                    <th>ITEMS</th>
                    <th>SUBTOTAL</th>
                    <th>TAX</th>
                    <th>TOTAL</th>
                    <th>DETAILS</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredSales.map(sale => (
                    <tr key={sale.id}>
                      <td><strong>#{sale.id}</strong></td>
                      <td>{sale.date ? new Date(sale.date).toLocaleDateString('en-IN') : '-'}</td>
                      <td>{sale.items?.map(item => item.productName).join(', ') || '-'}</td>
                      <td>{sale.items?.reduce((sum, item) => sum + item.quantity, 0) || 0}</td>
                      <td>{currency(sale.subtotal)}</td>
                      <td>{currency(sale.taxAmount)}</td>
                      <td><strong>{currency(sale.total)}</strong></td>
                      <td>
                        <button className="sale-view-button"
                          onClick={() => setSelectedSale(sale)}>
                          View
                        </button>
                      </td>
                    </tr>
                  ))}
                  {filteredSales.length === 0 && (
                    <tr><td colSpan={8}>No sales found.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </section>

      {selectedSale && (
        <div className="modal-overlay">
          <div className="product-form sale-details">
            <div className="form-heading">
              <h2>Sale #{selectedSale.id}</h2>
              <button onClick={() => setSelectedSale(null)}>✕</button>
            </div>

            <p className="sale-date">
              {selectedSale.date
                ? new Date(selectedSale.date).toLocaleString('en-IN')
                : 'Date unavailable'}
            </p>

            <h3>Purchased Items</h3>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr><th>PRODUCT</th><th>QTY</th><th>AMOUNT</th></tr>
                </thead>
                <tbody>
                  {selectedSale.items?.map(item => (
                    <tr key={item.id}>
                      <td>{item.productName}</td>
                      <td>{item.quantity}</td>
                      <td>{currency(item.lineTotal)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="summary-line">
              <span>Subtotal</span>
              <strong>{currency(selectedSale.subtotal)}</strong>
            </div>
            <div className="summary-line">
              <span>Discount</span>
              <strong>{currency(selectedSale.discount)}</strong>
            </div>
            <div className="summary-line">
              <span>Tax</span>
              <strong>{currency(selectedSale.taxAmount)}</strong>
            </div>
            <div className="summary-grand">
              <span>Grand Total</span>
              <strong>{currency(selectedSale.total)}</strong>
            </div>

            <button className="primary-button" onClick={() => window.print()}>
              Print Sale
            </button>
          </div>
        </div>
      )}
    </main>
  )
}

export default Sales
