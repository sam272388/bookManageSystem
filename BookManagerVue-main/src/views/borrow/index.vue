<template>
  <div class="app-container">
    <!-- 頂部功能 -->
    <div class="filter-container" style="margin-bottom: 15px">
      <!-- 用戶名輸入 -->
      <el-input v-model="queryParam.username" v-permission="['admin']" placeholder="用戶名" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <!-- 圖書名輸入 -->
      <el-input v-model="queryParam.bookname" placeholder="圖書名" style="width: 200px; margin-left: 10px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <!-- 一些按鈕 -->
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter" style="margin-left: 10px;">
        搜索
      </el-button>
      <el-button v-permission="['admin']" class="filter-item" style="margin-left: 10px;" type="danger" icon="el-icon-delete" @click="handleDeleteSome">
        批量刪除
      </el-button>
      <!-- <el-button class="filter-item" style="margin-left: 10px;" type="success" icon="el-icon-edit" @click="handleReturnSome">
        批量還書
      </el-button> -->
    </div>

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
        prop="borrowid"
        label="序號"
        width="100"
      />
      <el-table-column
        prop="username"
        label="用戶名"
        show-overflow-tooltip
      />
      <el-table-column
        prop="bookname"
        label="圖書名"
        show-overflow-tooltip
      />
      <el-table-column
        prop="borrowtimestr"
        label="借書時間"
      />
      <el-table-column
        label="還書時間"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.returntimestr === null || scope.row.returntimestr === ''" style="color: red">等待還書</span>
          <span v-else style="color: #1aac1a">{{ scope.row.returntimestr }}</span>
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        :width="roleIsAdmin?'180px':'110px'"
      >
        <template slot-scope="scope">
          <el-button type="warning" size="small" @click="handleReturn(scope.row,scope.$index)">歸還圖書</el-button>
          <el-button v-permission="['admin']" type="danger" size="small" @click="handleDelete(scope.row,scope.$index)">刪除</el-button>
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
import permission from '@/directive/permission/index.js' // 權限判斷指令
import waves from '@/directive/waves' // waves directive
import { getCount, queryBorrows, queryBorrowsByPage, addBorrow, deleteBorrow, deleteBorrows, updateBorrow, returnBook } from '@/api/borrow'

export default {
  name: 'Bookinfo',
  directives: { waves, permission },
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
        userid: null,
        username: null,
        bookname: null
      }
    }
  },
  // 創建後
  created() {
    // 從服務器獲取數據表格第一頁的信息
    queryBorrowsByPage(this.queryParam).then(res => {
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
      queryBorrowsByPage(params).then(res => {
        console.log('分頁數據獲取成功', res)
        this.tableData = res.data
        this.recordTotal = res.count
      })
    },

    // 點擊分頁監聽方法
    handleCurrentChange(curPage) {
      const params = this.queryParam
      params.page = curPage
      queryBorrowsByPage(params).then(res => {
        console.log('分頁數據獲取成功', res)
        this.tableData = res.data
        this.recordTotal = res.count
      })
    },

    // 搜索
    handleFilter() {
      this.queryParam.page = 1
      queryBorrowsByPage(this.queryParam).then(res => {
        if (res.code === 0) {
          this.tableData = res.data
          this.recordTotal = res.count
        }
      })
    },

    // 顯示全部
    handleShowAll() {
      this.queryParam.page = 1
      this.queryParam.username = null
      this.queryParam.bookname = null
      queryBorrowsByPage(this.queryParam).then(res => {
        if (res.code === 0) {
          this.tableData = res.data
          this.recordTotal = res.count
        }
      })
    },

    // 刪除記錄
    handleDelete(row, index) {
      this.$confirm('確定要刪除該條記錄嗎?', '提示', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteBorrow(row).then(res => {
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
        deleteBorrows(items).then(res => {
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
    },

    // 還書
    handleReturn(row, index) {
      this.$confirm('確定要還書嗎?', '提示', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        returnBook(row.borrowid, row.bookid).then(res => {
          if (res === 1) {
            this.$message.success('還書成功')
            this.handleCurrentChange(this.queryParam.page)
          } else {
            this.$message.error('還書失敗')
          }
        })
      })
    }

    // 批量還書
    // handleReturn(row, index) {
    //   this.$confirm('確定要還書嗎?', '提示', {
    //     confirmButtonText: '確定',
    //     cancelButtonText: '取消',
    //     type: 'warning'
    //   }).then(() => {
    //     returnBook(row.borrowid, row.bookid).then(res => {
    //       if(res === 1) {
    //         this.$message.success('還書成功')
    //         this.handleCurrentChange(this.queryParam.page)
    //       } else {
    //         this.$message.error('還書失敗')
    //       }
    //     })
    //   })
    // },

  },
  computed: {
    // 獲得user信息
    ...mapGetters(['id', 'name', 'roles']),
    roleIsAdmin() {
      if (this.roles[0] === 'admin') return true
      else return false
    }
  },
  watch: {
    'queryParam.userid': {
      immediate: true,
      handler() {
        console.log('我被觸發了')
        if (this.roleIsAdmin) {
          this.queryParam.userid = null
        } else {
          this.queryParam.userid = this.id
        }
      }
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
