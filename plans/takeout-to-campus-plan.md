# 外卖项目 → 校园跑腿项目 改造计划

## 一、当前项目中的"外卖痕迹"

### 1. 数据库层
| 项目 | 当前（外卖） | 目标（校园跑腿） |
|------|-------------|----------------|
| 数据库名 | `hanye_take_out` | 需改名 |
| `dish` 表 | 菜品表 | 改为任务表 |
| `setmeal` 表 | 套餐表 | 改为任务套餐/类型 |
| `category` 表 | 菜品/套餐分类 | 改为任务分类 |
| `dish_flavor` 表 | 菜品口味 | 废弃或改为任务选项 |
| `setmeal_dish` 表 | 套餐-菜品关联 | 废弃或改为任务子项 |
| `employee` 表 | 餐厅员工 | 改为平台管理员 |
| `orders` 表 | 外卖订单 | 改为跑腿订单 |
| `order_detail` 表 | 订单明细 | 改为任务明细 |
| `store_rating_stats` 表 | 店铺评分 | 改为骑手/任务评分 |

### 2. 后端 Java 代码
| 包/类 | 当前（外卖） | 目标（校园跑腿） |
|-------|-------------|----------------|
| `entity/Dish.java` | 菜品实体 | 改为任务实体 |
| `entity/Setmeal.java` | 套餐实体 | 改为任务模板 |
| `entity/DishFlavor.java` | 菜品口味 | 废弃 |
| `entity/SetmealDish.java` | 套餐关联 | 废弃 |
| `entity/Category.java` | 分类（菜品/套餐） | 改为任务分类 |
| `entity/StoreRatingStats.java` | 店铺评分 | 改为跑腿评分 |
| `entity/Employee.java` | 员工 | 改为管理员(Admin) |
| `controller/admin/DishController.java` | 菜品管理 | 改为任务管理 |
| `controller/admin/SetmealController.java` | 套餐管理 | 废弃或合并 |
| `controller/admin/CategoryController.java` | 分类管理 | 改为任务分类管理 |
| `controller/admin/ShopController.java` | 店铺开关 | 改为系统配置 |
| `controller/user/DishController.java` | 用户端菜品 | 改为用户端任务 |
| `controller/user/CartController.java` | 购物车 | 改为任务委托单 |
| `service/` 相关接口 | 菜品/套餐服务 | 改为任务服务 |

### 3. 前端管理后台 `hanye-take-out-vue3/`
| 页面 | 当前功能 | 改造方向 |
|------|---------|---------|
| `views/dish/` | 菜品管理 CRUD | 改为任务类型管理 |
| `views/setmeal/` | 套餐管理 | 废弃或合并 |
| `views/category/` | 分类管理 | 改为任务分类 |
| `views/employee/` | 员工管理 | 改为管理员/骑手管理 |
| `views/order/` | 订单管理 | 继续保留改造 |
| `views/statistics/` | 营业统计 | 改为平台统计 |
| `views/dashboard/` | 数据看板 | 改为任务看板 |

### 4. 小程序前端 `my-vue3-project/`
这部分已经部分改造为校园风格，但仍有：
| 页面 | 当前 | 改造方向 |
|------|------|---------|
| `pages/shop/index.vue` | 商家列表 | 改为任务大厅/发布 |
| `pages/cart/index.vue` | 购物车 | 改为我的委托 |
| `pages/checkout/index.vue` | 结算页 | 改为确认委托 |
| `pages/index/index.vue` | 首页 | 改为任务广场 |
| 各类API | 获取菜品/套餐 | 改为获取任务 |

## 二、改造方案选择

### 方案 A：渐进式改造（推荐）
不急于一次性重命名所有表，先确保功能跑通，再逐步优化命名：

**阶段 1（核心功能）：**
1. 数据库新增 `task` 表替代 `dish` 的业务含义
2. 新增任务发布/接单 API
3. 小程序端 `shop` 改为任务大厅
4. 小程序端购物车改造

**阶段 2（管理后台）：**
1. 管理后台的菜品管理改为任务管理
2. 分类管理改为任务分类
3. 员工管理改为骑手管理

**阶段 3（收尾优化）：**
1. 数据库重命名
2. 清理废弃代码
3. 项目重命名

### 方案 B：彻底重写（大工程）
一次性将所有表、实体、控制器全部重命名，风险较高。

## 三、需要你确认的问题

1. **改造范围：** 你主要想改造哪个部分？后端？管理后台？小程序？
2. **核心功能：** 校园跑腿的核心流程是什么？（发布任务 → 抢单 → 完成 → 结算？）
3. **任务类型：** 需要哪些任务分类？（代取快递、代买物品、代排队、代办文件等）
4. **骑手机制：** 是用户发布任务后骑手抢单，还是系统指派？
5. **管理后台：** 还需要保留管理后台吗？如果需要，需要管理哪些内容？
