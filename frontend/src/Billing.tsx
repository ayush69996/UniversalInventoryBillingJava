
import { useEffect, useState } from 'react'

interface Product {
  id: number
  name: string
  sku: string
  sellingPrice: number
  quantity: number
}

interface BillLine {
  productId: number
  quantity: number
}

interface InvoiceItem {
  productName: string
  sku: string
  price: number
  quantity: number
  amount: number
}

interface Invoice {
  id: number
  date: string
  items: InvoiceItem[]
  subtotal: number
  discount: number
  taxRate: number
  taxAmount: number
  total: number
}

function Billing() {
  const [products, setProducts] = useState<Product[]>([])
  const [items, setItems] = useState<BillLine[]>([])
  const [taxRate, setTaxRate] = useState(18)
  const [discount, setDiscount] = useState(0)
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const [invoice, setInvoice] = useState<Invoice | null>(null)

  useEffect(() => {
    fetch('/api/products')
      .then(response => {
        if (!response.ok) throw new Error('Unable to load products')
        return response.json()
      })
      .then(setProducts)
      .catch(() => setMessage('Could not load products. Check the backend.'))
  }, [])

  const addItem = () => {
    setItems([...items, { productId: 0, quantity: 1 }])
  }

  const updateItem = (index: number, field: keyof BillLine, value: number) => {
    setItems(current => current.map((item, i) =>
      i === index ? { ...item, [field]: value } : item
    ))
  }

  const selectedProducts = items.map(item => ({
    ...item,
    product: products.find(product => product.id === item.productId)
  }))

  const subtotal = selectedProducts.reduce(
    (sum, item) => sum + (item.product?.sellingPrice || 0) * item.quantity, 0
  )

  const taxableAmount = Math.max(0, subtotal - discount)
  const taxAmount = taxableAmount * taxRate / 100
  const total = taxableAmount + taxAmount

  const currency = (amount: number) =>
    new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2
    }).format(amount || 0)

  async function createBill() {
    setMessage('')

    if (!items.length || items.some(item => !item.productId || item.quantity < 1)) {
      setMessage('Please select products and enter valid quantities.')
      return
    }

    if (items.some(item => {
      const product = products.find(p => p.id === item.productId)
      return !product || item.quantity > product.quantity
    })) {
      setMessage('One or more products have insufficient stock.')
      return
    }

    if (discount < 0 || discount > subtotal || taxRate < 0) {
      setMessage('Please enter valid tax and discount values.')
      return
    }

    const invoiceItems: InvoiceItem[] = selectedProducts.map(item => ({
      productName: item.product?.name || 'Product',
      sku: item.product?.sku || '',
      price: item.product?.sellingPrice || 0,
      quantity: item.quantity,
      amount: (item.product?.sellingPrice || 0) * item.quantity
    }))

    setLoading(true)

    try {
      const response = await fetch('/api/billing', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ items, taxRate, discount })
      })

      if (!response.ok) {
        const error = await response.text()
        throw new Error(error || 'Bill creation failed.')
      }

      const sale = await response.json()

      setInvoice({
        id: sale.id,
        date: sale.date || new Date().toISOString(),
        items: invoiceItems,
        subtotal: sale.subtotal ?? subtotal,
        discount: sale.discount ?? discount,
        taxRate: sale.taxRate ?? taxRate,
        taxAmount: sale.taxAmount ?? taxAmount,
        total: sale.total
      })

      setItems([])
      setDiscount(0)

      const updatedProducts = await fetch('/api/products').then(r => {
        if (!r.ok) throw new Error('Unable to refresh products')
        return r.json()
      })

      setProducts(updatedProducts)
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Something went wrong.')
    } finally {
      setLoading(false)
    }
  }

  function printInvoice() {
    if (!invoice) return

    const printWindow = window.open('', '_blank', 'width=850,height=700')

    if (!printWindow) {
      setMessage('Please allow popups to print your invoice.')
      return
    }

    const safe = (value: string) =>
      value.replace(/[&<>"']/g, char => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
      })[char] || char)

    const rows = invoice.items.map(item => `
      <tr>
        <td>${safe(item.productName)}</td>
        <td>${safe(item.sku)}</td>
        <td>${item.quantity}</td>
        <td>${currency(item.price)}</td>
        <td>${currency(item.amount)}</td>
      </tr>
    `).join('')

    printWindow.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>Invoice #${invoice.id}</title>
        <style>
          body { font-family: Arial, sans-serif; padding: 40px; color: #222; }
          h1 { margin-bottom: 5px; }
          .muted { color: #666; }
          table { width: 100%; border-collapse: collapse; margin-top: 30px; }
          th, td { padding: 12px; border-bottom: 1px solid #ddd; text-align: left; }
          th { background: #f3f3f3; }
          .totals { width: 320px; margin: 25px 0 0 auto; }
          .line { display: flex; justify-content: space-between; padding: 8px; }
          .grand { font-size: 20px; font-weight: bold; border-top: 2px solid #222; }
          @media print { body { padding: 10px; } }
        </style>
      </head>
      <body>
        <h1>Universal Inventory & Billing System</h1>
        <p class="muted">CUSTOMER INVOICE</p>
        <hr>
        <p><strong>Invoice Number:</strong> #${invoice.id}</p>
        <p><strong>Date:</strong> ${new Date(invoice.date).toLocaleString('en-IN')}</p>
        <table>
          <thead>
            <tr><th>Product</th><th>SKU</th><th>Quantity</th><th>Price</th><th>Amount</th></tr>
          </thead>
          <tbody>${rows}</tbody>
        </table>
        <div class="totals">
          <div class="line"><span>Subtotal</span><strong>${currency(invoice.subtotal)}</strong></div>
          <div class="line"><span>Discount</span><strong>${currency(invoice.discount)}</strong></div>
          <div class="line"><span>Tax (${invoice.taxRate}%)</span><strong>${currency(invoice.taxAmount)}</strong></div>
          <div class="line grand"><span>Grand Total</span><strong>${currency(invoice.total)}</strong></div>
        </div>
        <p style="text-align:center;margin-top:60px;">Thank you for your business!</p>
        <script>window.onload = () => window.print();<\/script>
      </body>
      </html>
    `)

    printWindow.document.close()
  }

  return (
    <main className="main-content">
      <header className="topbar">
        <div>
          <span className="breadcrumb">Workspace / Transactions</span>
          <h1>Billing & Invoices</h1>
        </div>
      </header>

      <section className="billing-page">
        <div className="panel billing-panel">
          <div className="panel-heading">
            <div>
              <h3>Create New Bill</h3>
              <p>Select products and generate a customer invoice.</p>
            </div>
            <button className="primary-button" onClick={addItem}>+ Add Item</button>
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>PRODUCT</th>
                  <th>AVAILABLE STOCK</th>
                  <th>PRICE</th>
                  <th>QUANTITY</th>
                  <th>AMOUNT</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {selectedProducts.map((item, index) => (
                  <tr key={index}>
                    <td>
                      <select
                        value={item.productId}
                        onChange={e => updateItem(index, 'productId', Number(e.target.value))}
                      >
                        <option value={0}>Select product</option>
                        {products.map(product => (
                          <option key={product.id} value={product.id}>
                            {product.name} ({product.sku})
                          </option>
                        ))}
                      </select>
                    </td>
                    <td>{item.product?.quantity ?? '-'}</td>
                    <td>{item.product ? currency(item.product.sellingPrice) : '-'}</td>
                    <td>
                      <input
                        className="billing-quantity"
                        type="number"
                        min="1"
                        max={item.product?.quantity || 1}
                        value={item.quantity}
                        onChange={e => updateItem(index, 'quantity', Number(e.target.value))}
                      />
                    </td>
                    <td>{currency((item.product?.sellingPrice || 0) * item.quantity)}</td>
                    <td>
                      <button className="remove-item" onClick={() =>
                        setItems(items.filter((_, i) => i !== index))
                      }>✕</button>
                    </td>
                  </tr>
                ))}
                {items.length === 0 && (
                  <tr><td colSpan={6}>No items added. Click Add Item to begin.</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        <div className="panel billing-total">
          <h3>Bill Summary</h3>

          <div className="billing-field">
            <label>Tax Rate (%)</label>
            <input type="number" min="0" value={taxRate}
              onChange={e => setTaxRate(Number(e.target.value))} />
          </div>

          <div className="billing-field">
            <label>Discount (₹)</label>
            <input type="number" min="0" value={discount}
              onChange={e => setDiscount(Number(e.target.value))} />
          </div>

          <div className="summary-line"><span>Subtotal</span><strong>{currency(subtotal)}</strong></div>
          <div className="summary-line"><span>Discount</span><strong>{currency(discount)}</strong></div>
          <div className="summary-line"><span>Tax</span><strong>{currency(taxAmount)}</strong></div>
          <div className="summary-grand"><span>Grand Total</span><strong>{currency(total)}</strong></div>

          <button className="primary-button create-bill-button"
            onClick={createBill} disabled={loading || items.length === 0}>
            {loading ? 'Creating Bill...' : 'Create Bill'}
          </button>

          {message && <p className="billing-message">{message}</p>}
        </div>
      </section>

      {invoice && (
        <div className="modal-overlay" onClick={() => setInvoice(null)}>
          <div
            className="product-form"
            style={{ maxWidth: '750px', width: '95%', maxHeight: '90vh', overflowY: 'auto' }}
            onClick={e => e.stopPropagation()}
          >
            <div className="form-heading">
              <div>
                <h2>Invoice Generated!</h2>
                <p>Invoice #{invoice.id}</p>
              </div>
              <button type="button" onClick={() => setInvoice(null)}>✕</button>
            </div>

            <div style={{ textAlign: 'center', padding: '15px' }}>
              <h2 style={{ color: '#16a34a' }}>✓ Payment Bill Created</h2>
              <p>Universal Inventory & Billing System</p>
              <p>Invoice Date: {new Date(invoice.date).toLocaleString('en-IN')}</p>
            </div>

            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>PRODUCT</th>
                    <th>SKU</th>
                    <th>PRICE</th>
                    <th>QUANTITY</th>
                    <th>AMOUNT</th>
                  </tr>
                </thead>
                <tbody>
                  {invoice.items.map((item, index) => (
                    <tr key={index}>
                      <td>{item.productName}</td>
                      <td>{item.sku}</td>
                      <td>{currency(item.price)}</td>
                      <td>{item.quantity}</td>
                      <td>{currency(item.amount)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div style={{ marginTop: '20px' }}>
              <div className="summary-line"><span>Subtotal</span><strong>{currency(invoice.subtotal)}</strong></div>
              <div className="summary-line"><span>Discount</span><strong>{currency(invoice.discount)}</strong></div>
              <div className="summary-line"><span>Tax ({invoice.taxRate}%)</span><strong>{currency(invoice.taxAmount)}</strong></div>
              <div className="summary-grand"><span>Grand Total</span><strong>{currency(invoice.total)}</strong></div>
            </div>

            <p style={{ textAlign: 'center', marginTop: '20px' }}>Thank you for your business!</p>

            <div className="form-actions">
              <button className="date-button" onClick={() => setInvoice(null)}>
                Close
              </button>
              <button className="primary-button" onClick={printInvoice}>
                Print Invoice
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  )
}

export default Billing
