<template>
  <div class="dashboard-container home">
    <!-- 营业数据 -->
    <Overview :overviewData="overviewData" />
    <!-- end -->
    <!-- 订单管理 -->
    <Orderview :orderviewData="orderviewData" />
    <!-- end -->
    <div class="homeMain">
      <!-- 菜品总览 -->
      <CuisineStatistics :dishesData="dishesData" />
      <!-- end -->
      <!-- 套餐总览 -->
      <SetMealStatistics :setMealData="setMealData" />
      <!-- end -->
    </div>
    <!-- 订单信息 -->
    <OrderList :order-statics="orderStatics" @getOrderListBy3Status="getOrderListBy3Status" />
    <!-- end -->
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { getBusinessDataAPI, getOrderDataAPI, getOverviewDishesAPI, getSetMealStatisticsAPI } from '@/api/dashboard'
import { getOrderListByAPI } from '@/api/order'
import Overview from './components/overview.vue'
import Orderview from './components/orderview.vue'
import CuisineStatistics from './components/dishStatistics.vue'
import SetMealStatistics from './components/setmealStatistics.vue'
import OrderList from './components/orderList.vue'

const overviewData = ref<any>({})
const orderviewData = ref<any>({})
const dishesData = ref<any>({})
const setMealData = ref<any>({})
const orderStatics = ref<any>({})

const init = async () => {
  try {
    const businessData = await getBusinessDataAPI()
    overviewData.value = businessData.data.data
    const orderData = await getOrderDataAPI()
    orderviewData.value = orderData.data.data
    const overviewDishes = await getOverviewDishesAPI()
    dishesData.value = overviewDishes.data.data
    const setMealStatistics = await getSetMealStatisticsAPI()
    setMealData.value = setMealStatistics.data.data
    await getOrderListBy3Status()
  } catch (error) {
    console.error('初始化数据时出错: ', error)
  }
}

const getOrderListBy3Status = async () => {
  try {
    const res = await getOrderListByAPI()
    if (res.data.code === 0) {
      orderStatics.value = res.data.data
    } else {
      console.error(res.data.msg)
    }
  } catch (err) {
    console.error('请求出错了: ', err)
  }
}

onMounted(() => {
  init()
})
</script>

<style lang="less">
li { list-style: none; }

.homeTitle {
  font-weight: 600;
  font-size: 16px;
  color: #2d1b4e;
  letter-spacing: -0.2px;
  padding: 5px 0 0;
  margin-bottom: 18px;
  i {
    font-size: 14px;
    color: #8E8E93;
    padding-left: 10px;
    font-style: normal;
    font-weight: normal
  }
  .more {
    display: flex;
    align-items: center;
    float: right;
    color: #8E8E93;
    font-size: 14px;
    font-weight: normal;
    line-height: 16px;
    a {
      display: inline-block;
      color: #8E8E93;
      font-size: 15px;
      text-decoration: none;
      &:hover { color: #6C4FA7; }
    }
    .el-icon { font-size: 24px; color: #8E8E93; }
  }
}

.homeMain {
  display: flex;
  .container {
    flex: 1;
    &:last-child { margin-left: 20px; }
  }
}

.dashboard-container {
  margin: 30px;
  .container {
    position: relative;
    z-index: 1;
    background: #fff;
    padding: 30px;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  }
  &.home {
    .container { padding: 20px; margin-bottom: 20px; }
    .top10 { padding-bottom: 0; }
  }
}

// 总览 - 鸣潮主题
.overviewBox {
  ul {
    display: flex;
    text-align: left;
    margin-left: -20px;
    li {
      flex: 1;
      background: linear-gradient(135deg, #F8F7FC, #F0EDF5);
      border-radius: 12px;
      margin-left: 20px;
      padding: 20px;
      border: 1px solid rgba(108,79,167,0.06);
    }
    .tit { font-size: 14px; color: #8E8E93; }
    .num {
      font-weight: Bold;
      font-size: 28px;
      line-height: 34px;
      padding: 12px 0 10px;
      color: #2d1b4e;
    }
    .tip {
      color: #8E8E93;
      &>span { padding-left: 5px; span { font-weight: 600; } }
      .red { color: #FF6B6B; }
      .green { color: #4CAF50; }
    }
  }
}

// 订单 - 鸣潮主题
.orderviewBox {
  ul {
    display: flex;
    text-align: left;
    margin-left: -20px;
    li {
      flex: 1;
      background: linear-gradient(135deg, #F8F7FC, #F0EDF5);
      border-radius: 12px;
      margin-left: 20px;
      padding: 20px;
      font-size: 14px;
      line-height: 36px;
      color: #2d1b4e;
      border: 1px solid rgba(108,79,167,0.06);
      &.add {
        width: 100px;
        flex: none;
        background: linear-gradient(135deg, #6C4FA7, #4A2D7A) !important;
        border-radius: 12px;
        text-align: center;
        color: #fff;
        padding: 18px 20px 10px;
        border: none;
        a { color: #fff; line-height: 20px; text-decoration: none; }
        .el-icon { font-size: 25px; margin: 0 auto; color: #fff; margin-bottom: 5px; }
      }
    }
    .status {
      display: inline-flex;
      align-items: center;
      vertical-align: middle;
      line-height: 36px;
      .el-icon { font-size: 25px; margin-right: 10px; }
    }
    .num {
      font-weight: Bold;
      font-size: 28px;
      float: right;
      a { color: #2d1b4e; text-decoration: none; &:hover { color: #6C4FA7; } }
    }
    .tip { a { color: #FF6B6B; text-decoration: none; } }
  }
  .iconfont { font-size: 28px; }
}

.homeTitle .conTab .el-badge__content.is-fixed { top: 12px; }

.conTab {
  float: right;
  font-weight: 400;
  font-size: 14px;
  color: #2d1b4e;
  display: flex;
  height: 36px;
  line-height: 32px;
  background: #FFFFFF;
  border: 1px solid #E5E5EA;
  border-radius: 8px;
  width: 240px;
  li {
    flex: 1;
    text-align: center;
    cursor: pointer;
    &.active { background: #6C4FA7; color: #fff; }
  }
  .el-badge__content.is-fixed {
    top: 16px; right: 2px; width: 18px; height: 18px;
    font-size: 10px; line-height: 16px; border-radius: 50%; padding: 0;
  }
  .badgeW .el-badge__content.is-fixed { width: 30px; border-radius: 20px; }
}
</style>
