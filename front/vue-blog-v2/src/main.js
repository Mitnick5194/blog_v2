import Vue from 'vue'
import App from './App.vue'
import route from "@/route";
import CKEditor from "ckeditor4-vue";

/**
 * 将日期格式化为yyyy-MM-dd hh:mm:ss
 * @returns {string}
 */
Date.prototype.format = function () {
  let YY = this.getFullYear() + '-';
  let MM = (this.getMonth() + 1 < 10 ? '0' + (this.getMonth() + 1) : this.getMonth() + 1) + '-';
  let DD = (this.getDate() < 10 ? '0' + (this.getDate()) : this.getDate());
  let hh = (this.getHours() < 10 ? '0' + this.getHours() : this.getHours()) + ':';
  let mm = (this.getMinutes() < 10 ? '0' + this.getMinutes() : this.getMinutes()) + ':';
  let ss = (this.getSeconds() < 10 ? '0' + this.getSeconds() : this.getSeconds());
  return YY + MM + DD + " " + hh + mm + ss;
}

/**
 * 将日期格式化为yyyy-MM-dd，如与今年同年，则显示MM-dd
 * @returns {string}
 */
Date.prototype.handleViewDate = function () {
  let d = new Date();
  let year = d.getFullYear();
  let YY = this.getFullYear();
  let MM = (this.getMonth() + 1 < 10 ? '0' + (this.getMonth() + 1) : this.getMonth() + 1);
  let DD = (this.getDate() < 10 ? '0' + (this.getDate()) : this.getDate());
  if (year == YY) {
    return MM + "-" + DD
  }
  return YY + "-" + MM + "-" + DD
}

Vue.use( CKEditor );
Vue.config.productionTip = false

new Vue({
  router: route,
  render: h => h(App),
}).$mount('#app')
