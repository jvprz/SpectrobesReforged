/* Edition controls must describe the loaded page, including after browser Back/Forward. */
'use strict';
const editionSelects = [...document.querySelectorAll('select[data-navigate]')];
function rememberedLanguage() {
  try { return sessionStorage.getItem('wiki-language'); } catch (_) { return null; }
}
function rememberLanguage(language) {
  try { sessionStorage.setItem('wiki-language', language); } catch (_) { /* Storage is optional. */ }
}
function syncEditions() {
  const language = document.documentElement.lang;
  const archived = document.body.dataset.archived === 'true';
  if (!archived) rememberLanguage(language);
  editionSelects.forEach(select => {
    const current = [...select.options].find(option => option.defaultSelected);
    if (current) select.value = current.value;
  });
  // Historical editions are English-only; returning to Current preserves the last chosen language.
  if (archived) {
    const preferred = rememberedLanguage() || language;
    const target = [...document.querySelector('#language').options].find(option =>
      option.dataset.language === preferred && !option.defaultSelected);
    const currentVersion = document.querySelector('#version option[data-current="true"]');
    if (target && currentVersion) {
      currentVersion.value = target.value;
      const original = currentVersion.dataset.originalLabel || currentVersion.textContent;
      currentVersion.dataset.originalLabel = original;
      currentVersion.textContent = original.replace(/ · English$/, '') + ' · ' + target.textContent;
    }
  }
}
syncEditions();
window.addEventListener('pageshow', syncEditions);
editionSelects.forEach(select => {
  select.addEventListener('change', () => {
    const option = select.selectedOptions[0];
    if (!option || !option.value) return;
    if (select.id === 'language') rememberLanguage(option.dataset.language);
    window.location.assign(option.value);
  });
});
const mobile = window.matchMedia('(max-width: 760px)');
const menu = document.querySelector('.sidebar details');
function fitNavigation() { if (menu) menu.open = !mobile.matches; }
fitNavigation();
mobile.addEventListener('change', fitNavigation);
