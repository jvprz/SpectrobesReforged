const assert = require('node:assert/strict');
const fs = require('node:fs');
const http = require('node:http');
const path = require('node:path');
const {chromium} = require('playwright');
// Optional browser regression: node tools/wiki/test_navigation.cjs [build/wiki] [screenshots-folder]
(async () => {
  const root = path.resolve(process.argv[2] || 'build/wiki');
  const server = http.createServer((req,res) => {
    const file = path.resolve(root, '.'+decodeURIComponent(new URL(req.url,'http://localhost').pathname));
    if (!file.startsWith(root+path.sep)) { res.writeHead(403).end(); return; }
    fs.readFile(file,(err,data) => {
      if(err){res.writeHead(404).end();return;}
      const types={'.html':'text/html; charset=utf-8','.css':'text/css','.js':'text/javascript','.png':'image/png','.svg':'image/svg+xml'};
      res.setHeader('Content-Type',types[path.extname(file)]||'application/octet-stream');res.end(data);
    });
  });
  await new Promise(resolve => server.listen(0,'127.0.0.1',resolve));
  let browser;
  try {
    browser=await chromium.launch({headless:true,channel:process.env.WIKI_BROWSER_CHANNEL || 'msedge'});
    const page=await browser.newPage({viewport:{width:1440,height:1000}});
    const origin='http://127.0.0.1:'+server.address().port;
    await page.goto(origin+'/vortices.html');
    for(const language of ['es','fr','de','it','pt-BR','en','es']){
      await Promise.all([page.waitForURL(language==='en' ? origin+'/vortices.html' : origin+'/'+language+'/vortices.html'),page.selectOption('#language',{value:await page.locator('#language option[data-language="'+language+'"]').getAttribute('value')})]);
      assert.equal(await page.locator('html').getAttribute('lang'),language);
      assert.equal(await page.locator('#language option:checked').getAttribute('data-language'),language);
      assert.equal(await page.locator('#version option:checked').getAttribute('data-current'),'true');
    }
    await Promise.all([page.waitForURL('**/versions/0.6.0-beta.1/vortices.html'),page.selectOption('#version',{label:'0.6.0-beta.1 · English'})]);
    const label=await page.locator('#version option[data-current="true"]').textContent();
    await page.evaluate(()=>window.dispatchEvent(new PageTransitionEvent('pageshow',{persisted:true})));
    assert.equal(await page.locator('#version option[data-current="true"]').textContent(),label);
    const latest=await page.locator('#version option[data-current="true"]').getAttribute('value');
    await Promise.all([page.waitForURL(origin+'/es/vortices.html'),page.selectOption('#version',latest)]);
    await page.goBack();
    assert.equal(await page.locator('#version option:checked').textContent(),'0.6.0-beta.1 · English');
    await page.goForward();
    assert.equal(await page.locator('#language option:checked').getAttribute('data-language'),'es');
    assert.equal(await page.locator('#version option:checked').getAttribute('data-current'),'true');
    const screenshots=process.argv[3];if(screenshots)fs.mkdirSync(screenshots,{recursive:true});
    for(const language of ['en','es','fr','de','it','pt-BR'])for(const slug of ['vortices','spectrobes','minerals','fossils','settings']){
      await page.setViewportSize({width:390,height:844});
      await page.goto(origin+'/'+(language==='en'?'':language+'/')+slug+'.html');
      assert.equal(await page.evaluate(()=>document.documentElement.scrollWidth>innerWidth),false,language+'/'+slug+' overflow');
      assert.ok(await page.locator('.table-scroll table').count()>0);
    }
    if(screenshots){
      await page.goto(origin+'/es/vortices.html#krawl-by-biome');
      await page.screenshot({path:path.join(screenshots,'beta8-wiki-mobile.png')});
      await page.setViewportSize({width:1440,height:1000});
      await page.goto(origin+'/es/spectrobes.html');
      await page.evaluate(()=>{document.documentElement.dataset.theme='dark'});
      await page.screenshot({path:path.join(screenshots,'beta8-wiki-tables.png')});
    }
    console.log('PASS: six-language navigation, archive return, Back/Forward, idempotent restoration, 30 mobile table pages.');
  } finally {if(browser)await browser.close();server.close();}
})().catch(error=>{console.error(error);process.exitCode=1});
