<template>
  <div class="container">
    <div class="box">
      <el-form :model="loginForm" :rules="rules" ref="loginForm">
        <el-form-item prop="username">
          <label>
            <input
                type="text"
                placeholder="用户名"
                class="input-box"
                v-model.trim="loginForm.username"/>
          </label>
        </el-form-item>
        <el-form-item prop="password">
          <label>
            <input
                type="password"
                placeholder="密码"
                class="input-box"
                v-model.trim="loginForm.password"/>
          </label>
        </el-form-item>
        <button type="submit" class="btn" @click.prevent="login">登 录</button>
      </el-form>
      <span class="option">第三方登录</span>
      <div class="social">
        <div class="box-radius">
          <img src="@/assets/img/github.png" alt="" @click="githubAuthorize">
        </div>
        <div class="box-radius">
          <img src="@/assets/img/oauth.png" alt="" @click="oauth2Authorize">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {Form, FormItem, Message} from 'element-ui'
import {request} from '@/network'
import {ERROR} from '@/assets/js/const'
import {mapMutations} from 'vuex'

export default {
  name: 'Login',
  components: {
    'el-form': Form,
    'el-form-item': FormItem
  },
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
        password: [{required: true, message: '请输入密码', trigger: 'blur'}]
      }
    }
  },
  methods: {
    ...mapMutations(['setToken']),
    login() {
      this.$refs['loginForm'].validate(valid => {
        if (valid) {
          request({
            method: 'post',
            url: '/login',
            data: {
              ...this.loginForm
            }
          }).then(res => {
            if (res.code === ERROR) {
              Message.error(res.message)
              return
            }
            this.setToken(res.data)
            this.$router.push('/home')
            Message.success('登录成功')
          })
        }
      })
    },
    githubAuthorize() {
      const env = process.env
      window.location.href = `https://github.com/login/oauth/authorize?client_id=${env.VUE_APP_GITHUB_CLIENT_ID}&redirect_uri=${env.VUE_APP_GITHUB_REDIRECT_URI}`
    },
    oauth2Authorize() {
      const env = process.env
      window.location.href = `http://localhost:9002/oauth/authorize?client_id=${env.VUE_APP_OAUTH_CLIENT_ID}&response_type=code`
    }
  }
}
</script>

<style scoped>
* {
  font-family: 'Inter', Arial, Helvetica, sans-serif;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

*::selection {
  background-color: #c7c9ca;
}

.container {
  margin: 150px auto;
  max-width: 960px;
}

.box {
  box-sizing: border-box;
  margin: 30px auto auto;
  height: 500px;
  padding: 80px 40px;
  width: 340px;
  border-radius: 35px;
  background-color: #ecf0f3;
  box-shadow: -8px -8px 8px #feffff, 8px 8px 8px #161b1d2f;
}

.box label {
  font-size: 16px;
  font-weight: 500;
  color: #858686;
}

.box .input-box {
  width: 100%;
  height: 35px;
  padding-left: 20px;
  border: none;
  color: #858686;
  margin-top: 30px;
  background-color: #ecf0f3;
  outline: none;
  border-radius: 20px;
  box-shadow: inset 5px 5px 5px #cbced1,
  inset -5px -5px 5px #ffffff;
}

.box .input-box::placeholder {
  color: #9ea0a0;
}

.btn {
  width: 100%;
  margin-top: 20px;
  height: 38px;
  border: none;
  outline: none;
  border-radius: 20px;
  background-color: #727171;
  font-size: 16px;
  font-weight: 500;
  color: #ffffff;
  cursor: pointer;
  box-shadow: -5px -5px 8px #d8e2e6, 5px 5px 10px #2c313378;
  transition: 0.8s;
}

.btn:hover {
  background-color: #535658;
  box-shadow: inset 5px 5px 10px #05050578,
  inset -5px -5px 10px #9e9c9c;
}

.social {
  display: flex;
  justify-content: center;
  margin-top: 14px;
}

.box-radius {
  border-radius: 50%;
  width: 40px;
  display: block;
  height: 40px;
  margin: 6px;
  background-color: #ecf0f3;
  box-shadow: 5px 5px 6px #0d275023, -5px -5px 6px #ffffff;
  padding: 11px;
  cursor: pointer;
}

.box-radius:hover {
  box-shadow: inset 5px 5px 5px #cbced1,
  inset -5px -5px 5px #ffffff;
}

.box-radius img {
  width: 18px;
  margin: auto;
  height: 18px;
}

.option {
  display: block;
  margin-top: 35px;
  color: #6c6d6d;
  text-align: center;
}
</style>
