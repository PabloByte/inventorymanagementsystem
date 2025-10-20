document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('supplier-form');
  const responseMessage = document.getElementById('response-message');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Capturamos los valores del formulario
    const supplierData = {
      name: document.getElementById('name').value.trim(),
      email: document.getElementById('email').value.trim(),
      phone: document.getElementById('phone').value.trim(),
      address: document.getElementById('address').value.trim(),
      contactPerson: document.getElementById('contacto').value.trim()
    };

    try {
      const response = await fetch('/api/suppliers/createSupplier', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(supplierData)
      });

      if (!response.ok) {
        throw new Error('Error en la creación del proveedor');
      }

      const data = await response.json();

      // Mostramos mensaje de éxito
      responseMessage.innerHTML = `
        <p style="color: green;">
          ✅ Proveedor creado con éxito: <strong>${data.name}</strong>
        </p>
      `;

      // Limpiar formulario
      form.reset();

    } catch (err) {
      console.error(err);
      responseMessage.innerHTML = `
        <p style="color: red;">
          ❌ No se pudo crear el proveedor. Intenta nuevamente.
        </p>
      `;
    }
  });
});
