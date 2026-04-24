<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-title">总捐赠单数</div>
            <div class="stat-value">{{ overviewData.totalAppointments || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-title">公益对接次数</div>
            <div class="stat-value">{{ overviewData.totalDockings || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-title">回收点数量</div>
            <div class="stat-value">{{ overviewData.totalStations || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-title">捐赠用户数</div>
            <div class="stat-value">{{ overviewData.totalDonors || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>月度捐赠趋势</span>
            </div>
          </template>
          <v-chart class="chart" :option="trendOption" autoresize />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>旧物类型分布</span>
            </div>
          </template>
          <v-chart class="chart" :option="pieOption" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getOverview, getMonthlyTrend, getItemTypeDistribution } from '@/api/statistics'

use([CanvasRenderer, BarChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const overviewData = ref<any>({})
const trendOption = ref<any>({})
const pieOption = ref<any>({})

const itemTypeMap: Record<string, string> = {
  book: '书籍',
  clothing: '衣物',
  electronics: '电子产品',
  daily: '生活用品',
  other: '其他'
}

const loadData = async () => {
  try {
    // 概览数据
    const overviewRes: any = await getOverview()
    overviewData.value = overviewRes || {}

    // 月度趋势
    const trendRes: any = await getMonthlyTrend()
    const trendData = trendRes || []
    trendOption.value = {
      tooltip: {
        trigger: 'axis'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: trendData.map((d: any) => d.month).reverse()
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '捐赠单数',
          type: 'bar',
          barWidth: '60%',
          data: trendData.map((d: any) => d.count).reverse(),
          itemStyle: {
            color: '#409EFF'
          }
        }
      ]
    }

    // 物品分布
    const pieRes: any = await getItemTypeDistribution()
    const pieData = pieRes || []
    pieOption.value = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'horizontal',
        bottom: 0
      },
      series: [
        {
          name: '旧物类型',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 18,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: pieData.map((d: any) => ({
            name: itemTypeMap[d.type] || d.type,
            value: d.count
          }))
        }
      ]
    }
  } catch (error) {
    console.error('Failed to load dashboard data', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  text-align: center;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.chart-row {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart {
  height: 400px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
