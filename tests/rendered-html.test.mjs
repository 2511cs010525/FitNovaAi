import assert from "node:assert/strict";
import {access, readFile} from "node:fs/promises";
import test from "node:test";

async function render(path="/") {
  const workerUrl = new URL("../dist/server/index.js", import.meta.url);
  workerUrl.searchParams.set("test", `${process.pid}-${Date.now()}`);
  const {default: worker} = await import(workerUrl.href);
  return worker.fetch(new Request(`http://localhost${path}`, {headers:{accept:"text/html"}}), {ASSETS:{fetch:async()=>new Response("Not found",{status:404})}}, {waitUntil(){},passThroughOnException(){}});
}

test("server-renders the FitNova landing page", async()=>{
  const response=await render();
  assert.equal(response.status,200);
  assert.match(response.headers.get("content-type")??"",/^text\/html\b/i);
  const html=await response.text();
  assert.match(html,/<title>FitNova — Fitness that adapts to you<\/title>/i);
  assert.match(html,/YOUR BODY\./);
  assert.match(html,/Personal training that adapts/);
  assert.match(html,/href=["']\/signup["']/);
  assert.match(html,/fitnova-hero\.mp4/);
  assert.doesNotMatch(html,/Your site is taking shape|Building your site/);
});

test("ships the authenticated API and on-device Form Coach integration",async()=>{
  const [app,api,coach,packageJson]=await Promise.all([
    readFile(new URL("../app/FitNovaApp.tsx",import.meta.url),"utf8"),
    readFile(new URL("../app/services/api.ts",import.meta.url),"utf8"),
    readFile(new URL("../app/services/formCoach.ts",import.meta.url),"utf8"),
    readFile(new URL("../package.json",import.meta.url),"utf8"),
  ]);
  assert.match(app,/createPoseLandmarker/);
  assert.match(app,/STOP & SAVE SESSION/);
  assert.match(app,/formApi\.save/);
  assert.match(api,/Authorization:`Bearer \$\{token\}`/);
  assert.match(api,/\/form-sessions/);
  assert.match(coach,/PoseLandmarker\.createFromOptions/);
  assert.match(coach,/detectForVideo|analyzeSquat/);
  assert.match(packageJson,/@mediapipe\/tasks-vision/);
  await access(new URL("../public/models/pose_landmarker_lite.task",import.meta.url));
  await access(new URL("../public/mediapipe/wasm/vision_wasm_internal.wasm",import.meta.url));
});
