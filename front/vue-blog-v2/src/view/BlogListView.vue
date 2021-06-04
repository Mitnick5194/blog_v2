<template>
  <div v-cloak id="app" class="main darkMode">
    <div id="iSlider" class="tag-btn darkMode" @click="slideOutTag">
      <span class="bar-icon"></span>
      <span class="bar-icon"></span>
      <span class="bar-icon"></span>
      <span class="bar-icon"></span>
    </div>
    <div v-show="null == account.token " class="header-navi darkMode">
      <div class="user-header">
        <div class="search-dv">
          <input v-model="keyword" placeholder="搜索" @keyup.enter="search"/>
          <div class="search-img" @click="search">搜索</div>
        </div>
        <div class="login-btn" @click="gotoLogin">登录/注册</div>
      </div>
      <div class="addblog" @click="gotoAddBlog">写博客</div>
    </div>
    <div v-show="null != account.token" class="header-navi darkMode">
      <div class="search-dv">
        <input v-model="keyword" placeholder="搜索" @keyup.enter="search"/>
        <div class="search-img" @click="search">搜索</div>
      </div>
      <div class="user-header">
        <div class="user-info user" data-id="6" data-type="userinfo">
          <img :src="account.headerUrl" class="user-header-img"/>
          <div class="user-name" @click="logout">{{ account.accountName }}</div>
        </div>
      </div>
      <div class="addblog" @click="gotoAddBlog">写博客</div>
    </div>

    <!--手机版搜索栏-->
    <div class="search-box">
      <input v-model="keyword" placeholder="搜索" @keyup.enter="search"/>
      <div class="img-box" @click="search">
        <img src="images/search.png"/>
      </div>

    </div>

    <div class="container">
      <div id="iTags" :class="{'active':slideOutTagFlag}" class="tags-block darkMode">
        <div id="iListTags" class="list-group ">
          <div class="title">标签分类</div>
          <div class="tag" @click="searchByTag">全部</div>
          <div v-for="(item,index) in tags" v-bind:key="item.id" :class="{'active':index==tagIdx}" :data-id="item.id"
               :data-idx="index"
               class="tag" @click="searchByTag">
            {{ item.tag }}（{{ item.blogCount }}）
          </div>
          <div v-if="showLoadTagMore" class="tag moreTags" @click="loadMoreTag">更多标签</div>
        </div>
      </div>
      <div id="iBlogs" class="blogs">
        <section v-for="(item,index) in blogs" v-bind:key="item.id" class="darkMode">
          <div :data-id="item.id" :index="index" class="title" @click="gotoDetail">{{ item.title }}</div>
          <div class="abstract-content">{{ item.abstractContent }}</div>
          <div class="extract-list">
            <div class="list-left flex">
              <img :src="item.userHeaderUrl" class="user" data-id="6"
                   data-type="userinfo">
              <div class="user" data-id="6" data-type="userinfo">{{ item.userName }}</div>
              <div>{{ item.simpleDate }}</div>
            </div>
            <div class="list-right flex">
              <div>阅读 {{ item.readCount }}</div>
              <div>评论 {{ item.commentCount }}</div>
            </div>
          </div>
        </section>
        <div v-if="showLoadMore" class="load-more" @click="loadMore">{{ loadMoreTip }}</div>
        <div v-else class="footer-dv darkMode">
          <div class="footer-line"></div>
          <div id="iFooter" class="footer-content">我是有底线的</div>
          <div class="footer-line after-left"></div>
        </div>

        <div :class="{'hide-mask':hideMask}" class="loading-mask">
          <div class="shapes-4"></div>
        </div>

      </div>
      <div class="qr-code">
        <div>关注公众号和小程序，获取获取最新状态</div>
        <div>
          <img src="@/assets/images/my_wxgz_qrcode.jpg"/>
        </div>
        <div>
          <img src="@/assets/images/my_wxapp_code.jpg"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import RequestMixin from "@/mixin/RequestMixin";

const queryBlogParam = {
  currentPage: 1,
  pageSize: 20,
  keyword: null,
  tagList: [],
  status: 1
}
let queryTagParam = {
  currentPage: 1,
  pageSize: 20,
}
const loadMoreTips = ["加载更多", "正在加载……"];
const account = {}


export default {
  name: "BlogListView",
  mixins:[RequestMixin],
  data() {
    return {
      blogs: [],
      tags: [],
      tagId: null,
      account: account,
      loadMoreTip: loadMoreTips[0],
      currentPage: 1,//要触发计算属性，必须放在这里
      totalBlog: 0,
      totalTagCount: 0,
      currentTagPage: 1,//要触发计算属性，必须放在这里
      keyword: null,
      slideOutTagFlag: false,//控制标签划出
      tagIdx: -1, //选中的标签
      hideMask: false,
    }
  },
  computed: {
    showLoadMore() {
      let currentTotal = this.currentPage * queryBlogParam.pageSize;
      return this.totalBlog > currentTotal;
    },
    showLoadTagMore() {
      let currentTotal = this.currentTagPage * queryTagParam.pageSize;
      return this.totalTagCount > currentTotal;
    },

  },
  created(){
    let account = this.getLocalValue("account") || {};
    this.account = account;
  },
  mounted() {
    window.document.title = "首页";
    this.loadBlogs();
    this.loadTags();
  },
  methods: {
    loadBlogs(clearData) {
      let isClear = !!clearData;
      let that = this;
      this.doPost("/api-blog/v2/blog/query-by-page", queryBlogParam, (data) => {
        if (data.data) {
          let list = data.data || [];
          for (let i = 0; i < list.length; i++) {
            let item = list[i];
            let d = new Date(item.createTime);
            item["simpleDate"] = d.handleViewDate();
          }
          if (isClear) {
            that.blogs = list;
          } else {
            that.blogs = that.blogs.concat(list);
          }

          that.totalBlog = data.total;
        }
        that.toggleLoadMore(true);
        if (!that.hideMask) {
          that.hideMask = true;
        }
      })
    },
    loadTags() {
      let that = this;
      this.doPost("/api-blog/v2/tag/query-by-page", queryTagParam, (data) => {
        if (data.data) {
          that.tags = that.tags.concat(data.data);
        }
        that.totalTagCount = data.total;

      })
    },
    gotoDetail(e) {
      let id = e.srcElement.dataset.id;
      window.open("blog_detail?id=" + id);
    },
    gotoLogin() {
      location.href = "login.html";
    },
    loadMore() {
      this.currentPage += 1;
      this.toggleLoadMore(false)
      queryBlogParam.currentPage = this.currentPage;
      this.loadBlogs();
    },
    toggleLoadMore(flag) { //true表示加载更多
      if (flag) {
        this.loadMoreTip = loadMoreTips[0]
      } else {
        this.loadMoreTip = loadMoreTips[1]
      }
    },
    search() {
      this.hideMask = false;
      let kw = this.keyword;
      queryBlogParam.keyword = kw;
      this.currentPage = 1;
      this.loadBlogs(true);
    },
    searchByTag(e) {
      this.slideOutTagFlag = false;
      let idx = e.srcElement.dataset.idx;
      this.tagIdx = idx;
      let id = e.srcElement.dataset.id;
      if (id) {
        queryBlogParam.tagList = [].concat(id);
      } else {
        queryBlogParam.tagList = [];
      }
      queryBlogParam.currentPage = 1;
      this.loadBlogs(true);
    },
    loadMoreTag() {
      this.currentTagPage += 1;
      queryTagParam.currentPage = this.currentTagPage
      this.loadTags();
    },
    gotoAddBlog() {
      location.href = "add_blog";
    },
    slideOutTag() {
      this.slideOutTagFlag = !this.slideOutTagFlag;
    },
    logout() {
      if (confirm("是否退出登录")) {
        this.doGet("/api-account/v2/account/logout", {}, () => {
          this.saveLocalValue("account", "");
          location.reload()
        });
      }
    }
  }


}
</script>

<style scoped>
body, html {
  background: #fafafa;
}

.flex {
  display: flex;
}

div {
  -webkit-tab-highlight-color: rgba(0, 0, 0, 0);
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
  background: url(../assets/images/edit.jpg) no-repeat left center;
  background-size: 20px;
  margin-right: 30px
}

/* 标签按钮 */
.tag-btn {
  width: 30px;
  height: 40px;
  position: fixed;
  top: 2px;
  left: 10px;
  z-index: 1001;
}

.tag-btn > span {
  position: absolute;
  height: 2px;
  width: 30px;
  background: #bbb;
  left: 0
}

.tag-btn .bar-icon:nth-child(1) {
  top: 8px;
}

.tag-btn .bar-icon:nth-child(2) {
  top: 16px;
}

.tag-btn .bar-icon:nth-child(3) {
  top: 24px;
}

.tag-btn .bar-icon:nth-child(4) {
  top: 32px;
}

/* 标签 */
.tags-block {
  position: fixed;
  z-index: 10001;
  width: 200px;
  height: 80%;
  overflow: scroll;
  top: 45px;
  left: 0;
  transform: translateX(-100%);
  transition: transform .25s ease-in-out;
  background: #fff;
  z-index: 10001
}

.tags-block.active {
  transform: translateX(0%);
}

.list-group {
  border: 1px solid #eee;
  border-radius: 3px;
}

.list-group .title {
  background-color: #337ab7;
  color: #fff;
}

.list-group .active {
  color: #337ab7
}

.list-group div + div {
  border-top: 1px solid #eee;
}

.list-group div {
  display: inline-block;
  width: 100%;
  padding: 10px 0;
  text-align: center;
}

.tag {
  cursor: pointer
}

/* 博客内容 */
.container {
  margin-top: 100px;
  margin-bottom: 20px;
}

.blogs {
  position: relative;
}

.blogs > section {
  border-bottom: 1px solid #eee;
  padding: 20px 15px;
  background: #fff;
  word-break: break-word;
}

.blogs > section.darkModeActive {
  border-bottom: 1px solid #000;
}

.blogs .title {
  font-size: 22px;
  font-weight: bold;
  padding-bottom: 10px;
  cursor: pointer;
}

.abstract-content {
  padding-bottom: 10px;
}

.extract-list {
  display: flex;
}

.extract-list > div {
  width: 50%;
  white-space: nowrap;
  align-items: center;
}

.extract-list img {
  width: 25px;
  border-radius: 50%;
  height: 25px;
}

.list-left > div, .list-right > div {
  margin-left: 10px;
}

.list-right {
  text-align: right;
}

.list-right div:nth-child(1) {
  width: 100%;
  border-right: 1px solid #888;
  padding-right: 10px;
}

.list-right > div {
  text-align: right;
}

.log-frame {
  width: 85%;
  min-height: 200px;
  word-wrap: break-word;
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

.col-green {
  color: green
}

.col-red {
  color: red
}

/* 右侧二维码 */
.qr-code {
  display: none;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
}


/*搜索栏（大屏版本），默认是<768px的*/
.search-dv {
  display: none !important;
}


/*搜索栏 手机版*/
.search-box {
  position: fixed;
  width: 100%;
  height: 45px;
  display: flex;
  padding: 5px 15px;
  background: #fafafa;
  top: 45px;
}

.search-box input {
  background: #fafafa;
  border: 1px solid #eee;
  -webkit-border-radius: 15px;
  -moz-border-radius: 15px;
  border-radius: 15px;
  width: calc(100% - 45px);
  padding-left: 15px;
}

.search-box input:focus {
  border: 1px solid #337ab7;
}

.search-box .img-box {
  position: absolute;
  right: 50px;
  display: inline-flex;
  width: 30px;
  height: 30px;
  top: calc(50% - 15px);
  justify-content: center;
  align-items: center;
}

.search-box .img-box img {
  width: 25px;
  height: 25px;
}

/*手机版的加载动画*/
.loading-mask {
  position: fixed;
  top: 100px;
  left: 0px;
  width: 100%;
  height: 100%;
  background: #eee;
  z-index: 20001;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 默认不显示，只有在大屏幕时才显示 */
/* 平板 */
@media screen and (min-width: 768px) {
  .tag-btn {
    display: none;
  }

  .container {
    display: flex;
    margin-top: 55px;
    padding: 30px 50px 0 50px;
  }

  /* 理论上去了定位，上面设置的z-index不会再生效的啊，怎么还会有效呢，先设为1吧 */
  .tags-block {
    position: inherit;
    z-index: 1;
    transform: translateX(0);
    transition: unset;
    margin-right: 30px;
  }

  .blogs {
    flex: 1
  }

  .search-dv {
    display: block !important;
    top: 5px;
    position: absolute;
    left: calc((100% - 400px) / 2);
    width: 250px;
    height: 31px;
    margin-bottom: 15px;
  }

  .search-dv input {
    top: 2px;
    left: 0px;
    position: absolute;
    height: 100%;
    width: 100%;
    border: 1px solid #888;
    border-radius: 5px;
    background: #fafafa;
    padding: 0 5px 0 15px;
  }

  .search-dv input:focus {
    border: 1px solid #337ab7;
  }

  .search-img {
    width: 50px;
    height: 33px;
    background: #337ab7;
    position: absolute;
    top: 2px;
    right: -45px;
    text-align: center;
    color: #fff !important;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  .search-box {
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
  .tags-block {
    width: 250px;
  }

  .qr-code {
    width: 250px;
    display: block;
    margin-left: 30px;
  }

  /* 隐藏菜单 */
  .operating {
    display: none !important;
  }

  .search-dv {
    position: absolute;
    left: calc((100% - 300px) / 2);
    width: 250px;
    height: 31px;
    margin-bottom: 15px;
  }

  .search-dv input {
    height: 100%;
    border: 1px solid #888;
    border-radius: 5px;
    background: #fafafa;
    padding: 0 5px;
  }

  .search-img {
    width: 50px;
    height: 33px;
    background: #337ab7;
    position: absolute;
    top: 2px;
    right: -45px;
    text-align: center;
    color: #fff !important;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }
}

.load-more {
  padding: 10px 0;
  margin: 0 auto;
  text-align: center;
  font-size: 16px;
  color: #337ab7;

  cursor: pointer;
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