
// dashboard.js - versión corregida y lista para pegar
import { API_BASE_URL } from "./config.js";

class Dashboard {

    constructor() {
        this.currentModule = 'dashboard';
        this.currentEditId = null;
        this.currentFields = [];
        this.pendingInboundItems = []; // <-- lista temporal de items para la orden
        // Aquí declaramos qué métodos espera el frontend que soporte el backend.
        // Ajusta cuando expandas backend (por ejemplo, si añades GET /api/products pon products.get = true)
        this.backendSupports = {
            products: { get: false, post: true, put: false, delete: false },
            categories: { get: true }, // <-- ACTIVADO para permitir listar categorías
            suppliers: { post: true },
            orders: { get: false },
            transactions: { get: false },
            inboundOrders: { post: true } // <-- nuevo soporte para crear inbound orders
        };
        this.init();
    }

    init() {
        this.checkAuth();
        this.bindEvents();
        this.loadModule('dashboard');
    }

    checkAuth() {
        if (!localStorage.getItem('isLoggedIn')) {
            window.location.href = '/pages/login.html';
            return;
        }
        const cu = document.getElementById('currentUser');
        if (cu) cu.textContent = localStorage.getItem('username') || 'Admin';
    }

    bindEvents() {
        // Menu navigation
        document.querySelectorAll('.menu-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const module = item.dataset.module;

                // Si NO tiene data-module (ejemplo: Suppliers como link normal), deja que el link funcione
                if (!module) return;

                // Bloqueamos la navegación normal
                e.preventDefault();

                // Quitar la clase 'active' de todos
                document.querySelectorAll('.menu-item').forEach(el => el.classList.remove('active'));
                item.classList.add('active');

                // Cargar módulo SPA normalmente
                this.loadModule(module);
            });
        });

        // Sign out
        const signOutEl = document.getElementById('signOut');
        if (signOutEl) {
            signOutEl.addEventListener('click', (e) => {
                e.preventDefault();
                localStorage.removeItem('isLoggedIn');
                localStorage.removeItem('username');
                window.location.href = '/pages/login.html';
            });
        }

        // Modal close
        const closeEl = document.querySelector('.close');
        if (closeEl) closeEl.addEventListener('click', () => this.closeModal());

        // Modal form submit
        const modalForm = document.getElementById('modalForm');
        if (modalForm) modalForm.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleFormSubmit();
        });

        // Cerrar modal al click fuera (si el modal existe)
        window.onclick = (event) => {
            const modal = document.getElementById('modal');
            if (event.target === modal) {
                if (window.dashboard) window.dashboard.closeModal();
            }
        };
    }

    supports(module, method) {
        return !!(this.backendSupports[module] && this.backendSupports[module][method]);
    }

    loadModule(module) {
        document.querySelectorAll('.menu-item').forEach(item => item.classList.remove('active'));
        document.querySelector(`[data-module="${module}"]`)?.classList.add('active');

        this.currentModule = module;
        const pageTitle = document.getElementById('pageTitle');
        if (pageTitle) pageTitle.textContent = this.getModuleTitle(module);

        switch (module) {
            case 'dashboard':
                this.loadDashboard();
                break;
            case 'products':
                this.loadProducts();
                break;
            case 'categories':
                this.loadCategories(); // ahora la que INYECTA la vista
                break;
            case 'suppliers':
                this.loadSuppliersPage();
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
            case 'inboundOrders':
                this.loadInboundOrders();
                break;
            default:
                this.loadDashboard();
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
            <div class="card">
                <div class="card-header"><h3 class="card-title">Dashboard</h3></div>
                <div class="card-body">
                    <p>Estado actual: el frontend está configurado para usar únicamente <code>POST ${API_BASE_URL}/products</code> (creación).</p>
                    <p>Cuando añadas endpoints GET/PUT/DELETE en el backend el dashboard mostrará estadísticas y listados automáticamente.</p>
                </div>
            </div>
        `;
        const contentArea = document.getElementById('contentArea');
        if (contentArea) contentArea.innerHTML = content;
    }

    loadProducts() {
        // Ahora usamos inputs (incluyendo categoryId/supplierId como number) para no depender de GET
        this.loadCrudModule('products', [
            { key: 'name', label: 'Name', type: 'text', required: true },
            { key: 'description', label: 'Description', type: 'textarea' },
            { key: 'price', label: 'Price', type: 'number', required: true },
            { key: 'stock', label: 'Stock', type: 'number', required: true },
            { key: 'categoryId', label: 'Category', type: 'select', required: true },
            { key: 'supplierId', label: 'Supplier ID', type: 'select', required: true }
        ]);
    }

    // Si prefieres una página Suppliers dentro del SPA
    loadSuppliersPage() {
        const content = `<div class="card"><div class="card-header"><h3>Suppliers</h3></div><div class="card-body"><p>Página de proveedores.</p></div></div>`;
        const contentArea = document.getElementById('contentArea');
        if (contentArea) contentArea.innerHTML = content;
    }

    loadOrders() {
        const content = `<div class="card"><div class="card-header"><h3>Orders</h3></div><div class="card-body"><p>Endpoint GET /api/orders no disponible en backend (por ahora).</p></div></div>`;
        const contentArea = document.getElementById('contentArea');
        if (contentArea) contentArea.innerHTML = content;
    }

    loadTransactions() {
        const content = `<div class="card"><div class="card-header"><h3>Transactions</h3></div><div class="card-body"><p>Endpoint GET /api/transactions no disponible en backend (por ahora).</p></div></div>`;
        const contentArea = document.getElementById('contentArea');
        if (contentArea) contentArea.innerHTML = content;
    }

    loadReports() {
        const content = `<div class="card"><div class="card-header"><h3>Reports</h3></div><div class="card-body"><p>Reports (no hay endpoints aún).</p></div></div>`;
        const contentArea = document.getElementById('contentArea');
        if (contentArea) contentArea.innerHTML = content;
    }

    loadSettings() {
        const content = `<div class="card"><div class="card-header"><h3>Settings</h3></div><div class="card-body"><p>Configuración de la app.</p></div></div>`;
        const contentArea = document.getElementById('contentArea');
        if (contentArea) contentArea.innerHTML = content;
    }

    /* -------------------------
       LOAD CATEGORIES (PAGE)
       ------------------------- */
    // Esta función inyecta la vista de categorías y obtiene los datos (incluyendo products)
    async loadCategories() {
        const content = `
          <div class="card">
            <div class="card-header"><h3>Listado de Categorías</h3></div>
            <div class="card-body">
                <div id="categories-container">Cargando categorías...</div>
            </div>
          </div>
        `;
        const contentArea = document.getElementById('contentArea');
        if (!contentArea) return console.warn('loadCategories: no existe contentArea en el DOM');
        contentArea.innerHTML = content;

        const container = document.getElementById('categories-container');
        if (!container) return console.warn('loadCategories: no existe categories-container');

        try {
            console.log('GET', `${API_BASE_URL}/categories/showAllCategorys`);
            const r = await fetch(`${API_BASE_URL}/categories/showAllCategorys`);
            console.log('loadCategories response status', r.status);
            if (!r.ok) {
                console.warn('loadCategories: respuesta no OK', r.status);
                container.innerText = 'Error cargando categorías';
                return;
            }
            const data = await r.json();
            console.log('loadCategories payload', data);

            if (!Array.isArray(data) || data.length === 0) {
                container.innerHTML = '<p>No hay categorías registradas.</p>';
                return;
            }

            container.innerHTML = ''; // limpiar

            data.forEach(cat => {
                const card = document.createElement('div');
                card.className = 'category-card';
                card.style.border = '1px solid #e0e0e0';
                card.style.padding = '12px';
                card.style.marginBottom = '10px';
                card.innerHTML = `
                    <h4 style="margin:0 0 6px 0;">${cat.name || '—'}</h4>
                    <p style="margin:0;"><strong>ID:</strong> ${cat.id ?? '—'}</p>
                    <p style="margin:6px 0;"><strong>Descripción:</strong> ${cat.description || '—'}</p>
                `;

                if (Array.isArray(cat.products) && cat.products.length > 0) {
                    const ul = document.createElement('ul');
                    ul.style.marginTop = '8px';
                    cat.products.forEach(p => {
                        const li = document.createElement('li');
                        li.textContent = `${p.name ?? '—'} (ID: ${p.id ?? '—'})`;
                        ul.appendChild(li);
                    });
                    card.appendChild(ul);
                } else {
                    const p = document.createElement('p');
                    p.style.fontStyle = 'italic';
                    p.textContent = 'Sin productos registrados.';
                    card.appendChild(p);
                }

                container.appendChild(card);
            });

        } catch (err) {
            console.error('Error en loadCategories:', err);
            container.innerText = 'Error al cargar categorías.';
        }
    }

    /* -------------------------
       populateCategoriesSelect(selectElement, selectedId)
       ------------------------- */
    // Rellena selects con categorías (para formularios)
    async populateCategoriesSelect(selectElement, selectedId = null) {
        if (!selectElement) return console.warn('populateCategoriesSelect: selectElement no proporcionado');

        try {
            console.log('GET', `${API_BASE_URL}/categories/showCategorysByNames`);
            const r = await fetch(`${API_BASE_URL}/categories/showCategorysByNames`);
            if (!r.ok) {
                console.warn('populateCategoriesSelect: respuesta no OK', r.status);
                selectElement.innerHTML = '<option value="">-- No hay categorías --</option>';
                return;
            }

            const categories = await r.json();
            selectElement.innerHTML = '<option value="">-- Selecciona una categoría --</option>';

            categories.forEach(cat => {
                const option = document.createElement("option");
                option.value = cat.id;
                option.textContent = cat.name;
                if (selectedId !== null && String(selectedId) === String(cat.id)) {
                    option.selected = true;
                }
                selectElement.appendChild(option);
            });
        } catch (error) {
            console.error("Error cargando categorías (populateCategoriesSelect):", error);
            selectElement.innerHTML = '<option value="">-- Error cargando categorías --</option>';
        }
    }

    /* -------------------------
       Suppliers & Products loaders (for selects / inbound module)
       ------------------------- */
    async loadSuppliers(selectElement, selectedId = null) {
        if (!selectElement) return console.warn('loadSuppliers: selectElement no proporcionado');

        try {
            const r = await fetch(`${API_BASE_URL}/suppliers/showSuppliersByName`);
            if (!r.ok) {
                throw new Error('respuesta no OK ' + r.status);
            }
            const suppliers = await r.json();
            selectElement.innerHTML = '<option value="">-- Selecciona un Proveedor --</option>';
            suppliers.forEach(s => {
                const opt = document.createElement('option');
                opt.value = s.id;
                opt.textContent = s.name;
                if (selectedId !== null && String(selectedId) === String(s.id)) opt.selected = true;
                selectElement.appendChild(opt);
            });
        } catch (err) {
            console.error('loadSuppliers error', err);
            selectElement.innerHTML = '<option value="">-- No hay proveedores (error) --</option>';
        }
    }

    async loadProductsForInbound(selectElement, selectedId = null) {
        if (!selectElement) return console.warn('loadProductsForInbound: selectElement no proporcionado');

        try {
            const r = await fetch(`${API_BASE_URL}/products/showProductsByName`);
            if (!r.ok) {
                throw new Error('respuesta no OK ' + r.status);
            }
            const products = await r.json();
            selectElement.innerHTML = '<option value="">-- Selecciona los productos --</option>';
            products.forEach(p => {
                const opt = document.createElement('option');
                opt.value = p.id;
                opt.textContent = p.name;
                if (selectedId !== null && String(selectedId) === String(p.id)) opt.selected = true;
                selectElement.appendChild(opt);
            });
        } catch (err) {
            console.error('loadProductsForInbound error', err);
            selectElement.innerHTML = '<option value="">-- No hay productos (error) --</option>';
        }
    }

    /* -------------------------
       INBOUND ORDERS MODULE
       ------------------------- */
    async loadInboundOrders() {
        const content = `
          <div class="card">
            <div class="card-header"><h3 class="card-title">Crear remision entrante de almacen</h3></div>
            <div class="card-body">
              <div class="form-row">
                <div class="form-col">
                  <label>Proveedor</label>
                  <select id="inb_supplierSelect" class="form-control">
                    <option value="">Cargando proveedores...</option>
                  </select>
                </div>
                <div class="form-col">
                  <label>Estado</label>
                  <select id="inb_status" class="form-control">
                    <option value="CANCELLED">CANCELLED</option>
                    <option value="PENDING">PENDING</option>
                    <option value="RECEIVED">RECEIVED</option>
                  </select>
                </div>
              </div>

              <hr/>

              <h4>Agregar item</h4>
              <div class="form-row" style="gap:10px; align-items:end;">
                <div class="form-col">
                  <label>Producto</label>
                  <select id="inb_productSelect" class="form-control">
                    <option value="">Cargando productos...</option>
                  </select>
                </div>

                <div class="form-col">
                  <label>O productoId </label>
                  <input id="inb_productIdManual" class="form-control" placeholder="productId (opcional si seleccionas producto)">
                </div>
                <div class="form-col">
                  <label>Cantidad</label>
                  <input id="inb_quantity" type="number" min="1" class="form-control" value="1">
                </div>
                <div class="form-col" style="width:auto;">
                  <button class="btn btn-primary" id="inb_addItemBtn">Agregar item</button>
                </div>
              </div>

              <div style="margin-top:12px;">
                <h4>Items añadidos</h4>
                <table class="table" id="inb_itemsTable">
                  <thead><tr><th>Producto Id</th><th>Nombre</th><th>Cantidad</th><th>Acción</th></tr></thead>
                  <tbody><tr><td colspan="4">No hay items</td></tr></tbody>
                </table>
              </div>

              <div style="margin-top:12px;">
                <button class="btn btn-success" id="inb_createOrderBtn">Crear orden entrante</button>
                <button class="btn btn-secondary" id="inb_clearBtn">Limpiar</button>
              </div>
            </div>
          </div>
        `;
        const contentArea = document.getElementById('contentArea');
        if (!contentArea) return console.warn('loadInboundOrders: no existe contentArea');
        contentArea.innerHTML = content;

        // referencias
        const supplierSelect = document.getElementById('inb_supplierSelect');
        const productSelect = document.getElementById('inb_productSelect');
        const addItemBtn = document.getElementById('inb_addItemBtn');
        const createBtn = document.getElementById('inb_createOrderBtn');
        const clearBtn = document.getElementById('inb_clearBtn');

        // cargas iniciales
        this.loadSuppliers(supplierSelect);     // usa el método que ya tienes
        this.loadProductsForInbound(productSelect).catch(err => {
            console.warn('No se pudo cargar lista de productos, usuario podrá usar productId manual', err);
            if (productSelect) productSelect.innerHTML = '<option value="">Lista no disponible — use productId manual</option>';
        });

        // listeners
        if (addItemBtn) addItemBtn.addEventListener('click', (e) => { e.preventDefault(); this.addInboundItem(); });
        if (createBtn) createBtn.addEventListener('click', (e) => { e.preventDefault(); this.createInboundOrder(); });
        if (clearBtn) clearBtn.addEventListener('click', (e) => { e.preventDefault(); this.clearInboundForm(); });

        // reset temporal
        this.pendingInboundItems = [];
        this.renderInboundItemsList();
    }

    addInboundItem() {
        const productSelect = document.getElementById('inb_productSelect');
        const manualId = document.getElementById('inb_productIdManual')?.value.trim();
        const qty = Number(document.getElementById('inb_quantity')?.value);

        let productId = null;
        let productName = '';

        if (productSelect && productSelect.value) {
            productId = Number(productSelect.value);
            productName = productSelect.options[productSelect.selectedIndex]?.text || '';
        } else if (manualId) {
            productId = Number(manualId);
            productName = `id:${productId}`;
        } else {
            alert('Selecciona un producto o introduce productId manual.');
            return;
        }

        if (!productId || isNaN(qty) || qty <= 0) {
            alert('Cantidad inválida o productId inválido.');
            return;
        }

        // push a la lista temporal
        const newItem = { productId: productId, quantity: qty, name: productName };
        this.pendingInboundItems.push(newItem);

        console.log('Item añadido ->', newItem);
        console.log('Lista pendingInboundItems ->', this.pendingInboundItems);

        this.renderInboundItemsList();

        // reset campos del item
        if (productSelect) productSelect.value = '';
        const manualEl = document.getElementById('inb_productIdManual');
        if (manualEl) manualEl.value = '';
        const qtyEl = document.getElementById('inb_quantity');
        if (qtyEl) qtyEl.value = '1';
    }

    renderInboundItemsList() {
        const tbody = document.querySelector('#inb_itemsTable tbody');
        if (!tbody) return;
        if (!this.pendingInboundItems.length) {
            tbody.innerHTML = '<tr><td colspan="4">No hay items</td></tr>';
            return;
        }
        tbody.innerHTML = this.pendingInboundItems.map((it, idx) => `
            <tr>
              <td>${it.productId}</td>
              <td>${it.name || ''}</td>
              <td>${it.quantity}</td>
              <td><button class="btn btn-sm btn-danger" data-idx="${idx}" onclick="dashboard.removeInboundItem(${idx})">Eliminar</button></td>
            </tr>
        `).join('');
    }

    removeInboundItem(index) {
        this.pendingInboundItems.splice(index, 1);
        this.renderInboundItemsList();
    }

    async createInboundOrder() {
        try {
            // Construir el DTO
            const supplierId = document.getElementById('inb_supplierSelect')?.value;
            const status = document.getElementById('inb_status')?.value || 'PENDING';

            const dto = {
                supplierId: supplierId,
                status: status,
                items: this.pendingInboundItems.map(item => ({
                    productId: item.productId,
                    quantity: item.quantity
                }))
            };

            // Llamada al backend (usar API_BASE_URL)
            const response = await fetch(`${API_BASE_URL}/inbound-orders/create`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            });

            if (!response.ok) throw new Error("Error al crear la orden entrante: " + response.status);

            const data = await response.json();

            // Verificamos que el backend envíe la URL
            const pdfDownloadUrl = data.pdfDownloadUrl;
            if (!pdfDownloadUrl) {
                throw new Error("El backend no devolvió la URL del PDF");
            }

            // Setear link en el modal (si existe)
            const pdfLink = document.getElementById("pdfDownloadLink");
            if (pdfLink) pdfLink.href = pdfDownloadUrl;

            // Mostrar modal
            const modal = document.getElementById("pdfModal");
            if (modal) modal.style.display = "block";

            // Eventos de cerrar modal
            const closeBtn = document.getElementById("pdfModalClose");
            const cancelBtn = document.getElementById("pdfModalCancel");
            if (closeBtn) closeBtn.onclick = () => { if (modal) modal.style.display = "none"; };
            if (cancelBtn) cancelBtn.onclick = () => { if (modal) modal.style.display = "none"; };

            // Limpiar items después de crear
            this.pendingInboundItems = [];
            this.renderInboundItemsList();

        } catch (err) {
            console.error(err);
            alert("Hubo un error al crear la orden.");
        }
    }

    clearInboundForm() {
        // limpiar UI y lista
        this.pendingInboundItems = [];
        const supplierSelect = document.getElementById('inb_supplierSelect');
        const productSelect = document.getElementById('inb_productSelect');
        if (supplierSelect) supplierSelect.value = '';
        if (productSelect) productSelect.value = '';
        const manualEl = document.getElementById('inb_productIdManual');
        if (manualEl) manualEl.value = '';
        const qtyEl = document.getElementById('inb_quantity');
        if (qtyEl) qtyEl.value = '1';
        this.renderInboundItemsList();
    }

    /* -------------------------
       CRUD / generic helpers
       ------------------------- */
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
                                <tr><td colspan="${fields.length + 1}">No data (GET not available)</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;
        const contentArea = document.getElementById('contentArea');
        if (!contentArea) return console.warn('loadCrudModule: no existe contentArea');
        contentArea.innerHTML = content;
        this.currentFields = fields;

        // Solo intentamos cargar datos si el backend soporta GET para este módulo
        if (this.supports(module, 'get')) {
            await this.loadData(module);
        } else {
            console.info(`GET no soportado para ${module}, omitiendo loadData.`);
            const tbody = document.getElementById('tableBody');
            if (tbody) tbody.innerHTML = `<tr><td colspan="${fields.length + 1}">Endpoint GET /api/${module} no disponible en backend.</td></tr>`;
        }
    }

    async loadData(module) {
        if (!this.supports(module, 'get')) {
            console.warn(`loadData: GET no soportado para ${module}`);
            this.renderTable([], module);
            return;
        }

        try {
            const r = await fetch(`${API_BASE_URL}/${module}`);
            if (!r.ok) {
                console.warn(`loadData ${module} returned ${r.status}`);
                this.renderTable([], module);
                return;
            }
            const data = await r.json();
            this.renderTable(data, module);
        } catch (error) {
            console.error(`Error loading ${module}:`, error);
            const tbody = document.getElementById('tableBody');
            if (tbody) tbody.innerHTML = `<tr><td colspan="100%">Error loading data</td></tr>`;
        }
    }

    renderTable(data, module) {
        const tbody = document.getElementById('tableBody');
        if (!tbody) return;
        if (!Array.isArray(data) || data.length === 0) {
            tbody.innerHTML = `<tr><td colspan="100%">No data available (backend GET missing or empty)</td></tr>`;
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
        const modalTitle = document.getElementById('modalTitle');
        if (modalTitle) modalTitle.textContent = `Add ${module.slice(0, -1)}`;
        this.buildForm();
        const modal = document.getElementById('modal');
        if (modal) modal.style.display = 'block';
    }

    async openEditModal(module, id) {
        // Si backend no soporta GET para este recurso, no intentamos cargarlo
        if (!this.supports(module, 'get')) {
            alert('Editar no disponible: endpoint GET no implementado en el backend.');
            return;
        }

        this.currentEditId = id;
        const modalTitle = document.getElementById('modalTitle');
        if (modalTitle) modalTitle.textContent = `Edit ${module.slice(0, -1)}`;
        try {
            const r = await fetch(`${API_BASE_URL}/${module}/${id}`);
            if (!r.ok) {
                alert('Error loading item for editing');
                return;
            }
            const data = await r.json();
            this.buildForm(data);
            const modal = document.getElementById('modal');
            if (modal) modal.style.display = 'block';
        } catch (error) {
            console.error('Error loading item:', error);
            alert('Error loading item for editing');
        }
    }

    buildForm(data = {}) {
        const formFields = document.getElementById('formFields');
        if (!formFields) return;

        formFields.innerHTML = this.currentFields.map(field => {
            let input = '';
            const value = data[field.key] !== undefined ? data[field.key] : '';

            switch (field.type) {
                case 'textarea':
                    input = `<textarea class="form-control" name="${field.key}" ${field.required ? 'required' : ''}>${value}</textarea>`;
                    break;

                case 'number':
                    input = `<input type="number" class="form-control" name="${field.key}" value="${value}" ${field.required ? 'required' : ''}>`;
                    break;

                case 'select': // aquí insertamos selects vacíos y los llenamos luego
                    input = `<select class="form-control" name="${field.key}" ${field.required ? 'required' : ''}></select>`;
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

        // Después de insertar el HTML, llenamos selects específicos
        // Categoría -> llamamos al nuevo populateCategoriesSelect
        const categorySelect = formFields.querySelector('select[name="categoryId"]');
        if (categorySelect) {
            const selectedCatId = data.categoryId !== undefined ? data.categoryId : null;
            this.populateCategoriesSelect(categorySelect, selectedCatId);
        }

        // Proveedores (si existe)
        const supplierSelect = formFields.querySelector('select[name="supplierId"]');
        if (supplierSelect && typeof this.loadSuppliers === 'function') {
            const selectedSupId = data.supplierId !== undefined ? data.supplierId : null;
            this.loadSuppliers(supplierSelect, selectedSupId);
        }
    }

    async handleFormSubmit() {
        const modalForm = document.getElementById('modalForm');
        if (!modalForm) return;

        const formData = new FormData(modalForm);
        let data = Object.fromEntries(formData);

        // Ajuste para products: enviar categoryId y supplierId como números
        if (this.currentModule === 'products') {
            // Si backend solo soporta POST, permitimos crear; editing no está soportado por ahora
            data = {
                name: data.name,
                description: data.description,
                price: parseFloat(data.price),
                stock: parseInt(data.stock),
                categoryId: data.categoryId ? parseInt(data.categoryId) : null,
                supplierId: data.supplierId ? parseInt(data.supplierId) : null
            };
        }

        // Si intentan editar pero backend no soporta PUT -> mostrar aviso
        if (this.currentEditId && !this.supports(this.currentModule, 'put')) {
            alert('Edición no disponible: endpoint PUT no implementado en el backend.');
            return;
        }

        // Si intentan crear pero backend no soporta POST -> avisar
        if (!this.currentEditId && !this.supports(this.currentModule, 'post')) {
            alert('Creación no disponible: endpoint POST no implementado en el backend.');
            return;
        }

        try {
            const url = this.currentEditId
                ? `${API_BASE_URL}/${this.currentModule}/${this.currentEditId}`
                : `${API_BASE_URL}/${this.currentModule}`;
            const method = this.currentEditId ? 'PUT' : 'POST';

            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                this.closeModal();
                // Si GET no está disponible no podemos recargar la tabla, así que mostramos confirmación
                if (this.supports(this.currentModule, 'get')) {
                    await this.loadData(this.currentModule);
                } else {
                    alert(`${this.currentModule.slice(0, -1)} ${this.currentEditId ? 'updated' : 'created'} successfully! (backend: no GET to refresh list)`);
                }
            } else {
                const text = await response.text();
                console.error('Save error:', response.status, text);
                alert('Error saving data: ' + response.status);
            }
        } catch (error) {
            console.error('Error saving:', error);
            alert('Error saving data');
        }
    }

    async deleteItem(module, id) {
        if (!confirm('Are you sure you want to delete this item?')) return;

        if (!this.supports(module, 'delete')) {
            alert('Eliminar no disponible: endpoint DELETE no implementado en el backend.');
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/${module}/${id}`, { method: 'DELETE' });
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
        const modal = document.getElementById('modal');
        if (modal) modal.style.display = 'none';
        const form = document.getElementById('modalForm');
        if (form) form.reset();
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


// Exponer globalmente
document.addEventListener('DOMContentLoaded', () => {
    window.dashboard = new Dashboard();
});

