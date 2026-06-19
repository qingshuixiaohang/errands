<script setup>
import { onLaunch } from "@dcloudio/uni-app"
onLaunch(() => { console.log("校园外跑 launched") })
</script>

<style>
/* ===== CSS Variables ===== */
page {
  --bg: #FAFAFA;
  --surface: #FFFFFF;
  --fg: #111111;
  --muted: #6B6B6B;
  --border: #E5E5E5;
  --accent: #2F6FEB;
  --accent-light: #EBF1FF;
  --success: #17A34A;
  --warn: #EAB308;
  --danger: #DC2626;
  --font: -apple-system, system-ui, sans-serif;
  --radius: 12px;
  --radius-sm: 8px;
  --safe-top: 12px;
  --safe-bottom: 8px;
}

/* ===== Base Reset ===== */
page {
  font-family: var(--font);
  background: var(--bg);
  color: var(--fg);
  -webkit-font-smoothing: antialiased;
  user-select: none;
}

view, text, scroll-view, image, input, textarea { box-sizing: border-box; }

/* ===== Animations ===== */
@keyframes slideIn {
  from { transform: translateX(100%); opacity: .4; }
  to { transform: translateX(0); opacity: 1; }
}
@keyframes slideBack {
  from { transform: translateX(-30%); opacity: .4; }
  to { transform: translateX(0); opacity: 1; }
}
@keyframes fadeSlideIn {
  from { transform: translateY(10px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.slide-enter { animation: slideIn .35s cubic-bezier(.16,1,.3,1) forwards; }
.slide-back { animation: slideBack .3s cubic-bezier(.16,1,.3,1) forwards; }
.fade-enter { animation: fadeSlideIn .3s ease forwards; }

/* ===== Status Bar ===== */
.status-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 20px 4px;
  padding-top: var(--safe-top);
  font-size: 12px;
  font-weight: 600;
  color: var(--muted);
  background: var(--bg);
  flex-shrink: 0;
}
.status-bar .time { color: var(--fg); }
.status-icons svg { width: 16px; height: 12px; stroke: var(--muted); fill: none; }

/* ===== Header ===== */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 20px;
  background: var(--bg);
  flex-shrink: 0;
}
.header h1 {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -.02em;
}
.header .back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 500;
  color: var(--muted);
  cursor: pointer;
  background: none;
  border: none;
  padding: 4px 0;
  font-family: var(--font);
}
.header .back-btn svg {
  width: 20px;
  height: 20px;
  stroke: currentColor;
  fill: none;
}

/* ===== Bottom Nav ===== */
.bottom-nav {
  display: flex;
  align-items: center;
  justify-content: space-around;
  height: 62px;
  padding: 0 8px;
  padding-bottom: var(--safe-bottom);
  background: var(--surface);
  border-top: 1px solid var(--border);
  flex-shrink: 0;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 50;
}
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  flex: 1;
  height: 100%;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--muted);
  font-size: 10px;
  font-weight: 500;
  font-family: var(--font);
  padding: 0;
  transition: color .2s;
}
.nav-item svg {
  width: 22px;
  height: 22px;
  stroke: currentColor;
  fill: none;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}
.nav-item.active { color: var(--accent); }
.nav-item.active svg { stroke: var(--accent); }

/* ===== Search Bar ===== */
.search-bar {
  margin: 8px 20px 12px;
  padding: 10px 16px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--muted);
  font-size: 14px;
}
.search-bar svg {
  width: 18px;
  height: 18px;
  stroke: var(--muted);
  fill: none;
  flex-shrink: 0;
}

/* ===== Shop Card ===== */
.shop-card {
  display: flex;
  gap: 12px;
  background: var(--surface);
  border-radius: var(--radius);
  padding: 14px;
  margin: 0 20px 12px;
  border: 1px solid var(--border);
  cursor: pointer;
  transition: transform .12s;
}
.shop-card:active { transform: scale(.98); }
.shop-card .thumb {
  width: 64px;
  height: 64px;
  border-radius: 10px;
  background: var(--accent-light);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: var(--accent);
}
.shop-card .info { flex: 1; min-width: 0; }
.shop-card .info h3 { font-size: 15px; font-weight: 600; margin-bottom: 2px; }
.shop-card .info .tags { display: flex; gap: 4px; flex-wrap: wrap; margin-bottom: 4px; }
.shop-card .info .tags span { font-size: 10px; background: var(--accent-light); color: var(--accent); padding: 1px 6px; border-radius: 6px; }
.shop-card .info .meta { display: flex; gap: 12px; font-size: 12px; color: var(--muted); }
.shop-card .info .meta .rating { color: var(--warn); font-weight: 600; }

/* ===== Food Item ===== */
.food-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}
.food-item .info { flex: 1; }
.food-item .info h4 { font-size: 14px; font-weight: 500; }
.food-item .info .desc { font-size: 12px; color: var(--muted); margin-top: 2px; }
.food-item .price { font-size: 16px; font-weight: 700; color: var(--accent); }
.qty-control { display: flex; align-items: center; gap: 6px; }
.qty-control button {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  border: 1px solid var(--border);
  background: var(--surface);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: var(--fg);
  font-family: var(--font);
}
.qty-control button:active { background: var(--border); }
.qty-control span { font-size: 15px; font-weight: 500; min-width: 20px; text-align: center; }

/* ===== Cart Bar (floating) ===== */
.cart-bar {
  position: fixed;
  bottom: 70px;
  left: 50%;
  transform: translateX(-50%);
  max-width: 390px;
  width: calc(100% - 40px);
  background: var(--fg);
  color: white;
  border-radius: 24px;
  padding: 12px 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 50;
  box-shadow: 0 4px 20px rgba(0,0,0,.12);
  cursor: pointer;
  transition: transform .15s;
}
.cart-bar:active { transform: translateX(-50%) scale(.97); }
.cart-bar .left { display: flex; align-items: center; gap: 10px; }
.cart-bar .left .icon-badge { position: relative; }
.cart-bar .left .icon-badge svg { width: 24px; height: 24px; stroke: white; fill: none; }
.cart-bar .left .badge {
  position: absolute;
  top: -4px;
  right: -6px;
  background: var(--danger);
  color: white;
  font-size: 10px;
  font-weight: 700;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cart-bar .total { font-size: 16px; font-weight: 700; }

/* ===== Cart Item ===== */
.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}
.cart-item .info { flex: 1; }
.cart-item .info h4 { font-size: 14px; font-weight: 500; }
.cart-item .price { font-size: 15px; font-weight: 700; color: var(--accent); min-width: 50px; }
.cart-item .qty-control { display: flex; align-items: center; gap: 6px; }
.cart-item .qty-control button {
  width: 26px; height: 26px; border-radius: 50%;
  border: 1px solid var(--border);
  background: var(--surface);
  cursor: pointer;
  font-size: 16px;
  font-family: var(--font);
}
.cart-item .qty-control span { min-width: 20px; text-align: center; font-weight: 500; }

/* ===== Checkout ===== */
.checkout-section { padding: 20px; }
.checkout-section h3 { font-size: 16px; font-weight: 600; margin-bottom: 8px; }
.order-summary {
  background: var(--surface);
  border-radius: var(--radius);
  padding: 16px;
  border: 1px solid var(--border);
}
.order-summary .row { display: flex; justify-content: space-between; font-size: 14px; padding: 4px 0; }
.order-summary .total {
  font-size: 18px;
  font-weight: 700;
  border-top: 1px solid var(--border);
  padding-top: 8px;
  margin-top: 8px;
}
.pay-methods { margin-top: 16px; }
.pay-methods label {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
  font-size: 14px;
  cursor: pointer;
}
.pay-methods input[type="radio"] { accent-color: var(--accent); }

/* ===== Order Card ===== */
.order-card {
  background: var(--surface);
  border-radius: var(--radius);
  margin: 0 20px 12px;
  border: 1px solid var(--border);
  overflow: hidden;
}
.order-card .top {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
  font-size: 12px;
  font-weight: 600;
}
.order-card .top.status-paid { background: var(--accent-light); color: var(--accent); }
.order-card .top.status-delivering { background: #FFF7ED; color: #C2410C; }
.order-card .top.status-delivered { background: #F0FDF4; color: var(--success); }
.order-card .top.status-completed { background: var(--border); color: var(--muted); }
.order-card .content { padding: 12px 16px; }
.order-card .content .shop { font-weight: 600; font-size: 14px; }
.order-card .content .items { font-size: 12px; color: var(--muted); margin-top: 4px; }
.order-card .content .amount { font-size: 16px; font-weight: 700; color: var(--accent); margin-top: 6px; }
.order-card .actions { display: flex; gap: 8px; padding: 0 16px 12px; }
.order-card .actions button {
  flex: 1;
  padding: 10px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 600;
  font-family: var(--font);
  cursor: pointer;
  transition: background .1s;
}

/* ===== Buttons ===== */
.btn-outline {
  background: none;
  border: 1px solid var(--border);
  color: var(--fg);
}
.btn-outline:active { background: var(--bg); }
.btn-solid {
  background: var(--accent);
  border: none;
  color: white;
}
.btn-solid:active { opacity: .85; }
.btn-success {
  background: var(--success);
  border: none;
  color: white;
}
.btn-success:active { opacity: .85; }
.btn-warn {
  background: #C2410C;
  border: none;
  color: white;
}
.btn-warn:active { opacity: .85; }

/* ===== Rider Card ===== */
.rider-card {
  background: var(--surface);
  border-radius: var(--radius);
  margin: 0 20px 12px;
  border: 1px solid var(--border);
  padding: 16px;
}
.rider-card .order-id { font-size: 13px; color: var(--muted); margin-bottom: 6px; }
.rider-card .rider-action { margin-top: 10px; }
.rider-card .rider-action button {
  width: 100%;
  padding: 12px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 600;
  font-family: var(--font);
  cursor: pointer;
  border: none;
}
.rider-card .rider-action button:active { opacity: .85; }

/* ===== Login Page ===== */
.login-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px 32px;
  min-height: 100vh;
}
.login-logo {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  background: var(--accent);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}
.login-logo svg { width: 32px; height: 32px; stroke: white; fill: none; stroke-width: 2; }
.login-page h2 { font-size: 24px; font-weight: 700; }
.login-page .sub { color: var(--muted); font-size: 14px; margin-bottom: 28px; }
.role-btn {
  width: 100%;
  padding: 16px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: var(--surface);
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: all .1s;
  margin-bottom: 12px;
  font-family: var(--font);
  text-align: left;
}
.role-btn:active { border-color: var(--accent); background: var(--accent-light); }
.role-btn .icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.role-btn .icon.customer { background: #F0F9FF; color: var(--accent); }
.role-btn .icon.rider { background: #FFF7ED; color: #C2410C; }
.role-btn .info h4 { font-size: 15px; font-weight: 600; }
.role-btn .info p { font-size: 12px; color: var(--muted); }

/* ===== Profile ===== */
.profile-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}
.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--accent-light);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accent);
  font-size: 20px;
  font-weight: 700;
  flex-shrink: 0;
}
.profile-info h2 { font-size: 16px; font-weight: 700; }
.profile-info .sub { font-size: 12px; color: var(--muted); margin-top: 2px; }
.menu-list { padding: 8px 0; background: var(--surface); margin-top: 12px; }
.menu-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 20px;
  font-size: 14px;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
  cursor: pointer;
  color: var(--fg);
  font-family: var(--font);
  transition: background .1s;
}
.menu-item:active { background: var(--bg); }
.menu-item svg { width: 20px; height: 20px; stroke: var(--muted); fill: none; flex-shrink: 0; }
.btn-switch {
  width: calc(100% - 40px);
  margin: 24px 20px 40px;
  padding: 14px;
  background: none;
  border: 1px solid var(--accent);
  border-radius: var(--radius-sm);
  color: var(--accent);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  font-family: var(--font);
  transition: background .1s;
}
.btn-switch:active { background: var(--accent-light); }

/* ===== Rider Stats ===== */
.rider-stats {
  display: flex;
  gap: 16px;
  padding: 16px 20px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
  justify-content: space-around;
}
.rider-stats .item { text-align: center; }
.rider-stats .item .num { font-size: 18px; font-weight: 700; }
.rider-stats .item .label { font-size: 11px; color: var(--muted); }
.rider-stats .item .num.warn { color: #C2410C; }

/* ===== Stats Row (Profile) ===== */
.stats-row {
  display: flex;
  justify-content: space-around;
  padding: 16px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}
.stats-row .item { text-align: center; }
.stats-row .item .num { font-size: 18px; font-weight: 700; }
.stats-row .item .label { font-size: 12px; color: var(--muted); }

/* ===== Messages ===== */
.msg-list { padding: 0; }
.msg-item {
  display: flex;
  gap: 12px;
  padding: 14px 20px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}
.msg-item .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--accent);
  flex-shrink: 0;
  margin-top: 6px;
}
.msg-item .dot.read { background: var(--border); }
.msg-item .content { flex: 1; }
.msg-item .content .title { font-size: 14px; font-weight: 500; }
.msg-item .content .body { font-size: 12px; color: var(--muted); margin-top: 2px; }
.msg-item .content .time { font-size: 11px; color: var(--border); margin-top: 4px; }

/* ===== AI Float Button（毛玻璃） ===== */
.ai-float-btn {
  position: fixed;
  right: 20px;
  bottom: 80px;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: rgba(47,111,235,0.7);
  -webkit-backdrop-filter: blur(12px);
  backdrop-filter: blur(12px);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(47,111,235,0.35), 0 0 0 1px rgba(255,255,255,0.2) inset;
  border: none;
  transition: all .25s cubic-bezier(.16,1,.3,1);
}
.ai-float-btn:active { transform: scale(.9); }
.ai-float-btn svg { width: 22px; height: 22px; stroke: white; fill: none; stroke-width: 2; stroke-linecap: round; stroke-linejoin: round; }

/* ===== AI Panel ===== */
.ai-panel {
  position: fixed;
  right: 16px;
  bottom: 80px;
  width: 320px;
  max-width: calc(100vw - 32px);
  height: 460px;
  background: var(--surface);
  border-radius: 16px;
  border: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  z-index: 110;
  box-shadow: 0 8px 30px rgba(0,0,0,.12);
  overflow: hidden;
  opacity: 0;
  pointer-events: none;
  transform: translateY(12px) scale(.96);
  transition: opacity .25s, transform .25s cubic-bezier(.16,1,.3,1);
}
.ai-panel.open { opacity: 1; pointer-events: all; transform: translateY(0) scale(1); }

.ai-panel .ai-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border);
}
.ai-panel .ai-header .ai-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--accent-light);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
}
.ai-panel .ai-header .ai-avatar svg { width: 18px; height: 18px; stroke: var(--accent); fill: none; }
.ai-panel .ai-header .ai-title { font-size: 14px; font-weight: 600; flex: 1; }
.ai-panel .ai-header .close-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--bg);
  border: none;
  cursor: pointer;
  color: var(--muted);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  transition: background .15s;
}
.ai-panel .ai-header .close-btn:active { background: var(--border); }
.ai-panel .ai-header .close-btn svg { width: 16px; height: 16px; stroke: currentColor; fill: none; }

.ai-panel .msg-area {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.ai-panel .msg-area .msg { display: flex; }
.ai-panel .msg-area .msg.ai { justify-content: flex-start; }
.ai-panel .msg-area .msg.user { justify-content: flex-end; }
.ai-panel .msg-area .msg .bubble {
  max-width: 80%;
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 13px;
  line-height: 1.45;
  white-space: pre-wrap;
  word-break: break-word;
}
.ai-panel .msg-area .msg.ai .bubble { background: var(--bg); color: var(--fg); border-bottom-left-radius: 4px; }
.ai-panel .msg-area .msg.user .bubble { background: var(--accent); color: white; border-bottom-right-radius: 4px; }

.ai-panel .input-area {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-top: 1px solid var(--border);
}
.ai-panel .input-area input {
  flex: 1;
  border: none;
  padding: 8px 4px;
  font-size: 14px;
  font-family: var(--font);
  outline: none;
  background: transparent;
  color: var(--fg);
}
.ai-panel .input-area .send-btn {
  background: var(--accent);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  margin-left: 8px;
  transition: opacity .1s;
}
.ai-panel .input-area .send-btn:active { opacity: .8; }

/* ===== Toast ===== */
.toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%,-50%) scale(.9);
  background: var(--fg);
  color: white;
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  opacity: 0;
  pointer-events: none;
  transition: opacity .25s, transform .25s cubic-bezier(.16,1,.3,1);
  z-index: 200;
}
.toast.show { opacity: 1; transform: translate(-50%,-50%) scale(1); }

/* ===== Page Spacer ===== */
.page-spacer { height: 80px; }
</style>
