import axios from "axios";
import getConfig from "@/config";

const storage = localStorage
const keyPre = location.host + "-";
const config = getConfig();

const mixin = {
    methods: {
        getLocalValue(name) {
            let item = storage.getItem(keyPre + name);
            //判断是否为对象
            try {
                let ret = JSON.parse(item);
                return ret;
            } catch (e) {
                //不是对象
            }
            return item;
        },
        doRequest(uri, params, method, succCallback, errorCallback) {
            if (!uri) {
                console.error("uri无效");
                return;
            }
            if (!uri.startsWith("/")) {
                uri = "/" + uri;
            }
            if (config.basePath && config.basePath.length && !uri.startsWith(config.basePath)) {
                uri = config.basePath + uri;
            }
            if (!!method && method == 'get') {
                this.doGet(uri, params, succCallback, errorCallback);
            } else {
                //Post请求
                this.doPost(uri, params, succCallback, errorCallback);
            }
        },
        doGet(uri, params, succCallback, errorCallback) {
            let url = config.api + uri;
            axios.get(url, {
                params: params,
                headers: {
                    "auth": this.getToken()
                }
            }).then( (response)=> {
                this.handleRequestSuccess(response, succCallback, errorCallback);
            }).catch( (error)=>  {
                this.handleRequestError(error);
            });
        },
        doPost(uri, params, succCallback, errorCallback) {
            let url = config.api + uri;
            //Post请求
            axios.post(url, params, {headers: {auth: this.getToken()}}).then( (response)=>  {
                this.handleRequestSuccess(response, succCallback, errorCallback);
            }).catch((error)=>{
                this.handleRequestError(error);
            });
        },
        handleRequestSuccess(response, callback, errorCallback) {
            let data = this.checkAndGetData(response, errorCallback);
            if (data === 0 || data) {
                //结果返回是0（注意要用严格===判断）或者对象或者true，则表示成功了
                typeof callback === 'function' && callback(data);
            }


        },
        handleRequestError(error, errorCallback) {
            console.error(error);
            if (typeof errorCallback === 'function') {
                errorCallback(error);

            }

        },
        checkAndGetData(data, errorCallback) {
            let ret = data.data;
            if (!ret) {
                if (typeof errorCallback === 'function') {
                    errorCallback(ret);
                    return false;
                }
                console.error(data);
            }
            let code = ret.code;
            if (!code || code != 200) {
                if (typeof errorCallback === 'function') {
                    errorCallback(ret);
                    return false;
                }
                //判断状态
                if (code == 401) {
                    //登录过期
                    let ref = location.href;
                    window.location.href = "login?ref=" + ref;
                    return;
                }
                if (code == 403) {
                    //权限不足
                    alert("无权限")
                    return;
                }
                //let msg = ret.msg ? ret.msg : "业务请求失败";
                return false;
            }
            return ret.data == 0 ? 0 : ret.data || true;
        },
        getToken() {
            try {
                let account = this.getLocalValue("account");
                if (account) {
                    return account.token;
                }
            } catch (e) {
                console.error(e);
            }
            return "";

        },
        saveLocalValue(name, value) {
            //判断value是否为对象
            let isObj = Object.prototype.toString.call(value) === "[object Object]"
            let obj = value;
            if (isObj) {
                obj = JSON.stringify(value);
            }
            storage.setItem(keyPre + name, obj);
        },
        gotoRef() {
            let href = location.href
            let ref = "index"
            let idx = href.indexOf("ref=");
            if (idx > -1) {
                ref = href.substring(idx + 4);
            }
            location.href = ref;
        }
    }
};


export default mixin;
