const buildSvgDataUrl = (title, subtitle, colorA, colorB) => {
  const safeTitle = String(title).slice(0, 18)
  const safeSubtitle = String(subtitle).slice(0, 28)
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="960" height="640" viewBox="0 0 960 640"><defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="${colorA}"/><stop offset="1" stop-color="${colorB}"/></linearGradient></defs><rect width="960" height="640" rx="36" fill="url(#g)"/><circle cx="780" cy="140" r="120" fill="rgba(255,255,255,0.18)"/><circle cx="180" cy="520" r="160" fill="rgba(255,255,255,0.14)"/><text x="64" y="250" font-size="54" font-family="Rubik, Nunito Sans, system-ui" fill="rgba(255,255,255,0.96)" font-weight="700">${safeTitle}</text><text x="64" y="320" font-size="28" font-family="Nunito Sans, system-ui" fill="rgba(255,255,255,0.9)">${safeSubtitle}</text><text x="64" y="520" font-size="22" font-family="Nunito Sans, system-ui" fill="rgba(255,255,255,0.75)">零食商场 · Mock Image</text></svg>`
  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}

export const categories = [
  { id: 'c1', name: '坚果炒货' },
  { id: 'c2', name: '糖巧零食' },
  { id: 'c3', name: '肉类零食' },
  { id: 'c4', name: '膨化食品' },
  { id: 'c5', name: '饼干糕点' },
  { id: 'c6', name: '饮品冲调' },
  { id: 'c7', name: '果干蜜饯' },
  { id: 'c8', name: '海苔紫菜' },
  { id: 'c9', name: '速食代餐' }
]

export const products = [
  {
    id: 'p1',
    name: '盐焗开心果',
    price: 29.9,
    stock: 120,
    status: '上架',
    categoryId: 'c1',
    image: buildSvgDataUrl('盐焗开心果', '坚果炒货 · 咸香酥脆', '#7C3AED', '#A78BFA'),
    images: [
      buildSvgDataUrl('盐焗开心果', '坚果炒货 · 咸香酥脆', '#7C3AED', '#A78BFA'),
      buildSvgDataUrl('盐焗开心果', '颗粒饱满 · 越嚼越香', '#FFAA00', '#7C3AED'),
      buildSvgDataUrl('盐焗开心果', '热卖爆款 · 休闲必备', '#00FFFF', '#A78BFA')
    ],
    brand: 'SnackLab',
    shipFrom: '广东·深圳',
    origin: '福建·泉州',
    sku: 'SL-PST-001',
    specs: {
      '净含量': '250g',
      '口味': '盐焗原味',
      '保质期': '12个月',
      '储存方式': '阴凉干燥处密封保存'
    },
    description: '颗粒饱满，咸香酥脆'
  },
  {
    id: 'p2',
    name: '每日坚果混合装',
    price: 39.9,
    stock: 80,
    status: '上架',
    categoryId: 'c1',
    image: buildSvgDataUrl('每日坚果', '混合装 · 营养均衡', '#A78BFA', '#7C3AED'),
    images: [
      buildSvgDataUrl('每日坚果', '混合装 · 营养均衡', '#A78BFA', '#7C3AED'),
      buildSvgDataUrl('每日坚果', '轻食搭配 · 随手一包', '#22C55E', '#A78BFA'),
      buildSvgDataUrl('每日坚果', '多种搭配 · 口感丰富', '#FF1493', '#7C3AED')
    ],
    brand: 'DailyMix',
    shipFrom: '浙江·杭州',
    origin: '新疆·阿克苏',
    sku: 'DM-NUT-010',
    specs: {
      '净含量': '30g x 7袋',
      '口味': '混合坚果',
      '保质期': '10个月',
      '适用场景': '早餐/加班/健身'
    },
    description: '多种坚果搭配，营养均衡'
  },
  {
    id: 'p3',
    name: '黑巧克力礼盒',
    price: 49.9,
    stock: 50,
    status: '上架',
    categoryId: 'c2',
    image: buildSvgDataUrl('黑巧礼盒', '糖巧零食 · 低甜不腻', '#4C1D95', '#7C3AED'),
    images: [
      buildSvgDataUrl('黑巧礼盒', '糖巧零食 · 低甜不腻', '#4C1D95', '#7C3AED'),
      buildSvgDataUrl('黑巧礼盒', '可可醇厚 · 入口丝滑', '#FF1493', '#4C1D95'),
      buildSvgDataUrl('黑巧礼盒', '送礼体面 · 颜值在线', '#00FFFF', '#7C3AED')
    ],
    brand: 'CocoaNoir',
    shipFrom: '上海',
    origin: '比利时',
    sku: 'CN-CHO-888',
    specs: {
      '净含量': '360g',
      '可可含量': '72%',
      '保质期': '18个月',
      '建议人群': '低糖控/黑巧爱好者'
    },
    description: '醇厚可可香，低甜不腻'
  },
  {
    id: 'p4',
    name: '牛肉干经典款',
    price: 32.0,
    stock: 60,
    status: '上架',
    categoryId: 'c3',
    image: buildSvgDataUrl('牛肉干', '肉类零食 · 香辣回甘', '#7C3AED', '#22C55E'),
    images: [
      buildSvgDataUrl('牛肉干', '肉类零食 · 香辣回甘', '#7C3AED', '#22C55E'),
      buildSvgDataUrl('牛肉干', '紧实有嚼劲 · 越嚼越香', '#FFAA00', '#22C55E'),
      buildSvgDataUrl('牛肉干', '独立小包装 · 方便携带', '#00FFFF', '#7C3AED')
    ],
    brand: 'BeefPro',
    shipFrom: '四川·成都',
    origin: '内蒙古',
    sku: 'BP-BEEF-120',
    specs: {
      '净含量': '120g',
      '口味': '香辣',
      '保质期': '9个月',
      '食用提示': '开袋即食'
    },
    description: '肉质紧实，香辣回甘'
  },
  {
    id: 'p5',
    name: '薯片原味',
    price: 12.9,
    stock: 150,
    status: '上架',
    categoryId: 'c4',
    image: buildSvgDataUrl('薯片原味', '膨化食品 · 经典酥脆', '#A78BFA', '#22C55E'),
    images: [
      buildSvgDataUrl('薯片原味', '膨化食品 · 经典酥脆', '#A78BFA', '#22C55E'),
      buildSvgDataUrl('薯片原味', '咔嚓酥脆 · 追剧必备', '#FFAA00', '#A78BFA'),
      buildSvgDataUrl('薯片原味', '大袋分享 · 朋友聚会', '#00FFFF', '#22C55E')
    ],
    brand: 'CrispyCo',
    shipFrom: '江苏·苏州',
    origin: '山东·日照',
    sku: 'CC-CHIP-008',
    specs: {
      '净含量': '80g',
      '口味': '原味',
      '保质期': '8个月',
      '包装形式': '充氮锁鲜'
    },
    description: '酥脆轻盈，经典原味'
  },
  {
    id: 'p6',
    name: '牛奶曲奇',
    price: 18.8,
    stock: 100,
    status: '上架',
    categoryId: 'c5',
    image: buildSvgDataUrl('牛奶曲奇', '饼干糕点 · 奶香浓郁', '#22C55E', '#7C3AED'),
    images: [
      buildSvgDataUrl('牛奶曲奇', '饼干糕点 · 奶香浓郁', '#22C55E', '#7C3AED'),
      buildSvgDataUrl('牛奶曲奇', '松脆不腻 · 口感细腻', '#FF1493', '#A78BFA'),
      buildSvgDataUrl('牛奶曲奇', '下午茶搭配 · 氛围拉满', '#FFAA00', '#7C3AED')
    ],
    brand: 'CookieTime',
    shipFrom: '广东·广州',
    origin: '广东·佛山',
    sku: 'CT-CK-266',
    specs: {
      '净含量': '200g',
      '口味': '牛奶',
      '保质期': '12个月',
      '建议搭配': '咖啡/牛奶'
    },
    description: '奶香浓郁，口感松脆'
  },
  {
    id: 'p7',
    name: '抹茶拿铁冲调',
    price: 25.0,
    stock: 40,
    status: '上架',
    categoryId: 'c6',
    image: buildSvgDataUrl('抹茶拿铁', '饮品冲调 · 冷热皆宜', '#22C55E', '#A78BFA'),
    images: [
      buildSvgDataUrl('抹茶拿铁', '饮品冲调 · 冷热皆宜', '#22C55E', '#A78BFA'),
      buildSvgDataUrl('抹茶拿铁', '细腻抹茶香 · 入口丝滑', '#00FFFF', '#22C55E'),
      buildSvgDataUrl('抹茶拿铁', '浓郁奶香 · 一杯治愈', '#FF1493', '#A78BFA')
    ],
    brand: 'MatchaCraft',
    shipFrom: '北京',
    origin: '日本·静冈',
    sku: 'MC-LAT-520',
    specs: {
      '净含量': '20g x 10条',
      '口味': '抹茶拿铁',
      '保质期': '18个月',
      '冲泡建议': '180ml 温水/牛奶'
    },
    description: '细腻抹茶香，冷热皆宜'
  },
  {
    id: 'p8',
    name: '麻辣鸭脖',
    price: 28.0,
    stock: 0,
    status: '上架',
    categoryId: 'c3',
    image: buildSvgDataUrl('麻辣鸭脖', '肉类零食 · 麻辣入味', '#4C1D95', '#A78BFA'),
    images: [
      buildSvgDataUrl('麻辣鸭脖', '肉类零食 · 麻辣入味', '#4C1D95', '#A78BFA'),
      buildSvgDataUrl('麻辣鸭脖', '越啃越香 · 追剧灵魂', '#FFAA00', '#4C1D95'),
      buildSvgDataUrl('麻辣鸭脖', '重口必备 · 回味十足', '#22C55E', '#A78BFA')
    ],
    brand: 'SpicyDuck',
    shipFrom: '湖南·长沙',
    origin: '湖南·岳阳',
    sku: 'SD-NECK-007',
    specs: {
      '净含量': '160g',
      '口味': '麻辣',
      '保质期': '6个月',
      '温馨提示': '辣度较高，酌情食用'
    },
    description: '麻辣入味，回味十足'
  },
  {
    id: 'p9',
    name: '芒果干大片',
    price: 16.9,
    stock: 90,
    status: '上架',
    categoryId: 'c7',
    image: buildSvgDataUrl('芒果干', '果干蜜饯 · 酸甜软糯', '#FFAA00', '#FF1493'),
    images: [
      buildSvgDataUrl('芒果干', '果干蜜饯 · 酸甜软糯', '#FFAA00', '#FF1493'),
      buildSvgDataUrl('芒果干', '厚切大片 · 口感满足', '#7C3AED', '#FFAA00'),
      buildSvgDataUrl('芒果干', '下午茶搭配 · 解馋', '#A78BFA', '#FF1493')
    ],
    brand: 'FruitJoy',
    shipFrom: '广西·南宁',
    origin: '广西·百色',
    sku: 'FJ-MNG-101',
    specs: { '净含量': '140g', '口味': '原味', '保质期': '10个月', '储存方式': '阴凉干燥处保存' },
    description: '厚切大片，酸甜软糯不粘牙'
  },
  {
    id: 'p10',
    name: '话梅酸甜装',
    price: 9.9,
    stock: 160,
    status: '上架',
    categoryId: 'c7',
    image: buildSvgDataUrl('话梅', '果干蜜饯 · 酸甜开胃', '#22C55E', '#A78BFA'),
    images: [
      buildSvgDataUrl('话梅', '果干蜜饯 · 酸甜开胃', '#22C55E', '#A78BFA'),
      buildSvgDataUrl('话梅', '饭后解腻 · 口气清新', '#00FFFF', '#22C55E'),
      buildSvgDataUrl('话梅', '小袋便携 · 随手一包', '#7C3AED', '#A78BFA')
    ],
    brand: 'MeiMei',
    shipFrom: '浙江·温州',
    origin: '浙江·温州',
    sku: 'MM-HM-202',
    specs: { '净含量': '100g', '口味': '酸甜', '保质期': '12个月', '食用提示': '开袋即食' },
    description: '酸甜开胃，追剧学习都合适'
  },
  {
    id: 'p11',
    name: '蔓越莓干',
    price: 19.9,
    stock: 70,
    status: '上架',
    categoryId: 'c7',
    image: buildSvgDataUrl('蔓越莓干', '果干蜜饯 · 微酸回甘', '#FF1493', '#7C3AED'),
    images: [
      buildSvgDataUrl('蔓越莓干', '果干蜜饯 · 微酸回甘', '#FF1493', '#7C3AED'),
      buildSvgDataUrl('蔓越莓干', '烘焙搭档 · 酸甜点缀', '#FFAA00', '#FF1493'),
      buildSvgDataUrl('蔓越莓干', '轻食沙拉 · 口感升级', '#22C55E', '#A78BFA')
    ],
    brand: 'BerryPop',
    shipFrom: '上海',
    origin: '加拿大',
    sku: 'BP-CRB-303',
    specs: { '净含量': '200g', '口味': '微酸', '保质期': '15个月', '适用场景': '烘焙/酸奶/沙拉' },
    description: '微酸回甘，烘焙轻食百搭'
  },
  {
    id: 'p12',
    name: '烤海苔脆片',
    price: 14.9,
    stock: 110,
    status: '上架',
    categoryId: 'c8',
    image: buildSvgDataUrl('烤海苔', '海苔紫菜 · 香脆不腻', '#00FFFF', '#7C3AED'),
    images: [
      buildSvgDataUrl('烤海苔', '海苔紫菜 · 香脆不腻', '#00FFFF', '#7C3AED'),
      buildSvgDataUrl('烤海苔', '轻盐配方 · 入口即化', '#22C55E', '#00FFFF'),
      buildSvgDataUrl('烤海苔', '办公零食 · 不脏手', '#A78BFA', '#7C3AED')
    ],
    brand: 'SeaCrisp',
    shipFrom: '福建·厦门',
    origin: '福建·宁德',
    sku: 'SC-NT-404',
    specs: { '净含量': '3g x 12包', '口味': '原味', '保质期': '12个月', '储存方式': '密封防潮' },
    description: '香脆不腻，轻盐更健康'
  },
  {
    id: 'p13',
    name: '紫菜蛋花汤包',
    price: 12.0,
    stock: 95,
    status: '上架',
    categoryId: 'c8',
    image: buildSvgDataUrl('紫菜汤包', '海苔紫菜 · 3分钟即食', '#A78BFA', '#22C55E'),
    images: [
      buildSvgDataUrl('紫菜汤包', '海苔紫菜 · 3分钟即食', '#A78BFA', '#22C55E'),
      buildSvgDataUrl('紫菜汤包', '热水一冲 · 鲜香开胃', '#FFAA00', '#A78BFA'),
      buildSvgDataUrl('紫菜汤包', '早餐夜宵 · 都合适', '#7C3AED', '#22C55E')
    ],
    brand: 'SoupMate',
    shipFrom: '福建·福州',
    origin: '福建·福州',
    sku: 'SM-SUP-505',
    specs: { '净含量': '8g x 10包', '口味': '紫菜蛋花', '保质期': '12个月', '冲泡建议': '200ml 热水' },
    description: '热水一冲，鲜香开胃，快速补充能量'
  },
  {
    id: 'p14',
    name: '海苔肉松卷',
    price: 22.9,
    stock: 60,
    status: '上架',
    categoryId: 'c8',
    image: buildSvgDataUrl('肉松海苔', '海苔紫菜 · 咸香松软', '#7C3AED', '#FFAA00'),
    images: [
      buildSvgDataUrl('肉松海苔', '海苔紫菜 · 咸香松软', '#7C3AED', '#FFAA00'),
      buildSvgDataUrl('肉松海苔', '独立包装 · 方便携带', '#FF1493', '#A78BFA'),
      buildSvgDataUrl('肉松海苔', '下午茶点 · 解馋', '#22C55E', '#7C3AED')
    ],
    brand: 'RollKing',
    shipFrom: '广东·汕头',
    origin: '广东·汕头',
    sku: 'RK-RSN-606',
    specs: { '净含量': '210g', '口味': '肉松海苔', '保质期': '9个月', '储存方式': '避免高温潮湿' },
    description: '咸香松软，肉松与海苔的双重满足'
  },
  {
    id: 'p15',
    name: '燕麦代餐杯',
    price: 19.9,
    stock: 75,
    status: '上架',
    categoryId: 'c9',
    image: buildSvgDataUrl('燕麦代餐', '速食代餐 · 冲泡即食', '#22C55E', '#00FFFF'),
    images: [
      buildSvgDataUrl('燕麦代餐', '速食代餐 · 冲泡即食', '#22C55E', '#00FFFF'),
      buildSvgDataUrl('燕麦代餐', '饱腹低负担 · 轻食', '#A78BFA', '#22C55E'),
      buildSvgDataUrl('燕麦代餐', '加班早餐 · 快速搞定', '#FFAA00', '#00FFFF')
    ],
    brand: 'OatGo',
    shipFrom: '江苏·南京',
    origin: '澳大利亚',
    sku: 'OG-OAT-707',
    specs: { '净含量': '50g x 6杯', '口味': '原味', '保质期': '12个月', '冲泡建议': '200ml 热水/牛奶' },
    description: '冲泡即食，饱腹又轻负担'
  },
  {
    id: 'p16',
    name: '自热小火锅',
    price: 29.9,
    stock: 45,
    status: '上架',
    categoryId: 'c9',
    image: buildSvgDataUrl('自热火锅', '速食代餐 · 15分钟开吃', '#FF1493', '#FFAA00'),
    images: [
      buildSvgDataUrl('自热火锅', '速食代餐 · 15分钟开吃', '#FF1493', '#FFAA00'),
      buildSvgDataUrl('自热火锅', '麻辣过瘾 · 一人份', '#7C3AED', '#FF1493'),
      buildSvgDataUrl('自热火锅', '宿舍办公室 · 都能吃', '#22C55E', '#FFAA00')
    ],
    brand: 'HotPotNow',
    shipFrom: '重庆',
    origin: '重庆',
    sku: 'HPN-HOT-808',
    specs: { '净含量': '420g', '口味': '麻辣', '保质期': '9个月', '食用提示': '注意蒸汽烫伤' },
    description: '15分钟开吃，麻辣过瘾一人份'
  },
  {
    id: 'p17',
    name: '速食拌面葱油味',
    price: 8.9,
    stock: 180,
    status: '上架',
    categoryId: 'c9',
    image: buildSvgDataUrl('葱油拌面', '速食代餐 · 5分钟上桌', '#7C3AED', '#00FFFF'),
    images: [
      buildSvgDataUrl('葱油拌面', '速食代餐 · 5分钟上桌', '#7C3AED', '#00FFFF'),
      buildSvgDataUrl('葱油拌面', '浓郁葱香 · 顺滑劲道', '#FFAA00', '#7C3AED'),
      buildSvgDataUrl('葱油拌面', '一人食 · 方便省心', '#A78BFA', '#00FFFF')
    ],
    brand: 'NoodleLab',
    shipFrom: '上海',
    origin: '上海',
    sku: 'NL-NDL-909',
    specs: { '净含量': '115g', '口味': '葱油', '保质期': '10个月', '冲泡建议': '沸水焖泡 4-5 分钟' },
    description: '浓郁葱香，劲道顺滑，5分钟搞定'
  },
  {
    id: 'p18',
    name: '坚果蜂蜜巴旦木',
    price: 27.9,
    stock: 100,
    status: '上架',
    categoryId: 'c1',
    image: buildSvgDataUrl('巴旦木', '坚果炒货 · 蜂蜜微甜', '#FFAA00', '#A78BFA'),
    images: [
      buildSvgDataUrl('巴旦木', '坚果炒货 · 蜂蜜微甜', '#FFAA00', '#A78BFA'),
      buildSvgDataUrl('巴旦木', '颗粒饱满 · 脆香可口', '#22C55E', '#FFAA00'),
      buildSvgDataUrl('巴旦木', '办公室零食 · 饱腹', '#7C3AED', '#A78BFA')
    ],
    brand: 'NutCharm',
    shipFrom: '浙江·宁波',
    origin: '美国·加州',
    sku: 'NC-ALM-110',
    specs: { '净含量': '200g', '口味': '蜂蜜', '保质期': '12个月', '储存方式': '密封保存' },
    description: '蜂蜜微甜，脆香可口，越吃越香'
  },
  {
    id: 'p19',
    name: '夹心威化巧克力',
    price: 15.8,
    stock: 130,
    status: '上架',
    categoryId: 'c2',
    image: buildSvgDataUrl('夹心威化', '糖巧零食 · 酥脆层层', '#A78BFA', '#FF1493'),
    images: [
      buildSvgDataUrl('夹心威化', '糖巧零食 · 酥脆层层', '#A78BFA', '#FF1493'),
      buildSvgDataUrl('夹心威化', '巧克力夹心 · 甜而不腻', '#7C3AED', '#A78BFA'),
      buildSvgDataUrl('夹心威化', '下午茶搭配 · 咖啡好友', '#FFAA00', '#FF1493')
    ],
    brand: 'ChocoLayer',
    shipFrom: '广东·东莞',
    origin: '广东·东莞',
    sku: 'CL-WAF-120',
    specs: { '净含量': '168g', '口味': '巧克力', '保质期': '12个月', '包装形式': '独立小包装' },
    description: '酥脆层层，巧克力夹心浓郁'
  },
  {
    id: 'p20',
    name: '草莓软糖',
    price: 11.9,
    stock: 200,
    status: '上架',
    categoryId: 'c2',
    image: buildSvgDataUrl('草莓软糖', '糖巧零食 · Q弹酸甜', '#FF1493', '#A78BFA'),
    images: [
      buildSvgDataUrl('草莓软糖', '糖巧零食 · Q弹酸甜', '#FF1493', '#A78BFA'),
      buildSvgDataUrl('草莓软糖', '果味浓郁 · 软糯不粘牙', '#FFAA00', '#FF1493'),
      buildSvgDataUrl('草莓软糖', '小袋便携 · 随手吃', '#7C3AED', '#A78BFA')
    ],
    brand: 'CandyKiss',
    shipFrom: '福建·厦门',
    origin: '福建·厦门',
    sku: 'CK-GUM-130',
    specs: { '净含量': '120g', '口味': '草莓', '保质期': '18个月', '适用场景': '分享/解馋' },
    description: 'Q弹酸甜，果味浓郁不粘牙'
  },
  {
    id: 'p21',
    name: '卤味鸡翅中',
    price: 24.9,
    stock: 55,
    status: '上架',
    categoryId: 'c3',
    image: buildSvgDataUrl('卤味鸡翅', '肉类零食 · 鲜香入味', '#22C55E', '#7C3AED'),
    images: [
      buildSvgDataUrl('卤味鸡翅', '肉类零食 · 鲜香入味', '#22C55E', '#7C3AED'),
      buildSvgDataUrl('卤味鸡翅', '肉质紧实 · 越啃越香', '#FFAA00', '#22C55E'),
      buildSvgDataUrl('卤味鸡翅', '追剧搭子 · 解馋', '#A78BFA', '#7C3AED')
    ],
    brand: 'BraisedHub',
    shipFrom: '湖北·武汉',
    origin: '湖北·武汉',
    sku: 'BH-WNG-140',
    specs: { '净含量': '180g', '口味': '卤香', '保质期': '6个月', '食用提示': '开袋即食' },
    description: '鲜香入味，肉质紧实，越啃越香'
  },
  {
    id: 'p22',
    name: '猪肉脯蜜汁味',
    price: 26.9,
    stock: 68,
    status: '上架',
    categoryId: 'c3',
    image: buildSvgDataUrl('猪肉脯', '肉类零食 · 蜜汁微甜', '#FFAA00', '#22C55E'),
    images: [
      buildSvgDataUrl('猪肉脯', '肉类零食 · 蜜汁微甜', '#FFAA00', '#22C55E'),
      buildSvgDataUrl('猪肉脯', '肉香浓郁 · 越嚼越香', '#7C3AED', '#FFAA00'),
      buildSvgDataUrl('猪肉脯', '独立包装 · 方便携带', '#FF1493', '#22C55E')
    ],
    brand: 'Porky',
    shipFrom: '福建·厦门',
    origin: '福建·漳州',
    sku: 'PK-JRK-150',
    specs: { '净含量': '150g', '口味': '蜜汁', '保质期': '9个月', '储存方式': '常温避光保存' },
    description: '蜜汁微甜，肉香浓郁，越嚼越香'
  },
  {
    id: 'p23',
    name: '玉米棒香辣味',
    price: 10.9,
    stock: 140,
    status: '上架',
    categoryId: 'c4',
    image: buildSvgDataUrl('玉米棒', '膨化食品 · 香辣酥脆', '#FF1493', '#22C55E'),
    images: [
      buildSvgDataUrl('玉米棒', '膨化食品 · 香辣酥脆', '#FF1493', '#22C55E'),
      buildSvgDataUrl('玉米棒', '咔嚓酥脆 · 越吃越上头', '#FFAA00', '#FF1493'),
      buildSvgDataUrl('玉米棒', '追剧必备 · 分享装', '#A78BFA', '#22C55E')
    ],
    brand: 'CornPop',
    shipFrom: '河南·郑州',
    origin: '河南·周口',
    sku: 'CP-COR-160',
    specs: { '净含量': '95g', '口味': '香辣', '保质期': '8个月', '包装形式': '充氮锁鲜' },
    description: '香辣酥脆，追剧必备，越吃越上头'
  },
  {
    id: 'p24',
    name: '爆米花焦糖味',
    price: 13.5,
    stock: 125,
    status: '上架',
    categoryId: 'c4',
    image: buildSvgDataUrl('焦糖爆米花', '膨化食品 · 香甜松脆', '#A78BFA', '#FFAA00'),
    images: [
      buildSvgDataUrl('焦糖爆米花', '膨化食品 · 香甜松脆', '#A78BFA', '#FFAA00'),
      buildSvgDataUrl('焦糖爆米花', '看电影必备 · 香甜不腻', '#FF1493', '#A78BFA'),
      buildSvgDataUrl('焦糖爆米花', '大颗饱满 · 口感丰富', '#00FFFF', '#FFAA00')
    ],
    brand: 'PopFun',
    shipFrom: '北京',
    origin: '河北·廊坊',
    sku: 'PF-POP-170',
    specs: { '净含量': '110g', '口味': '焦糖', '保质期': '9个月', '储存方式': '密封防潮' },
    description: '香甜松脆，电影时刻的绝配'
  },
  {
    id: 'p25',
    name: '芝士夹心饼干',
    price: 17.9,
    stock: 88,
    status: '上架',
    categoryId: 'c5',
    image: buildSvgDataUrl('芝士饼干', '饼干糕点 · 咸甜夹心', '#7C3AED', '#A78BFA'),
    images: [
      buildSvgDataUrl('芝士饼干', '饼干糕点 · 咸甜夹心', '#7C3AED', '#A78BFA'),
      buildSvgDataUrl('芝士饼干', '香浓芝士 · 口感细腻', '#FFAA00', '#7C3AED'),
      buildSvgDataUrl('芝士饼干', '下午茶搭配 · 轻松解馋', '#22C55E', '#A78BFA')
    ],
    brand: 'BiscuitHouse',
    shipFrom: '广东·珠海',
    origin: '广东·中山',
    sku: 'BH-BIS-180',
    specs: { '净含量': '240g', '口味': '芝士', '保质期': '12个月', '建议搭配': '牛奶/咖啡' },
    description: '香浓芝士夹心，咸甜平衡不腻口'
  },
  {
    id: 'p26',
    name: '蛋黄酥礼盒',
    price: 36.9,
    stock: 35,
    status: '上架',
    categoryId: 'c5',
    image: buildSvgDataUrl('蛋黄酥', '饼干糕点 · 酥皮流沙', '#FFAA00', '#7C3AED'),
    images: [
      buildSvgDataUrl('蛋黄酥', '饼干糕点 · 酥皮流沙', '#FFAA00', '#7C3AED'),
      buildSvgDataUrl('蛋黄酥', '咸蛋黄 · 流沙绵密', '#FF1493', '#FFAA00'),
      buildSvgDataUrl('蛋黄酥', '送礼体面 · 下午茶', '#A78BFA', '#7C3AED')
    ],
    brand: 'PastryLab',
    shipFrom: '江苏·苏州',
    origin: '江苏·苏州',
    sku: 'PL-YOL-190',
    specs: { '净含量': '55g x 6枚', '口味': '蛋黄红豆', '保质期': '45天', '储存方式': '常温避光' },
    description: '酥皮层层，咸蛋黄流沙绵密'
  },
  {
    id: 'p27',
    name: '柠檬气泡水',
    price: 6.9,
    stock: 160,
    status: '上架',
    categoryId: 'c6',
    image: buildSvgDataUrl('气泡水', '饮品冲调 · 清爽零糖', '#00FFFF', '#22C55E'),
    images: [
      buildSvgDataUrl('气泡水', '饮品冲调 · 清爽零糖', '#00FFFF', '#22C55E'),
      buildSvgDataUrl('气泡水', '冰镇更爽 · 清爽解腻', '#A78BFA', '#00FFFF'),
      buildSvgDataUrl('气泡水', '柠檬清香 · 轻负担', '#FFAA00', '#22C55E')
    ],
    brand: 'SparkNow',
    shipFrom: '广东·深圳',
    origin: '广东·深圳',
    sku: 'SN-SPK-200',
    specs: { '净含量': '330ml', '口味': '柠檬', '保质期': '12个月', '饮用建议': '冷藏口感更佳' },
    description: '清爽零糖，冰镇更爽，解腻好搭档'
  },
  {
    id: 'p28',
    name: '冰美式咖啡液',
    price: 24.9,
    stock: 70,
    status: '上架',
    categoryId: 'c6',
    image: buildSvgDataUrl('咖啡液', '饮品冲调 · 0糖0脂', '#4C1D95', '#00FFFF'),
    images: [
      buildSvgDataUrl('咖啡液', '饮品冲调 · 0糖0脂', '#4C1D95', '#00FFFF'),
      buildSvgDataUrl('咖啡液', '加牛奶更香 · 自制拿铁', '#FFAA00', '#4C1D95'),
      buildSvgDataUrl('咖啡液', '提神续命 · 随手一杯', '#A78BFA', '#00FFFF')
    ],
    brand: 'CoffeeJet',
    shipFrom: '上海',
    origin: '云南·普洱',
    sku: 'CJ-CAF-210',
    specs: { '净含量': '30ml x 10袋', '口味': '美式', '保质期': '18个月', '冲调建议': '冷水/气泡水/牛奶' },
    description: '0糖0脂，随手一杯冰美式，提神不负担'
  }
]

export const users = [
  {
    id: 'u1',
    username: 'liao',
    phone: '13800001234',
    email: 'liao@example.com',
    status: '启用',
    avatar: buildSvgDataUrl('liao', '用户头像', '#7C3AED', '#A78BFA')
  },
  {
    id: 'u2',
    username: 'chen',
    phone: '13900001111',
    email: 'chen@example.com',
    status: '启用',
    avatar: buildSvgDataUrl('chen', '用户头像', '#22C55E', '#7C3AED')
  }
]

export const adminUser = {
  id: 'a1',
  username: 'admin',
  role: '管理员'
}

export const addresses = [
  {
    id: 'addr1',
    userId: 'u1',
    name: '廖同学',
    phone: '13800001234',
    region: '广东省 深圳市 南山区',
    detail: '科技园路 88 号',
    isDefault: true
  },
  {
    id: 'addr2',
    userId: 'u1',
    name: '廖同学',
    phone: '13800001234',
    region: '广东省 深圳市 福田区',
    detail: '中心大道 18 号',
    isDefault: false
  }
]

export const initialOrders = [
  {
    id: 'o10001',
    userId: 'u1',
    status: '待收货',
    amount: 61.9,
    createdAt: '2026-01-28 14:35',
    address: {
      name: '廖同学',
      phone: '13800001234',
      region: '广东省 深圳市 南山区',
      detail: '科技园路 88 号'
    },
    items: [
      { productId: 'p1', quantity: 1, price: 29.9 },
      { productId: 'p5', quantity: 2, price: 16.0 }
    ]
  },
  {
    id: 'o10002',
    userId: 'u1',
    status: '待发货',
    amount: 49.9,
    createdAt: '2026-01-29 10:12',
    address: {
      name: '廖同学',
      phone: '13800001234',
      region: '广东省 深圳市 福田区',
      detail: '中心大道 18 号'
    },
    items: [{ productId: 'p3', quantity: 1, price: 49.9 }]
  }
]

export const initialReviews = [
  {
    id: 'r10001',
    productId: 'p5',
    userId: 'u1',
    rating: 5,
    content: '薯片很酥脆，原味不腻，追剧必备。',
    createdAt: '2026-01-29 20:18'
  },
  {
    id: 'r10002',
    productId: 'p1',
    userId: 'u2',
    rating: 4,
    content: '开心果颗粒饱满，咸香刚好，唯一不足是我吃太快。',
    createdAt: '2026-01-29 21:05'
  }
]
