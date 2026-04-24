# 校园旧物公益捐赠回收管理系统

> 基于 Spring Boot 3.x + Vue 3 的前后端分离校园公益捐赠管理平台

## 项目简介

本系统是一个完整的校园旧物公益捐赠回收管理平台,支持捐赠者、志愿者、公益对接员和管理员四种角色,实现从捐赠预约、旧物接收、整理分类、公益对接到证明颁发的完整流程。

### 技术栈

**后端:**
- Spring Boot 3.2.0
- MyBatis-Plus 3.5.5
- MySQL 8.x
- Redis
- Spring Security + JWT
- Swagger/OpenAPI 3.0

**前端:**
- Vue 3.4 (Composition API)
- Vite 5.0
- TypeScript 5.3
- Element Plus 2.5
- Pinia 2.1
- Axios 1.6
- ECharts 5.4

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

### 1. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source campus-donation-backend/src/main/resources/db/schema.sql
```

### 2. 启动后端

```bash
cd campus-donation-backend

# 修改 application.yml 中的数据库密码
# spring.datasource.password: your_password

# 启动Redis
redis-server

# 启动Spring Boot
mvn clean install
mvn spring-boot:run
```

后端启动成功后访问: http://localhost:8080/api/swagger-ui.html

### 3. 启动前端

```bash
cd campus-donation-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动成功后访问: http://localhost:5173

## 功能特性

### 已实现功能 ✅

- **用户认证**: JWT无状态认证、BCrypt密码加密
- **角色管理**: 4种用户角色(捐赠者、志愿者、公益对接员、管理员)
- **权限控制**: 基于角色的路由守卫和API权限控制
- **用户注册**: 支持用户名唯一性校验、密码强度验证
- **用户登录**: 自动跳转到对应角色首页
- **响应式UI**: 基于Element Plus的现代化界面

### 待开发功能 📋

- 捐赠预约管理(预约码生成、二维码展示)
- 旧物管理(志愿者扫码确认、状态流转)
- 公益对接(对接申请、受赠方反馈)
- 电子证明生成(PDF证书下载)
- 系统通知推送
- 数据统计看板(ECharts可视化)

## 项目结构

```
.
├── campus-donation-backend/          # 后端Spring Boot项目
│   ├── src/main/java/
│   │   └── com/campus/donation/
│   │       ├── common/               # 通用组件(响应体、异常、枚举)
│   │       ├── config/               # 配置类(Security、MyBatis-Plus)
│   │       ├── module/               # 业务模块
│   │       │   └── user/             # 用户模块
│   │       └── utils/                # 工具类(JWT)
│   └── src/main/resources/
│       ├── application.yml           # 配置文件
│       └── db/schema.sql             # 数据库初始化脚本
│
└── campus-donation-frontend/         # 前端Vue 3项目
    ├── src/
    │   ├── api/                      # API接口封装
    │   ├── stores/                   # Pinia状态管理
    │   ├── router/                   # 路由配置
    │   ├── layouts/                  # 布局组件
    │   └── views/                    # 页面组件
    │       ├── auth/                 # 登录注册
    │       ├── donor/                # 捐赠者页面
    │       ├── volunteer/            # 志愿者页面
    │       ├── charity/              # 公益对接员页面
    │       └── admin/                # 管理员页面
    └── vite.config.ts                # Vite配置
```

## 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 系统预置管理员账号 |

其他角色请通过注册页面创建。

## API文档

启动后端后访问 Swagger UI: http://localhost:8080/api/swagger-ui.html

主要接口:
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

## 开发指南

### 后端开发

1. 在 `module` 目录下创建新的业务模块
2. 遵循 `entity -> mapper -> service -> controller` 的分层结构
3. 使用 `@PreAuthorize` 进行方法级权限控制
4. 统一使用 `R<T>` 包装响应数据

### 前端开发

1. 在 `api` 目录下定义接口函数
2. 使用 TypeScript 定义接口类型
3. 在 `views` 目录下创建页面组件
4. 在 `router/index.ts` 中配置路由和权限

## 部署说明

> Render + Vercel 的完整部署步骤见：`docs/DEPLOY_RENDER_VERCEL.md`

### 后端部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/campus-donation-backend-1.0.0.jar
```

### 前端部署

```bash
# 构建
npm run build

# 将 dist 目录部署到 Nginx
```

### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /var/www/campus-donation-frontend/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 许可证

MIT License

## 联系方式

如有问题或建议,请提交 Issue。

---

**开发时间**: 2026年2月  
**版本**: v1.0.0
