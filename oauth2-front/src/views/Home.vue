<template>
  <div class="home">
    <el-card class="box-card">
      <json-viewer :value="user" style="text-align: left;"/>
      <el-button
          type="danger"
          @click="logout"
          plain
      >注销</el-button>
    </el-card>
  </div>
</template>

<script>
import {Button, Message} from 'element-ui'
import {mapMutations} from 'vuex'
import {request} from '@/network'

export default {
  name: 'Home',
  components: {
    'el-button': Button
  },
  data() {
    return {
      user: {}
    }
  },
  created() {
    request({
      method: 'get',
      url: '/token/github/info'
    }).then(res => {
      const _ = res.data
      this.user = {
        "姓名": _.username,
        "类别": this.getTypeName(_.type)
      }
    })
  },
  methods: {
    ...mapMutations(['removeToken']),
    logout() {
      this.removeToken()
      this.$router.push('/login')
      Message.success('注销成功')
    },
    getTypeName(type = 0) {
      const typeNameArr = ['平台用户', 'GitHub 用户', 'OAuth2 用户', '未知用户']
      type = Math.min(type, typeNameArr.length - 1)
      return typeNameArr[type]
    }
  }
}
</script>

<style scoped>
.home {
  width: 100%;
  margin-top: calc(50vh - 386px);
}

.box-card {
  width: 600px;
  margin-left: auto;
  margin-right: auto;
}
</style>
