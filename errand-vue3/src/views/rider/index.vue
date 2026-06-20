<template>
  <div class="rider-page">
    <el-tabs v-model="activeTab" class="rider-tabs">
      <!-- ===== Tab 1: 骑手列表 ===== -->
      <el-tab-pane label="骑手列表" name="list">
        <!-- 统计卡片 -->
        <el-row :gutter="20" style="margin-bottom:20px">
          <el-col :span="8">
            <el-card shadow="never" class="stat-card stat-total">
              <div class="stat-inner">
                <div class="stat-icon">👥</div>
                <div>
                  <div class="stat-num">{{ stats.total }}</div>
                  <div class="stat-label">总骑手数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="never" class="stat-card stat-idle">
              <div class="stat-inner">
                <div class="stat-icon">🟢</div>
                <div>
                  <div class="stat-num">{{ stats.idle }}</div>
                  <div class="stat-label">空闲中</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="never" class="stat-card stat-delivering">
              <div class="stat-inner">
                <div class="stat-icon">🟠</div>
                <div>
                  <div class="stat-num">{{ stats.delivering }}</div>
                  <div class="stat-label">配送中</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 骑手表格 -->
        <el-table :data="riders" style="width:100%" stripe @row-click="onRiderClick" row-key="id">
          <el-table-column label="头像" width="70">
            <template #default="{ row }">
              <div class="rider-avatar">{{ (row.name || '?').charAt(0) }}</div>
            </template>
          </el-table-column>
          <el-table-column label="姓名" prop="name" min-width="100" />
          <el-table-column label="手机号" prop="phone" min-width="130" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="riderStatusTag(row.riderStatus)" size="small" effect="dark">
                {{ riderStatusText(row.riderStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="评分" width="100">
            <template #default="{ row }">
              <span class="rating-stars">⭐ {{ row.rating ?? 5.0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="完成单数" prop="totalOrders" width="100" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click.stop="viewRiderDetail(row)">
                查看详情
              </el-button>
              <el-button type="danger" link size="small" @click.stop="handleRemoveRider(row)">
                取消骑手
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- ===== Tab 2: 派单记录 ===== -->
      <el-tab-pane label="派单记录" name="logs">
        <!-- 状态筛选 -->
        <div style="margin-bottom:16px">
          <el-radio-group v-model="logFilter" size="small">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="pending">待响应</el-radio-button>
            <el-radio-button label="accepted">已接受</el-radio-button>
            <el-radio-button label="rejected">已拒绝</el-radio-button>
            <el-radio-button label="timeout">超时</el-radio-button>
          </el-radio-group>
        </div>

        <el-table :data="filteredLogs" style="width:100%" stripe>
          <el-table-column label="订单号" prop="orderId" width="90" />
          <el-table-column label="配送地址" prop="deliveryAddr" min-width="160" show-overflow-tooltip />
          <el-table-column label="骑手" prop="riderName" width="100" />
          <el-table-column label="选择方式" width="120">
            <template #default="{ row }">
              <el-tag :type="row.selectionMethod === 'ai_assist' ? 'warning' : 'info'" size="small">
                {{ row.selectionMethod === 'ai_assist' ? 'AI 辅助' : '直接分配' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="选择理由" prop="reason" min-width="140" show-overflow-tooltip />
          <el-table-column label="骑手响应" width="110">
            <template #default="{ row }">
              <el-tag :type="responseTagType(row.riderResponse)" size="small" effect="dark">
                {{ responseText(row.riderResponse) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="派单时间" prop="assignmentTime" width="170" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- ===== 骑手详情抽屉 ===== -->
    <el-drawer v-model="detailVisible" :title="detailRider?.name || '骑手详情'" size="500px" destroy-on-close>
      <template v-if="detailRider">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border size="small" style="margin-bottom:20px">
          <el-descriptions-item label="姓名">{{ detailRider.name }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detailRider.phone }}</el-descriptions-item>
          <el-descriptions-item label="评分">⭐ {{ detailRider.rating ?? 5.0 }}</el-descriptions-item>
          <el-descriptions-item label="完成单数">{{ detailRider.totalOrders ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="riderStatusTag(detailRider.riderStatus)" size="small" effect="dark">
              {{ riderStatusText(detailRider.riderStatus) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 派单记录 -->
        <h4 style="margin:0 0 10px;color:#6C4FA7">📋 派单记录</h4>
        <el-table :data="riderLogs" style="width:100%" stripe size="small" v-if="riderLogs.length > 0">
          <el-table-column label="订单号" prop="orderId" width="80" />
          <el-table-column label="方式" width="90">
            <template #default="{ row }">
              <el-tag :type="row.selectionMethod === 'ai_assist' ? 'warning' : 'info'" size="small">
                {{ row.selectionMethod === 'ai_assist' ? 'AI' : '直接' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="响应" width="80">
            <template #default="{ row }">
              <el-tag :type="responseTagType(row.riderResponse)" size="small" effect="dark">
                {{ responseText(row.riderResponse) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="时间" prop="assignmentTime" width="160" />
        </el-table>
        <el-empty v-else description="暂无派单记录" :image-size="60" />

        <!-- 当前排班 -->
        <h4 style="margin:16px 0 10px;color:#6C4FA7">📅 当前排班</h4>
        <el-table :data="riderSchedules" style="width:100%" stripe size="small" v-if="riderSchedules.length > 0">
          <el-table-column label="星期" prop="weekday" width="80">
            <template #default="{ row }">
              {{ weekdayText(row.weekday) }}
            </template>
          </el-table-column>
          <el-table-column label="时段" min-width="150">
            <template #default="{ row }">
              {{ row.startTime?.slice(0,5) }} - {{ row.endTime?.slice(0,5) }}
            </template>
          </el-table-column>
          <el-table-column label="常驻区域" prop="location" min-width="120" />
        </el-table>
        <el-empty v-else description="暂无排班" :image-size="60" />
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getRiderListAPI, getRiderStatsAPI, getRiderLogsAPI, getRiderScheduleAPI, removeRiderAPI } from '@/api/rider'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('list')
const logFilter = ref('')
const riders = ref<any[]>([])
const stats = ref({ total: 0, idle: 0, delivering: 0 })
const logs = ref<any[]>([])
const detailVisible = ref(false)
const detailRider = ref<any>(null)
const riderLogs = ref<any[]>([])
const riderSchedules = ref<any[]>([])

// 过滤后的派单记录
const filteredLogs = computed(() => {
  if (!logFilter.value) return logs.value
  return logs.value.filter(l => l.riderResponse === logFilter.value)
})

// 骑手状态
const riderStatusText = (s: number | null | undefined) => {
  if (s === 1) return '空闲'
  if (s === 2) return '配送中'
  return '未激活'
}
const riderStatusTag = (s: number | null | undefined) => {
  if (s === 1) return 'success'
  if (s === 2) return 'warning'
  return 'info'
}

// 响应状态
const responseTagType = (r: string) => {
  if (r === 'accepted') return 'success'
  if (r === 'rejected') return 'danger'
  if (r === 'timeout') return 'info'
  return 'warning' // pending
}
const responseText = (r: string) => {
  if (r === 'accepted') return '已接受'
  if (r === 'rejected') return '已拒绝'
  if (r === 'timeout') return '超时'
  return '待响应'
}

const weekdayText = (w: number) => {
  const map: Record<number, string> = { 1:'周一',2:'周二',3:'周三',4:'周四',5:'周五',6:'周六',7:'周日' }
  return map[w] || `周${w}`
}

// 加载骑手列表
async function loadRiders() {
  try {
    const res = await getRiderListAPI()
    riders.value = res.data.data || []
  } catch { ElMessage.error('获取骑手列表失败') }
}

// 加载统计
async function loadStats() {
  try {
    const res = await getRiderStatsAPI()
    stats.value = res.data.data || { total:0, idle:0, delivering:0 }
  } catch { /* ignore */ }
}

// 加载派单记录
async function loadLogs() {
  try {
    const res = await getRiderLogsAPI()
    logs.value = res.data.data || []
  } catch { ElMessage.error('获取派单记录失败') }
}

async function handleRemoveRider(row: any) {
  try {
    await ElMessageBox.confirm(`确认取消骑手 ${row.name}？`, '提示')
    await removeRiderAPI(row.id)
    ElMessage.success('已取消骑手')
    await loadRiders()
    await loadStats()
  } catch { /* 取消 */ }
}

// 点击行展开骑手详情
function onRiderClick(row: any) {
  viewRiderDetail(row)
}

async function viewRiderDetail(rider: any) {
  detailRider.value = rider
  detailVisible.value = true
  riderLogs.value = []
  riderSchedules.value = []

  // 加载该骑手的派单记录（从全部日志中筛选）
  riderLogs.value = logs.value.filter((l: any) => l.riderId === rider.id)

  // 加载排班
  try {
    const res = await getRiderScheduleAPI(rider.id)
    riderSchedules.value = res.data.data || []
  } catch { /* ignore */ }
}

onMounted(() => {
  loadRiders()
  loadStats()
  loadLogs()
})
</script>

<style scoped>
.rider-page { padding: 0; }
.rider-tabs { --el-tabs-header-height: 48px; }

.stat-card { border-radius: 12px; transition: transform 0.2s; }
.stat-card:hover { transform: translateY(-2px); }
.stat-total { border-left: 4px solid #6C4FA7; }
.stat-idle { border-left: 4px solid #4CAF50; }
.stat-delivering { border-left: 4px solid #FF9800; }
.stat-inner { display: flex; align-items: center; gap: 16px; }
.stat-icon { font-size: 32px; line-height: 1; }
.stat-num { font-size: 28px; font-weight: 700; color: #1C1C1E; line-height: 1.2; }
.stat-label { font-size: 13px; color: #8E8E93; margin-top: 4px; }

.rider-avatar {
  width: 36px; height: 36px; border-radius: 50%;
  background: linear-gradient(135deg, #6C4FA7, #F5A623);
  display: flex; align-items: center; justify-content: center;
  font-size: 16px; font-weight: 700; color: #fff;
}

.rating-stars { font-size: 13px; color: #8E8E93; }

:deep(.el-table th.el-table__cell) {
  background: #F8F7FC !important;
  color: #3C3C43;
  font-weight: 600;
}
:deep(.el-table tr) { cursor: pointer; }
:deep(.el-drawer__header) { color: #6C4FA7; font-weight: 600; }
</style>

