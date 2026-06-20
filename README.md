# 抱抱速达 - 智能外卖配送系统

## 项目简介

抱抱速达是一个基于Spring Boot 3 + Vue 3 + Uni-app的智能外卖配送系统，集成了AI智能分配、实时订单管理、多端支持等功能。系统采用前后端分离架构，支持Web管理端、移动端和骑手端，为外卖配送业务提供完整的解决方案。

## 技术栈

### 后端技术栈
- **框架**: Spring Boot 3.2.5
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **ORM**: MyBatis 3.0.3
- **安全**: JWT
- **AI集成**: LangChain4j
- **工具**: Lombok, FastJSON, Apache HttpClient

### 前端技术栈

#### Web管理端 (errand-vue3)
- **框架**: Vue 3.4.21
- **UI组件**: Element Plus 2.7.1
- **路由**: Vue Router 4.3.0
- **状态管理**: Pinia 2.1.7
- **构建工具**: Vite 5.2.8
- **图表**: ECharts 5.5.0
- **HTTP客户端**: Axios 1.6.8

#### 移动端 (errand-uniapp)
- **框架**: Uni-app 3.0.0
- **UI**: Uni-app原生组件
- **状态管理**: Pinia 2.1.7
- **构建工具**: Vite 5.2.8
- **支持平台**: H5、微信小程序、支付宝小程序等

## 系统架构

```
抱抱速达系统
├── errand-springboot3/          # 后端服务
│   ├── server/                  # 主服务模块
│   ├── common/                  # 公共模块
│   ├── pojo/                    # 数据模型
│   └── ai/                      # AI智能分配模块
├── errand-vue3/                 # Web管理端
└── errand-uniapp/               # 移动端应用
```

## 核心功能

### 1. 订单管理系统
- 订单创建、查询、取消、确认
- 订单状态实时跟踪（待付款、待接单、已接单、派送中、已完成、已取消）
- 订单统计分析
- AI智能骑手分配

### 2. 用户管理
- 用户注册、登录
- 用户信息管理
- 骑手管理（设为骑手、取消骑手）
- 用户订单历史

### 3. 骑手管理
- 骑手列表查看
- 骑手订单分配
- 骑手排班管理
- 骑手统计分析

### 4. AI智能分配
- 基于多因素的智能骑手分配
- 考虑距离、负载、效率等因素
- 分配日志记录

### 5. 数据统计
- 订单统计
- 销售统计
- 用户统计
- 骑手绩效统计

### 6. AI助手
- 内置AI聊天助手
- 智能客服功能
- 订单查询助手

## 数据库设计

### 主要数据表
- `user` - 用户表
- `order` - 订单表
- `order_detail` - 订单详情表
- `rider_schedule` - 骑手排班表
- `evaluate` - 评价表
- `dish` - 菜品表
- `setmeal` - 套餐表
- `category` - 分类表

### 订单状态枚举
```java
public static final Integer PENDING_PAYMENT = 1;    // 待付款
public static final Integer TO_BE_CONFIRMED = 2;   // 待接单
public static final Integer CONFIRMED = 3;         // 已接单
public static final Integer DELIVERY_IN_PROGRESS = 4; // 派送中
public static final Integer COMPLETED = 5;          // 已完成
public static final Integer CANCELLED = 6;         // 已取消
public static final Integer AI_ASSIGNING = 7;      // AI分配中
```

## 环境要求

### 开发环境
- **JDK**: 17+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Node.js**: 18+
- **Maven**: 3.6+

### 生产环境
- **服务器**: Linux/Windows Server
- **JDK**: 17+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Nginx**: 1.18+ (反向代理)

## 详细启动流程

### 第一步：环境准备

#### 1. 安装基础软件
```bash
# 安装JDK 17
# 下载地址: https://www.oracle.com/java/technologies/downloads/#java17

# 安装MySQL 8.0
# 下载地址: https://dev.mysql.com/downloads/mysql/

# 安装Redis 6.0+
# 下载地址: https://redis.io/download

# 安装Node.js 18+
# 下载地址: https://nodejs.org/

# 安装Maven 3.6+
# 下载地址: https://maven.apache.org/download.cgi/
```

#### 2. 数据库配置
```bash
# 启动MySQL服务
sudo systemctl start mysql  # Linux
# 或在Windows中通过服务管理器启动

# 创建数据库
mysql -u root -p
CREATE DATABASE hanye_take_out CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;

# 导入数据库结构（如果有的话）
mysql -u root -p hanye_take_out < database/schema.sql
```

#### 3. Redis配置
```bash
# 启动Redis服务
sudo systemctl start redis  # Linux
# 或在Windows中运行 redis-server.exe

# 测试Redis连接
redis-cli ping
# 应该返回 PONG
```

### 第二步：后端服务启动

#### 1. 配置数据库连接
编辑 `errand-springboot3/server/src/main/resources/application-dev.yml`:

```yaml
hanye:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/hanye_take_out?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
    username: root
    password: your_mysql_password  # 修改为你的MySQL密码
  redis:
    host: localhost
    port: 6380
    password: redis123  # 修改为你的Redis密码
    database: 1
```

#### 2. 配置微信支付（可选）
如果需要微信支付功能，请在 `application-dev.yml` 中配置微信相关参数：

```yaml
hanye:
  wechat:
    appid: your_wechat_appid
    secret: your_wechat_secret
    mchid: your_mchid
    mchSerialNo: your_mch_serial_no
    privateKeyFilePath: /path/to/your/apiclient_key.pem
    apiV3Key: your_api_v3_key
    weChatPayCertFilePath: /path/to/your/wechatpay_cert.pem
    notifyUrl: https://your-domain.com/notify/paySuccess
    refundNotifyUrl: https://your-domain.com/notify/refundSuccess
```

#### 3. 编译和启动后端服务

```bash
# 进入后端项目根目录
cd errand-springboot3

# 使用Maven编译项目
mvn clean compile

# 运行测试（可选）
mvn test

# 启动Spring Boot应用
mvn spring-boot:run

# 或者直接运行主类
# 在IDE中运行 ServerApplication.java
```

#### 4. 验证后端服务
```bash
# 检查服务是否启动成功
curl http://localhost:8081/admin/order/conditionSearch

# 应该返回类似以下的JSON响应
{
  "code": 1,
  "message": "查询成功",
  "data": {
    "records": [],
    "total": 0,
    "size": 10,
    "current": 1,
    "pages": 0
  }
}
```

### 第三步：Web管理端启动

#### 1. 安装依赖
```bash
# 进入Web管理端目录
cd errand-vue3

# 安装npm依赖
npm install

# 或使用yarn
yarn install
```

#### 2. 配置API地址
编辑 `errand-vue3/src/utils/request.ts`，确保API地址正确：

```typescript
const request = axios.create({
  baseURL: 'http://localhost:8081',  // 后端服务地址
  timeout: 15000
})
```

#### 3. 启动开发服务器
```bash
# 启动开发服务器
npm run dev

# 或使用yarn
yarn dev
```

#### 4. 访问Web管理端
打开浏览器访问：`http://localhost:5173`（或控制台显示的实际地址）

### 第四步：移动端启动

#### 1. 安装依赖
```bash
# 进入移动端目录
cd errand-uniapp

# 安装npm依赖
npm install

# 或使用yarn
yarn install
```

#### 2. 配置API地址
编辑 `errand-uniapp/src/utils/http.js`，确保API地址正确：

```javascript
const http = (options = {}) => {
  return new Promise((resolve, reject) => {
    uni.request({
      url: 'http://localhost:8081' + options.url,  // 后端服务地址
      method: options.method || 'GET',
      data: options.data || {},
      success: (res) => {
        resolve(res.data)
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}
```

#### 3. 启动开发服务器
```bash
# 启动H5版本开发服务器
npm run dev:h5

# 或启动微信小程序版本
npm run dev:mp-weixin
```

#### 4. 访问移动端
- **H5版本**: 打开浏览器访问 `http://localhost:3000`（或控制台显示的实际地址）
- **微信小程序**: 在微信开发者工具中导入项目

### 第五步：系统初始化

#### 1. 创建管理员账户
系统启动后，首次访问需要创建管理员账户：

```bash
# 访问登录页面
http://localhost:5173/login

# 点击"注册"按钮创建管理员账户
# 填写用户名、密码等信息
```

#### 2. 配置基础数据
登录管理后台后，需要配置以下基础数据：

1. **商家信息**: 商店名称、地址、联系方式等
2. **菜品分类**: 创建菜品分类
3. **菜品管理**: 添加菜品信息
4. **骑手管理**: 添加骑手信息
5. **系统设置**: 配置配送费、起送价等

#### 3. 测试功能
1. **用户端测试**:
   - 用户注册登录
   - 浏览商家和菜品
   - 下单支付
   - 查看订单状态

2. **骑手端测试**:
   - 骑手登录
   - 查看待接订单
   - 接单和派送
   - 完成订单

3. **管理端测试**:
   - 订单管理
   - 用户管理
   - 骑手管理
   - 数据统计

## 常见问题解决

### 1. 数据库连接失败
```bash
# 检查MySQL服务是否启动
sudo systemctl status mysql

# 检查数据库配置
# 确保数据库名称、用户名、密码正确

# 测试数据库连接
mysql -u root -p -h localhost
```

### 2. Redis连接失败
```bash
# 检查Redis服务是否启动
sudo systemctl status redis

# 检查Redis配置
# 确保host、port、password正确

# 测试Redis连接
redis-cli -h localhost -p 6380 -a redis123 ping
```

### 3. 前端页面无法访问后端API
```bash
# 检查后端服务是否启动
curl http://localhost:8081/admin/order/conditionSearch

# 检查CORS配置
# 确保后端允许前端域名访问

# 检查API地址配置
# 确保前端request.ts中的baseURL正确
```

### 4. 端口冲突
```bash
# 查看端口占用
netstat -ano | findstr :8081  # Windows
lsof -i :8081                 # Linux

# 修改端口配置
# 在application.yml中修改server.port
```

## 开发指南

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用ESLint + Prettier进行代码格式化
- 遵循Vue 3组合式API规范

### 提交规范
```bash
# 提交格式
git commit -m "feat: 添加新功能"
git commit -m "fix: 修复bug"
git commit -m "docs: 更新文档"
git commit -m "style: 代码格式化"
git commit -m "refactor: 重构代码"
git commit -m "test: 添加测试"
git commit -m "chore: 构建过程或辅助工具的变动"
```

### 开发流程
1. 创建功能分支
2. 开发功能
3. 提交代码
4. 创建Pull Request
5. 代码审查
6. 合并到主分支

## 部署指南

### Docker部署（可选）

#### 1. 创建Dockerfile
```dockerfile
# 后端Dockerfile
FROM openjdk:17-jdk-slim
COPY target/hanye-take-out-1.0-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### 2. 构建和运行
```bash
# 构建镜像
docker build -t hanye-take-out .

# 运行容器
docker run -d -p 8081:8081 --name hanye-take-out hanye-take-out
```

### 生产环境部署

#### 1. 后端部署
```bash
# 编译项目
mvn clean package

# 复制jar包到服务器
scp target/hanye-take-out-1.0-SNAPSHOT.jar user@server:/opt/

# 使用systemd管理服务
sudo vim /etc/systemd/system/hanye-take-out.service
```

#### 2. 前端部署
```bash
# 构建生产版本
npm run build

# 复制dist文件到Nginx目录
sudo cp -r dist/* /var/www/html/
```

## 联系方式

如有问题或建议，请联系：
- 邮箱: your-email@example.com
- 项目地址: https://github.com/your-username/hanye-take-out
- 文档地址: https://your-docs-site.com

## 许可证

本项目采用 MIT 许可证。详情请参阅 [LICENSE](LICENSE) 文件。

---

**注意**: 本项目仅供学习和参考使用，请勿直接用于商业生产环境。在生产环境使用前，请确保进行充分的安全测试和性能优化。
