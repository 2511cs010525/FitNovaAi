"use client";
/* eslint-disable react/no-unescaped-entities, jsx-a11y/anchor-is-valid */

import { useEffect, useRef, useState } from "react";

const durations = [10, 15, 20, 30, 45, 60];
const facts = [
  ["TIME", "15 MIN"], ["ENERGY", "GOOD"], ["GOAL", "STRENGTH"],
  ["EQUIPMENT", "DUMBBELLS"], ["EXPERIENCE", "INTERMEDIATE"],
];

function Arrow() { return <span aria-hidden="true">↗</span>; }

function SilkAurora() {
  return <div className="silk" aria-hidden="true"><i/><i/><i/><div className="grain"/></div>;
}

function Navigation() {
  const [scrolled, setScrolled] = useState(false);
  useEffect(() => { const f=()=>setScrolled(scrollY>40); addEventListener("scroll",f); return()=>removeEventListener("scroll",f); },[]);
  return <header className={scrolled ? "nav scrolled" : "nav"}>
    <a href="#top" className="wordmark">FITNOVA<span>•</span></a>
    <div className="nav-actions"><a className="text-btn" href="/login">Log in</a><a className="small-cta" href="/signup">Get started <Arrow/></a></div>
  </header>;
}

function Landing({ onView }: { onView: (v: "login" | "signup" | "app") => void }) {
  const [duration,setDuration]=useState(15); const [meal,setMeal]=useState(false);
  const [playing,setPlaying]=useState(true); const heroVideo=useRef<HTMLVideoElement>(null);
  const warm=Math.max(2,Math.round(duration*.14)), cool=Math.max(2,Math.round(duration*.12)), train=duration-warm-cool;
  return <main id="top">
    <Navigation/>
    <section className="hero video-hero"><video ref={heroVideo} className="hero-video" autoPlay muted loop playsInline preload="auto"><source src="/fitnova-hero.mp4" type="video/mp4"/></video><div className="video-wash"/><div className="hero-copy"><p className="eyebrow enter e1">FITNOVA / INTELLIGENT FITNESS</p><h1><span className="enter e2">YOUR BODY.</span><span className="enter e3">YOUR PLAN.</span></h1><p className="hero-desc enter e4">Personal training that adapts to your time, energy and progress — every single day.</p><div className="hero-actions enter e5"><button className="primary hero-cta" onClick={()=>onView("signup")}>BUILD MY PLAN <Arrow/></button><a href="#features">SEE HOW IT WORKS <span>↓</span></a></div></div><button className="video-control compact" aria-label={playing?"Pause background video":"Play background video"} onClick={()=>{const v=heroVideo.current;if(!v)return;if(playing)v.pause();else void v.play();setPlaying(!playing)}}><i>{playing?"Ⅱ":"▶"}</i></button>
    </section>

    <section className="manifest" id="features"><p className="kicker">THE REALITY</p><h2>YOUR SCHEDULE CHANGES.<br/>YOUR ENERGY CHANGES.<br/>YOUR BODY CHANGES.</h2><div><span>SO SHOULD YOUR TRAINING.</span><p>FitNova reads the day you are actually having—then shapes movement, nutrition and recovery around it.</p></div></section>

    <section className="capabilities" id="how"><div className="section-top"><p className="kicker">ONE COACH / THREE SIGNALS</p><p>Scroll through the intelligence behind every recommendation.</p></div><div className="split">
      <article className="panel training"><p>01</p><div><h3>ADAPTIVE<br/>TRAINING</h3><p>10 minutes or 60. FitNova builds training around the time you actually have.</p></div><b>MOVE</b></article>
      <article className="panel form"><p>02</p><div><h3>FORM<br/>INTELLIGENCE</h3><p>Your camera becomes your form coach. Reps, alignment, technique, progress.</p></div><div className="pose-mini"><i/><i/><i/><i/><i/></div></article>
      <article className="panel food"><p>03</p><div><h3>SMART<br/>NUTRITION</h3><p>Turn what is already in your kitchen into a meal aligned with your goal.</p></div><b>FUEL</b></article>
    </div></section>

    <section className="time-demo"><div><p className="kicker">DESIGNED FOR REAL DAYS</p><h2><span>{duration}</span> MINUTES<br/>IS ENOUGH.</h2><p>Your workout should fit your schedule—not fight it.</p></div><div className="time-ui"><div className="durations" role="group" aria-label="Workout duration">{durations.map(d=><button aria-pressed={d===duration} className={d===duration?"active":""} onClick={()=>setDuration(d)} key={d}>{d}</button>)}</div><div className="timeline"><div className="segment warm" style={{flex:warm}}><span>WARM-UP</span></div><div className="segment work" style={{flex:train}}><span>TRAINING</span></div><div className="segment cool" style={{flex:cool}}><span>COOL-DOWN</span></div></div><div className="time-labels"><span>00:00</span><b>{duration}:00</b></div><div className="session-line"><span>TODAY'S SESSION</span><strong>{train} MIN · FULL BODY · MODERATE</strong></div></div></section>

    <section className="adaptive"><p className="kicker">FROM SIGNALS TO SESSION</p><div className="flow"><div className="inputs">{facts.map(([a,b])=><div key={a}><span>{a}</span><b>{b}</b></div>)}</div><div className="flow-line"><i/><span>FITNOVA INTELLIGENCE</span></div><div className="result"><span>TODAY</span><strong>FULL BODY</strong><div><b>15 MIN</b><b>MODERATE</b></div><button onClick={()=>onView("app")}>VIEW SESSION <Arrow/></button></div></div></section>

    <section className="form-coach" id="form"><div className="form-copy"><p className="kicker">COMPUTER VISION / HUMAN FEEDBACK</p><h2>YOUR FORM.<br/>IN REAL TIME.</h2><p>Private, precise guidance designed around joint alignment and range of motion—without turning movement into a video game.</p></div><div className="camera"><div className="camera-top"><span>FORM COACH / PRODUCT PREVIEW</span><i>CAMERA OFF</i></div><div className="human"><span className="head"/><span className="torso"/><span className="arm a1"/><span className="arm a2"/><span className="leg l1"/><span className="leg l2"/><i className="joint j1"/><i className="joint j2"/><i className="joint j3"/><i className="joint j4"/></div><div className="camera-metrics"><div><span>REPS</span><b>—</b></div><div><span>FORM</span><b>—</b></div></div><div className="feedback"><span>PREVIEW</span> Scores appear only during supported analysis.</div></div></section>

    <section className="readiness"><div className="readiness-score"><div className="ring"><strong>—</strong><span>CHECK IN</span></div></div><div><p className="kicker">DAILY READINESS</p><h2>TRAIN FOR<br/>HOW YOU FEEL TODAY.</h2><div className="readiness-data"><span>SLEEP <b>YOU REPORT</b></span><span>ENERGY <b>YOU REPORT</b></span><span>SORENESS <b>YOU REPORT</b></span><span>MOOD <b>YOU REPORT</b></span></div><p>FitNova calculates guidance from your check-in. It does not invent or claim to measure biometrics.</p></div></section>

    <section className="kitchen" id="nutrition"><div><p className="kicker">AI KITCHEN</p><h2>DINNER IS ALREADY<br/>IN YOUR KITCHEN.</h2><p>Start with what you have. FitNova works backward from your goal.</p></div><div className="meal-maker"><div className="ingredients">{["EGGS","TOMATO","ONION","BREAD"].map(x=><button key={x}>{x} <span>×</span></button>)}</div><button className="primary sand" onClick={()=>setMeal(!meal)}>{meal?"RESET INGREDIENTS":"CREATE A MEAL"} <Arrow/></button><div className={meal?"recipe show":"recipe"}><span>HIGH-PROTEIN</span><h3>EGG & TOMATO<br/>TOAST</h3><div><b>12 MIN<small>PREPARATION</small></b><b>24G<small>PROTEIN</small></b><b>~380<small>CALORIES</small></b></div></div></div></section>

    <section className="progress" id="progress"><div><p className="kicker">PROGRESS INTELLIGENCE</p><h2>DON'T JUST TRACK.<br/>UNDERSTAND.</h2><p>Your history becomes the signal that improves what comes next.</p></div><div className="chart"><div className="chart-head"><span>SQUAT FORM / 3 WEEKS</span><strong>+17 <small>PTS</small></strong></div><div className="bars"><i style={{height:"46%"}}><b>69</b></i><i style={{height:"68%"}}><b>77</b></i><i style={{height:"92%"}}><b>86</b></i></div><p>Your squat form has improved consistently over the last three weeks.</p></div></section>

    <section className="weekly"><p className="kicker">FITNOVA / REAL SIGNALS</p><div className="weekly-grid"><div><b>01</b><span>YOUR PROFILE</span></div><div><b>02</b><span>YOUR CHECK-INS</span></div><div><b>03</b><span>YOUR SESSIONS</span></div><div><b>04</b><span>YOUR FEEDBACK</span></div></div><div className="next"><span>WHAT CHANGES</span><p>Your next plan improves only from information you provide and activity you actually complete.</p></div></section>

    <section className="final-cta"><SilkAurora/><div><p className="kicker">START WHERE YOU ARE</p><h2>BUILD FITNESS<br/>AROUND YOUR LIFE.</h2><p>Start with the time, energy and equipment you have today.</p><button className="primary" onClick={()=>onView("signup")}>START WITH FITNOVA <Arrow/></button></div></section>
    <footer><div className="wordmark">FITNOVA<span>•</span></div><p>INTELLIGENT PERSONAL FITNESS COACH</p><nav><a href="#features">Features</a><a href="#form">Form coach</a><a href="#nutrition">Nutrition</a><a href="#progress">Progress</a><a href="#">Privacy</a></nav><small>© 2026 FITNOVA</small></footer>
  </main>;
}

export function FitNovaExperience() { const open=(view:"login"|"signup"|"app")=>location.assign(view==="app"?"/dashboard":`/${view}`); return <Landing onView={open}/>; }
