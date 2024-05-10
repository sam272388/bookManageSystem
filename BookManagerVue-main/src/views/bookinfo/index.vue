<template>
  <div class="app-container">
    <!-- 頂部功能 -->
    <div class="filter-container" style="margin-bottom: 15px">
      <!-- 書名輸入 -->
      <el-input v-model="queryParam.bookname" placeholder="書名" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <!-- 作者輸入 -->
      <el-input v-model="queryParam.bookauthor" placeholder="作者" style="width: 200px; margin-left: 10px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <!-- 類型選擇 -->
      <el-select v-model="queryParam.booktypeid" filterable placeholder="類型" clearable class="filter-item" style="width: 200px; margin-left: 10px;">
        <el-option v-for="item in typeData" :key="item.booktypeid" :label="item.booktypename" :value="item.booktypeid" />
      </el-select>
      <!-- 一些按鈕 -->
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter" style="margin-left: 10px;">
        搜索
      </el-button>
      <br><br/>
      <el-button v-permission="['admin']" class="filter-item" style="margin-top: -5px; " type="primary" icon="el-icon-edit" @click="handleCreate">
        添加圖書
      </el-button>
      <el-button v-permission="['admin']" class="filter-item" style="margin-top: -5px;margin-left: 10px;" type="danger" icon="el-icon-delete" @click="handleDeleteSome">
        批量刪除
      </el-button>
    </div>

    <!--彈出框-->
    <el-dialog :title="formTitle" :visible.sync="dialogFormVisible" width="40%">
      <el-row>
        <el-col :span="16">
          <!--普通表單-->
          <el-form ref="ruleForm" :model="form" :rules="rules" label-width="80px">

            <el-form-item label="圖書名稱" prop="bookname">
              <el-input v-model="form.bookname" />
            </el-form-item>

            <el-form-item label="作者" prop="bookauthor">
              <el-input v-model="form.bookauthor" />
            </el-form-item>

            <el-form-item label="價格" prop="bookprice">
              <el-input v-model="form.bookprice" />
            </el-form-item>

            <el-form-item label="圖書類型" prop="booktypeid">
              <el-select v-model="form.booktypeid" placeholder="請選擇類型">
                <el-option
                  v-for="item in typeData"
                  :key="item.booktypeid"
                  :label="item.booktypename"
                  :value="item.booktypeid"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="書籍描述" prop="bookdesc">
              <el-input v-model="form.bookdesc" type="textarea" />
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">確 定</el-button>
      </div>
    </el-dialog>

    <!--彈出框2-->
    <el-dialog title="選擇用戶" :visible.sync="dialogFormVisible2" width="400px">
      <el-form :model="form2">
        <el-form-item label="用戶名" prop="userid" label-width="80px">
          <el-select v-model="form2.userid" placeholder="請選擇用戶">
            <el-option
              v-for="item in userData"
              :key="item.userid"
              :label="item.username"
              :value="item.userid"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible2 = false">取 消</el-button>
        <el-button type="primary" @click="submitForm2">確 定</el-button>
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
        prop="bookid"
        label="序號"
        width="100"
      />
      <el-table-column
        prop="bookname"
        label="書籍名稱"
        width="150"
        show-overflow-tooltip
      />
      <el-table-column
        prop="bookauthor"
        label="作者"
        width="100"
        show-overflow-tooltip
      />
      <el-table-column
        prop="bookprice"
        label="書籍價格"
        width="100"
      />
      <el-table-column
        prop="booktypename"
        label="書籍類別"
        width="100"
        show-overflow-tooltip
      />
      <el-table-column
        prop="bookdesc"
        label="書籍描述"
        min-width="300"
        show-overflow-tooltip
      />
      <el-table-column
        label="是否已借出"
        width="100"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.isborrowed === 1" style="color: red">已借出</span>
          <span v-else style="color: #1aac1a">未借出</span>
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        :width="roleIsAdmin?'240px':'110px'"
      >
        <template slot-scope="scope">
          <el-button type="warning" size="small" @click="handleBorrow(scope.row)">借閱書籍</el-button>
          <el-button v-permission="['admin']" type="primary" size="small" @click="handleUpdate(scope.row)">編輯</el-button>
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
import {
  getCount,
  queryBookInfosByPage,
  addBookInfo,
  deleteBookInfo,
  deleteBookInfos,
  updateBookInfo
} from '@/api/bookinfo'
import { queryBookTypes } from '@/api/booktype'
import { borrowBook } from '@/api/borrow'
import { queryUsers } from '@/api/user'

export default {
  name: 'Bookinfo',
  directives: { waves, permission },
  data() {
    return {
      // 表格數據
      tableData: [],
      // 記錄總數
      recordTotal: 0,
      // 圖書類型數據
      typeData: [],
      // 用戶數據
      userData: [],
      // 查詢參數
      queryParam: {
        page: 1,
        limit: 10,
        bookname: null,
        bookauthor: null,
        booktypeid: null
      },
      // 對話框表單顯示
      dialogFormVisible: false,
      dialogFormVisible2: false,
      // 表單類型（添加數據:0,修改數據:1）
      formType: 0,
      // 表單數據
      form: {
        bookid: null,
        bookname: '',
        bookauthor: '',
        bookprice: 0,
        booktypeid: 1,
        bookdesc: '',
        isborrowed: 0,
        bookimg: ''
      },
      form2: {
        userid: '',
        bookid: ''
      },
      rules: {
        bookname: [
          { required: true, message: '請輸入圖書名稱', trigger: 'blur' }
        ],
        bookauthor: [
          { required: true, message: '請輸入作者', trigger: 'blur' }
        ],
        bookprice: [
          { required: true, message: '請輸入價格', trigger: 'blur' }
        ],
        booktypeid: [
          { required: true, message: '請選擇類型', trigger: 'blur' }
        ],
        bookdesc: [
          { required: true, message: '請輸入描述', trigger: 'blur' }
        ],
        isborrowed: [
          { required: true, message: '請選擇狀態', trigger: 'blur' }
        ]
      }
    }
  },
  // 創建後
  created() {
    // 從服務器獲取數據表格第一頁的信息
    queryBookInfosByPage(this.queryParam).then(res => {
      console.log('首頁數據獲取成功', res)
      this.tableData = res.data
      this.recordTotal = res.count
    })
    // 從服務器獲取所有的圖書類型
    queryBookTypes().then(res => {
      console.log('圖書類型獲取成功', res)
      this.typeData = res
    })
  },
  mounted() {
    if (this.roleIsAdmin === false) {
      this.queryParam.limit = 5
      this.handleSizeChange(this.queryParam.limit)
    }
  },
  methods: {
    // 分頁大小改變監聽
    handleSizeChange(curSize) {
      const params = this.queryParam
      params.limit = curSize
      queryBookInfosByPage(params).then(res => {
        console.log('分頁數據獲取成功', res)
        this.tableData = res.data
        this.recordTotal = res.count
      })
    },

    // 點擊分頁監聽方法
    handleCurrentChange(curPage) {
      const params = this.queryParam
      params.page = curPage
      queryBookInfosByPage(params).then(res => {
        console.log('分頁數據獲取成功', res)
        this.tableData = res.data
        this.recordTotal = res.count
      })
    },

    // 搜索圖書
    handleFilter() {
      this.queryParam.page = 1
      queryBookInfosByPage(this.queryParam).then(res => {
        if (res.code === 0) {
          this.tableData = res.data
          this.recordTotal = res.count
        }
      })
    },

    // 圖片上傳成功監聽
    handleAvatarSuccess(res, file) {
      console.log(res)
      console.log(file)
      if (res.code === 0) {
        this.$message.success('上傳成功')
        this.form.bookimg = res.data
      } else {
        this.$message.error('上傳失敗，請聯系管理員')
      }
    },

    // // 圖片上傳之前監聽
    // beforeAvatarUpload(file) {
    //   const isJPG = file.type === 'image/jpeg'
    //   const isLt2M = file.size / 1024 / 1024 < 2

    //   if (!isJPG) {
    //     this.$message.error('上傳封面圖片只能是 JPG 格式!')
    //   }
    //   if (!isLt2M) {
    //     this.$message.error('上傳封面圖片大小不能超過 2MB!')
    //   }
    //   return isJPG && isLt2M
    // },

    // 點擊添加記錄
    handleCreate() {
      // 從服務器獲取所有的圖書類型
      queryBookTypes().then(res => {
        console.log('圖書類型獲取成功', res)
        this.typeData = res
      })
      // 表單是添加狀態
      this.formType = 0
      // 將空數據置入form
      this.form = {
        bookid: null,
        bookname: '',
        bookauthor: '',
        bookprice: '',
        booktypeid: '',
        bookdesc: '',
        isborrowed: 0
        // bookimg: 'http://wangpeng-imgsubmit.oss-cn-hangzhou.aliyuncs.com/img/202111131322401.jpg'
      }
      // 顯示表單框
      this.dialogFormVisible = true
    },

    // 點擊編輯記錄
    handleUpdate(row) {
      // 從服務器獲取所有的圖書類型
      queryBookTypes().then(res => {
        console.log('圖書類型獲取成功', res)
        this.typeData = res
      })
      // 表單是編輯狀態
      this.formType = 1
      // 將行數據置入form
      this.form = {
        bookid: row.bookid,
        bookname: row.bookname,
        bookauthor: row.bookauthor,
        bookprice: row.bookprice,
        booktypeid: row.booktypeid,
        bookdesc: row.bookdesc,
        isborrowed: row.isborrowed,
        bookimg: row.bookimg
      }
      // 顯示表單框
      this.dialogFormVisible = true
    },

    // 點擊借閱圖書
    handleBorrow(row) {
      console.log('111')
      if (this.roleIsAdmin) {
        // 顯示表單框
        this.dialogFormVisible2 = true
        // 獲取圖書信息
        this.form2.bookid = row.bookid

        // 獲取用戶信息
        queryUsers().then(res => {
          this.userData = res
        })
      } else {
        this.$confirm('您確定要借書嗎?', '提示').then(() => {
          borrowBook(this.id, row.bookid).then(res => {
            if (res === 1) {
              this.$message.success('借書成功')
              this.handleCurrentChange(this.queryParam.page)
            } else {
              this.$message.error('借書失敗')
            }
            this.dialogFormVisible2 = false // 關閉對話框
          })
        })
      }
    },

    // 添加和更新的提交表單
    submitForm() {
      if (this.formType === 0) { // 添加記錄
        addBookInfo(this.form).then(res => {
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
        updateBookInfo(this.form).then(res => {
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

    // 借書的提交表單
    submitForm2() {
      borrowBook(this.form2.userid, this.form2.bookid).then(res => {
        console.log('userid', this.form2.userid)
        console.log('bookid', this.form2.bookid)
        if (res === 1) {
          this.$message.success('借書成功')
          this.handleCurrentChange(this.queryParam.page)
        } else {
          this.$message.error('借書失敗')
        }
        this.dialogFormVisible2 = false // 關閉對話框
      })
    },

    // 刪除記錄
    handleDelete(row, index) {
      this.$confirm('確定要刪除該條記錄嗎?', '提示', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteBookInfo(row).then(res => {
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
        deleteBookInfos(items).then(res => {
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
      return this.formType === 0 ? '添加圖書' : '修改記錄'
    },
    roleIsAdmin() {
      if (this.roles[0] === 'admin') return true
      else return false
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
