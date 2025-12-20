classDiagram
    class 管理员 {
        +id: int
        +用户名: String
        +密码: String
    }

    class 用户 {
        +用户ID: int
        +E-Mail: String
        +密码: String
    }

    class 商品类型 {
        +类型ID: int
        +类型名称: String
    }

    class 商品 {
        +商品编号: int
        +商品名称: String
        +原价: double
        +现价: double
        +库存: int
        +图片: String
        +是否推荐: boolean
        +是否广告: boolean
    }

    class 购物车 {
        +购物车ID: int
        +购买数量: int
    }

    class 关注 {
        +ID: int
        +关注时间: datetime
    }

    class 订单基础 {
        +订单编号: int
        +订单金额: double
        +订单状态: int
        +订单时间: datetime
    }

    class 订单详情 {
        +ID: int
        +购买数量: int
    }

    用户 "1" -- "0..*" 订单基础 : 下单
    用户 "1" -- "0..*" 购物车 : 拥有
    用户 "1" -- "0..*" 关注 : 收藏

    订单基础 "1" -- "1..*" 订单详情 : 包含
    商品 "1" -- "0..*" 订单详情 : 被购买
    商品 "1" -- "0..*" 购物车 : 包含
    商品 "1" -- "0..*" 关注 : 被收藏

    商品类型 "1" -- "0..*" 商品 : 分类
