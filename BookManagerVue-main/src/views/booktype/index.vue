<template>
  <div class="app-container">
    <!-- 頂部功能 -->
    <div class="filter-container" style="margin-bottom: 15px">
      <!-- 類型名輸入 -->
      <el-input v-model="queryParam.booktypename" placeholder="類型名" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <!-- 一些按鈕 -->
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter" style="margin-left: 10px;">
        搜索
      </el-button>

      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">
        添加書籍類型
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="danger" icon="el-icon-delete" @click="handleDeleteSome">
        批量刪除
      </el-button>
    </div>

    <!--彈出框-->
    <el-dialog :title="formTitle" :visible.sync="dialogFormVisible" width="30%">
      <!--普通表單-->
      <el-form ref="ruleForm" :model="form" :rules="rules" label-width="80px">

        <el-form-item label="書籍類型" prop="booktypename">
          <el-input v-model="form.booktypename" />
        </el-form-item>

        <el-form-item label="類型描述" prop="booktypedesc">
          <el-input v-model="form.booktypedesc" type="textarea" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">確 定</el-button>
      </div>
    </el-dialog>

    <!--數據表格-->
    <el-table
      ref="multipleTable"
      :data="tableData"
      border
      style="width: 100%"
    >
      <el-table-column
        fixed
        type="selection"
        width="55"
      />
      <el-table-column
        fixed
        prop="booktypeid"
        label="序號"
        width="100"
      />
      <el-table-column
        prop="booktypename"
        label="書籍類型"
        show-overflow-tooltip
      />
      <el-table-column
        prop="booktypedesc"
        label="類型描述"
        show-overflow-tooltip
      />
      <el-table-column
        fixed="right"
        label="操作"
        width="150"
      >
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="handleUpdate(scope.row)">編輯</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row,scope.$index)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分頁條-->
    <el-pagination
      background
      :current-page.sync="queryParam.page"
      :page-sizes="[5, 10, 20, 50]"
      :page-size="queryParam.limit"
      layout="total, sizes, prev, pager, next, jumper"
      :total="recordTotal"
      style="margin-top: 15px"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import waves from '@/directive/waves' // waves directive
import { getCount, queryBookTypes, queryBookTypesByPage, addBookType, deleteBookType, deleteBookTypes, updateBookType }
  from '@/api/booktype'

export default {
  name: 'Bookinfo',
  directives: { waves },
  data() {
    return {
      // 表格數據
      tableData: [],
      // 記錄總數
      recordTotal: 0,
      // 查詢參數
      queryParam: {
        page: 1,
        limit: 10,
        booktypename: null
      },
      // 對話框表單顯示
      dialogFormVisible: false,
      // 表單類型（添加數據:0,修改數據:1）
      formType: 0,
      // 表單數據
      form: {
        bookid: null,
        booktypename: '',
        booktypedesc: ''
      },
      rules: {
        booktypename: [
          { required: true, message: '請輸入圖書類型名稱', trigger: 'blur' }
        ],
        booktypedesc: [
          { required: true, message: '請輸入圖書類型描述', trigger: 'blur' }
        ]
      }
    }
  },
  // 創建後
  created() {
    // 從服務器獲取數據表格第一頁的信息
    queryBookTypesByPage(this.queryParam).then(res => {
      console.log('首頁數據獲取成功', res)
      this.tableData = res.data
      this.recordTotal = res.count
    })
  },
  methods: {
    // 分頁大小改變監聽
    handleSizeChange(curSize) {
      const params = this.queryParam
      params.limit = curSize
      queryBookTypesByPage(params).then(res => {
        console.log('分頁數據獲取成功', res)
        this.tableData = res.data
        this.recordTotal = res.count
      })
    },

    // 點擊分頁監聽方法
    handleCurrentChange(curPage) {
      const params = this.queryParam
      params.page = curPage
      queryBookTypesByPage(params).then(res => {
        console.log('分頁數據獲取成功', res)
        this.tableData = res.data
        this.recordTotal = res.count
      })
    },

    // 搜索
    handleFilter() {
      this.queryParam.page = 1
      queryBookTypesByPage(this.queryParam).then(res => {
        if (res.code === 0) {
          this.tableData = res.data
          this.recordTotal = res.count
        }
      })
    },

    // 顯示全部
    handleShowAll() {
      this.queryParam.page = 1
      this.queryParam.booktypename = null
      queryBookTypesByPage(this.queryParam).then(res => {
        if (res.code === 0) {
          this.tableData = res.data
          this.recordTotal = res.count
        }
      })
    },

    // 點擊添加記錄
    handleCreate() {
      // 表單是添加狀態
      this.formType = 0
      // 將空數據置入form
      this.form = {
        booktypeid: null,
        booktypename: '',
        booktypedesc: ''
      }
      // 顯示表單框
      this.dialogFormVisible = true
    },

    // 點擊編輯記錄
    handleUpdate(row) {
      // 表單是編輯狀態
      this.formType = 1
      // 將行數據置入form
      this.form = {
        booktypeid: row.booktypeid,
        booktypename: row.booktypename,
        booktypedesc: row.booktypedesc
      }
      // 顯示表單框
      this.dialogFormVisible = true
    },

    // 添加和更新的提交表單
    submitForm() {
      if (this.formType === 0) { // 添加記錄
        addBookType(this.form).then(res => {
          if (res === 1) {
            this.$message.success('添加記錄成功')
            // 跳轉到末尾
            getCount().then(res => {
              this.recordTotal = res
              this.queryParam.page = Math.ceil(this.recordTotal / this.queryParam.limit)
              this.handleCurrentChange(this.queryParam.page)
            })
          } else {
            this.$message.error('添加記錄失敗')
          }
          this.dialogFormVisible = false // 關閉對話框
        })
      } else if (this.formType === 1) { // 更新記錄
        updateBookType(this.form).then(res => {
          if (res === 1) {
            this.$message.success('更新記錄成功')
            this.handleCurrentChange(this.queryParam.page)
          } else {
            this.$message.error('更新記錄失敗')
          }
          this.dialogFormVisible = false // 關閉對話框
        })
      }
    },

    // 刪除記錄
    handleDelete(row, index) {
      this.$confirm('確定要刪除該條記錄嗎?', '提示', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteBookType(row).then(res => {
          if (res === 1) {
            this.$message.success('刪除記錄成功')
            this.tableData.splice(index, 1)
            // 如果刪完了，獲取上一頁
            if (this.tableData.length === 0) {
              this.queryParam.page = this.queryParam.page - 1
              this.handleCurrentChange(this.queryParam.page)
            }
          } else {
            this.$message.error('刪除記錄失敗')
          }
        })
      })
    },

    // 刪除一些
    handleDeleteSome() {
      this.$confirm('確定要刪除這些記錄嗎?', '提示', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 獲取選中的對象數組
        const items = this.$refs.multipleTable.selection
        deleteBookTypes(items).then(res => {
          if (res > 0) {
            this.$message.success('刪除' + res + '條記錄成功')
            if (this.tableData.length === res) { // 如果本頁內容全部刪光了
              // 當前頁為上一頁
              if (this.queryParam.page !== 0) {
                this.queryParam.page = this.queryParam.page - 1
              }
            }
            // 重載當前頁
            this.handleCurrentChange(this.queryParam.page)
          } else {
            this.$message.error('刪除記錄失敗')
          }
        })
      })
    }

  },
  computed: {
    // 獲得user信息
    ...mapGetters(['id', 'name', 'roles']),
    // 通過表單類型計算表單標題
    formTitle() {
      return this.formType === 0 ? '添加記錄' : '修改記錄'
    }
  }
}

</script>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 150px;
  height: 200px;
  display: block;
}
</style>
