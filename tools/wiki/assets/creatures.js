/* Variant previews use only the existing local creature renders. */
'use strict';
for (const profile of document.querySelectorAll('[data-creature-profile]')) {
  const preview=profile.querySelector('[data-profile-preview]');
  const link=profile.querySelector('[data-preview-link]');
  const caption=profile.querySelector('[data-preview-caption]');
  const buttons=[...profile.querySelectorAll('[data-profile-image]')];
  if(!preview||!link||!caption)continue;
  for(const button of buttons)button.addEventListener('click',()=>{
    preview.src=button.dataset.profileImage;
    link.href=button.dataset.profileImage;
    caption.textContent=button.dataset.variantLabel;
    for(const other of buttons)other.setAttribute('aria-pressed',String(other.dataset.profileImage===button.dataset.profileImage));
  });
  const selected=buttons.find(button=>'#'+button.id===location.hash);
  if(selected)selected.click();
}
