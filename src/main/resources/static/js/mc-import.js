(() => {
  const form = document.querySelector('[data-opentdb-form]');
  const status = document.querySelector('[data-opentdb-status]');
  if (!form || !status) return;

  const setStatus = (message, tone) => {
    status.textContent = message;
    status.classList.remove('hidden', 'border-green-300', 'bg-green-50', 'text-green-800',
      'border-yellow-300', 'bg-yellow-50', 'text-yellow-800');
    if (tone === 'success') {
      status.classList.add('border-green-300', 'bg-green-50', 'text-green-800');
    } else {
      status.classList.add('border-yellow-300', 'bg-yellow-50', 'text-yellow-800');
    }
  };

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const submitButton = form.querySelector('button[type="submit"]');
    if (submitButton) {
      submitButton.disabled = true;
      submitButton.classList.add('opacity-70', 'cursor-not-allowed');
    }

    const formData = new FormData(form);
    const params = new URLSearchParams();
    const amount = formData.get('amount');
    const category = formData.get('category');
    const difficulty = formData.get('difficulty');

    if (amount) params.set('amount', amount);
    if (category) params.set('category', category);
    if (difficulty) params.set('difficulty', difficulty);

    try {
      const response = await fetch(`/api/mc/preguntas/import?${params.toString()}`, {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        setStatus(errorText || 'No se pudo importar desde OpenTDB.', 'error');
        return;
      }

      const data = await response.json();
      const count = Array.isArray(data) ? data.length : 0;
      setStatus(`Importadas ${count} preguntas. Actualizando listado...`, 'success');
      setTimeout(() => window.location.reload(), 900);
    } catch (error) {
      setStatus('No se pudo conectar con OpenTDB.', 'error');
    } finally {
      if (submitButton) {
        submitButton.disabled = false;
        submitButton.classList.remove('opacity-70', 'cursor-not-allowed');
      }
    }
  });
})();
