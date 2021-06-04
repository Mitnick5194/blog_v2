<template>
  <div v-cloak id="app" class="flex">
    <div class="left-picture">
      <img alt="图片加载失败" src="../assets/images/logo.jpg">
    </div>
    <div class="login-dv">
      <div class="mobile-login-dv">
        <div id="iNvaBar" class="navBar">
          <div class="select-item" data-type="login" @click="loginToggle">账号登录</div>
          <div class="select-item" @click="loginToggle">快速注册</div>
          <div id="iSelectBar" :class="isRegister ? 'right' : 'left'" class="select-bar"></div>
        </div>
        <div id="iForms" :class="{'register-box':isRegister}" class="form-group ">
          <div id="iLoginForm" class="login-form form">
            <div class="key-dv">
              <input v-model="user" name="key" placeholder="用户名/手机号/邮箱" type="text" @keyup.enter="login"/>
            </div>
            <div class="passwd-dv">
              <input v-model="password" class="enter" data-type="login" name="password" placeholder="密码"
                     type="password"
                     @keyup.enter="login"/>
            </div>
            <div class="login-btn submitBtn" @click="login">登录</div>
          </div>
          <div id="iRegisterForm" class="register-form form">
            <div class="key-dv"><input v-model="user" name="key" placeholder="请输入用户名" type="text"/></div>
            <div class="exsit-name hidden">*用户名已存在</div>
            <div class="passwd-dv"><input v-model="password" name="password" placeholder="密码" type="password"/></div>
            <div class="passwd-dv"><input v-model="confirmPassword" name="confirmPasswd" placeholder="确认密码"
                                          type="password"/></div>
            <div class="vertify-dv">
              <div class="vertify-input-dv">
                <input v-model="code" class="enter" data-type="register" name="vertifyCode" placeholder="验证码"
                       type="text"/>
              </div>
              <div class="vertify-image">
                <img v-if="null != verifyCode" :src="verifyCode" class="verifyImg" @click="getVerifyCode"/>
                <img v-else class="verifyImg" src="../assets/images/peding.gif"/>
              </div>
            </div>
            <div class="login-btn submitBtn" data-type="register" @click="register">注册</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import RequestMixin from "@/mixin/RequestMixin"

export default {
  name: "LoginView",
  mixins: [RequestMixin],
  data() {
    return {
      user: null,
      password: null,
      isRegister: false,
      key: null,
      code: null, //用户输入的验证码
      verifyCode: null,
      confirmPassword: null,
    }
  },
  mounted() {
    window.document.title = "登录";
    this.getVerifyCode();
  },
  methods: {
    login() {
      let user = this.user;
      let password = this.password;
      if (!user) {
        alert("请输入用户名/手机号/邮箱")
        return;
      }
      if (!password) {
        alert("请输入密码")
        return;
      }
      let param = {
        user: user,
        password: password
      }
      this.doPost("/api-account/v2/account/login", param, (data) => {
        this.saveLocalValue("account", data);
        this.gotoRef();
      }, (error) => {
        alert(error.msg);
      })
    },
    loginToggle() {
      this.isRegister = !this.isRegister
    },
    getVerifyCode() {
      let that = this
      this.doGet("/api-account/v2/account/get-verify-code", {}, (data) => {
        that.key = data.key;
        let code = data.verifyCode
        that.verifyCode = "data:image/png;base64," + code;
      })
    },
    register() {
      let that = this;
      let user = this.user;
      let password = this.password;
      let code = this.code;
      let confirmPassword = this.confirmPassword;
      let key = this.key;
      if (!user) {
        alert("请输入用户名/手机号/邮箱")
        return;
      }
      if (!password) {
        alert("请输入密码")
        return;
      }
      if (!code) {
        alert("请输入验证码")
        return;
      }
      if (password != confirmPassword) {
        alert("两次密码不一致")
        return
      }
      let param = {
        accountName: user,
        nickName: "test",
        password: password,
        verifyCode: code,
        key: key
      }
      this.doPost("/api-account/v2/account/register", param, () => {
        alert("注册成功");
        that.isRegister = false;
      }, (error) => {
        alert(error.msg);
      })
    }
  }
}
</script>

<style scoped>
input[type=text] {
  border: none
}

.hidden {
  display: none;
}

.hiddenforce {
  display: none !important;
}

* {
  font-size: 14px;
}

body {
  padding: 20px;
}

input, textarea, select, a:focus {
  outline: none;
}

.flex {
  display: flex;
}

.mobile-login-dv {
  overflow: hidden;
  max-width: 400px;
  border: 1px solid #eeeeee;
  padding: 10px;
  background-color: #fff;
}

.login-form {
  margin-right: 30px;
}

.key-dv, .passwd-dv, .vertify-dv {
  padding: 0 10px;
  margin: 20px 0;
  height: 45px;
  line-height: 45px;
  border: 1px solid #eee;
  border-radius: 3px;
}

.key-dv > input, .passwd-dv > input {
  border: none
}

.login-btn {
  padding: 10px 5px;
  background: #337ab7;
  border-radius: 5px;
  text-align: center;
  font-size: 18px;
  color: #fff;
  margin-top: 25px;
  cursor: pointer;
}

.form-group {
  display: flex;
  width: 200%;
  transition: all .2s ease;
  height: 70vh
}

.form-group input {
  width: 98%;
  height: 80%;
}

.form-group > div {
  width: 100%;
}

.navBar {
  width: 100%;
  display: flex;
  position: relative;
}

.navBar > .select-item {
  width: 50%;
  padding: 10px 30px;
  font-size: 16px;
  text-align: center;
  cursor: pointer;
}

.navBar > div:nth-child(2) {
}

.register-box {
  transform: translateX(-50%)
}

.select-bar.left {
  right: unset;
  left: 0;
}

.select-bar.right {
  left: unset;
  right: 0;
}

/* .navBar>.active{border-bottom:2px solid #337ab7;} */
.select-bar {
  position: absolute;
  width: 50%;
  height: 2px;
  background: #337ab7;
  left: 0;
  bottom: 0;
}

.exsit-name {
  color: red;
  font-size: 12px;
}

.vertify-dv {
  display: flex;
  border: none !important;
  padding-left: 0;
}

.vertify-input-dv {
  display: inline-block;
  width: calc(40% - 2px);
  width: -webkit-calc(40% - 2px);
  height: 45px;
  border: 1px solid #eee;
}

.vertify-dv input {
  width: 90%;
  height: 98%;
  padding: 0 5px;
}

.vertify-image {
  width: 60%;
  text-align: right;
  height: 98%;
  background-size: 80px 40px;
  background-position: right;
}

.vertify-image > img {
  border: 1px solid #888;
  height: 40px;
  width: 80px
}


.left-picture {
  display: none;
  width: 40%;
  padding: 0 40px;
}

.left-picture > img {
  width: 100%;
}

.pc-form-group {
  padding: 0 40px;
  border: 1px solid #eee;
}

.wrap {
  width: 450px;
  overflow: hidden
}

/* 判断屏幕大小 */
/* 移动端或pad */
@media screen and (min-width: 768px) {
  .left-picture {
    display: inline-block;
  }

  .register-box {
    transform: translateX(-400px)
  }
}
</style>