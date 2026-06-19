# 后端接入计划 — 对比分析与实施步骤

## 一、现有资产盘点

### 已有后端 Spring Boot 项目
| 路径 | 说明 |
|------|------|
| `F:\hanye-take-out-main\hanye-take-out-springboot3\server` | Spring Boot 3.x 后端 |
| 基础包 | `fun.cyhgraph` |
| 用户端 Controller | AddressBook, Cart, Category, Dish, Evaluate, Order, Setmeal, Shop, User |
| 数据库 | MySQL（有 `hanye-take-out.sql`） |

### 已有前端后端连接代码
| 路径 | 文件数 | 说明 |
|------|--------|------|
| `hanye-take-out-uniapp/src/utils/http.ts` | 1 | HTTP 封装 + Token 拦截器 |
| `hanye-take-out-uniapp/src/api/*.ts` | 11 | API 模块 |
| `hanye-take-out-uniapp/src/stores/modules/*.ts` | 3 | Pinia stores（含 persist） |
| `hanye-take-out-uniapp/src/types/*.d.ts` | 9 | TypeScript 类型定义 |

### 后端 API 端点清单（用户端，共 32+ 个）

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户 | POST | `/user/user/login` | 登录 |
| 用户 | GET | `/user/user/{id}` | 用户信息 |
| 用户 | PUT | `/user/user` | 更新用户 |
| 店铺 | GET | `/user/shop/status` | 店铺营业状态 |
| 分类 | GET | `/user/category/list` | 分类列表 |
| 菜品 | GET | `/user/dish/list/{id}` | 按分类查菜品 |
| 菜品 | GET | `/user/dish/dish/{id}` | 菜品详情 |
| 套餐 | GET | `/user/setmeal/list/{id}` | 按分类查套餐 |
| 套餐 | GET | `/user/setmeal/{id}` | 套餐详情 |
| 购物车 | POST | `/user/cart/add` | 加购物车 |
| 购物车 | PUT | `/user/cart/sub` | 减购物车 |
| 购物车 | GET | `/user/cart/list` | 购物车列表 |
| 购物车 | DELETE | `/user/cart/clean` | 清空购物车 |
| 订单 | POST | `/user/order/submit` | 提交订单 |
| 订单 | PUT | `/user/order/payment` | 支付 |
| 订单 | GET | `/user/order/unPayOrderCount` | 未支付数量 |
| 订单 | GET | `/user/order/orderDetail/{id}` | 订单详情 |
| 订单 | GET | `/user/order/historyOrders` | 历史订单 |
| 订单 | PUT | `/user/order/cancel/{id}` | 取消订单 |
| 订单 | POST | `/user/order/reOrder/{id}` | 再来一单 |
| 订单 | GET | `/user/order/reminder/{id}` | 催单 |
| 地址 | POST | `/user/address` | 新增地址 |
| 地址 | GET | `/user/address/default` | 默认地址 |
| 地址 | GET | `/user/address/list` | 地址列表 |
| 地址 | GET | `/user/address/{id}` | 地址详情 |
| 地址 | PUT | `/user/address` | 修改地址 |
| 地址 | PUT | `/user/address/default` | 设默认地址 |
| 地址 | DELETE | `/user/address/{id}` | 删除地址 |
| 评价 | POST | `/evaluate/submit` | 提交评价 |
| 评价 | GET | `/evaluate/list` | 评价列表 |
| 评价 | GET | `/evaluate/stats/{storeId}` | 店铺统计 |

---

## 二、差异分析：现有后端 vs 当前项目需求

### 1. 订单状态流转差异

| 当前项目（Mock） | 现有后端（Spring Boot） |
|------------------|------------------------|
| paid（待接单） | 2-待接单 |
| pending_pay（骑手已接单/待垫付） | ❌ 无此状态 |
| delivering（配送中） | 4-派送中 |
| delivered（已送达待确认） | ❌ 无此状态（直接到已完成） |
| completed（已完成） | 5-已完成 |
| — | 1-待付款 |
| — | 6-已取消 |

**结论**：当前项目有一个独特的「骑手垫付」流程（pending_pay），现有后端不支持。需要：
- 在现有后端基础上扩展骑手相关功能
- 或调整当前项目流程以适配后端

### 2. 骑手模块缺失

当前项目有完整的骑手流程（接单 → 垫付 → 配送 → 送达），但现有后端**没有骑手相关功能**。这是最大的差异。

### 3. 数据结构差异

| 当前项目 | 现有后端 |
|----------|----------|
| 商家直接展示（SHOPS数组） | 分类(Category)→菜品(Dish) 层级 |
| 无套餐概念 | 有套餐(Setmeal)概念 |
| 订单5状态流转 | 订单6状态（含取消） |
| 无支付模块 | 有支付接口 |
| 无评价模块 | 有完整评价系统 |
| 有消息通知 | 无消息表（可用WebSocket替代） |
| 骑手有垫付/收入 | 无骑手功能 |

---

## 三、整合方案

### 方案选择

由于现有后端功能与当前项目需求有较大差异（特别是骑手模块），建议采用**分层整合**策略：

### 步骤 1：复制基础设施（文件复制）

将以下文件从 `hanye-take-out-uniapp` 复制到 `my-vue3-project`，并适配 JavaScript（非 TypeScript）：

| 源文件 | 目标文件 | 操作 |
|--------|----------|------|
| `src/utils/http.ts` | `src/utils/http.js` | **已存在**，对比后更新 |
| `src/types/*.d.ts` | — | 读取类型参考，转为 JSDoc 注释 |
| `src/api/login.ts` | `src/api/login.js` | 新建 |
| `src/api/user.ts` | `src/api/user.js` | **已存在**，对比后更新 |
| `src/api/category.ts` | `src/api/category.js` | 新建 |
| `src/api/dish.ts` | `src/api/dish.js` | 新建 |
| `src/api/setmeal.ts` | `src/api/setmeal.js` | 新建 |
| `src/api/cart.ts` | `src/api/cart.js` | 新建 |
| `src/api/order.ts` | `src/api/order.js` | **已存在**，对比后更新 |
| `src/api/address.ts` | `src/api/address.js` | **已存在**，对比后更新 |
| `src/api/shop.ts` | `src/api/shop.js` | 新建 |
| `src/api/evaluate.ts` | `src/api/evaluate.js` | 新建 |
| `src/stores/modules/user.ts` | `src/stores/user.js` | **已存在**，重写为 persist 模式 |
| `src/main.ts` | `src/main.js` | 对比更新，注册 persist |

### 步骤 2：安装依赖

```bash
npm install pinia-plugin-persistedstate
```

### 步骤 3：更新核心文件

#### 3.1 更新 `src/utils/http.js`
参考 `http.ts` 的完整实现，当前已有基本结构，需要确保：
- 拦截器已注册（当前在模块顶层调用 `uni.addInterceptor`）
- Token 处理逻辑一致
- 响应拦截统一处理 401

#### 3.2 更新 `src/main.js`
添加 `pinia-plugin-persistedstate` 注册：
```javascript
import persist from 'pinia-plugin-persistedstate'
const pinia = createPinia()
pinia.use(persist)
```

#### 3.3 重写 `src/stores/user.js`
参考 `stores/modules/user.ts` 的 setup 语法 + persist 配置。

### 步骤 4：改造 Store 对接 API

#### 4.1 `stores/campus.js` → 拆分为多个 Store
当前的 `campus.js` 太庞大，建议参考后端模块拆分为：
- **`stores/user.js`** — 用户信息 + token（使用 persist）
- **`stores/cart.js`** — 购物车操作（调用 API）
- **`stores/order.js`** — 订单操作（调用 API）
- **`stores/shop.js`** — 商家/菜品数据（调用 API）

#### 4.2 核心数据流改造
```
当前: 页面 → campus store (mock数据)
目标: 页面 → store → API → 后端数据库
```

### 步骤 5：页面适配

| 页面 | 需要改造的内容 |
|------|---------------|
| `pages/login/index.vue` | 调用 `loginAPI(code)` 获取微信登录 |
| `pages/index/index.vue` | 调用分类/菜品API替代硬编码SHOPS |
| `pages/shop/index.vue` | 调用菜品API替代硬编码MENUS |
| `pages/cart/index.vue` | 调用购物车API |
| `pages/checkout/index.vue` | 调用提交订单API |
| `pages/orders/index.vue` | 调用历史订单API + 确认收货 |
| `pages/profile/index.vue` | 调用用户信息API |
| `pages/messages/index.vue` | 暂无后端API，保留mock或改用WebSocket |
| `pages/address/index.vue` | 调用地址API |
| `pages/rider-*.vue` | 骑手模块——需扩展后端 |

### 步骤 6：后端扩展（如需骑手功能）

当前后端缺少骑手模块，如需完整骑手功能，需要在 Spring Boot 中添加：
1. 订单接单/垫付/送达接口
2. 骑手账户表 + 接口
3. 消息通知表 + WebSocket 推送

---

## 四、实施顺序建议

```
阶段1（基础设施）
  ├── 安装 pinia-plugin-persistedstate
  ├── 更新 src/main.js 注册 persist
  ├── 更新 src/utils/http.js
  ├── 重写 src/stores/user.js（+ persist）
  └── 新建所有 API 模块文件

阶段2（核心业务）
  ├── 改造 stores/campus.js → 拆分 store
  ├── 改造 pages/index/index.vue（商家/菜品列表）
  ├── 改造 pages/shop/index.vue（菜品 + 购物车）
  ├── 改造 pages/cart/index.vue（购物车 API）
  ├── 改造 pages/checkout/index.vue（提交订单）
  └── 改造 pages/orders/index.vue（订单查询 + 确认）

阶段3（完善功能）
  ├── 改造 pages/login/index.vue（微信登录）
  ├── 改造 pages/address/index.vue（地址管理）
  ├── 改造 pages/profile/index.vue（用户信息）
  └── 改造 pages/messages/index.vue（消息）

阶段4（骑手模块扩展——可选）
  ├── 后端添加骑手相关接口
  ├── 改造 stores 添加骑手逻辑
  └── 改造 rider-* 页面
```
