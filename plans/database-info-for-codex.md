# 数据库信息 — 给 Codex 改造用

## 数据库连接配置

```
数据库名: hanye_take_out
地址: localhost:3306
用户名: root
密码: root
```

## 当前表结构（共11张表）

### 1. employee — 后台管理员（原外卖员工）
```sql
id int PK AUTO_INCREMENT
name varchar(64) NOT NULL
account varchar(64) UNIQUE NOT NULL
password varchar(255) NOT NULL
phone varchar(16) NULL
age int NULL
gender tinyint NULL
pic longtext NULL
status tinyint NOT NULL DEFAULT 0  -- 1启用 0禁用
create_user int NOT NULL
update_user int NOT NULL
create_time datetime NOT NULL
update_time datetime NOT NULL
```

### 2. category — 商品分类（原外卖菜品/套餐分类）
```sql
id int PK AUTO_INCREMENT
name varchar(64) UNIQUE NOT NULL
type tinyint NOT NULL  -- 1=菜品分类 2=套餐分类 → 建议统一为1
sort int NOT NULL
status tinyint NOT NULL DEFAULT 1
create_user int NOT NULL
update_user int NOT NULL
create_time datetime NOT NULL
update_time datetime NOT NULL
```

### 3. dish — 商家表（原外卖菜品表 → 现在前台展示为商家）
```sql
id int PK AUTO_INCREMENT
name varchar(64) UNIQUE NOT NULL       -- 商家名称
pic longtext NULL                       -- 商家Logo
detail varchar(255) NOT NULL            -- 商家简介
price decimal(10,2) NOT NULL            -- 起送价
status tinyint NOT NULL DEFAULT 1       -- 营业状态
category_id int NOT NULL FK→category   -- 商家分类
create_user int NOT NULL
update_user int NOT NULL
create_time datetime NOT NULL
update_time datetime NOT NULL
```

### 4. dish_flavor — 商家服务标签（原外卖菜品口味）
```sql
id int PK AUTO_INCREMENT
name varchar(64) NOT NULL   -- 标签类别（如：服务方式、优惠活动）
list varchar(255) NOT NULL  -- 标签列表（如：["自取","配送"]）
dish_id int NOT NULL FK→dish.id
```

### 5. setmeal — 商品表（原外卖套餐 → 现在前台展示为商品）
```sql
id int PK AUTO_INCREMENT
name varchar(64) UNIQUE NOT NULL   -- 商品名称
pic longtext NULL                   -- 商品图片
detail varchar(255) NOT NULL        -- 商品描述
price decimal(10,2) NOT NULL        -- 销售价
status tinyint NOT NULL DEFAULT 1
category_id int NOT NULL FK→category  -- 商品分类
create_user int NOT NULL
update_user int NOT NULL
create_time datetime NOT NULL
update_time datetime NOT NULL
```

### 6. setmeal_dish — 商品关联表（原外卖套餐-菜品中间表）
```sql
id int PK AUTO_INCREMENT
name varchar(64) NOT NULL      -- 关联商品名称
price decimal(10,2) NOT NULL   -- 关联商品单价
copies int NOT NULL             -- 数量
dish_id int NOT NULL FK→dish.id    -- 关联的商家ID
setmeal_id int NOT NULL FK→setmeal.id  -- 商品ID
```

### 7. user — 微信用户表（不变）
```sql
id int PK AUTO_INCREMENT
name varchar(64) NULL
openid varchar(45) NOT NULL
phone varchar(11) NULL
gender tinyint NULL
id_number varchar(18) NULL
pic longtext NULL
create_time datetime NOT NULL
```

### 8. order_detail — 订单明细（基本不变）
```sql
id bigint PK AUTO_INCREMENT
name varchar(32) NULL
pic longtext NULL
order_id bigint NOT NULL
dish_id bigint NULL    -- 商家ID
setmeal_id bigint NULL -- 商品ID
dish_flavor varchar(50) NULL
number int NOT NULL DEFAULT 1
amount decimal(10,2) NOT NULL
```

### 9. orders — 订单表（保留跑腿配送字段）
```sql
id bigint PK AUTO_INCREMENT
number varchar(50) NULL
status int NOT NULL DEFAULT 1  -- 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
user_id bigint NOT NULL
address_book_id bigint NOT NULL
order_time datetime NOT NULL
checkout_time datetime NULL
pay_method int NOT NULL DEFAULT 1
pay_status tinyint NOT NULL DEFAULT 0
amount decimal(10,2) NOT NULL
remark varchar(100) NULL
phone varchar(11) NULL
address varchar(255) NULL
user_name varchar(32) NULL
consignee varchar(32) NULL
cancel_reason varchar(255) NULL
rejection_reason varchar(255) NULL
cancel_time datetime NULL
estimated_delivery_time datetime NULL
delivery_status tinyint NOT NULL DEFAULT 1
delivery_time datetime NULL
pack_amount int NULL
tableware_number int NULL
tableware_status tinyint NOT NULL DEFAULT 1
```

### 10. cart — 购物车（基本不变）
```sql
id bigint PK AUTO_INCREMENT
name varchar(32) NULL
pic longtext NULL
user_id bigint NOT NULL
dish_id bigint NULL      -- 商家ID
setmeal_id bigint NULL   -- 商品ID
dish_flavor varchar(50) NULL
number int NOT NULL DEFAULT 1
amount decimal(10,2) NOT NULL
create_time datetime NULL
```

### 11. address_book — 地址簿（不变）
```sql
id bigint PK AUTO_INCREMENT
user_id bigint NOT NULL
consignee varchar(50) NULL
gender tinyint NULL
phone varchar(11) NOT NULL
province_code varchar(12) NULL
province_name varchar(32) NULL
city_code varchar(12) NULL
city_name varchar(32) NULL
district_code varchar(12) NULL
district_name varchar(32) NULL
detail varchar(200) NULL
label varchar(100) NULL
is_default tinyint NOT NULL DEFAULT 0
```

## 需要 Codex 改造的点

1. 修改 `category.type` 字段含义：当前 1=菜品分类 2=套餐分类，建议统一改为 1=商家分类（去掉2）
2. 各表的 `dish_id`/`setmeal_id` 字段需要保持不动（业务含义已变但字段名不改也能用）
3. 如果要做彻底重构，可以重命名表名：`dish`→`merchant`，`setmeal`→`product` 等
