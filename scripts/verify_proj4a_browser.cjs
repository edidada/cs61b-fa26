const { chromium } = require('../.ci-browser/node_modules/playwright');
const assert = require('node:assert/strict');
const fs = require('node:fs');

(async () => {
  const browser = await chromium.launch({headless: true});
  try {
    const page = await browser.newPage({viewport: {width: 1050, height: 900}});
    const errors = [];
    page.on('pageerror', error => errors.push(error.message));
    await page.goto('http://localhost:4567/ngordnet_4a.html');
    await page.locator('#words').fill('request, airport');
    await page.locator('#start').fill('2006');
    await page.locator('#end').fill('2007');
    await page.locator('#historytext').click();
    assert.equal(await page.locator('#textresult').inputValue(), fs.readFileSync('evidence/http-raw-csv-expected.txt', 'utf8'));
    assert.equal(await page.locator('#textresult').isVisible(), true);
    assert.equal(await page.locator('#plot').isVisible(), false);
    await page.screenshot({path: 'evidence/browser-text.png', fullPage: true});
    await page.locator('#words').fill('cat, dog');
    await page.locator('#start').fill('1900');
    await page.locator('#end').fill('1950');
    await page.locator('#history').click();
    await page.waitForFunction(() => {
      const plot = document.getElementById('plot');
      return plot.src.startsWith('data:image/png;base64,') && plot.complete && plot.naturalWidth === 800;
    });
    assert.equal(await page.locator('#plot').isVisible(), true);
    assert.equal(await page.locator('#textresult').isVisible(), false);
    await page.screenshot({path: 'evidence/browser-chart.png', fullPage: true});
    await page.locator('#words').fill('missing-no-such-word');
    await page.locator('#historytext').click();
    assert.equal(await page.locator('#textresult').inputValue(), 'missing-no-such-word: {}\n');
    assert.deepEqual(errors, []);
    fs.writeFileSync('evidence/browser-results.json', JSON.stringify({browser: await browser.version(), textExact: true, graphDecoded: true, missingWord: true, pageErrors: errors}, null, 2));
  } finally {
    await browser.close();
  }
})().catch(error => {console.error(error); process.exitCode = 1;});
