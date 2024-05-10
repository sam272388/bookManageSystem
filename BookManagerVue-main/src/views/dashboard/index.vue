<template>
  <div class="dashboard-container">
    <div title="歡迎頁面" style="padding: 50px; overflow: hidden; color: #409eff;font: lighter 16px 'Lucida Sans Unicode';"
         data-options="iconCls:'icon-heart',plain:true">
      <b style="font-size: 36px; line-height: 30px; height: 30px;">歡迎使用圖書管理系統</b>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  computed: {
    ...mapGetters([
      'id',
      'name',
      'roles'
    ])
  }
}
</script>
<style>
b {
  margin-left: 400px;
}
</style>
