// Dashboard JavaScript for SpringWeb Admin
class Dashboard {
    constructor() {
        this.currentModule = 'dashboard';
        this.currentEditId = null;
        this.init();
    }

    init() {
        this.checkAuth();
        this.bindEvents();
        this.loadModule('dashboard');
    }

    checkAuth() {
        if (!localStorage.getItem('isLoggedIn')) {
            window.location.href = '/';
            return;
        }
        document.getElementById('currentUser').textContent = localStorage.getItem('username') || 'Admin';
    }

    bindEvents() {
        // Menu navigation
        document.querySelectorAll('.menu-item').forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const module = item.dataset.module;
                if (module) {
                    this.loadModule(module);
                }
            });
        });

        // Sign out
        document.getElementById('signOut').addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.removeItem('isLoggedIn');
            localStorage.removeItem('username');
            window.location.href = '/';
        });

        // Modal close
        document.querySelector('.close').addEventListener('click', () => {
            this.closeModal();
        });

        // Modal form submit
        document.getElementById('modalForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleFormSubmit();
        });
    }

    loadModule(module) {
        // Update active menu
        document.querySelectorAll('.menu-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector(`[data-module="${module}"]`)?.classList.add('active');

        this.currentModule = module;
        document.getElementById('pageTitle').textContent = this.getModuleTitle(module);

        // Load module content
        switch (module) {
            case 'dashboard':
                this.loadDashboard();
                break;
            case 'products':
                this.loadProducts();
                break;
            case 'categories':
                this.loadCategories();
                break;
            case 'suppliers':
                this.loadSuppliers();
                break;
            case 'orders':
                this.loadOrders();
                break;
            case 'transactions':
                this.loadTransactions();
                break;
            case 'reports':
                this.loadReports();
                break;
            case 'settings':
                this.loadSettings();
                break;
        }
    }

    getModuleTitle(module) {
        const titles = {
            dashboard: 'Dashboard',
            products: 'Products',
            categories: 'Categories',
            suppliers: 'Suppliers',
            orders: 'Orders',
            transactions: 'Transactions',
            reports: 'Reports',
            settings: 'Settings'
        };
        return titles[module] || 'Dashboard';
    }

    loadDashboard() {
        const content = `
            <div class="dashboard-stats">
                <div class="stat-card">
                    <div class="stat-number" id="totalProducts">0</div>
                    <div class="stat-label">Total Products</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="totalOrders">0</div>
                    <div class="stat-label">Total Orders</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="totalCategories">0</div>
                    <div class="stat-label">Categories</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="totalSuppliers">0</div>
                    <div class="stat-label">Suppliers</div>
                </div>
            </div>
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Recent Activities</h3>
                </div>
                <div class="card-body">
                    <p>Welcome to SpringWeb Admin Dashboard. Use the sidebar to navigate between modules.</p>
                </div>
            </div>
        `;
        document.getElementById('contentArea').innerHTML = content;
        this.loadDashboardStats();
    }

    async loadDashboardStats() {
        try {
            const [products, orders, categories, suppliers] = await Promise.all([
                fetch('/api/products').then(r => r.json()),
                fetch('/api/orders').then(r => r.json()),
                fetch('/api/categories').then(r => r.json()),
                fetch('/api/suppliers').then(r => r.json())
            ]);

            document.getElementById('totalProducts').textContent = products.length;
            document.getElementById('totalOrders').textContent = orders.length;
            document.getElementById('totalCategories').textContent = categories.length;
            document.getElementById('totalSuppliers').textContent = suppliers.length;
        } catch (error) {
            console.error('Error loading dashboard stats:', error);
        }
    }

    loadProducts() {
        this.loadCrudModule('products', [
            { key: 'name', label: 'Name', type: 'text', required: true },
            { key: 'description', label: 'Description', type: 'textarea' },
            { key: 'price', label: 'Price', type: 'number', required: true },
            { key: 'stock', label: 'Stock', type: 'number', required: true }
        ]);
    }

    loadCategories() {
        this.loadCrudModule('categories', [
            { key: 'name', label: 'Name', type: 'text', required: true },
            { key: 'description', label: 'Description', type: 'textarea' }
        ]);
    }

    loadSuppliers() {
        this.loadCrudModule('suppliers', [
            { key: 'name', label: 'Name', type: 'text', required: true },
            { key: 'contactPerson', label: 'Contact Person', type: 'text' },
            { key: 'email', label: 'Email', type: 'email' },
            { key: 'phone', label: 'Phone', type: 'tel' },
            { key: 'address', label: 'Address', type: 'textarea' }
        ]);
    }

    loadOrders() {
        this.loadCrudModule('orders', [
            { key: 'orderNumber', label: 'Order Number', type: 'text', required: true },
            { key: 'customerName', label: 'Customer Name', type: 'text', required: true },
            { key: 'customerEmail', label: 'Customer Email', type: 'email' },
            { key: 'totalAmount', label: 'Total Amount', type: 'number', required: true },
            { key: 'status', label: 'Status', type: 'select', options: ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'] }
        ]);
    }

    loadTransactions() {
        this.loadCrudModule('transactions', [
            { key: 'transactionNumber', label: 'Transaction Number', type: 'text', required: true },
            { key: 'amount', label: 'Amount', type: 'number', required: true },
            { key: 'type', label: 'Type', type: 'select', options: ['PAYMENT', 'REFUND', 'ADJUSTMENT'] },
            { key: 'paymentMethod', label: 'Payment Method', type: 'select', options: ['CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'BANK_TRANSFER', 'PAYPAL'] },
            { key: 'status', label: 'Status', type: 'select', options: ['PENDING', 'COMPLETED', 'FAILED', 'CANCELLED'] },
            { key: 'description', label: 'Description', type: 'textarea' }
        ]);
    }

    loadReports() {
        const content = `
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Reports</h3>
                </div>
                <div class="card-body">
                    <div class="dashboard-stats">
                        <div class="stat-card">
                            <button class="btn btn-primary" onclick="dashboard.generateReport('sales')">
                                <i class="fas fa-chart-line"></i> Sales Report
                            </button>
                        </div>
                        <div class="stat-card">
                            <button class="btn btn-success" onclick="dashboard.generateReport('inventory')">
                                <i class="fas fa-boxes"></i> Inventory Report
                            </button>
                        </div>
                        <div class="stat-card">
                            <button class="btn btn-warning" onclick="dashboard.generateReport('customers')">
                                <i class="fas fa-users"></i> Customer Report
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        document.getElementById('contentArea').innerHTML = content;
    }

    loadSettings() {
        const content = `
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Application Settings</h3>
                </div>
                <div class="card-body">
                    <form>
                        <div class="form-row">
                            <div class="form-col">
                                <label>Application Name</label>
                                <input type="text" class="form-control" value="SpringWeb Admin">
                            </div>
                            <div class="form-col">
                                <label>Theme</label>
                                <select class="form-control">
                                    <option>Default</option>
                                    <option>Dark</option>
                                    <option>Light</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-col">
                                <label>Items per page</label>
                                <select class="form-control">
                                    <option>10</option>
                                    <option>25</option>
                                    <option>50</option>
                                </select>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary">Save Settings</button>
                    </form>
                </div>
            </div>
        `;
        document.getElementById('contentArea').innerHTML = content;
    }

    async loadCrudModule(module, fields) {
        const content = `
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">${this.getModuleTitle(module)}</h3>
                    <button class="btn btn-primary" onclick="dashboard.openCreateModal('${module}')">
                        <i class="fas fa-plus"></i> Add ${module.slice(0, -1)}
                    </button>
                </div>
                <div class="card-body">
                    <div class="search-filter">
                        <input type="text" class="search-input" placeholder="Search..." onkeyup="dashboard.filterTable(this.value)">
                        <button class="btn btn-secondary" onclick="dashboard.loadData('${module}')">
                            <i class="fas fa-refresh"></i> Refresh
                        </button>
                    </div>
                    <div class="table-container">
                        <table class="table" id="dataTable">
                            <thead>
                                <tr>
                                    ${fields.map(field => `<th>${field.label}</th>`).join('')}
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="tableBody">
                                <tr><td colspan="${fields.length + 1}">Loading...</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;
        document.getElementById('contentArea').innerHTML = content;
        this.currentFields = fields;
        await this.loadData(module);
    }

    async loadData(module) {
        try {
            const response = await fetch(`/api/${module}`);
            const data = await response.json();
            this.renderTable(data, module);
        } catch (error) {
            console.error(`Error loading ${module}:`, error);
            document.getElementById('tableBody').innerHTML = `<tr><td colspan="100%">Error loading data</td></tr>`;
        }
    }

    renderTable(data, module) {
        const tbody = document.getElementById('tableBody');
        if (data.length === 0) {
            tbody.innerHTML = `<tr><td colspan="100%">No data found</td></tr>`;
            return;
        }

        tbody.innerHTML = data.map(item => `
            <tr>
                ${this.currentFields.map(field => `<td>${item[field.key] || ''}</td>`).join('')}
                <td>
                    <button class="btn btn-warning btn-sm" onclick="dashboard.openEditModal('${module}', ${item.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="dashboard.deleteItem('${module}', ${item.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    }

    openCreateModal(module) {
        this.currentEditId = null;
        document.getElementById('modalTitle').textContent = `Add ${module.slice(0, -1)}`;
        this.buildForm();
        document.getElementById('modal').style.display = 'block';
    }

    async openEditModal(module, id) {
        this.currentEditId = id;
        document.getElementById('modalTitle').textContent = `Edit ${module.slice(0, -1)}`;
        
        try {
            const response = await fetch(`/api/${module}/${id}`);
            const data = await response.json();
            this.buildForm(data);
            document.getElementById('modal').style.display = 'block';
        } catch (error) {
            console.error('Error loading item:', error);
            alert('Error loading item for editing');
        }
    }

    buildForm(data = {}) {
        const formFields = document.getElementById('formFields');
        formFields.innerHTML = this.currentFields.map(field => {
            let input = '';
            const value = data[field.key] || '';
            
            switch (field.type) {
                case 'textarea':
                    input = `<textarea class="form-control" name="${field.key}" ${field.required ? 'required' : ''}>${value}</textarea>`;
                    break;
                case 'select':
                    input = `<select class="form-control" name="${field.key}" ${field.required ? 'required' : ''}>
                        <option value="">Select...</option>
                        ${field.options.map(opt => `<option value="${opt}" ${value === opt ? 'selected' : ''}>${opt}</option>`).join('')}
                    </select>`;
                    break;
                default:
                    input = `<input type="${field.type}" class="form-control" name="${field.key}" value="${value}" ${field.required ? 'required' : ''}>`;
            }
            
            return `
                <div class="form-group">
                    <label>${field.label}</label>
                    ${input}
                </div>
            `;
        }).join('');
    }

    async handleFormSubmit() {
        const formData = new FormData(document.getElementById('modalForm'));
        const data = Object.fromEntries(formData);
        
        try {
            const url = this.currentEditId ? 
                `/api/${this.currentModule}/${this.currentEditId}` : 
                `/api/${this.currentModule}`;
            
            const method = this.currentEditId ? 'PUT' : 'POST';
            
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            
            if (response.ok) {
                this.closeModal();
                await this.loadData(this.currentModule);
                alert(`${this.currentModule.slice(0, -1)} ${this.currentEditId ? 'updated' : 'created'} successfully!`);
            } else {
                alert('Error saving data');
            }
        } catch (error) {
            console.error('Error saving:', error);
            alert('Error saving data');
        }
    }

    async deleteItem(module, id) {
        if (!confirm('Are you sure you want to delete this item?')) return;
        
        try {
            const response = await fetch(`/api/${module}/${id}`, { method: 'DELETE' });
            if (response.ok) {
                await this.loadData(module);
                alert('Item deleted successfully!');
            } else {
                alert('Error deleting item');
            }
        } catch (error) {
            console.error('Error deleting:', error);
            alert('Error deleting item');
        }
    }

    closeModal() {
        document.getElementById('modal').style.display = 'none';
        document.getElementById('modalForm').reset();
    }

    filterTable(searchTerm) {
        const rows = document.querySelectorAll('#tableBody tr');
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(searchTerm.toLowerCase()) ? '' : 'none';
        });
    }

    generateReport(type) {
        alert(`Generating ${type} report... (Feature coming soon)`);
    }
}

// Initialize dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.dashboard = new Dashboard();
});

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('modal');
    if (event.target === modal) {
        dashboard.closeModal();
    }
}
