# 模拟器无响应问题诊断与修复计划

## 问题概述

微信小程序模拟器长时间无响应，提示"请确认你的业务逻辑中是否有复杂运算，或者死循环"。

## 代码审查发现的问题

### 🔴 问题1（严重）：`document.querySelectorAll` 在微信小程序中不可用

**影响范围**：所有页面（共 9 个文件）

| 文件 | 行号 |
|------|------|
| `src/pages/index/index.vue` | 80-81 |
| `src/pages/shop/index.vue` | 86-87 |
| `src/pages/cart/index.vue` | 72-73 |
| `src/pages/checkout/index.vue` | 87-88 |
| `src/pages/orders/index.vue` | 85-86 |
| `src/pages/profile/index.vue` | 93-94 |
| `src/pages/messages/index.vue` | 56-57 |
| `src/pages/rider-active/index.vue` | 75-76 |
| `src/pages/rider-pending/index.vue` | 103-104 |
| `src/pages/rider-profile/index.vue` | 100-101 |

**问题代码片段**（每个页面的 `onShow` 中）：
```javascript
onShow(() => {
  const n = new Date()
  const h = n.getHours().toString().padStart(2, "0")
  const m = n.getMinutes().toString().padStart(2, "0")
  const els = document.querySelectorAll(".status-bar .time")
  els.forEach(el => { el.textContent = h + ":" + m })
})
```

**根因分析**：
- 微信小程序运行环境**没有** `document` 对象（没有 DOM/BOM API）
- 调用 `document.querySelectorAll()` 会抛出 `TypeError: Cannot read properties of undefined`
- 这个错误在每次页面 `onShow` 时都会触发，可能导致小程序运行时崩溃或进入异常状态
- **这是最可能导致模拟器无响应的原因**

### 🔴 问题2（严重）：模板中 `{{ time }}` 变量未定义

**影响范围**：以下页面在 `<text class="time">{{ time }}</text>` 中使用了未定义的变量 `time`：

| 文件 | 行号 |
|------|------|
| `src/pages/index/index.vue` | 3 |
| `src/pages/shop/index.vue` | 3 |
| `src/pages/cart/index.vue` | 3 |
| `src/pages/checkout/index.vue` | 3 |

其他页面（`orders/index.vue`、`profile/index.vue`、`messages/index.vue`、`rider-active/index.vue`、`rider-pending/index.vue`、`rider-profile/index.vue`）直接写死了 `12:00` 静态文本。

**根因分析**：
- `time` 在 `<script setup>` 中从未声明为 `ref` 或 `reactive`
- 尝试渲染未定义的变量可能导致 Vue 渲染异常或告警
- 它与问题1配合，原本意图是通过 DOM 操作更新状态栏时间，但 DOM API 不可用，导致时间无法显示

### 🟡 问题3（中等）：Shop 页面模板中直接调用函数

**文件**：`src/pages/shop/index.vue` 第 33 行

```vue
<span>{{ qty(m.id) }}</span>
```

**根因分析**：
- `qty()` 函数在模板中**每次渲染都会被调用**
- Vue 的响应式系统在状态变更时（如添加商品到购物车）会触发重新渲染
- 频繁调用 `store.getFoodQty()` 虽然是轻量操作，但每次渲染都会执行
- 同时 `inc()` 和 `dec()` 函数在操作后调用空的 `refreshQtys()` 函数，存在冗余调用

### 🟢 问题4（轻微）：AIChat 组件被重复实例化

**文件**：多个页面都引入了 `<AIChat />`

**说明**：
- 每个页面都独立创建 AIChat 实例
- 每个实例都有独立的 `messages` 响应式数组
- 不会导致死循环，但会额外消耗内存

## 修复计划

### 修复步骤

#### 步骤 1：修复状态栏时间显示机制（解决根本问题）

**目标**：移除所有 `document.querySelectorAll` 调用，改用 Vue 响应式数据驱动时间显示。

**修改方案**：
- 为每个页面添加 `const time = ref("")` 声明
- 在 `onShow` 中使用 `time.value = h + ":" + m` 更新响应式变量
- 移除所有 `document.querySelectorAll` 和 DOM 直接操作

**涉及文件**（共 10 个页面需要修复）：
1. `src/pages/index/index.vue`
2. `src/pages/shop/index.vue`
3. `src/pages/cart/index.vue`
4. `src/pages/checkout/index.vue`
5. `src/pages/orders/index.vue`（当前是 `12:00` 静态文本，建议改为动态）
6. `src/pages/profile/index.vue`（当前是 `12:00` 静态文本，建议改为动态）
7. `src/pages/messages/index.vue`（当前是 `12:00` 静态文本，建议改为动态）
8. `src/pages/rider-active/index.vue`（当前是 `12:00` 静态文本，建议改为动态）
9. `src/pages/rider-pending/index.vue`（当前是 `12:00` 静态文本，建议改为动态）
10. `src/pages/rider-profile/index.vue`（当前是 `12:00` 静态文本，建议改为动态）

#### 步骤 2：优化 Shop 页面模板性能

**目标**：减少模板中的函数调用，使用计算属性或缓存机制。

**修改方案**：
- 将 `qty(m.id)` 的调用方式改为使用 `computed` 生成一个 Map 或使用 `getFoodQty` 的返回值缓存
- 删除空的 `refreshQtys()` 函数
- 考虑为 `inc` 和 `dec` 添加 `@click.stop` 以防止事件冒泡

#### 步骤 3：统一状态栏时间组件（可选优化）

**目标**：将状态栏时间逻辑提取为可复用组件，减少代码重复。

**修改方案**：
- 创建一个 `StatusBar.vue` 组件
- 在所有页面中替换重复的状态栏代码

---

## 修复优先级

| 优先级 | 修复项 | 影响程度 |
|--------|--------|----------|
| P0 | 移除所有 `document.querySelectorAll` 调用 | 修复模拟器无响应问题 |
| P0 | 定义 `time` 响应式变量替代未定义变量 | 修复渲染异常 |
| P1 | 优化 shop 页面 qty 函数调用 | 提升渲染性能 |
| P2 | 提取复用 StatusBar 组件 | 代码可维护性 |
