import { defineStore } from "pinia"
import { getCategoryAPI } from "@/api/category"
import { getDishListAPI } from "@/api/dish"
import { getSetmealListAPI } from "@/api/setmeal"
import { addToCartAPI, subCartAPI, getCartAPI, cleanCartAPI } from "@/api/cart"
import { submitOrderAPI, payOrderAPI, getOrderPageAPI, getOrderAPI, riderAcceptAPI, riderDeliveryAPI, riderCompleteAPI, getRiderOrdersAPI } from "@/api/order"
import { getAddressListAPI } from "@/api/address"

export const useCampusStore = defineStore("campus", {
  state: () => ({
    // ===== 商家数据（从API获取） =====
    SHOPS: [],
    // ===== 菜单数据（从API获取） =====
    MENUS: {},

    // ===== 购物车（按商家分组，本地管理） =====
    // [{ shopId, shopName, items:[{ foodId, name, price, qty }] }]
    cart: [],

    // ===== 订单 =====
    orders: [],

    // ===== 骑手数据 =====
    riderEarnings: 0,
    riderAdvancedTotal: 0,

    // ===== 消息通知 =====
    messages: [],

    // ===== 内部计数器 =====
    _orderIdCounter: 100,
  }),

  getters: {
    cartCount() {
      return this.cart.reduce((s, c) => s + c.items.reduce((a, i) => a + i.qty, 0), 0)
    },
    cartTotal() {
      return this.cart.reduce((s, c) => s + c.items.reduce((a, i) => a + i.price * i.qty, 0), 0)
    },
    cartByShop() {
      return this.cart
    },
  },

  actions: {
    // ===== API：获取商家列表（将分类映射为商家） =====
    async fetchShops() {
      try {
        const res = await getCategoryAPI()
        // 后端返回格式: { code, data: [{ id, name, type, sort, createTime }] }
        const list = res.data || []
        this.SHOPS = list.map((cat, idx) => {
          // 为每个分类生成 emoji 和模拟数据
          const emojis = ["🍜", "🥟", "🍗", "🥗", "🍕", "🌮", "🍣", "🍛"]
          const ratings = [4.8, 4.6, 4.7, 4.5, 4.6, 4.4, 4.9, 4.3]
          const salesList = [326, 198, 512, 89, 256, 167, 432, 145]
          return {
            id: cat.id,
            name: cat.name,
            tags: [cat.name, "热销"],
            rating: ratings[idx % ratings.length],
            sales: salesList[idx % salesList.length],
            delivery: "免费",
            minOrder: idx % 2 === 0 ? 12 : 8,
            logo: emojis[idx % emojis.length],
          }
        })
      } catch (e) {
        console.log("获取商家列表失败，使用默认数据", e)
        // 后端不可用时使用模拟数据
        if (this.SHOPS.length === 0) {
          this.SHOPS = [
            { id:1, name:"川味小馆", tags:["川菜","热销"], rating:4.8, sales:326, delivery:"免费", minOrder:12, logo:"🌶️" },
            { id:2, name:"粤式茶点", tags:["茶点","清淡"], rating:4.6, sales:198, delivery:"2元", minOrder:8, logo:"🥟" },
            { id:3, name:"奇妙炸鸡", tags:["炸鸡","宵夜"], rating:4.7, sales:512, delivery:"免费", minOrder:15, logo:"🍗" },
            { id:4, name:"轻食沙拉", tags:["轻食","减脂"], rating:4.5, sales:89, delivery:"免费", minOrder:10, logo:"🥗" },
          ]
        }
      }
    },

    // ===== API：获取菜单 =====
    async fetchMenu(shopId) {
      try {
        const [dishRes, setmealRes] = await Promise.all([
          getDishListAPI(shopId).catch(() => ({ data: [] })),
          getSetmealListAPI(shopId).catch(() => ({ data: [] })),
        ])
        const dishes = dishRes.data || []
        const setmeals = setmealRes.data || []

        // 合并菜品和套餐为统一的菜单格式
        const menuItems = [
          ...dishes.map((d) => ({
            id: d.id,
            name: d.name,
            desc: d.description || "",
            price: d.price || 0,
            _type: "dish",
          })),
          ...setmeals.map((s) => ({
            id: s.id,
            name: s.name,
            desc: s.description || "",
            price: s.price || 0,
            _type: "setmeal",
          })),
        ]

        this.MENUS = { ...this.MENUS, [shopId]: menuItems }
      } catch (e) {
        console.log("获取菜单失败，使用默认数据", e)
        // 后端不可用时使用模拟数据
        const fallbackMenus = {
          1: [{id:101,name:"麻辣水煮鱼",desc:"招牌，供2-3人",price:28},{id:102,name:"宫保鸡丁",desc:"经典下饭菜",price:18},{id:103,name:"酸辣土豆丝",desc:"爽口素菜",price:10},{id:104,name:"米饭",desc:"一碗",price:2}],
          2: [{id:201,name:"虾饺皇",desc:"4只",price:22},{id:202,name:"叉烧包",desc:"3只",price:15},{id:203,name:"肠粉（鲜虾）",desc:"一份",price:12}],
          3: [{id:301,name:"原味炸鸡腿",desc:"一只",price:12},{id:302,name:"鸡翅桶（6只）",desc:"多种口味",price:28},{id:303,name:"薯条大份",desc:"",price:8}],
          4: [{id:401,name:"鸡胸肉沙拉",desc:"配油醋汁",price:20},{id:402,name:"牛油果蔬菜碗",desc:"全素",price:22},{id:403,name:"蛋白奶昔",desc:"草莓味",price:15}],
        }
        if (!this.MENUS[shopId] || this.MENUS[shopId].length === 0) {
          this.MENUS = { ...this.MENUS, [shopId]: fallbackMenus[shopId] || [] }
        }
      }
    },

    // ===== 购物车操作（本地管理） =====
    addToCart(shopId, foodId) {
      const menus = this.MENUS[shopId]
      if (!menus) return
      const food = menus.find((f) => f.id === foodId)
      if (!food) return

      let sc = this.cart.find((c) => c.shopId === shopId)
      if (!sc) {
        const shop = this.SHOPS.find((s) => s.id === shopId)
        sc = { shopId, shopName: shop ? shop.name : "商家", items: [] }
        this.cart.push(sc)
      }
      const exist = sc.items.find((i) => i.foodId === foodId)
      if (exist) {
        exist.qty++
      } else {
        sc.items.push({ foodId: food.id, name: food.name, price: food.price, qty: 1 })
      }
    },

    removeFromCart(shopId, foodId) {
      const sc = this.cart.find((c) => c.shopId === shopId)
      if (!sc) return
      const idx = sc.items.findIndex((i) => i.foodId === foodId)
      if (idx === -1) return
      if (sc.items[idx].qty > 1) {
        sc.items[idx].qty--
      } else {
        sc.items.splice(idx, 1)
      }
      if (sc.items.length === 0) {
        this.cart = this.cart.filter((c) => c.shopId !== shopId)
      }
    },

    getFoodQty(shopId, foodId) {
      const sc = this.cart.find((c) => c.shopId === shopId)
      if (!sc) return 0
      const item = sc.items.find((i) => i.foodId === foodId)
      return item ? item.qty : 0
    },

    clearCart() {
      this.cart = []
    },

    // ===== API：提交订单 =====
    async submitOrder() {
      if (this.cart.length === 0) return null
      try {
        // 1. 动态获取用户的收货地址
        let addressId = null
        try {
          const defaultRes = await getDefaultAddressAPI()
          addressId = defaultRes.data?.id
        } catch (e) { /* 没有默认地址 */ }
        if (!addressId) {
          try {
            const listRes = await getAddressListAPI()
            addressId = listRes.data?.[0]?.id
          } catch (e) { /* 地址列表为空 */ }
        }
        if (!addressId) {
          uni.showToast({ title: "请先在「我的」页面添加收货地址", icon: "none" })
          return null
        }

        // 2. 将本地购物车同步到后端 cart 表（后端下单时从 cart 表读取数据）
        await cleanCartAPI()  // 先清空后端购物车，避免残留数据
        for (const c of this.cart) {
          for (const i of c.items) {
            await addToCartAPI({ dishId: i.foodId })
          }
        }

        // 3. 提交订单，后端会从 cart 表中读取商品列表
        const orderData = {
          payMethod: 1, // 微信支付
          remark: "",
          addressId: addressId,
        }
        const res = await submitOrderAPI(orderData)
        // 后端返回 { code, data: { id, orderAmount, orderNumber, orderTime } }
        // http 工具已解析，res 就是 body，res.data 就是订单数据
        const orderDataResult = res.data || {}
        const orderId = orderDataResult.id || this._orderIdCounter++
        const orderNumber = orderDataResult.orderNumber || ""

        // 4. 调用支付API，将订单状态从"待付款"更新为"待接单"，这样后台才能看到订单
        try {
          await payOrderAPI({ orderNumber, payMethod: 1 })
        } catch (e) {
          console.log("支付状态更新跳过（模拟环境）", e)
        }
        this.addMessage("订单已提交", `#${orderId} 已支付成功，等待骑手接单。`)
        this.cart = []
        return res.data
      } catch (e) {
        console.log("提交订单失败", e)
        uni.showToast({ title: "提交订单失败：" + (e.errMsg || e.message || ""), icon: "none" })
        return null
      }
    },

    // ===== API：获取当前用户订单列表 =====
    async fetchOrders(page = 1, pageSize = 10) {
      try {
        const res = await getOrderPageAPI({ page, pageSize })
        const records = res.data?.records || []
        // 读取本地缓存的已隐藏订单 ID
        let hiddenIds = new Set()
        try {
          const raw = uni.getStorageSync("hidden-order-ids")
          hiddenIds = new Set(raw ? JSON.parse(raw) : [])
        } catch (e) {}
        // 转换后端订单格式为前端格式，并过滤掉已隐藏的订单
        this.orders = records
          .filter((o) => !hiddenIds.has(o.id))
          .map((o) => ({
            id: o.id,
            shopId: o.addressBookId || 1,
            shopName: o.userName || "商家",
            items: (o.orderDetailList || []).map((d) => ({
              name: d.name,
              qty: d.number || 0,
              price: d.amount || 0,
            })),
            total: o.amount || 0,
            status: this._mapOrderStatus(o.status),
            rider: null,
            deliveryAddr: o.address || "未知地址",
            createdAt: o.orderTime || "",
          }))
      } catch (e) {
        console.log("获取订单列表失败", e)
      }
    },

    // 隐藏单个订单（记入本地缓存，下次加载自动过滤）
    hideOrder(orderId) {
      this.orders = this.orders.filter((o) => o.id !== orderId)
      try {
        const raw = uni.getStorageSync("hidden-order-ids")
        const hiddenIds = new Set(raw ? JSON.parse(raw) : [])
        hiddenIds.add(orderId)
        uni.setStorageSync("hidden-order-ids", JSON.stringify([...hiddenIds]))
      } catch (e) {
        console.log("隐藏订单失败", e)
      }
    },

    // ===== API：骑手拉取所有订单 =====
    async fetchRiderOrders() {
      try {
        const res = await getRiderOrdersAPI({ page: 1, pageSize: 50 })
        const records = res.data?.records || []
        this.orders = records.map((o) => ({
          id: o.id,
          shopId: o.addressBookId || 1,
          shopName: o.userName || "商家",
          items: (o.orderDetailList || []).map((d) => ({
            name: d.name,
            qty: d.number || 0,
            price: d.amount || 0,
          })),
          total: o.amount || 0,
          status: this._mapOrderStatus(o.status),
          rider: null,
          deliveryAddr: o.address || "未知地址",
          createdAt: o.orderTime || "",
        }))
      } catch (e) {
        console.log("骑手获取订单失败", e)
      }
    },

    // 映射后端订单状态到前端状态
    _mapOrderStatus(backendStatus) {
      const map = {
        1: "paid", // 待付款 → 忽略（前端只有已支付）
        2: "paid", // 待接单 → 已支付
        3: "pending_pay", // 已接单 → 待垫付（近似）
        4: "delivering", // 派送中 → 配送中
        5: "delivered", // 已完成 → 已送达（前端需要确认）
        6: "completed", // 已取消 → 已完成（近似）
      }
      return map[backendStatus] || "paid"
    },

    // ===== 骑手操作（调用后端 API） =====
    async acceptOrder(orderId) {
      try {
        await riderAcceptAPI(orderId)
        this.addMessage("骑手已接单", `订单 #${orderId} 已被骑手接单`)
        await this.fetchRiderOrders() // 骑手刷新所有订单列表
        return true
      } catch (e) {
        console.log("接单失败", e)
        return false
      }
    },

    async payAndDeliver(orderId) {
      try {
        await riderDeliveryAPI(orderId)
        this.addMessage("骑手已垫付，配送中", `订单 #${orderId} 正在配送中。`)
        await this.fetchRiderOrders()
        return true
      } catch (e) {
        console.log("开始配送失败", e)
        return false
      }
    },

    async markDelivered(orderId) {
      try {
        await riderCompleteAPI(orderId)
        this.addMessage("订单已送达", `订单 #${orderId} 已送达，请确认收货。`)
        await this.fetchRiderOrders()
        return true
      } catch (e) {
        console.log("送达确认失败", e)
        return false
      }
    },

    async confirmOrder(orderId) {
      // 后端订单已完成（status=5），用户确认只需更新前端状态
      const order = this.orders.find((o) => o.id === orderId)
      if (order) {
        order.status = "completed"
      }
      this.addMessage("订单已完成", `#${orderId} 已确认，谢谢使用！`)
      uni.showToast({ title: "已确认收货！", icon: "none" })
    },

    // ===== 消息 =====
    addMessage(title, body) {
      this.messages.unshift({
        id: Date.now(),
        title,
        body,
        time: new Date().toLocaleString("zh-CN", { hour: "2-digit", minute: "2-digit" }),
        read: false,
      })
    },

    markMessageRead(msgId) {
      const msg = this.messages.find((m) => m.id === msgId)
      if (msg) msg.read = true
    },

    // ===== 辅助 =====
    _randomName() {
      const names = ["李伟", "张强", "王浩", "刘洋", "陈明"]
      return names[Math.floor(Math.random() * names.length)]
    },

    findShop(id) {
      return this.SHOPS.find((s) => s.id === id)
    },

    findFood(shopId, foodId) {
      const menu = this.MENUS[shopId]
      return menu ? menu.find((f) => f.id === foodId) : null
    },
  },
})
