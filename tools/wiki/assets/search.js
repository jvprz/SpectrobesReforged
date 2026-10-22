'use strict';
const input = document.querySelector('#search');
const results = document.querySelector('#search-results');
const status = document.querySelector('#search-status');
input.addEventListener('input', () => {
  const query = input.value.trim().toLocaleLowerCase();
  results.replaceChildren();
  results.hidden = query.length < 2;
  status.textContent = '';
  if (query.length < 2) return;
  const words = query.split(/\s+/);
  const matches = (window.WIKI_SEARCH || []).filter(page => {
    const haystack = (page.title + ' ' + page.text).toLocaleLowerCase();
    return words.every(word => haystack.includes(word));
  }).sort((a, b) => Number(b.title.toLocaleLowerCase().includes(query)) - Number(a.title.toLocaleLowerCase().includes(query)));
  for (const page of matches.slice(0, 8)) {
    const item = document.createElement('li');
    const link = document.createElement('a');
    link.href = page.url;
    link.textContent = page.title;
    item.append(link);
    results.append(item);
  }
  status.textContent = matches.length ? `${matches.length} matching guide${matches.length === 1 ? '' : 's'}` : 'No matching guides.';
});
input.addEventListener('keydown', event => {
  if (event.key === 'Escape') {
    input.value = '';
    input.dispatchEvent(new Event('input'));
  }
});
