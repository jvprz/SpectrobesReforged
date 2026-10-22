(function () {
  'use strict';
  const root = document.documentElement;
  const preference = window.matchMedia('(prefers-color-scheme: dark)');
  let saved = null;
  try { saved = localStorage.getItem('spectrobes-wiki-theme'); } catch (_) {}
  let manual = saved === 'light' || saved === 'dark';
  function apply(theme) {
    root.dataset.theme = theme;
    const meta = document.querySelector('meta[name="theme-color"]');
    if (meta) meta.content = theme === 'dark' ? '#101114' : '#ffffff';
    const button = document.querySelector('#theme-toggle');
    if (button) {
      button.hidden = false;
      button.textContent = theme === 'dark' ? 'Light mode' : 'Dark mode';
      button.setAttribute('aria-label', theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode');
    }
  }
  apply(manual ? saved : (preference.matches ? 'dark' : 'light'));
  document.addEventListener('DOMContentLoaded', function () {
    apply(root.dataset.theme);
    const button = document.querySelector('#theme-toggle');
    if (!button) return;
    button.addEventListener('click', function () {
      const next = root.dataset.theme === 'dark' ? 'light' : 'dark';
      manual = true;
      apply(next);
      try { localStorage.setItem('spectrobes-wiki-theme', next); } catch (_) {}
    });
  });
  preference.addEventListener('change', function (event) {
    if (!manual) apply(event.matches ? 'dark' : 'light');
  });
})();
