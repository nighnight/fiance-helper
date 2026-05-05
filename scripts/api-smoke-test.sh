#!/bin/bash
set -e

BASE_URL="http://localhost:8080"

echo "=== 接口冒烟测试开始 ==="

# 1. 注册
curl -s -X POST "$BASE_URL/user/register" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=testuser&nickname=测试用户&password=123456" | grep -q "success"
echo "✓ 注册通过"

# 2. 登录（保存 Cookie）
curl -s -c cookies.txt -X POST "$BASE_URL/user/login" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=testuser&password=123456" | grep -q "success"
echo "✓ 登录通过"

# 3. 查询记录列表
curl -s -b cookies.txt "$BASE_URL/record/list?page=1&size=10" | grep -q "data"
echo "✓ 查询列表通过"

# 4. 新增收入
curl -s -b cookies.txt -X POST "$BASE_URL/record/add" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "type=收入&amount=500&accountId=1&categoryId=1" | grep -q "success"
echo "✓ 新增收入通过"

# 5. 导出 Excel
CONTENT_TYPE=$(curl -s -b cookies.txt -o /dev/null -D - "$BASE_URL/record/export" | grep -i "content-type" | tr -d '\r')
echo "$CONTENT_TYPE" | grep -q "spreadsheetml"
echo "✓ 导出 Excel 通过"

echo "=== 接口冒烟测试全部通过 ==="
