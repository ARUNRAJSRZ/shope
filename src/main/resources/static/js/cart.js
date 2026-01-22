// Backend cart logic
function addToCart(product) {
  const csrfMeta = document.querySelector('meta[name="_csrf"]');
  const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
  const headers = { 'X-Requested-With': 'XMLHttpRequest' };
  if (csrfMeta && csrfHeaderMeta) {
    headers[csrfHeaderMeta.getAttribute('content')] = csrfMeta.getAttribute('content');
  }

  fetch('/api/cart/add?productId=' + product.id + '&quantity=1', {
    method: 'POST',
    headers: headers
  })
    .then(resp => {
      if (!resp.ok) throw new Error('Failed to add to cart');
      return resp.json();
    })
    .then(() => {
      // Redirect to cart page after successful add
     const popup = document.getElementById('cartPopup');
    popup.style.display = 'flex';
    document.body.style.overflow = 'hidden';

  document.getElementById('cartBtn').onclick = () => {
    window.location.href = '/cart';
  };

  document.getElementById('homeBtn').onclick = () => {
    window.location.href = '/home';
  }
    })
    .catch(() => {
      alert('You must be logged in to add to cart.');
    });
}

function getCart(callback) {
  fetch('/api/cart')
    .then(resp => resp.json())
    .then(callback)
    .catch(() => callback([]));
}

function getCsrfHeaders(json) {
  const csrfMeta = document.querySelector('meta[name="_csrf"]');
  const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
  const headers = {'X-Requested-With': 'XMLHttpRequest'};
  if (json) headers['Content-Type'] = 'application/json';
  if (csrfMeta && csrfHeaderMeta) headers[csrfHeaderMeta.getAttribute('content')] = csrfMeta.getAttribute('content');
  return headers;
}

function renderCart() {
  getCart(function(cart) {
    const cartItemsDiv = document.getElementById('cart-items');
    if (!cart.length) {
      cartItemsDiv.innerHTML = '<p>Your cart is empty.</p>';
      return;
    }
    let total = 0;
    let html = '<table><thead><tr><th></th><th>Product</th><th>Qty</th><th>Price</th><th>Subtotal</th><th>-</th></tr></thead><tbody>';
    cart.forEach(item => {
      const subtotal = (item.unitPrice || 0) * (item.quantity || 0);
      total += subtotal;
      const name = item.productName || (item.product && item.product.name) || 'Product';
      const img = item.image || (item.productId ? `/api/products/${item.productId}/image` : (item.product && item.product.image) || '/images/placeholder.png');
      html += `<tr>
        <td style="width:120px"><a href="#" class="cart-product" data-id="${item.productId}"><img src="${img}" alt="${name}" style="max-width:120px;max-height:120px;object-fit:cover"></a></td>
        <td><a href="#" class="cart-product" data-id="${item.productId}">${name}</a></td>
        <td>
          <div style="display:flex;align-items:center;gap:6px">
            <button class="qty-decrease btn" data-id="${item.id}">−</button>
            <input class="qty-input" data-id="${item.id}" value="${item.quantity}" style="width:48px;text-align:center">
            <button class="qty-increase btn" data-id="${item.id}">+</button>
          </div>
        </td>
        <td style="padding-right:24px">₹${item.unitPrice}</td>
        <td>₹${subtotal}</td><td><div style="margin-top:6px"><button class="delete-cart btn" data-id="${item.id}">Remove</button></div></td>
      </tr>`;
    });
    html += `</tbody><tfoot><tr><td colspan="3"></td><td style="text-align:right"><strong>Total</strong></td><td>₹${total}</td></tr></tfoot></table>`;
    cartItemsDiv.innerHTML = html;

    // attach handlers for qty change, delete and opening product modal
    const headersJson = getCsrfHeaders(true);
    const headersNoBody = getCsrfHeaders(false);

    cartItemsDiv.querySelectorAll('.qty-decrease').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.dataset.id;
        const input = cartItemsDiv.querySelector(`.qty-input[data-id="${id}"]`);
        let val = parseInt(input.value || '0', 10);
        if (val > 1) val--;
        btn.disabled = true;
        fetch(`/api/cart/${id}?quantity=${val}`, { method: 'PUT', headers: headersJson })
          .then(r=>{ if(!r.ok) throw new Error('fail'); return r.json(); })
          .then(()=> renderCart())
          .catch(()=> { alert('Unable to update quantity'); btn.disabled = false; });
      });
    });
    cartItemsDiv.querySelectorAll('.qty-increase').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.dataset.id;
        const input = cartItemsDiv.querySelector(`.qty-input[data-id="${id}"]`);
        let val = parseInt(input.value || '0', 10);
        val++;
        btn.disabled = true;
        fetch(`/api/cart/${id}?quantity=${val}`, { method: 'PUT', headers: headersJson })
          .then(r=>{ if(!r.ok) throw new Error('fail'); return r.json(); })
          .then(()=> renderCart())
          .catch(()=> { alert('Unable to update quantity'); btn.disabled = false; });
      });
    });
    cartItemsDiv.querySelectorAll('.qty-input').forEach(input => {
      input.addEventListener('change', () => {
        const id = input.dataset.id;
        let val = parseInt(input.value || '0', 10);
        if (val < 1) val = 1;
        fetch(`/api/cart/${id}?quantity=${val}`, { method: 'PUT', headers: headersJson })
          .then(r=>{ if(!r.ok) throw new Error('fail'); return r.json(); })
          .then(()=> renderCart())
          .catch(()=> alert('Unable to update quantity'));
      });
    });

    cartItemsDiv.querySelectorAll('.delete-cart').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.dataset.id;
        btn.disabled = true;
        fetch(`/api/cart/${id}`, { method: 'DELETE', headers: headersNoBody })
          .then(r=>{ if(!r.ok) throw new Error('fail'); renderCart(); })
          .catch(()=> { alert('Unable to remove item'); btn.disabled = false; });
      });
    });

    cartItemsDiv.querySelectorAll('.cart-product').forEach(el => {
      el.addEventListener('click', (e) => {
        e.preventDefault();
        const pid = Number(el.dataset.id);
        const prod = (window.products || []).find(p => p.id === pid);
        if (prod && typeof showProductModal === 'function') {
          showProductModal(prod);
        } else {
          // fallback: fetch products then show
          fetch('/api/products').then(r=>r.json()).then(list=>{
            const p = list.find(x=>x.id===pid);
            if (p) {
              const obj = { id: p.id, code: p.code, name: p.name, category: p.category, price: p.price, image: p.id ? `/api/products/${p.id}/image` : (p.image || ''), description: p.description, affiliate: p.affiliate };
              if (typeof showProductModal === 'function') showProductModal(obj);
            }
          });
        }
      });
    });
  });
}

function getAddresses(callback) {
  fetch('/api/addresses')
    .then(resp => {
      if (!resp.ok) throw new Error('no addresses');
      return resp.json();
    })
    .then(callback)
    .catch(() => callback([]));
}

function renderAddresses() {
  const container = document.getElementById('addresses-list');
  if (!container) return;
  container.innerHTML = 'Loading...';
  getAddresses(function(addrs) {
    if (!addrs || !addrs.length) {
      container.innerHTML = '<p>No saved addresses.</p>';
      return;
    }
    let html = '<form id="addresses-form">';
    addrs.forEach(a => {
      html += `<div style="border:1px solid #eee;padding:8px;margin-bottom:8px;border-radius:4px">
        <label style="display:flex;gap:8px;align-items:flex-start"><input type="radio" name="selectedAddress" value="${a.id}" ${a.isDefault ? 'checked' : ''}> <div>
          <strong>${a.name}</strong><div>${a.addressLine1}${a.addressLine2 ? (', ' + a.addressLine2) : ''}</div>
          <div>${a.city || ''}${a.state ? (', ' + a.state) : ''} ${a.postalCode || ''}</div>
          <div>${a.country || ''}</div>
          <div>${a.phoneNumber || ''}</div>
        </div></label>
        <div style="margin-top:6px;text-align:right"><button type="button" class="delete-address btn" data-id="${a.id}">Delete</button></div>
      </div>`;
    });
    html += '</form>';
    container.innerHTML = html;
    // attach handlers for delete and default selection
    const csrfMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    const headers = {};
    if (csrfMeta && csrfHeaderMeta) headers[csrfHeaderMeta.getAttribute('content')] = csrfMeta.getAttribute('content');

    container.querySelectorAll('.delete-address').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.dataset.id;
        fetch('/api/addresses/' + id, { method: 'DELETE', headers: headers })
          .then(resp => {
            if (!resp.ok) throw new Error('delete failed');
            renderAddresses();
          }).catch(() => alert('Unable to delete address'));
      });
    });

    const formEl = container.querySelector('#addresses-form');
    if (formEl) {
      formEl.addEventListener('change', () => {
        const sel = formEl.selectedAddress && formEl.selectedAddress.value;
        if (!sel) return;
        fetch('/api/addresses/' + sel + '/default', { method: 'POST', headers: headers })
          .then(resp => {
            if (!resp.ok) throw new Error('set default failed');
            renderAddresses();
          }).catch(() => alert('Unable to set default address'));
      });
    }
  });
}

function setupAddressModal() {
  const addBtn = document.getElementById('add-address-btn');
  const modal = document.getElementById('address-modal');
  const cancel = document.getElementById('address-cancel');
  const form = document.getElementById('address-form');
  if (!addBtn || !modal || !cancel || !form) return;

  addBtn.addEventListener('click', () => { modal.style.display = 'flex'; });
  cancel.addEventListener('click', () => { modal.style.display = 'none'; });

  form.addEventListener('submit', function(e) {
    e.preventDefault();
    const data = {};
    new FormData(form).forEach((v,k) => data[k]=v);
    const csrfMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    const headers = {'Content-Type':'application/json'};
    if (csrfMeta && csrfHeaderMeta) headers[csrfHeaderMeta.getAttribute('content')] = csrfMeta.getAttribute('content');

    fetch('/api/addresses', {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(data)
    }).then(resp => {
      if (!resp.ok) throw new Error('Failed');
      return resp.json();
    }).then(() => {
      modal.style.display = 'none';
      form.reset();
      renderAddresses();
    }).catch(err => alert('Unable to save address'));
  });
}

document.addEventListener('DOMContentLoaded', function() {
  if (document.getElementById('cart-items')) {
    renderCart();
    renderAddresses();
    setupAddressModal();
  }
});

// Expose addToCart for use in product listing
window.addToCart = addToCart;
