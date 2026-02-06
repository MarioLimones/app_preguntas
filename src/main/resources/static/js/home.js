(() => {
  const navLinks = document.querySelectorAll('.nav a');
  if (!navLinks.length) return;
  const currentHash = window.location.hash;
  if (!currentHash) return;
  navLinks.forEach((link) => {
    if (link.getAttribute('href') === currentHash) {
      link.classList.add('is-active');
    }
  });
})();
