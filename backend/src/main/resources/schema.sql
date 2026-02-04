CREATE DATABASE IF NOT EXISTS snack_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE snack_store;


SET NAMES utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS admin_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  username VARCHAR(50) NOT NULL COMMENT '账号',
  password_hash VARCHAR(100) NOT NULL COMMENT '密码哈希',
  role VARCHAR(20) NOT NULL COMMENT '角色',
  status VARCHAR(10) NOT NULL COMMENT '状态',
  created_at DATETIME COMMENT '创建时间',
  UNIQUE KEY uk_admin_user_username (username)
) COMMENT='后台管理员';

CREATE TABLE IF NOT EXISTS user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  phone VARCHAR(20) NOT NULL COMMENT '手机号',
  email VARCHAR(100) COMMENT '邮箱',
  password_hash VARCHAR(100) NOT NULL COMMENT '密码哈希',
  status VARCHAR(10) NOT NULL COMMENT '状态',
  created_at DATETIME COMMENT '创建时间',
  UNIQUE KEY uk_user_phone (phone),
  UNIQUE KEY uk_user_email (email)
) COMMENT='用户';

CREATE TABLE IF NOT EXISTS category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort_order INT COMMENT '排序值',
  status VARCHAR(10) NOT NULL COMMENT '状态',
  created_at DATETIME COMMENT '创建时间'
) COMMENT='商品分类';

CREATE TABLE IF NOT EXISTS product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  category_id BIGINT NOT NULL COMMENT '分类ID',
  name VARCHAR(100) NOT NULL COMMENT '商品名称',
  price DECIMAL(10, 2) NOT NULL COMMENT '价格',
  stock INT NOT NULL COMMENT '库存',
  status VARCHAR(10) NOT NULL COMMENT '状态',
  main_image MEDIUMTEXT COMMENT '主图',
  brand VARCHAR(50) COMMENT '品牌',
  ship_from VARCHAR(50) COMMENT '发货地',
  origin VARCHAR(50) COMMENT '产地',
  sku VARCHAR(50) COMMENT 'SKU',
  description VARCHAR(1000) COMMENT '描述',
  created_at DATETIME COMMENT '创建时间',
  updated_at DATETIME COMMENT '更新时间',
  INDEX idx_product_category (category_id)
) COMMENT='商品';

CREATE TABLE IF NOT EXISTS product_image (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  url MEDIUMTEXT NOT NULL COMMENT '图片URL',
  sort_order INT COMMENT '排序值',
  INDEX idx_product_image_product (product_id)
) COMMENT='商品图片';

ALTER TABLE product MODIFY COLUMN main_image MEDIUMTEXT;
ALTER TABLE product_image MODIFY COLUMN url MEDIUMTEXT;

CREATE TABLE IF NOT EXISTS product_spec (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  spec_key VARCHAR(50) NOT NULL COMMENT '规格名',
  spec_value VARCHAR(100) NOT NULL COMMENT '规格值',
  INDEX idx_product_spec_product (product_id)
) COMMENT='商品规格';

CREATE TABLE IF NOT EXISTS cart (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  created_at DATETIME COMMENT '创建时间',
  updated_at DATETIME COMMENT '更新时间',
  INDEX idx_cart_user (user_id)
) COMMENT='购物车';

CREATE TABLE IF NOT EXISTS cart_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  cart_id BIGINT NOT NULL COMMENT '购物车ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  quantity INT NOT NULL COMMENT '数量',
  price DECIMAL(10, 2) NOT NULL COMMENT '单价',
  created_at DATETIME COMMENT '创建时间',
  INDEX idx_cart_item_cart (cart_id),
  INDEX idx_cart_item_product (product_id)
) COMMENT='购物车明细';

CREATE TABLE IF NOT EXISTS address (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  name VARCHAR(50) NOT NULL COMMENT '收货人',
  phone VARCHAR(20) NOT NULL COMMENT '手机号',
  region VARCHAR(100) NOT NULL COMMENT '地区',
  detail VARCHAR(200) NOT NULL COMMENT '详细地址',
  is_default TINYINT(1) COMMENT '默认地址',
  created_at DATETIME COMMENT '创建时间',
  INDEX idx_address_user (user_id)
) COMMENT='收货地址';

CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  order_no VARCHAR(32) NOT NULL COMMENT '订单号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  amount DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
  status VARCHAR(20) NOT NULL COMMENT '状态',
  receiver_name VARCHAR(50) COMMENT '收货人',
  receiver_phone VARCHAR(20) COMMENT '收货电话',
  receiver_region VARCHAR(100) COMMENT '收货地区',
  receiver_detail VARCHAR(200) COMMENT '收货地址',
  created_at DATETIME COMMENT '创建时间',
  updated_at DATETIME COMMENT '更新时间',
  UNIQUE KEY uk_orders_order_no (order_no),
  INDEX idx_orders_user (user_id)
) COMMENT='订单';

CREATE TABLE IF NOT EXISTS order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  product_name VARCHAR(100) COMMENT '商品名称',
  product_image TEXT COMMENT '商品图片',
  price DECIMAL(10, 2) NOT NULL COMMENT '成交价',
  quantity INT NOT NULL COMMENT '数量',
  INDEX idx_order_item_order (order_id),
  INDEX idx_order_item_product (product_id)
) COMMENT='订单明细';

ALTER TABLE order_item MODIFY COLUMN product_image TEXT COMMENT '商品图片';

CREATE TABLE IF NOT EXISTS shipment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  company VARCHAR(50) COMMENT '物流公司',
  tracking_no VARCHAR(50) COMMENT '运单号',
  shipped_at DATETIME COMMENT '发货时间',
  INDEX idx_shipment_order (order_id)
) COMMENT='物流信息';

INSERT INTO admin_user (id, username, password_hash, role, status, created_at)
VALUES (1, 'admin', 'admin123', '管理员', '启用', NOW())
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  role = VALUES(role),
  status = VALUES(status);

INSERT INTO user (id, username, phone, email, password_hash, status, created_at)
VALUES (1, 'liao', '13800001234', 'liao@example.com', 'user123', '启用', NOW()),
       (2, 'chen', '13900001111', 'chen@example.com', 'user123', '启用', NOW())
ON DUPLICATE KEY UPDATE
  username = VALUES(username),
  email = VALUES(email),
  password_hash = VALUES(password_hash),
  status = VALUES(status);

INSERT INTO category (id, name, sort_order, status, created_at)
VALUES (1, '坚果炒货', 1, '上架', NOW()),
       (2, '糖巧零食', 2, '上架', NOW()),
       (3, '肉类零食', 3, '上架', NOW()),
       (4, '膨化食品', 4, '上架', NOW()),
       (5, '饼干糕点', 5, '上架', NOW()),
       (6, '饮品冲调', 6, '上架', NOW()),
       (7, '果干蜜饯', 7, '上架', NOW()),
       (8, '海苔紫菜', 8, '上架', NOW()),
       (9, '速食代餐', 9, '上架', NOW())
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  sort_order = VALUES(sort_order),
  status = VALUES(status);

INSERT INTO product (id, category_id, name, price, stock, status, main_image, brand, ship_from, origin, sku, description, created_at, updated_at)
VALUES (1, 5, '芝士夹心饼干', 16.90, 120, '上架', '/images/p1.png', 'SnackLab', '浙江·杭州', '浙江·杭州', 'SL-CHE-001', '酥脆夹心，甜咸适口', NOW(), NOW()),
       (2, 1, '手剥巴旦木', 29.90, 80, '上架', '/images/p2.png', 'NutHouse', '新疆·阿克苏', '新疆·阿克苏', 'NH-BDM-002', '酥脆饱满，日常零嘴', NOW(), NOW()),
       (3, 7, '蔓越莓干', 19.90, 70, '上架', '/images/p3.png', 'BerryPop', '上海', '加拿大', 'BP-CRB-003', '微酸回甘，烘焙轻食百搭', NOW(), NOW()),
       (4, 1, '盐焗开心果', 29.90, 120, '上架', '/images/p4.png', 'SnackLab', '广东·深圳', '福建·泉州', 'SL-PST-001', '颗粒饱满，咸香酥脆', NOW(), NOW()),
       (5, 1, '每日坚果混合装', 39.90, 80, '上架', '/images/p5.png', 'DailyMix', '浙江·杭州', '新疆·阿克苏', 'DM-NUT-010', '多种坚果搭配，营养均衡', NOW(), NOW()),
       (6, 2, '黑巧克力礼盒', 49.90, 50, '上架', '/images/p6.png', 'CocoaNoir', '上海', '比利时', 'CN-CHO-888', '醇厚可可香，低甜不腻', NOW(), NOW()),
       (7, 3, '牛肉干经典款', 32.00, 60, '上架', '/images/p7.png', 'BeefPro', '四川·成都', '内蒙古', 'BP-BEEF-120', '肉质紧实，香辣回甘', NOW(), NOW()),
       (8, 4, '薯片原味', 12.90, 150, '上架', '/images/p8.png', 'CrispyCo', '江苏·苏州', '山东·日照', 'CC-CHIP-008', '酥脆轻盈，经典原味', NOW(), NOW()),
       (9, 5, '牛奶曲奇', 18.80, 100, '上架', '/images/p9.png', 'CookieTime', '广东·广州', '广东·佛山', 'CT-CK-266', '奶香浓郁，口感松脆', NOW(), NOW()),
       (10, 6, '抹茶拿铁冲调', 25.00, 40, '上架', '/images/p10.png', 'MatchaCraft', '北京', '日本·静冈', 'MC-LAT-520', '细腻抹茶香，冷热皆宜', NOW(), NOW()),
       (11, 3, '麻辣鸭脖', 28.00, 0, '上架', '/images/p11.png', 'SpicyDuck', '湖南·长沙', '湖南·岳阳', 'SD-NECK-007', '麻辣入味，回味十足', NOW(), NOW()),
       (12, 7, '芒果干大片', 16.90, 90, '上架', '/images/p12.png', 'FruitJoy', '广西·南宁', '广西·百色', 'FJ-MNG-101', '厚切大片，酸甜软糯不粘牙', NOW(), NOW()),
       (13, 7, '话梅酸甜装', 9.90, 160, '上架', '/images/p13.png', 'MeiMei', '浙江·温州', '浙江·温州', 'MM-HM-202', '酸甜开胃，追剧学习都合适', NOW(), NOW()),
       (14, 7, '蔓越莓干（果粒装）', 19.90, 70, '上架', '/images/p14.png', 'BerryPop', '上海', '加拿大', 'BP-CRB-303', '微酸回甘，烘焙轻食百搭', NOW(), NOW()),
       (15, 8, '烤海苔脆片', 14.90, 110, '上架', '/images/p15.png', 'SeaCrisp', '福建·厦门', '福建·宁德', 'SC-NT-404', '香脆不腻，轻盐更健康', NOW(), NOW()),
       (16, 8, '紫菜蛋花汤包', 12.00, 95, '上架', '/images/p16.png', 'SoupMate', '福建·福州', '福建·福州', 'SM-SUP-505', '热水一冲，鲜香开胃，快速补充能量', NOW(), NOW()),
       (17, 8, '海苔肉松卷', 22.90, 60, '上架', '/images/p17.png', 'RollKing', '广东·汕头', '广东·汕头', 'RK-RSN-606', '咸香松软，肉松与海苔的双重满足', NOW(), NOW()),
       (18, 9, '燕麦代餐杯', 19.90, 75, '上架', '/images/p18.png', 'OatGo', '江苏·南京', '澳大利亚', 'OG-OAT-707', '冲泡即食，饱腹又轻负担', NOW(), NOW()),
       (19, 9, '自热小火锅', 29.90, 45, '上架', '/images/p19.png', 'HotPotNow', '重庆', '重庆', 'HPN-HOT-808', '15分钟开吃，麻辣过瘾一人份', NOW(), NOW()),
       (20, 9, '速食拌面葱油味', 8.90, 180, '上架', '/images/p20.png', 'NoodleLab', '上海', '上海', 'NL-NDL-909', '浓郁葱香，劲道顺滑，5分钟搞定', NOW(), NOW()),
       (21, 1, '坚果蜂蜜巴旦木', 27.90, 100, '上架', '/images/p21.png', 'NutCharm', '浙江·宁波', '美国·加州', 'NC-ALM-110', '蜂蜜微甜，脆香可口，越吃越香', NOW(), NOW()),
       (22, 2, '夹心威化巧克力', 15.80, 130, '上架', '/images/p22.png', 'ChocoLayer', '广东·东莞', '广东·东莞', 'CL-WAF-120', '酥脆层层，巧克力夹心浓郁', NOW(), NOW()),
       (23, 2, '草莓软糖', 11.90, 200, '上架', '/images/p23.png', 'CandyKiss', '福建·厦门', '福建·厦门', 'CK-GUM-130', 'Q弹酸甜，果味浓郁不粘牙', NOW(), NOW()),
       (24, 3, '卤味鸡翅中', 24.90, 55, '上架', '/images/p24.png', 'BraisedHub', '湖北·武汉', '湖北·武汉', 'BH-WNG-140', '鲜香入味，肉质紧实，越啃越香', NOW(), NOW()),
       (25, 3, '猪肉脯蜜汁味', 26.90, 68, '上架', '/images/p25.png', 'Porky', '福建·厦门', '福建·漳州', 'PK-JRK-150', '蜜汁微甜，肉香浓郁，越嚼越香', NOW(), NOW()),
       (26, 4, '玉米棒香辣味', 10.90, 140, '上架', '/images/p26.png', 'CornPop', '河南·郑州', '河南·周口', 'CP-COR-160', '香辣酥脆，追剧必备，越吃越上头', NOW(), NOW()),
       (27, 4, '爆米花焦糖味', 13.50, 125, '上架', '/images/p27.png', 'PopFun', '北京', '河北·廊坊', 'PF-POP-170', '香甜松脆，电影时刻的绝配', NOW(), NOW()),
       (28, 5, '芝士夹心饼干（咸甜款）', 17.90, 88, '上架', '/images/p28.png', 'BiscuitHouse', '广东·珠海', '广东·中山', 'BH-BIS-180', '香浓芝士夹心，咸甜平衡不腻口', NOW(), NOW()),
       (29, 5, '蛋黄酥礼盒', 36.90, 35, '上架', '/images/p29.png', 'PastryLab', '江苏·苏州', '江苏·苏州', 'PL-YOL-190', '酥皮层层，咸蛋黄流沙绵密', NOW(), NOW()),
       (30, 6, '柠檬气泡水', 6.90, 160, '上架', '/images/p30.png', 'SparkNow', '广东·深圳', '广东·深圳', 'SN-SPK-200', '清爽零糖，冰镇更爽，解腻好搭档', NOW(), NOW()),
       (31, 6, '冰美式咖啡液', 24.90, 70, '上架', '/images/p31.png', 'CoffeeJet', '上海', '云南·普洱', 'CJ-CAF-210', '0糖0脂，随手一杯冰美式，提神不负担', NOW(), NOW()),
       (32, 1, '盐焗开心果加量装', 35.90, 90, '上架', '/images/p32.png', 'SnackLab', '广东·深圳', '福建·泉州', 'MOCK-P032', '颗粒饱满，咸香酥脆（加量装）', NOW(), NOW()),
       (33, 1, '每日坚果混合装家庭装', 59.90, 60, '上架', '/images/p33.png', 'DailyMix', '浙江·杭州', '新疆·阿克苏', 'MOCK-P033', '家庭装更划算，营养均衡随手一包', NOW(), NOW()),
       (34, 2, '黑巧克力礼盒72%可可', 52.80, 95, '上架', '/images/p34.png', 'CocoaNoir', '上海', '比利时', 'MOCK-P034', '醇厚可可香，72%可可含量更浓郁', NOW(), NOW()),
       (35, 3, '牛肉干经典款大包装', 49.90, 45, '上架', '/images/p35.png', 'BeefPro', '四川·成都', '内蒙古', 'MOCK-P035', '紧实有嚼劲，大包装更过瘾', NOW(), NOW()),
       (36, 4, '薯片原味分享装', 19.90, 110, '上架', '/images/p36.png', 'CrispyCo', '江苏·苏州', '山东·日照', 'MOCK-P036', '大袋分享，经典原味更满足', NOW(), NOW()),
       (37, 5, '牛奶曲奇大罐装', 29.90, 70, '上架', '/images/p37.png', 'CookieTime', '广东·广州', '广东·佛山', 'MOCK-P037', '奶香浓郁，大罐装更适合分享', NOW(), NOW()),
       (38, 6, '抹茶拿铁冲调低糖版', 26.90, 55, '上架', '/images/p38.png', 'MatchaCraft', '北京', '日本·静冈', 'MOCK-P038', '低糖配方，保留抹茶香与奶香', NOW(), NOW()),
       (39, 3, '麻辣鸭脖真空小包装', 15.90, 120, '上架', '/images/p39.png', 'SpicyDuck', '湖南·长沙', '湖南·岳阳', 'MOCK-P039', '独立小包装更方便，麻辣入味', NOW(), NOW()),
       (40, 7, '芒果干大片轻甜装', 18.90, 85, '上架', '/images/p40.png', 'FruitJoy', '广西·南宁', '广西·百色', 'MOCK-P040', '厚切大片，轻甜不腻更耐吃', NOW(), NOW()),
       (41, 7, '话梅酸甜装小袋装', 12.90, 220, '上架', '/images/p41.png', 'MeiMei', '浙江·温州', '浙江·温州', 'MOCK-P041', '随手一包更便携，酸甜开胃', NOW(), NOW()),
       (42, 8, '烤海苔脆片轻盐版', 16.90, 140, '上架', '/images/p42.png', 'SeaCrisp', '福建·厦门', '福建·宁德', 'MOCK-P042', '轻盐更清爽，入口香脆不腻', NOW(), NOW()),
       (43, 8, '紫菜蛋花汤包加料装', 14.90, 130, '上架', '/images/p43.png', 'SoupMate', '福建·福州', '福建·福州', 'MOCK-P043', '加料更鲜香，热水一冲更满足', NOW(), NOW()),
       (44, 8, '海苔肉松卷儿童款', 18.90, 160, '上架', '/images/p44.png', 'RollKing', '广东·汕头', '广东·汕头', 'MOCK-P044', '少油少辣更适口，松软不腻', NOW(), NOW()),
       (45, 9, '燕麦代餐杯水果味', 23.90, 90, '上架', '/images/p45.png', 'OatGo', '江苏·南京', '澳大利亚', 'MOCK-P045', '水果风味更清爽，冲泡即食饱腹', NOW(), NOW()),
       (46, 9, '自热小火锅番茄味', 31.90, 80, '上架', '/images/p46.png', 'HotPotNow', '重庆', '重庆', 'MOCK-P046', '番茄酸甜不辣口，15分钟开吃', NOW(), NOW()),
       (47, 9, '速食拌面葱油味升级装', 10.90, 150, '上架', '/images/p47.png', 'NoodleLab', '上海', '上海', 'MOCK-P047', '葱油更香，酱包升级更入味', NOW(), NOW()),
       (48, 1, '坚果蜂蜜巴旦木加量装', 32.90, 90, '上架', '/images/p48.png', 'NutCharm', '浙江·宁波', '美国·加州', 'MOCK-P048', '蜂蜜微甜加量更划算，脆香可口', NOW(), NOW()),
       (49, 2, '夹心威化巧克力榛子味', 17.90, 120, '上架', '/images/p49.png', 'ChocoLayer', '广东·东莞', '广东·东莞', 'MOCK-P049', '榛子风味更浓郁，酥脆层层', NOW(), NOW()),
       (50, 2, '草莓软糖夹心款', 14.90, 210, '上架', '/images/p50.png', 'CandyKiss', '福建·厦门', '福建·厦门', 'MOCK-P050', '外Q内软夹心，果味更浓不粘牙', NOW(), NOW()),
       (51, 3, '卤味鸡翅中微辣味', 26.90, 60, '上架', '/images/p51.png', 'BraisedHub', '湖北·武汉', '湖北·武汉', 'MOCK-P051', '卤香入味微辣不刺激，越啃越香', NOW(), NOW()),
       (52, 3, '猪肉脯蜜汁味加量装', 39.90, 75, '上架', '/images/p52.png', 'Porky', '福建·厦门', '福建·漳州', 'MOCK-P052', '蜜汁微甜加量更满足，肉香浓郁', NOW(), NOW()),
       (53, 4, '玉米棒香辣味大袋装', 16.90, 100, '上架', '/images/p53.png', 'CornPop', '河南·郑州', '河南·周口', 'MOCK-P053', '大袋分享更过瘾，香辣酥脆', NOW(), NOW()),
       (54, 4, '爆米花焦糖味影院桶装', 29.90, 60, '上架', '/images/p54.png', 'PopFun', '北京', '河北·廊坊', 'MOCK-P054', '影院桶装更有氛围，香甜松脆', NOW(), NOW()),
       (55, 5, '蛋黄酥礼盒双黄款', 42.90, 45, '上架', '/images/p55.png', 'PastryLab', '江苏·苏州', '江苏·苏州', 'MOCK-P055', '双黄更香，酥皮层层流沙绵密', NOW(), NOW()),
       (56, 6, '青柠气泡水', 6.90, 200, '上架', '/images/p56.png', 'SparkNow', '广东·深圳', '广东·深圳', 'MOCK-P056', '青柠清香更清爽，冰镇更爽', NOW(), NOW()),
       (57, 6, '冰美式咖啡液浓缩版', 26.90, 80, '上架', '/images/p57.png', 'CoffeeJet', '上海', '云南·普洱', 'MOCK-P057', '浓缩更提神，0糖0脂轻负担', NOW(), NOW()),
       (58, 8, '拌饭海苔碎', 12.90, 160, '上架', '/images/p58.png', 'SeaCrisp', '福建·厦门', '福建·宁德', 'MOCK-P058', '拌饭拌面都合适，香脆增香不腻', NOW(), NOW()),
       (59, 7, '果干综合礼盒', 39.90, 50, '上架', '/images/p59.png', 'FruitJoy', '广西·南宁', '广西·百色', 'MOCK-P059', '多种果干组合装，酸甜丰富更耐吃', NOW(), NOW()),
       (60, 9, '代餐蛋白棒', 19.90, 120, '上架', '/images/p60.png', 'FitBite', '上海', '江苏·南通', 'MOCK-P060', '高蛋白更饱腹，随手一根更省心', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  category_id = VALUES(category_id),
  name = VALUES(name),
  price = VALUES(price),
  stock = VALUES(stock),
  status = VALUES(status),
  main_image = VALUES(main_image),
  brand = VALUES(brand),
  ship_from = VALUES(ship_from),
  origin = VALUES(origin),
  sku = VALUES(sku),
  description = VALUES(description),
  updated_at = VALUES(updated_at);

INSERT INTO product_image (id, product_id, url, sort_order)
VALUES (1, 1, '/images/p1-1.png', 1),
       (2, 1, '/images/p1-2.png', 2),
       (3, 2, '/images/p2-1.png', 1),
       (4, 3, '/images/p3-1.png', 1)
ON DUPLICATE KEY UPDATE
  product_id = VALUES(product_id),
  url = VALUES(url),
  sort_order = VALUES(sort_order);

INSERT INTO product_spec (id, product_id, spec_key, spec_value)
VALUES (1, 1, '净含量', '160g'),
       (2, 1, '口味', '芝士'),
       (3, 2, '净含量', '200g'),
       (4, 2, '口味', '原味'),
       (5, 3, '净含量', '200g'),
       (6, 3, '口味', '微酸')
ON DUPLICATE KEY UPDATE
  product_id = VALUES(product_id),
  spec_key = VALUES(spec_key),
  spec_value = VALUES(spec_value);

INSERT INTO cart (id, user_id, created_at, updated_at)
VALUES (1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  updated_at = VALUES(updated_at);

INSERT INTO cart_item (id, cart_id, product_id, quantity, price, created_at)
VALUES (1, 1, 1, 2, 16.90, NOW())
ON DUPLICATE KEY UPDATE
  cart_id = VALUES(cart_id),
  product_id = VALUES(product_id),
  quantity = VALUES(quantity),
  price = VALUES(price);

INSERT INTO address (id, user_id, name, phone, region, detail, is_default, created_at)
VALUES (1, 1, '廖同学', '13800001234', '广东省 深圳市 南山区', '科技园路 88 号', 1, NOW())
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  name = VALUES(name),
  phone = VALUES(phone),
  region = VALUES(region),
  detail = VALUES(detail),
  is_default = VALUES(is_default);

INSERT INTO orders (id, order_no, user_id, amount, status, receiver_name, receiver_phone, receiver_region, receiver_detail, created_at, updated_at)
VALUES (1, 'O20260202001', 1, 33.80, '待收货', '廖同学', '13800001234', '广东省 深圳市 南山区', '科技园路 88 号', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  amount = VALUES(amount),
  status = VALUES(status),
  receiver_name = VALUES(receiver_name),
  receiver_phone = VALUES(receiver_phone),
  receiver_region = VALUES(receiver_region),
  receiver_detail = VALUES(receiver_detail),
  updated_at = VALUES(updated_at);

INSERT INTO order_item (id, order_id, product_id, product_name, product_image, price, quantity)
VALUES (1, 1, 1, '芝士夹心饼干', '/images/p1.png', 16.90, 2)
ON DUPLICATE KEY UPDATE
  order_id = VALUES(order_id),
  product_id = VALUES(product_id),
  product_name = VALUES(product_name),
  product_image = VALUES(product_image),
  price = VALUES(price),
  quantity = VALUES(quantity);

INSERT INTO shipment (id, order_id, company, tracking_no, shipped_at)
VALUES (1, 1, '顺丰', 'SF1234567890', NOW())
ON DUPLICATE KEY UPDATE
  order_id = VALUES(order_id),
  company = VALUES(company),
  tracking_no = VALUES(tracking_no),
  shipped_at = VALUES(shipped_at);
