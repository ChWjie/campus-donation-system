# Render + Vercel 部署文档

本文档对应仓库结构：

- 后端：`campus-donation-backend`
- 前端：`campus-donation-frontend`
- Render 蓝图：`render.yaml`

---

## 一、部署目标

- 后端部署到 Render（Spring Boot + MySQL）
- 前端部署到 Vercel（Vite + Vue3）

---

## 二、Render 部署（后端）

### 1) 一键蓝图部署（推荐）

1. 登录 Render 控制台。
2. 选择 **New +** → **Blueprint**。
3. 选择本 GitHub 仓库：`ChWjie/campus-donation-system`。
4. Render 会自动识别根目录的 `render.yaml`，创建：
   - `campus-donation-mysql`（Private Service，MySQL）
   - `campus-donation-backend`（Web Service，Spring Boot）
5. 点击创建并等待构建。

### 2) 部署完成后检查

1. 打开后端服务 URL（例如：`https://campus-donation-backend.onrender.com`）。
2. 访问健康检查：

```text
https://<你的后端域名>/api/public/health
```

返回 `code=200` 即代表后端在线。

3. API 文档地址：

```text
https://<你的后端域名>/api/swagger-ui.html
```

---

## 三、Vercel 部署（前端）

### 1) 在 Vercel 控制台导入仓库

1. 登录 Vercel 控制台。
2. 点击 **Add New...** → **Project**。
3. 选择仓库 `ChWjie/campus-donation-system`。
4. 关键设置：
   - **Root Directory**: `campus-donation-frontend`
   - Framework: 自动识别 Vite（仓库内有 `vercel.json`）

### 2) 配置前端环境变量

在 Vercel 项目 Settings → Environment Variables 添加：

- `VITE_API_BASE_URL` = `https://<你的Render后端域名>/api`

然后重新部署（Redeploy）。

---

## 四、联调验收

1. 打开 Vercel 前端域名，进入登录页。
2. 使用测试账号登录。
3. 在浏览器开发者工具 Network 检查：
   - 请求地址应为 `https://<Render后端域名>/api/...`
   - 无 CORS 报错。
4. 验证关键流程：
   - 捐赠者创建预约
   - 志愿者确认接收
   - 公益对接员完成对接并反馈
   - 证书下载

---

## 五、常见问题

### 1) Render 报数据库连接失败

检查后端环境变量是否由 `render.yaml` 自动注入：

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### 2) Vercel 前端调用到自身域名 `/api`，后端不通

通常是 `VITE_API_BASE_URL` 未设置。请在 Vercel 环境变量中补齐并重新部署。

### 3) Render 账号无法创建 Private Service（MySQL）

可改为外部 MySQL（云数据库）并在 Render 后端服务里手动设置上述三个数据源环境变量。

---

## 六、可选：Vercel CLI 部署

如果本地已登录 Vercel CLI，可在仓库根目录执行：

```bash
npx vercel --cwd campus-donation-frontend
npx vercel --cwd campus-donation-frontend --prod
```

如果提示需要设备登录，按 CLI 输出的 URL 和验证码完成授权即可。
