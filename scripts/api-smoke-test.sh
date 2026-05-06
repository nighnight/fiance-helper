#!/bin/bash
set -e

BASE_URL="http://localhost:8080"
COOKIE_JAR="cookies.txt"

echo "=== CLI 接口冒烟测试开始 ==="

# 清理旧 cookie
rm -f $COOKIE_JAR

# 1. 注册
echo "→ 注册"
curl -s -X POST "$BASE_URL/user/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"clitest","password":"123456","nickname":"CLI测试"}' > /dev/null
echo "✓ 注册通过"

# 2. 登录（保存 Cookie）
echo "→ 登录"
curl -s -c $COOKIE_JAR -X POST "$BASE_URL/user/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"clitest","password":"123456"}' > /dev/null
echo "✓ 登录通过"

# 3. 新增收入
echo "→ 新增收入"
curl -s -b $COOKIE_JAR -X POST "$BASE_URL/record" \
  -H "Content-Type: application/json" \
  -d '{"type":1,"amount":500,"accountId":1,"categoryId":1,"recordDate":"2026-05-06"}' > /dev/null
echo "✓ 新增收入通过"

# 4. 查询记录列表
echo "→ 查询列表"
RESP=$(curl -s -b $COOKIE_JAR "$BASE_URL/record/list?pageNum=1&pageSize=10")
echo "$RESP" | grep -q '"code".*200'
echo "✓ 查询列表通过"

# 5. 图表数据
echo "→ 图表数据"
RESP=$(curl -s -b $COOKIE_JAR "$BASE_URL/chart/data?month=2026-05")
echo "$RESP" | grep -q '"code".*200'
echo "✓ 图表数据通过"

# 6. 导出 Excel
echo "→ 导出Excel"
CONTENT_TYPE=$(curl -s -b $COOKIE_JAR -o /dev/null -D - "$BASE_URL/record/export" | grep -i "content-type" | tr -d '\r')
echo "$CONTENT_TYPE" | grep -q "spreadsheetml\|octet-stream"
echo "✓ 导出Excel通过"

echo "=== CLI 接口冒烟测试全部通过 ==="
