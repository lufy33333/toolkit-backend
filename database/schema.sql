-- schema.sql
-- Database schema for Toolkit (standalone initialization)

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    email_verified TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Favorites table
CREATE TABLE IF NOT EXISTS favorites (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tool_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, tool_id)
);

CREATE INDEX IF NOT EXISTS idx_favorites_user_id ON favorites(user_id);

-- History table
CREATE TABLE IF NOT EXISTS history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tool_id VARCHAR(50) NOT NULL,
    accessed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_history_user_id ON history(user_id);
CREATE INDEX IF NOT EXISTS idx_history_accessed_at ON history(accessed_at DESC);

-- Tools table
CREATE TABLE IF NOT EXISTS tools (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(100),
    category VARCHAR(50),
    path VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tools_category ON tools(category);

-- Insert initial tools data
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
('HTML格式化', '格式化美化HTML', 'Code', '开发', 'html-formatter')
ON CONFLICT (path) DO NOTHING;