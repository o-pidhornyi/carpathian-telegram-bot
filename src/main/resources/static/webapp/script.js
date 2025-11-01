(() => {
  const tg = window.Telegram.WebApp;
  tg.ready();
  tg.expand();

  // Set username
  const usernameSpan = document.getElementById('username');
  if (usernameSpan) {
    const user = tg.initDataUnsafe?.user;
    usernameSpan.textContent = user ? '@' + user.username : '';
  }

  // Close button
  const closeBtn = document.getElementById('closeBtn');
  if (closeBtn) {
    closeBtn.addEventListener('click', () => {
      tg.close();
    });
  }

  // Load houses button
  const loadBtn = document.getElementById('loadHouses');
  if (loadBtn) {
    loadBtn.addEventListener('click', async () => {
      const container = document.getElementById('housesContainer');
      if (!container) return;
      container.innerHTML = '<div>Loading...</div>';
      try {
        const res = await fetch('/api/houses', {
          headers: {
            'Authorization': 'tma ' + tg.initData
          }
        });
        if (!res.ok) throw new Error('Failed to fetch houses');
        const houses = await res.json();
        container.innerHTML = '';
        houses.forEach(h => {
          const card = document.createElement('div');
          card.className = 'house-card';
          card.innerHTML = `<h3>${h.name}</h3><p>${h.city}, ${h.region}</p>`;
          container.appendChild(card);
        });
      } catch (err) {
        container.innerHTML = '<div class="error">Error loading houses</div>';
      }
    });
  }
})();
