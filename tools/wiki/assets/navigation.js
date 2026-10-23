'use strict';
document.querySelectorAll('select[data-navigate]').forEach(select => {
  select.addEventListener('change', () => { if (select.value) window.location.assign(select.value); });
});

// Keep navigation available but collapsed on phones so news appears near entry.
const mobile = window.matchMedia('(max-width: 760px)');
const menu = document.querySelector('.sidebar details');
function fitNavigation() { if (menu) menu.open = !mobile.matches; }
fitNavigation();
mobile.addEventListener('change', fitNavigation);