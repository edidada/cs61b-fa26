const { chromium } = require('../.ci-browser/node_modules/playwright');
const assert = require('node:assert/strict');
const fs = require('node:fs');
(async () => {
  const browser = await chromium.launch({headless: true});
  try {
    const page = await browser.newPage({viewport: {width: 1050, height: 900}});
    const errors = [];
    page.on('pageerror', error => errors.push(error.message));
    await page.goto('http://localhost:4567/ngordnet.html');
    await page.locator('#words').fill('video, recording');
    await page.locator('#start').fill('2100');
    await page.locator('#end').fill('1400');
    await page.locator('#k').fill('0');
    await page.locator('#hyponyms').click();
    assert.equal(await page.locator('#textresult').inputValue(), '[video, video_recording, videocassette, videotape]');
    assert.equal(await page.locator('#textresult').isVisible(), true);
    assert.equal(await page.locator('#plot').isVisible(), false);
    await page.screenshot({path: 'evidence/browser-hyponyms.png', fullPage: true});
    await page.locator('#words').fill('food, cake');
    await page.locator('#start').fill('1950');
    await page.locator('#end').fill('1990');
    await page.locator('#k').fill('5');
    await page.locator('#hyponyms').click();
    assert.equal(await page.locator('#textresult').inputValue(), '[cake, cookie, kiss, snap, wafer]');
    await page.screenshot({path: 'evidence/browser-ranked-hyponyms.png', fullPage: true});
    await page.locator('#words').fill('missing-no-such-word');
    await page.locator('#hyponyms').click();
    assert.equal(await page.locator('#textresult').inputValue(), '[]');
    assert.deepEqual(errors, []);
    fs.writeFileSync('evidence/browser-results.json', JSON.stringify({browser: await browser.version(), positiveK: true, multiWord: true, ignoresYearsAtK0: true, missingWord: true, pageErrors: errors}, null, 2));
  } finally { await browser.close(); }
})().catch(error => {console.error(error); process.exitCode = 1;});
