
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

function Billing() {
  const [products, setProducts] = useState<Product[]>([])
  const [items, setItems] = useState<BillLine[]>([])
  const [taxRate, setTaxRate] = useState(18)
  const [discount, setDiscount] = useState(0)
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

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
      currency: 'INR'
    }).format(amount)

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
      setMessage(`Bill #${sale.id} created successfully. Total: ${currency(sale.total)}`)
      setItems([])
      setDiscount(0)

      const updatedProducts = await fetch('/api/products').then(r => r.json())
      setProducts(updatedProducts)
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Something went wrong.')
    } finally {
      setLoading(false)
    }
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
    </main>
  )
}

export default Billing
