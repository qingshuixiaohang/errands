<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { addCategoryAPI } from '@/api/category'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

// ------ 数据 ------
const formLabelWidth = '60px'
const form = reactive({
  id: 0,
  name: '',
  type: '1',
  sort: '',
})

const addRef = ref()

// 表单校验
const rules = {
  name: [
    { required: true, trigger: 'blur', message: '请输入商家名称' },
  ],
  sort: [
    { required: true, trigger: 'blur', message: '请输入排序序号' },
  ],
}


// ------ 方法 ------

const router = useRouter()
const route = useRoute()

// 添加分类信息后提交（只有管理员才能对其他分类进行修改，否则普通分类只能对自己进行修改）
const submit = async () => {
  try {
    const valid = await addRef.value.validate();
    if (valid) {
      console.log('submit')
      console.log(form)
      // 在这里执行表单提交操作
      const res = await addCategoryAPI(form)
      if (res.data.code !== 0) {
        console.log('新增分类失败！')
        return false
      }
      // 然后进行 消息提示，页面跳转 等操作
      ElMessage({
        message: '新增商家成功',
        type: 'success',
      })
      router.push({
        path: '/category',
      })
    } else {
      console.log('form not valid!');
      return false;
    }
  } catch (error) {
    console.error('执行过程中失败:', error);
  }
}
// 取消修改
const cancel = () => {
  router.push({
    path: '/category',
  })
}

const init = async () => {
  console.log(route.query)
}

init()
</script>

<template>
  <h1>添加商家</h1>
  <el-card>
    <el-form :model="form" :rules="rules" ref="addRef">
      <el-form-item label="商家名称" :label-width="formLabelWidth" prop="name">
        <el-input v-model="form.name" autocomplete="off" placeholder="例：快乐茶饮" />
      </el-form-item>
      <el-form-item label="排序" :label-width="formLabelWidth" prop="sort">
        <el-input v-model="form.sort" autocomplete="off" placeholder="数字越小越靠前" />
      </el-form-item>
    </el-form>
    <el-form-item>
      <el-button class="submit_btn" type="success" @click="submit">添加</el-button>
      <el-button class="cancel_btn" type="info" plain @click="cancel">取消</el-button>
    </el-form-item>
  </el-card>
</template>

<style scoped>
h1 {
  font-size: 20px;
  text-align: center;
  margin: 20px;
}

.el-form {
  margin-top: 30px;
  width: 500px;
  margin: 0 auto;
}

img {
  width: 60px;
  height: 50px;
  margin-right: 20px
}

.submit_btn {
  width: 100px;
  height: 40px;
  margin: 30px 0 0 400px;
}

.cancel_btn {
  width: 100px;
  height: 40px;
  margin: 30px 0 0 200px;
}
</style>
