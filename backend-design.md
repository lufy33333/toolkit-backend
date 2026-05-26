# Toolkit 后端 Spring Boot 工程设计文档

## 1. 项目概述

### 1.1 项目简介
Toolkit 是一个在线工具集合平台，提供 BMI 计算、货币转换、密码生成等实用工具。用户可以注册账号、收藏工具、查看使用历史。

### 1.2 项目目标
- 实现用户注册、登录、认证功能
- 提供工具收藏和历史记录功能
- 与前端 React 应用集成

### 1.3 前端技术栈
- React 18 + TypeScript
- Vite 构建工具
- React Router 路由
- Zustand 状态管理
- Tailwind CSS 样式

### 1.4 前端 API 配置
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## 2. 技术架构

### 2.1 后端技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | 编程语言 |
| Spring Boot | 3.2.x | Web 框架 |
| Spring Security | 6.x | 安全框架 |
| Spring Data JPA | 3.x | ORM 框架 |
| PostgreSQL | 15+ | 数据库 |
| JWT | - | 身份认证 |
| Lombok | - | 简化代码 |

### 2.2 项目结构
```
toolkit-backend/
├── src/main/java/com/toolkit/
│   ├── ToolkitApplication.java          # 应用入口
│   ├── config/
│   │   ├── SecurityConfig.java          # 安全配置
│   │   └── CorsConfig.java              # 跨域配置
│   ├── controller/
│   │   ├── AuthController.java          # 认证控制器
│   │   ├── FavoriteController.java      # 收藏控制器
│   │   └── HistoryController.java       # 历史控制器
│   ├── service/
│   │   ├── AuthService.java             # 认证服务
│   │   ├── UserService.java             # 用户服务
│   │   ├── FavoriteService.java         # 收藏服务
│   │   └── HistoryService.java          # 历史服务
│   ├── repository/
│   │   ├── UserRepository.java          # 用户仓库
│   │   ├── FavoriteRepository.java      # 收藏仓库
│   │   └── HistoryRepository.java       # 历史仓库
│   ├── entity/
│   │   ├── User.java                    # 用户实体
│   │   ├── Favorite.java                # 收藏实体
│   │   └── History.java                 # 历史实体
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java        # 登录请求
│   │   │   ├── RegisterRequest.java     # 注册请求
│   │   │   └── FavoriteRequest.java     # 收藏请求
│   │   └── response/
│   │       ├── AuthResponse.java        # 认证响应
│   │       └── UserResponse.java        # 用户响应
│   ├── security/
│   │   ├── JwtTokenProvider.java        # JWT 令牌提供者
│   │   ├── JwtAuthenticationFilter.java  # JWT 认证过滤器
│   │   └── UserDetailsServiceImpl.java  # 用户详情服务
│   └── exception/
│       ├── GlobalExceptionHandler.java   # 全局异常处理
│       └── ApiException.java            # API 异常
├── src/main/resources/
│   ├── application.yml                  # 应用配置
│   └── db/migration/
│       └── V1__init_schema.sql          # 数据库迁移
├── src/test/java/                        # 测试代码
└── pom.xml                              # Maven 配置
```

---

## 3. 数据库设计

### 3.1 数据库 ER 图
```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    users    │       │  favorites  │       │   history   │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ id (PK)     │──┐    │ id (PK)     │    ┌──│ id (PK)     │
│ email       │  │    │ user_id (FK)│──┐ │   │ user_id (FK)│──┐
│ password_hash│  └───│ tool_id     │  │ │   │ tool_id     │  │
│ username    │       │ created_at  │  │ │   │ accessed_at │  │
│ avatar_url  │       └─────────────┘  │ │   └─────────────┘  │
│ created_at  │                        │ │                    │
│ updated_at  │                        │ │                    │
│ email_verified                       │ │                    │
└─────────────┘                        │ │                    │
                                      │ │                    │
                                      ▼ ▼                    │
                                 ┌─────────────┐             │
                                 │    tools    │             │
                                 ├─────────────┤             │
                                 │ id (PK)     │◀────────────┘
                                 │ name        │
                                 │ description │
                                 │ icon        │
                                 │ category    │
                                 │ path        │
                                 │ created_at  │
                                 │ updated_at  │
                                 └─────────────┘
```

### 3.2 表结构定义

#### 3.2.1 users 用户表
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    email_verified TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
```

#### 3.2.2 favorites 收藏表
```sql
CREATE TABLE favorites (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tool_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, tool_id)
);

CREATE INDEX idx_favorites_user_id ON favorites(user_id);
```

#### 3.2.3 history 历史记录表
```sql
CREATE TABLE history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tool_id VARCHAR(50) NOT NULL,
    accessed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_history_user_id ON history(user_id);
CREATE INDEX idx_history_accessed_at ON history(accessed_at DESC);
```

#### 3.2.4 tools 工具表
```sql
CREATE TABLE tools (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(100),
    category VARCHAR(50),
    path VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tools_category ON tools(category);
```

### 3.3 初始化数据
```sql
INSERT INTO tools (name, description, icon, category, path) VALUES
('BMI计算器', '计算身体质量指数', 'Calculator', '健康', 'bmi-calculator'),
('货币转换器', '实时汇率货币转换', 'DollarSign', '金融', 'currency-converter'),
('密码生成器', '生成安全随机密码', 'Key', '安全', 'password-generator'),
('JSON格式化', '格式化美化JSON', 'Braces', '开发', 'json-formatter'),
('URL编码', 'URL编码解码工具', 'Link', '开发', 'url-encoder'),
('邮箱验证', '验证邮箱格式有效性', 'Mail', '验证', 'email-validator'),
('颜色选择器', '颜色选择与转换', 'Palette', '设计', 'color-picker'),
('时间戳转换', '时间戳与日期互转', 'Clock', '开发', 'timestamp-converter'),
('IP地址验证', '验证IP地址格式', 'Globe', '验证', 'ip-validator'),
('Base64转换', 'Base64编码解码', 'FileCode', '开发', 'base64-converter'),
('随机数生成', '生成随机数字', 'Hash', '工具', 'random-number-generator'),
('HTML格式化', '格式化美化HTML', 'Code', '开发', 'html-formatter');
```

---

## 4. API 接口设计

### 4.1 认证接口

#### 4.1.1 用户注册
```
POST /api/auth/register
Content-Type: application/json

Request:
{
  "email": "user@example.com",
  "password": "password123",
  "username": "username"
}

Response (201 Created):
{
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "username": "username",
    "avatarUrl": null,
    "createdAt": "2024-01-01T00:00:00Z"
  },
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

Error Responses:
- 400 Bad Request: "Email already exists" / "Username already exists"
- 400 Bad Request: "Invalid email format"
- 400 Bad Request: "Password must be at least 6 characters"
```

#### 4.1.2 用户登录
```
POST /api/auth/login
Content-Type: application/json

Request:
{
  "email": "user@example.com",
  "password": "password123"
}

Response (200 OK):
{
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "username": "username",
    "avatarUrl": null,
    "createdAt": "2024-01-01T00:00:00Z"
  },
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

Error Responses:
- 401 Unauthorized: "Invalid email or password"
```

#### 4.1.3 获取当前用户
```
GET /api/auth/me
Authorization: Bearer <token>

Response (200 OK):
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "username": "username",
  "avatarUrl": null,
  "createdAt": "2024-01-01T00:00:00Z"
}

Error Responses:
- 401 Unauthorized: "Invalid or expired token"
```

### 4.2 收藏接口

#### 4.2.1 获取收藏列表
```
GET /api/favorites
Authorization: Bearer <token>

Response (200 OK):
{
  "favorites": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "toolId": "bmi-calculator",
      "createdAt": "2024-01-01T00:00:00Z"
    }
  ]
}
```

#### 4.2.2 添加收藏
```
POST /api/favorites
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "toolId": "bmi-calculator"
}

Response (201 Created):
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "toolId": "bmi-calculator",
  "createdAt": "2024-01-01T00:00:00Z"
}

Error Responses:
- 409 Conflict: "Tool already in favorites"
```

#### 4.2.3 删除收藏
```
DELETE /api/favorites/{toolId}
Authorization: Bearer <token>

Response (204 No Content)
```

### 4.3 历史记录接口

#### 4.3.1 获取历史记录
```
GET /api/history
Authorization: Bearer <token>

Query Parameters:
- page: 页码 (default: 0)
- size: 每页数量 (default: 20)

Response (200 OK):
{
  "history": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "toolId": "bmi-calculator",
      "accessedAt": "2024-01-01T00:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 50,
  "totalPages": 3
}
```

#### 4.3.2 记录工具访问
```
POST /api/history
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "toolId": "bmi-calculator"
}

Response (201 Created):
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "toolId": "bmi-calculator",
  "accessedAt": "2024-01-01T00:00:00Z"
}
```

### 4.4 通用响应格式

#### 成功响应
```json
{
  "data": { ... },
  "message": "Success"
}
```

#### 错误响应
```json
{
  "error": "Error message",
  "code": "ERROR_CODE",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

---

## 5. 安全设计

### 5.1 认证机制
- 使用 JWT (JSON Web Token) 进行身份认证
- Token 有效期：1 天
- Token 存储在 localStorage

### 5.2 密码安全
- 使用 BCrypt 加密存储密码
- 密码强度要求：至少 6 个字符
- 不返回明文密码

### 5.3 接口安全
- 所有 `/api/auth` 以外的接口需要认证
- 使用 Spring Security 进行权限控制
- 配置 CORS 允许前端域名访问

### 5.4 Spring Security 配置
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### 5.5 JWT 实现
```java
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

---

## 6. 配置说明

### 6.1 application.yml
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/toolkit_db
    username: postgres
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

jwt:
  secret: ${JWT_SECRET:your-super-secret-key-at-least-32-characters}
  expiration: 86400000  # 1 day in milliseconds

logging:
  level:
    com.toolkit: DEBUG
    org.springframework.security: DEBUG
```

### 6.2 环境变量
```bash
# 必需
DB_PASSWORD=your_database_password
JWT_SECRET=your-jwt-secret-key-at-least-32-characters

# 可选（使用默认值）
DB_HOST=localhost
DB_PORT=5432
DB_NAME=toolkit_db
DB_USER=postgres
```

---

## 7. 部署方案

### 7.1 环境要求
- Java 17+
- PostgreSQL 15+
- 内存 512MB+
- 磁盘 1GB+

### 7.2 打包部署
```bash
# 构建
./mvnw clean package -DskipTests

# 运行
java -jar target/toolkit-backend-1.0.0.jar
```

### 7.3 Docker 部署
```dockerfile
FROM eclipse-temurin:17-jre-alpine
COPY target/toolkit-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 7.4 Docker Compose
```yaml
version: '3.8'

services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - db

  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=toolkit_db
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./database/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    ports:
      - "5432:5432"

volumes:
  postgres_data:
```

### 7.5 云平台部署推荐
| 平台 | 数据库 | 特点 |
|------|--------|------|
| Railway | PostgreSQL | 最简单，自动部署 |
| Render | PostgreSQL | 免费层可用 |
| Heroku | PostgreSQL | 经典选择 |
| Vercel | 需自建 | 前后端分离部署 |

---

## 8. 开发指南

### 8.1 本地开发
```bash
# 1. 克隆项目
git clone <repository-url>
cd toolkit-backend

# 2. 配置数据库
# 编辑 src/main/resources/application.yml 或设置环境变量

# 3. 运行数据库迁移
psql -U postgres -d toolkit_db -f database/schema.sql

# 4. 启动应用
./mvnw spring-boot:run
```

### 8.2 前端联调
确保前端 `.env` 文件配置正确的后端地址：
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

### 8.3 测试 API
```bash
# 注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","username":"testuser"}'

# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# 访问受保护接口（替换 <token>）
curl http://localhost:8080/api/favorites \
  -H "Authorization: Bearer <token>"
```

---

## 9. 注意事项

### 9.1 生产环境
- 使用强密码作为 JWT Secret
- 启用 HTTPS
- 配置正确的 CORS 策略
- 使用连接池管理数据库连接

### 9.2 数据库迁移
- 开发环境可使用 `ddl-auto: update`
- 生产环境必须使用迁移脚本
- 避免直接修改生产数据库

### 9.3 错误处理
- 所有异常通过 GlobalExceptionHandler 处理
- 不在响应中暴露敏感信息
- 记录详细日志便于排查问题
