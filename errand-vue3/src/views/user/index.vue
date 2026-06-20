<template>
  <div class="user-page">
    <div class="search-bar">
      <el-input v-model="searchName" placeholder="搜索姓名" clearable style="width:180px;margin-right:10px" @clear="loadUsers" @keyup.enter="loadUsers" />
      <el-input v-model="searchPhone" placeholder="搜索手机号" clearable style="width:180px;margin-right:10px" @clear="loadUsers" @keyup.enter="loadUsers" />
      <el-button type="primary" @click="loadUsers">搜索</el-button>
    </div>
    <el-table :data="users" style="width:100%" stripe @row-click="onUserClick" row-key="id">
      <el-table-column label="头像" width="70">
        <template #default="{ row }">
          <div class="user-avatar">{{ (row.name || '?').charAt(0) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="姓名" prop="name" min-width="100" />
      <el-table-column label="手机号" prop="phone" min-width="130" />
      <el-table-column label="OpenID" prop="openid" min-width="200" show-overflow-tooltip />
      <el-table-column label="注册时间" prop="createTime" width="170" />
      <el-table-column label="骑手状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.riderStatus > 0 ? 'success' : 'info'" size="small" effect="dark">
            {{ row.riderStatus > 0 ? '骑手' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button v-if="!row.riderStatus || row.riderStatus === 0" type="success" link size="small" @click.stop="handleSetRider(row)">
            设为骑手
          </el-button>
          <el-button v-else type="danger" link size="small" @click.stop="handleRemoveRider(row)">
            取消骑手
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 展开订单 -->
    <el-drawer v-model="orderVisible" :title="`${currentUser?.name || ''} 的订单`" size="500px" destroy-on-close>
      <el-table :data="userOrders" style="width:100%" stripe size="small">
        <el-table-column label="订单号" prop="id" width="80" />
        <el-table-column label="金额" width="80">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="orderStatusTag(row.status)" size="small">{{ orderStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" prop="orderTime" width="160" />
        <el-table-column label="地址" prop="address" min-width="120" show-overflow-tooltip />
      </el-table>
      <el-empty v-if="userOrders.length === 0" description="暂无订单" :image-size="60" />
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUserListAPI, getUserOrdersAPI, setRiderAPI, removeRiderAPI } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref<any[]>([])
const currentUser = ref<any>(null)
const userOrders = ref<any[]>([])
const orderVisible = ref(false)
const searchName = ref("")
const searchPhone = ref("")

async function loadUsers() {
  try {
    const res = await getUserListAPI({ page: 1, pageSize: 20, name: searchName.value || undefined, phone: searchPhone.value || undefined })
    users.value = res.data?.data?.records || []
  } catch { ElMessage.error('获取用户列表失败') }
}

async function handleSetRider(row: any) {
  try {
    await ElMessageBox.confirm(`确认将 ${row.name} 设为骑手？`, '提示')
    await setRiderAPI(row.id)
    ElMessage.success('已设为骑手')
    await loadUsers()
  } catch { /* 取消 */ }
}

async function handleRemoveRider(row: any) {
  try {
    await ElMessageBox.confirm(`确认取消 ${row.name} 的骑手资格？`, '提示')
    await removeRiderAPI(row.id)
    ElMessage.success('已取消骑手')
    await loadUsers()
  } catch { /* 取消 */ }
}

async function onUserClick(row: any) {
  currentUser.value = row
  orderVisible.value = true
  try {
    const res = await getUserOrdersAPI(row.id)
    userOrders.value = res.data?.data || []
  } catch { userOrders.value = [] }
}

const orderStatusText = (s: number) => {
  const map: Record<number, string> = { 1:'待付款',2:'待接单',3:'已接单',4:'配送中',5:'已送达',6:'已完成' }
  return map[s] || '未知'
}
const orderStatusTag = (s: number) => {
  const map: Record<number, string> = { 1:'info',2:'warning',3:'primary',4:'warning',5:'success',6:'info' }
  return map[s] || 'info'
}

onMounted(loadUsers)
</script>

<style scoped>
.user-page { padding: 0; }
.search-bar { margin-bottom: 16px; display: flex; align-items: center; }
.user-avatar {
  width: 36px; height: 36px; border-radius: 50%;
  background: linear-gradient(135deg, #6C4FA7, #F5A623);
  display: flex; align-items: center; justify-content: center;
  font-size: 16px; font-weight: 700; color: #fff;
}
:deep(.el-table th.el-table__cell) { background: #F8F7FC !important; color: #3C3C43; font-weight: 600; }
:deep(.el-table tr) { cursor: pointer; }
</style>
