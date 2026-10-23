'use strict';
const fold = value => value.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase();
const input = document.querySelector('#search');
const results = document.querySelector('#search-results');
const status = document.querySelector('#search-status');
input.addEventListener('input', () => {
  const query = fold(input.value.trim());
  results.replaceChildren();
  results.hidden = query.length < 2;
  status.textContent = '';
  if (query.length < 2) return;
  const words = query.split(/\s+/);
  const matches = (window.WIKI_SEARCH || []).filter(page => {
    const haystack = fold(page.title + ' ' + page.text);
    return words.every(word => haystack.includes(word));
  }).sort((a, b) => Number(fold(b.title).includes(query)) - Number(fold(a.title).includes(query)));
  for (const page of matches.slice(0, 8)) {
    const item = document.createElement('li');
    const link = document.createElement('a');
    link.href = page.url;
    link.textContent = page.title;
    item.append(link);
    results.append(item);
  }
  const ui = window.WIKI_UI || {};
  status.textContent = matches.length ? (ui.matches || 'Matching guides: {count}').replace('{count}', matches.length) : (ui.no_matches || 'No matching guides.');
});
input.addEventListener('keydown', event => {
  if (event.key === 'Escape') {
    input.value = '';
    input.dispatchEvent(new Event('input'));
  }
});
