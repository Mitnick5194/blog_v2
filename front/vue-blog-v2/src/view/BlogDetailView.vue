<template>
  <div v-cloak id="app" class="main darkMode">
    <!------------------------头部导航------------------------>
    <div v-show="null == account.token " class="header-navi darkMode">
      <div class="user-header">
        <div class="login-btn" @click="gotoLogin">登录/注册</div>
      </div>
      <div class="addblog">写博客</div>
    </div>
    <div v-show="null != account.token" class="header-navi darkMode">
      <div class="user-header">
        <div class="user-info user" data-id="6" data-type="userinfo">
          <img :src="account.headerUrl" class="user-header-img"/>
          <div class="user-name" @click="logout">{{ account.accountName }}</div>
        </div>
      </div>
      <div class="addblog" @click="gotoAddBlog">写博客</div>
    </div>
    <!------------------------end头部导航------------------------>
    <div class="container">
      <!------------------------内容------------------------>
      <div class="container-content">
        <div id="iBlogs" class="center-main darkMode">
          <section class="title">
            <span class="title-text">{{ blog.title }}</span>
          </section>
          <section class="user-list">
            <span>{{ blog.simpleDate }} | </span><span class="user" data-id="6">{{ blog.userName }} | </span><span>阅读数 {{
              blog.readCount
            }}</span>
          </section>
          <section class="tags">
            <span>标签</span>
            <span v-for="item in blog.tagList" v-bind:key="item.tag">{{ item.tag }}</span>
          </section>
          <section v-html="blog.content"></section>

          <div :class="{'hide-mask':hideMask}" class="loading-mask">
            <div class="shapes-4"></div>
          </div>

        </div>
        <div class="right-info">
          <span class="hits">关注公众号和小程序，获取最新动态</span>
          <div class="wxgz-qrcode">
            <img alt="找不到图片" src="@/assets/images/my_wxgz_qrcode.jpg">
          </div>
          <div class="wxgz-qrcode">
            <img alt="找不到图片" src="@/assets/images/my_wxapp_code.jpg">
          </div>
        </div>
      </div>
      <!------------------------end内容------------------------>
      <!-------------------------评论------------------------>
      <div class="comment darkMode">
        <input id="iUser" type="hidden" value="">
        <img id="iUserHeader" alt="" src="@/assets/images/default_user_header.png">
        <div class="text-dv">
                <textarea id="iComment" v-model="commentContent" class="comment-text darkMode"
                          placeholder="想对作者说点什么"></textarea>
        </div>
        <div id="iSubmit" class="submit-btn" @click="publish">发表</div>
      </div>
      <div id="iComments" class="comment-list darkMode">
        <section v-for="(item,index) in comments" v-bind:key="item.id" class="comment-item">
          <div class="left-user-info">
            <img :src="item.userHeaderUrl">
          </div>
          <div class="right-comment-info">
            <span class="commenter">{{ item.userName }}：</span>
            <span>
                        {{ item.content }}
                        <span class="create-date">（{{ item.simpleDate }}	#{{ index + 1 }}楼）</span>
                    </span>
          </div>
        </section>
      </div>
      <!------------------------end评论------------------------>
      <div class="footer-code darkMode">
        <span class="hits">关注公众号和小程序，获取最新动态</span>
        <div class="footer-qr-code">
          <div class="wxgz-qrcode">
            <img src="@/assets/images/my_wxgz_qrcode.jpg"/>
          </div>
          <div class="wxgz-qrcode">
            <img src="@/assets/images/my_wxapp_code.jpg"/>
          </div>
        </div>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <div v-show="showLogin" class="login-mask" @click.self="hideLoginMask">
      <div id="iLoginFrame" class="login-frame">
        <div class="login-dv">
          <div class="mobile-login-dv">
            <div class="navBar">
              <div>登录</div>
            </div>
            <div id="iForms" class="form-group ">
              <div id="iLoginForm" class="login-form">
                <div class="key-dv">
                  <input v-model="user" name="key" placeholder="用户名/手机号/邮箱" type="text"/>
                </div>
                <div class="passwd-dv">
                  <input v-model="password" name="password" placeholder="密码" type="password"/>
                </div>
                <div id="iLoginBtn" class="login-btn submitBtn" @click="login">登录</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import RequestMixin from "@/mixin/RequestMixin";

export default {
  name: "BlogDetailView",
  mixins: [RequestMixin],
  data() {
    return {
      blog: {},
      comments: [],
      blogId: null,
      account: {},
      hideMask: false, //加载动画
      showLogin: false, //登录页面
      user: null,
      password: null,
      commentContent: null,//评论内容
    }
  },
  created: function () {
    let account = this.getLocalValue("account") || {};
    this.account = account;
  },
  mounted: function () {
    window.document.title = "详情";
    let href = window.location.href;
    if (href.indexOf("=") > -1) {
      //先简单粗暴的处理吧
      let id = href.substring(href.indexOf("=") + 1);
      this.blogId = id;
    }
    this.loadBlog();
    this.loadComments();
  },
  methods: {
    loadBlog() {
      let id = this.blogId;
      let that = this;
      this.doGet("/api-blog/v2/blog/query-by-id", {id: id}, function (data) {
        if (data) {
          let date = new Date(data.createTime);
          data["simpleDate"] = date.handleViewDate();
        }
        that.blog = data;
        that.hideMask = true;
      })
    },
    gotoLogin() {
      this.gotoLoginPage();
    },
    loadComments() {
      let id = this.blogId;
      let that = this;
      this.doPost("/api-blog/v2/comment/query-by-blog-id", {
        blogId: id
      }, function (data) {
        if (data && data.data) {
          let list = data.data || [];
          list.forEach(function (item) {
            let d = new Date(item.createTime);
            item["simpleDate"] = d.handleViewDate();
          })
          that.comments = list;
        }
      })
    },
    gotoAddBlog() {
      location.href = "add_blog";
    },
    logout() {
      if (confirm("是否退出登录")) {
        this.doGet("/api-account/v2/account/logout", {}, () => {
              this.saveLocalValue("account", "");
              location.reload()
            }
        )
      }
    },
    publish() {
      let that = this;
      this.doGet("/api-blog/v2/blog/auth", {}, () => {
        let content = that.commentContent;
        if (!content || !content.trim().length) {
          alert("请输入内容");
          return;
        }
        this.doPost("/api-blog/v2/comment/create", {
          blogId: that.blogId,
          content: content
        }, () => {
          that.loadComments();
          that.commentContent = null;
        }, () => {
          //失败，登录过期了
          if (that.account && that.account.token) {
            //登录过，但是已经过期
            alert("登录已过期，请重新登录");
            this.saveLocalValue("account", "");
          }
          that.showLogin = true;
        })
      });
    },
    login() {
      let that = this;
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
        that.account = data;
        that.showLogin = false;
      }, (error) => {
        alert(error.msg);
      })
    },
    hideLoginMask() {
      this.showLogin = false;
    }
  }
}
</script>

<style scoped>
/* 如果不设置overflow:hidden 会出现横向滚动条 */
html, body {
  overflow: hidden;
  overflow-y: auto;
  background-color: #f5f6f7;
}

/* 导航 */
.header-navi {
  z-index: 1000;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 45px;
  line-height: 45px;
  background: #fff;
  text-align: right;
  border-bottom: 1px solid #eee;
}

.header-navi.darkModeActive {
  border-bottom: 1px solid #000;
}

.header-navi > div {
  display: inline-block;
}

.header-navi > .user-header {
  display: inline-flex;
  align-items: center;
  height: 100%;
  margin-right: 15px;
  cursor: pointer;
}

.header-navi > .user-header div:nth-child(2) {
  margin-left: 10px;
  color: #337ab7
}

.user-info {
  display: flex;
  align-items: center;
}

.user-info > .user-name {
  display: inline-block;
  margin-right: 5px;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-info > .user-header-img {
  width: 25px;
  height: 25px;
  border-radius: 50%;
}

.login-btn {
  color: blue
}

.addblog {
  cursor: pointer;
  padding: 0 10px 0 20px;
  vertical-align: top;
  background: url("../assets/images/edit.jpg") no-repeat left center;
  background-size: 20px;
  margin-right: 30px
}

/* 内容 */
.container {
  margin-top: 55px;
  margin-bottom: 20px;
}

.content {
  line-height: 25px;
  font-size: 16px;
}

.center-main {
  padding: 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
  overflow: scroll;
  position: relative;
}

.center-main > .title > .title-text {
  font-size: 22px;
  font-weight: bold;
}

.center-main > .title > .edit {
  cursor: pointer;
  background: url(../assets/images/edit.jpg) no-repeat left center;
  padding-left: 20px;
  margin-left: 15px;
  white-space: normal;
  display: inline-block;
}

.center-main > .title > .delete {
  /*background: url('/blog/images/blog_del.jpg') no-repeat left center;*/
  padding-left: 20px;
  margin-left: 15px;
  white-space: normal;
  display: inline-block;
  background-size: 18px;
  color: red;
  cursor: pointer;
}

.center-main > section {
  padding: 5px 0;
}

.user-list > span {
  display: inline-block;
  padding: 0 5px;
}

.tags > span {
  display: inline-block;
}

.tags span + span {
  border: 1px solid #eee;
  border-radius: 5px;
  margin-right: 5px;
  padding: 1px 10px
}

/* 右侧二维码 */
.right-info {
  display: none;
}

.right-info img {
  width: 100%;
}

.hits {
  padding: 10px 0;
  display: inline-block;
  width: 100%;
  font-size: 18px;
  text-align: center;
  font-weight: 600;
}

.wxgz-qrcode {
  text-align: center;
}

/* 底部二维码，移动端显示 */
.footer-code {
  background: #fff;
  padding: 15px;
}

.footer-code > .footer-qr-code {
  display: flex;
}

.footer-qr-code img {
  width: 100%;
}

.col-green {
  color: green
}

.col-red {
  color: red
}

/* 评论区 */
.comment {
  background: #fff;
  padding: 20px 20px 10px 20px;;
  display: flex
}

.comment img {
  width: 35px;
  height: 35px;
  border-radius: 50%;
}

.comment > .text-dv {
  border: 1px solid #c1c1c1;
  margin-left: 15px;
  padding: 3px 10px;
  flex: 1;
  border-radius: 5px;
}

.comment-text {
  border: none;
  height: 30px;
  display: block;
  width: 100%;
  background: #fff;
  resize: none;
  font-size: 14px;
  line-height: 30px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-transition: height .3s ease-in-out;
  transition: height .3s ease-in-out;
}

.comment-text:focus {
  height: 80px;
  outer: none;
  line-height: unset;
}

.submit-btn {
  height: 38px;
  padding: 0 10px;
  white-space: nowrap;
  background: #337ab7;
  margin-left: 10px;
  border-radius: 5px;
  line-height: 38px;
  color: #fff;
}

.comment-list {
  padding: 0 20px 20px 20px;
  background: #fff;
}

.comment-item {
  display: flex;
  padding: 10px 0;
}

.comment-list .comment-item + .comment-item {
  border-top: 1px dotted #c1c1c1;
}

.left-user-info {
  padding: 0 5px;
  height: 30px;
}

.right-comment-info {
  flex: 1;
  display: flex;
  align-items: center;
}

.left-user-info > img {
  width: 24px;
  height: 24px;
  border-radius: 50%;
}

.create-date {
  padding: 0 5px;
  color: #888;
}

/* 登录弹窗 */
.login-mask {
  position: fixed;
  top: 0px;
  left: 0px;
  height: 100%;
  width: 100%;
  z-index: 10001;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.3)
}

.login-frame {
  background: #fff;
  padding: 20px;
  width: 250px;
  height: 230px;
}

.mobile-login-dv {
  overflow: hidden;
  max-width: 400px;
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

.login-frame .login-btn {
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
  width: 100%;
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
}

.navBar > div {
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  cursor: pointer;
}

.navBar > div:nth-child(2) {
}

.navBar > .active {
  border-bottom: 2px solid #337ab7;
}

.exsit-name {
  color: red;
  font-size: 12px;
}

.vertify-dv {
  display: flex;
  border: none;
  padding-left: 0;
}

.vertify-dv > input {
  width: 40%;
  border: 1px solid #eee;
  border-radius: 3px;
  height: 100%;
  padding: 0 5px;
}

.vertify-dv > div {
  width: 60%;
  text-align: right;
  height: 100%;
}

.vertify-image > img {
  border: 1px solid #888;
  height: 40px;
  width: 80px
}

.log-frame {
  width: 85%;
  min-height: 200px;
  display: none;
}

.log-nav {
  padding: 10px;
  background: #337ab7;
  display: flex;
  white-space: nowrap;
  color: #fff;
}

.log-nav div:nth-child(1) {
  width: 100%
}

.system-info {
  padding: 10px;
  border: 1px solid #eee;
  font-size: 12px;
}

.page-log {
  padding: 10px;
}

.error-font {
  color: red;
}

/*手机版的加载动画*/
.loading-mask {
  position: fixed;
  top: 55px;
  left: 0px;
  width: 100%;
  height: 100%;
  background: #eee;
  z-index: 20001;
  display: flex;
  justify-content: center;
  align-items: center;
}


/* 平板 */
@media screen and (min-width: 768px) {
  .login-frame {
    width: 400px;
    height: 250px;
  }

  .container {
    padding: 30px 50px 0 50px;
  }

  .container-content {
    display: flex;
  }

  .right-info {
    display: block;
    width: 200px;
    margin-left: 30px;
  }

  .center-main {
    flex: 1
  }

  .footer-code {
    display: none;
  }

  /*大屏版的*/
  .loading-mask {
    position: absolute;
    top: 0px;
    left: 0px;
    padding: 50px 0;
    width: 100%;
    height: 100%;
    background: #eee;
    z-index: 20001;
    display: flex;
    justify-content: center;
    align-items: unset;
  }
}

/**PC桌面*/
@media screen and (min-width: 1100px) {
  .right-info {
    width: 250px;
  }

  /* 隐藏菜单 */
  .operating {
    display: none !important;
  }
}

.hide-mask {
  display: none;
}

.shapes-4 {
  width: 40px;
  height: 40px;
  color: #337ab7;
  background: conic-gradient(from -45deg at top 20px left 50%, #0000, currentColor 1deg 90deg, #0000 91deg),
  conic-gradient(from 45deg at right 20px top 50%, #0000, currentColor 1deg 90deg, #0000 91deg),
  conic-gradient(from 135deg at bottom 20px left 50%, #0000, currentColor 1deg 90deg, #0000 91deg),
  conic-gradient(from -135deg at left 20px top 50%, #0000, currentColor 1deg 90deg, #0000 91deg);
  animation: sh4 1.5s infinite cubic-bezier(0.3, 1, 0, 1);
}

@keyframes sh4 {
  50% {
    width: 60px;
    height: 60px;
    transform: rotate(180deg)
  }
  100% {
    transform: rotate(360deg)
  }
}

</style>