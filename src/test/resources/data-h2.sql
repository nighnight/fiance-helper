-- 测试基础数据

-- 系统默认收支分类
INSERT INTO finance_category (user_id, category_name, type, sort, is_default) VALUES
(0, '工资', 1, 1, 1),
(0, '奖金', 1, 2, 1),
(0, '投资收益', 1, 3, 1),
(0, '餐饮', 2, 1, 1),
(0, '交通', 2, 2, 1),
(0, '购物', 2, 3, 1),
(0, '娱乐', 2, 4, 1);
