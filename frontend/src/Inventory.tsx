
import { useEffect, useState } from 'react'

interface Product {
  id: number
  name: string
  sku: string
  category: string
  costPrice: number
  sellingPrice: number
  quantity: number
  lowStockLevel: number
}

const emptyProduct = {
  name: '',
  sku: '',
  category: '',
  costPrice: 0,
  sellingPrice: 0,
  quantity: 0,
  lowStockLevel: 5
}

function Inventory() {
  const [products, setProducts] = useState<Product[]>([])
  const [search, setSearch] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [form, setForm] = useState(emptyProduct)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function loadProducts() {
    try {
      setLoading(true)
      const response = await fetch('/api/products')

      if (!response.ok) throw new Error('Could not load products.')

      const data = await response.json()
      setProducts(data)
      setError('')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Something went wrong.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadProducts()
  }, [])

  function openAddForm() {
    setEditingId(null)
    setForm(emptyProduct)
    setShowForm(true)
  }

  function openEditForm(product: Product) {
    setEditingId(product.id)
    setForm({
      name: product.name,
      sku: product.sku,
      category: product.category,
      costPrice: product.costPrice,
      sellingPrice: product.sellingPrice,
      quantity: product.quantity,
      lowStockLevel: product.lowStockLevel
    })
    setShowForm(true)
  }

  async function saveProduct(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      const url = editingId
        ? `/api/products/${editingId}`
        : '/api/products'

      const response = await fetch(url, {
        method: editingId ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      })

      if (!response.ok) {
        const message = await response.text()
        throw new Error(message || 'Unable to save product.')
      }

      setShowForm(false)
      setEditingId(null)
      setForm(emptyProduct)
      await loadProducts()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Something went wrong.')
    }
  }

  async function deleteProduct(id: number) {
    if (!window.confirm('Are you sure you want to delete this product?')) return

    try {
      const response = await fetch(`/api/products/${id}`, {
        method: 'DELETE'
      })

      if (!response.ok) throw new Error('Unable to delete product.')

      await loadProducts()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Something went wrong.')
    }
  }

  const filteredProducts = products.filter(product =>
    `${product.name} ${product.sku} ${product.category}`
      .toLowerCase()
      .includes(search.toLowerCase())
  )

  const currency = (amount: number) =>
    new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 2
    }).format(amount || 0)

  return (
    <main className="main-content">
      <header className="topbar">
        <div>
          <span className="breadcrumb">Workspace / Management</span>
          <h1>Inventory</h1>
        </div>
        <button className="primary-button" onClick={openAddForm}>
          + Add Product
        </button>
      </header>

      <section className="inventory-page">
        <div className="inventory-toolbar">
          <div>
            <h2>Product Inventory</h2>
            <p>Manage and monitor all your products.</p>
          </div>
          <button className="date-button" onClick={loadProducts}>
            ↻ Refresh
          </button>
        </div>

        <div className="inventory-search">
          <input
            type="text"
            placeholder="Search by product name, SKU or category..."
            value={search}
            onChange={event => setSearch(event.target.value)}
          />
          <span>{filteredProducts.length} products</span>
        </div>

        {error && <p className="error-message">{error}</p>}
        {loading && <p>Loading products...</p>}

        {!loading && (
          <div className="panel inventory-table-panel">
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>PRODUCT</th>
                    <th>SKU</th>
                    <th>CATEGORY</th>
                    <th>COST PRICE</th>
                    <th>SELLING PRICE</th>
                    <th>QUANTITY</th>
                    <th>STATUS</th>
                    <th>ACTIONS</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredProducts.map(product => (
                    <tr key={product.id}>
                      <td><strong>{product.name}</strong></td>
                      <td>{product.sku}</td>
                      <td>{product.category}</td>
                      <td>{currency(product.costPrice)}</td>
                      <td>{currency(product.sellingPrice)}</td>
                      <td>{product.quantity}</td>
                      <td>
                        <span className={
                          product.quantity <= product.lowStockLevel
                            ? 'stock-low'
                            : 'stock-available'
                        }>
                          {product.quantity <= product.lowStockLevel
                            ? 'Low Stock'
                            : 'In Stock'}
                        </span>
                      </td>
                      <td>
                        <div className="action-buttons">
                          <button onClick={() => openEditForm(product)}>Edit</button>
                          <button onClick={() => deleteProduct(product.id)}>Delete</button>
                        </div>
                      </td>
                    </tr>
                  ))}

                  {filteredProducts.length === 0 && (
                    <tr>
                      <td colSpan={8}>No products found.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </section>

      {showForm && (
        <div className="modal-overlay">
          <form className="product-form" onSubmit={saveProduct}>
            <div className="form-heading">
              <h2>{editingId ? 'Edit Product' : 'Add New Product'}</h2>
              <button type="button" onClick={() => setShowForm(false)}>✕</button>
            </div>

            <label>Product Name</label>
            <input required value={form.name}
              onChange={e => setForm({ ...form, name: e.target.value })} />

            <label>SKU</label>
            <input required value={form.sku}
              onChange={e => setForm({ ...form, sku: e.target.value })} />

            <label>Category</label>
            <input required value={form.category}
              onChange={e => setForm({ ...form, category: e.target.value })} />

            <div className="form-grid">
              <div>
                <label>Cost Price (₹)</label>
                <input type="number" min="0" step="0.01" required
                  value={form.costPrice}
                  onChange={e => setForm({ ...form, costPrice: Number(e.target.value) })} />
              </div>
              <div>
                <label>Selling Price (₹)</label>
                <input type="number" min="0" step="0.01" required
                  value={form.sellingPrice}
                  onChange={e => setForm({ ...form, sellingPrice: Number(e.target.value) })} />
              </div>
              <div>
                <label>Quantity</label>
                <input type="number" min="0" required
                  value={form.quantity}
                  onChange={e => setForm({ ...form, quantity: Number(e.target.value) })} />
              </div>
              <div>
                <label>Low Stock Level</label>
                <input type="number" min="0" required
                  value={form.lowStockLevel}
                  onChange={e => setForm({ ...form, lowStockLevel: Number(e.target.value) })} />
              </div>
            </div>

            <div className="form-actions">
              <button type="button" className="date-button"
                onClick={() => setShowForm(false)}>Cancel</button>
              <button type="submit" className="primary-button">
                {editingId ? 'Update Product' : 'Save Product'}
              </button>
            </div>
          </form>
        </div>
      )}
    </main>
  )
}

export default Inventory
