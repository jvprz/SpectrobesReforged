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
// Header navigation is an accessible drawer; reading stays centered on the page.
const menuButton = document.querySelector('#wiki-menu-toggle');
const drawer = document.querySelector('#wiki-navigation');
const closeButton = document.querySelector('#wiki-menu-close');
const backdrop = document.querySelector('#wiki-nav-backdrop');
const guideMenu = document.querySelector('.guide-menu');
if (menuButton && drawer && closeButton && backdrop) {
  document.documentElement.classList.add('wiki-enhanced');
  menuButton.hidden = false;
  if (guideMenu) guideMenu.open = true;
  function setMenu(open, restoreFocus = true) {
    document.body.dataset.menuOpen = String(open);
    menuButton.setAttribute('aria-expanded', String(open));
    backdrop.hidden = !open;
    if (open) {
      document.querySelectorAll('.contents-popover[open]').forEach(element => { element.open = false; });
      closeButton.focus({preventScroll:true});
      const scrollRegion=drawer.querySelector('.drawer-scroll');
      if(scrollRegion)scrollRegion.scrollTop=0;
    }
    else if (restoreFocus) menuButton.focus();
  }
  menuButton.addEventListener('click', () => setMenu(document.body.dataset.menuOpen !== 'true'));
  closeButton.addEventListener('click', () => setMenu(false));
  backdrop.addEventListener('click', () => setMenu(false));
  document.addEventListener('keydown', event => {
    if (event.key === 'Escape') {
      if (document.body.dataset.menuOpen === 'true') setMenu(false);
      document.querySelectorAll('.contents-popover[open]').forEach(element => {
        const restore=element.contains(document.activeElement);element.open=false;
        if(restore)element.querySelector('summary').focus();
      });
    }
    if (event.key !== 'Tab' || document.body.dataset.menuOpen !== 'true') return;
    const stops = [...drawer.querySelectorAll('a,button,select,input')].filter(element => !element.hidden && element.getClientRects().length);
    if (event.shiftKey && document.activeElement === stops[0]) { event.preventDefault(); stops.at(-1).focus(); }
    else if (!event.shiftKey && document.activeElement === stops.at(-1)) { event.preventDefault(); stops[0].focus(); }
  });
}

// Contents acts as one compact popover: dismiss on selection, outside click or Escape.
for (const contents of document.querySelectorAll('.contents-popover')) {
  contents.addEventListener('click', event => {
    if (event.target.closest('nav a')) contents.open = false;
  });
  document.addEventListener('pointerdown', event => {
    if (contents.open && !contents.contains(event.target)) contents.open = false;
  });
}
