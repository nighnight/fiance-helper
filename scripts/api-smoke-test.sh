#!/bin/bash
set -e

BASE_URL="http://localhost:8080"
COOKIE_JAR="cookies.txt"

echo "=== CLI 接口冒烟测试开始 ==="

# 清理旧 cookie
rm -f $COOKIE_JAR

# 1. 注册
echo "→ 注册"
RESP=$(curl -s -X POST "$BASE_URL/user/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"clitest","password":"123456","nickname":"CLI测试"}')
echo "$RESP" | grep -q '"code".*200' || { echo "注册失败: $RESP"; exit 1; }
echo "✓ 注册通过"

# 2. 登录（保存 Cookie）
echo "→ 登录"
RESP=$(curl -s -c $COOKIE_JAR -X POST "$BASE_URL/user/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"clitest","password":"123456"}')
echo "$RESP" | grep -q '"code".*200' || { echo "登录失败: $RESP"; exit 1; }
echo "✓ 登录通过"

# 3. 创建账户
echo "→ 创建账户"
RESP=$(curl -s -b $COOKIE_JAR -X POST "$BASE_URL/account" \
  -H "Content-Type: application/json" \
  -d '{"accountName":"测试账户","accountType":"现金","initialBalance":1000}')
echo "$RESP" | grep -q '"code".*200' || { echo "创建账户失败: $RESP"; exit 1; }
echo "✓ 创建账户通过"

# 获取账户ID（兼容有无空格的情况）
ACCOUNT_ID=$(curl -s -b $COOKIE_JAR "$BASE_URL/account/list" | grep -oP '"id"\s*:\s*\K[0-9]+' | head -1)
echo "  账户ID: $ACCOUNT_ID"

if [ -z "$ACCOUNT_ID" ]; then
    echo "错误：无法获取账户ID"
    exit 1
fi

# 4. 新增收入
echo "→ 新增收入"
RESP=$(curl -s -b $COOKIE_JAR -X POST "$BASE_URL/record" \
  -H "Content-Type: application/json" \
  -d "{\"type\":1,\"amount\":500,\"accountId\":$ACCOUNT_ID,\"categoryId\":1,\"recordDate\":\"2026-05-06\"}")
echo "$RESP" | grep -q '"code".*200' || { echo "新增收入失败: $RESP"; exit 1; }
echo "✓ 新增收入通过"

# 5. 查询记录列表
echo "→ 查询列表"
RESP=$(curl -s -b $COOKIE_JAR "$BASE_URL/record/list?pageNum=1&pageSize=10")
echo "$RESP" | grep -q '"code".*200' || { echo "查询列表失败: $RESP"; exit 1; }
echo "✓ 查询列表通过"

# 6. 图表数据
echo "→ 图表数据"
RESP=$(curl -s -b $COOKIE_JAR "$BASE_URL/chart/data?month=2026-05")
echo "$RESP" | grep -q '"code".*200' || { echo "图表数据失败: $RESP"; exit 1; }
echo "✓ 图表数据通过"

# 7. 导出 Excel
echo "→ 导出Excel"
CONTENT_TYPE=$(curl -s -b $COOKIE_JAR -o /dev/null -D - "$BASE_URL/record/export" | grep -i "content-type" | tr -d '\r')
echo "$CONTENT_TYPE" | grep -q "spreadsheetml\|octet-stream" || { echo "导出Excel失败: $CONTENT_TYPE"; exit 1; }
echo "✓ 导出Excel通过"

echo "=== CLI 接口冒烟测试全部通过 ==="
