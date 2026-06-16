<script setup lang="ts">
</script>

<template>
  <router-view />
</template>

<style>
*{
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* ===== 校园跑腿 · 鸣潮主题 ===== */
:root {
  --el-color-primary: #6C4FA7;
  --el-color-primary-light-3: #8B5CF6;
  --el-color-primary-light-5: #A78BFA;
  --el-color-primary-dark-2: #5B3D8F;
  --el-color-primary-light: #F0EDF5;
  --el-color-primary-lighter: #F5F0FF;
  --el-color-warning: #F5A623;
  --el-color-danger: #FF6B6B;
  --el-color-success: #4CAF50;
  --el-color-info: #8E8E93;
  --el-border-color: #E5E5EA;
  --el-border-color-light: #F0F0F5;
  --el-fill-color: #F8F7FC;
  --el-fill-color-light: #FCFBFF;
  --el-text-color-primary: #1C1C1E;
  --el-text-color-regular: #3C3C43;
  --el-text-color-secondary: #8E8E93;
  --el-text-color-placeholder: #C7C7CC;
  --el-bg-color: #F8F7FC;
  --el-bg-color-overlay: #FFFFFF;
  --el-mask-color: rgba(0, 0, 0, 0.4);
  /* 按钮统一样式变量 */
  --btn-border-radius: 8px;
}

/* ===== 按钮全类型统一 ===== */
.el-button {
  border-radius: var(--btn-border-radius) !important;
  font-weight: 500 !important;
  transition: all 0.25s ease !important;
}
.el-button:active { transform: scale(0.97); }

/* 默认按钮 (无type) */
.el-button--default {
  --el-button-bg-color: #FFFFFF;
  --el-button-border-color: #D1D1D6;
  --el-button-text-color: #3C3C43;
  --el-button-hover-bg-color: #FFFFFF;
  --el-button-hover-border-color: #6C4FA7;
  --el-button-hover-text-color: #6C4FA7;
  --el-button-active-bg-color: #F8F7FC;
  --el-button-active-border-color: #5B3D8F;
  --el-button-active-text-color: #5B3D8F;
}

/* 主要按钮 */
.el-button--primary {
  --el-button-bg-color: #6C4FA7;
  --el-button-border-color: #6C4FA7;
  --el-button-text-color: #FFFFFF;
  --el-button-hover-bg-color: #7D5FB8;
  --el-button-hover-border-color: #7D5FB8;
  --el-button-hover-text-color: #FFFFFF;
  --el-button-active-bg-color: #5B3D8F;
  --el-button-active-border-color: #5B3D8F;
}

/* 成功按钮 */
.el-button--success {
  --el-button-bg-color: #4CAF50;
  --el-button-border-color: #4CAF50;
  --el-button-text-color: #FFFFFF;
  --el-button-hover-bg-color: #60C564;
  --el-button-hover-border-color: #60C564;
  --el-button-hover-text-color: #FFFFFF;
  --el-button-active-bg-color: #3D9140;
  --el-button-active-border-color: #3D9140;
}

/* 警告按钮 */
.el-button--warning {
  --el-button-bg-color: #F5A623;
  --el-button-border-color: #F5A623;
  --el-button-text-color: #FFFFFF;
  --el-button-hover-bg-color: #F7B94A;
  --el-button-hover-border-color: #F7B94A;
  --el-button-hover-text-color: #FFFFFF;
  --el-button-active-bg-color: #D4891E;
  --el-button-active-border-color: #D4891E;
}

/* 危险按钮 */
.el-button--danger {
  --el-button-bg-color: #FF6B6B;
  --el-button-border-color: #FF6B6B;
  --el-button-text-color: #FFFFFF;
  --el-button-hover-bg-color: #FF8585;
  --el-button-hover-border-color: #FF8585;
  --el-button-hover-text-color: #FFFFFF;
  --el-button-active-bg-color: #E05555;
  --el-button-active-border-color: #E05555;
}

/* 信息按钮 */
.el-button--info {
  --el-button-bg-color: #8E8E93;
  --el-button-border-color: #8E8E93;
  --el-button-text-color: #FFFFFF;
  --el-button-hover-bg-color: #A0A0A5;
  --el-button-hover-border-color: #A0A0A5;
  --el-button-hover-text-color: #FFFFFF;
  --el-button-active-bg-color: #7A7A7F;
  --el-button-active-border-color: #7A7A7F;
}

/* 文字/链接按钮 */
.el-button--text,
.el-button--link {
  --el-button-text-color: #6C4FA7;
  --el-button-hover-text-color: #8B5CF6;
  --el-button-active-text-color: #5B3D8F;
  border: none !important;
  background: transparent !important;
}
.el-button--text:hover,
.el-button--link:hover {
  background: transparent !important;
}

/* ===== 侧边栏 ===== */
.el-aside .el-menu {
  background: transparent !important;
  border-right: none !important;
}
.el-aside .el-menu-item {
  background: transparent !important;
  color: rgba(255,255,255,0.82) !important;
  border-radius: 8px !important;
  margin: 2px 8px !important;
  width: calc(100% - 16px) !important;
}
.el-aside .el-menu-item:hover {
  background: rgba(255,255,255,0.08) !important;
  color: #fff !important;
}
.el-aside .el-menu-item.is-active {
  background: linear-gradient(135deg, rgba(108,79,167,0.5), rgba(74,45,122,0.3)) !important;
  color: #F5A623 !important;
  font-weight: 600 !important;
}
.el-aside .el-sub-menu__title {
  color: rgba(255,255,255,0.7) !important;
  background: transparent !important;
}
.el-aside .el-sub-menu__title:hover {
  background: rgba(255,255,255,0.06) !important;
  color: #fff !important;
}
.el-aside .el-menu--inline .el-menu-item {
  background: rgba(0,0,0,0.15) !important;
  margin: 1px 8px !important;
}

/* ===== 标签页 ===== */
.el-tabs__item.is-active { color: #6C4FA7 !important; }
.el-tabs__active-bar { background-color: #6C4FA7 !important; }

/* ===== 卡片 ===== */
.el-card {
  border-radius: 12px !important;
  border: 1px solid var(--el-border-color) !important;
}
</style>
