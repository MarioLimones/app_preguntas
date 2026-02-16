(() => {
  const navLinks = document.querySelectorAll('[data-nav-link]');
  if (!navLinks.length) return;
  const currentHash = window.location.hash;
  const currentPath = window.location.pathname;
  navLinks.forEach((link) => {
    const href = link.getAttribute('href') || '';
    const matchesHash = currentHash && href === currentHash;
    const matchesPath = href && href === currentPath;
    if (matchesHash || matchesPath) {
      link.classList.add('bg-sky-100', 'text-slate-900');
      link.setAttribute('aria-current', 'page');
    }
  });
})();


