<template>
  <div v-cloak id="app" class="container">
    <div v-show="null != account.token" class="header-navi darkMode">
      <div class="user-header">
        <div class="user-info user" data-id="6" data-type="userinfo">
          <img :src="account.headerUrl" class="user-header-img"/>
          <div class="user-name" @click="logout">{{ account.accountName }}</div>
        </div>
      </div>
    </div>
    <div class="flex main">
      <div class="left">
        <span class="hits">关注公众号和小程序，获取最新动态</span>
        <div class="wxgz-qrcode">
          <img alt="找不到图片" src="@/assets/images/my_wxgz_qrcode.jpg">
        </div>
        <div class="wxgz-qrcode">
          <img alt="找不到图片" src="@/assets/images/my_wxapp_code.jpg">
        </div>
      </div>
      <div class="editor-area">
        <form id="iForm" class="form">
          <input v-model="title" name="title" placeholder="文章标题" type="text"/>
<!--          <textarea id="editor" name="editor"></textarea>-->
          <ckeditor ref="editor" v-model="editorData" :config="editorConfig" />
          <div class="labels flex">
            <div>文章标签：</div>
            <div id="iInputGruop" class="input-group">
              <section v-for="(item,index) in tags" v-bind:key="item.id">
                <input v-model="tags[index].value" focus="false" type="text">
                <span class="delLabelBtn">x</span>
              </section>
            </div>
            <div id="iAddLabelBtn" class="add-label">
              <div class="add-label-btn"><span></span><span></span></div>
              <div class="add-label-hits" @click="addTag">添加标签</div>
            </div>
          </div>
        </form>
        <div id="iBtn" class="submit-btn" @click="submit">发布</div>
        <div class="btn-col">
          <div id="iSaveDraft" class="submit-btn">保存草稿箱</div>
          <div id="iPre" class="submit-btn">预览</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script src="./node_modules/ckeditor4/ckeditor.js"></script>
<script>
//import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import RequestMixin from "@/mixin/RequestMixin";

export default {
  name: "AddBlog",
  mixins: [RequestMixin],
  data() {
    return {
      tags: [],
      account: {},
      title: null,
      editorData:"",
      editorConfig:{
          language: 'zh-cn',
          height:"600px"
      }
    }
  },
  created() {
    let account = this.getLocalValue("account") || {};
    if (!account || !account.token) {
      this.gotoLoginPage();
      return;
    }
    //验证用户
    this.doGet("/api-blog/v2/blog/auth")
    this.account = account;
  },
  mounted() {
    window.document.title = "博客创作";
  },
  methods: {
    addTag() {
      this.tags = this.tags.concat({});
    },
    submit() {
      let content = this.editorData;
      let title = this.title;
      //let tags = this.tags;
      if (!title) {
        alert("请输入标题");
        return;
      }
      if (!content) {
        alert("请输入内容");
        return;
      }
      let tagArr = this.checkAndGetTags(this.tags);
      if (!tagArr) {
        alert("请输入标签")
        return;
      }
      let param = {
        title: title,
        content: content,
        tagList: tagArr,
        type: 1
      };
      this.doPost("/api-blog/v2/blog/create", param, () => {
        location.href = "index"
      })
    },
    logout() {
      if (confirm("是否退出登录")) {
        this.doGet("/api-account/v2/account/logout", {}, () => {
          this.saveLocalValue("account", "");
          location.href = "index"
        });
      }
    },
    checkAndGetTags(tags) {
      if (!tags || !tags.length) {
        return null;
      }
      let arr = [];
      for (let i = 0; i < tags.length; i++) {
        let tag = tags[i];
        if (!tag.value) {
          continue;
        }
        arr.push({tag: tag.value});
      }
      if (!arr.length) {
        return null;
      }
      return arr;
    }
  }
}
</script>

<style scoped>
.container {
  margin-bottom: 50px;
}

.nav {
  width: 90%;
  margin: 0 auto;
  height: 40px;
  background: #eee;
}

.main {
  width: 90%;
  margin: 0 auto;
  margin-top: 20px;
  margin-top: 55px;
}

.main > .left {
  width: 260px;
  display: none
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

.header-navi > div {
  display: inline-block;
}

.header-navi > .user-header {
  display: inline-flex;
  align-items: center;
  height: 100%;
  margin-right: 25px;
  cursor: pointer;
}

.header-navi > .user-header div:nth-child(2) {
  margin-left: 10px;
  color: #337ab7
}

.header-navi .user-header-img {
  width: 25px;
  height: 25px;
  border-radius: 50%;
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

.main > .editor-area {
  width: 100%;
}

.form > input[name=title] {
  width: 94%;
  height: 40px;
  line-height: 40px;
  background: #eee;
  margin-bottom: 5px;
  padding: 0 10px;
  border: none;
  font-size: 16px;
}

.labels {
  align-items: center;
  margin: 15px 0;
  flex-wrap: wrap;
}

.add-label {
  position: relative;
  color: #349EDF;
  cursor: pointer;
}

/* .add-label:before{content:'';position: absolute;width: 20px;height: 20px;border-radius: 3px;background:#349EDF;color:#fff;left: -25px;top: 5px;} */
.add-label > div {
  display: inline-block;
}

.add-label-btn {
  position: absolute;
  left: 0;
  width: 20px;
  height: 20px;
  background: #349EDF;
  border-radius: 3px;
  top: 5px;
}

.add-label-btn > span:nth-child(1) {
  position: absolute;
  top: 9px;
  height: 2px;
  background: #fff;
  width: 16px;
  left: 2px;
}

.add-label-btn > span:nth-child(2) {
  position: absolute;
  width: 2px;
  background: #fff;
  left: 9px;
  top: 2px;
  height: 16px;
}

.add-label-hits {
  margin-left: 25px;
  height: 30px;
  line-height: 30px;
}

.input-group {
  display: flex;
  flex-wrap: wrap;
}

.input-group > section {
  margin-right: 5px;
}

.input-group input {
  background: #eee;
  border: none;
  width: 80px;
  border-radius: 3px;
  padding: 5px;
}

.delLabelBtn {
  display: inline-block;
  width: 20px;
  height: 100%;
  color: #888;
  font-size: 22px;
  margin-right: 3px;
  text-align: center;
  cursor: pointer;
}

.submit-btn {
  cursor: pointer;
  display: inline-block;
  width: 120px;
  padding: 8px 0;
  text-align: center;
  background: #349EDF;
  border-radius: 5px;
  color: #fff;
}

.hits-frame {
  width: 80%;
  display: none;
}

.hits-frame > div {
  padding: 10px;
}

.hits-frame > .hits-title {
  background: #349EDF;
  text-align: center;
  color: #fff;
}

.hits-frame > .hits-content {
  padding: 25px 15px 0 15px;
}

.hit-btn {
  text-align: center;
}

.hit-btn > div {
  width: 50%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 5px 0
}

.hit-btn div:nth-child(1) {
  color: #349EDF;
  border-right: 1px solid #888;
  width: 50%;
}

.center-main {
  padding: 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.center-main > .title {
  font-size: 22px;
  font-weight: bold;
}

.center-main > section {
  padding: 5px 0;
}

.user-list > span {
  display: inline-block;
  padding: 0 5px;
}

.user-list span + span {
  border-left: 1px solid #888;
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

.btn-col {
  display: flex;
  padding: 10px 0;
}

.btn-col > div {
  width: 50%;
  text-align: center;
  max-width: 200px;
}

.btn-col div:nth-child(1) {
  margin-right: 15px;
}

.btn-col div:nth-child(12) {
  margin-left: 15px;
}

/* 平板 */
@media screen and (min-width: 768px) {
  .main > .left {
    display: block
  }
}
</style>