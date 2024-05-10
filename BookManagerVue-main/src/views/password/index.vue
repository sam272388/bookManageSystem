<template>
  <div class="app-container">
    <el-form
      ref="form"
      :model="form"
      :rules="passwordRules"
      label-width="100px"
    >
      <el-form-item label="原始密碼" prop="oldPassword">
        <el-input
          v-model="form.oldPassword"
          placeholder="請輸入原始密碼"
          style="width: 300px"
        />
      </el-form-item>
      <el-form-item label="新密碼" prop="newPassword">
        <el-input
          v-model="form.newPassword"
          type="password"
          placeholder="請輸入新密碼"
          style="width: 300px"
        />
      </el-form-item>
      <el-form-item label="確認新密碼" prop="repeat">
        <el-input
          v-model="form.repeat"
          type="password"
          placeholder="請再輸入一遍新密碼"
          style="width: 300px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">更改密碼</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { alterPassword } from '@/api/user'
export default {
  data() {
    const validateRepeat = (rule, value, callback) => {
      if (value !== this.form.newPassword) {
        callback(new Error('兩次輸入的密碼不一致!'))
      } else {
        callback()
      }
    }
    return {
      form: {
        oldPassword: '',
        newPassword: '',
        repeat: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '請輸入舊密碼', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '請輸入新密碼', trigger: 'blur' }
        ],
        repeat: [
          { required: true, message: '請再輸入新密碼', trigger: 'blur' },
          { trigger: 'blur', validator: validateRepeat }
        ]
      }
    }
  },
  methods: {
    onSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const isadmin = this.roles[0] === 'admin' ? 1 : 0
          console.log(isadmin)
          alterPassword({ userid: this.id, username: this.name, isadmin: isadmin, oldPassword: this.form.oldPassword, newPassword: this.form.newPassword }).then(res => {
            if (res === 0) this.$message.error('舊密碼不正確')
            else this.$message.success('修改成功')
          })
        } else {
          console.log('不允許提交!')
          return
        }
      })
    }
  },
  computed: {
    // 獲得user信息
    ...mapGetters(['id', 'name', 'roles'])
  }
}
</script>

<style>
</style>
