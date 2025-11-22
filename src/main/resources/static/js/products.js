// Products loader: fetch product list from server API and then kick off the UI render
window.products = [];

fetch('/api/products')
  .then(resp => {
    if (!resp.ok) throw new Error('Failed to load products');
    return resp.json();
  })
  .then(data => {
    // ensure shape matches what the UI expects
    window.products = data.map(p => ({
      // use numeric DB id as primary id for API access
      id: p.id,
      code: p.code,
      name: p.name,
      category: p.category,
      price: p.price,
      // prefer serving image from API endpoint (will stream blob if stored in DB)
      image: p.id ? `/api/products/${p.id}/image` : (p.image && (p.image.startsWith('/') || p.image.startsWith('http')) ? p.image : (p.image ? `/images/${p.image}` : '')),
      description: p.description,
      affiliate: p.affiliate,
      offer: !!p.offer
    }));

    // populate UI (functions defined in script.js)
    if (typeof populateCategories === 'function') populateCategories();
    if (typeof render === 'function') render(getFiltered());
  })
  .catch(err => {
    console.error('Error loading products:', err);
  });