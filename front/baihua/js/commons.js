const preUri = "/api";

/**
 * 请求
 * @param uri 请求uri
 * @param params 请求参数
 * @param callback 回调函数
 * @param method 请求类型，不传则默认post，非必传
 * @param errorCallback 返回非200状态码回调，非必传
 */
function doRequest(uri, params, callback, method, errorCallback) {
    if (!uri) {
        console.error("uri无效");
        return;
    }
    if (!uri.startsWith("/")) {
        uri = "/" + uri;
    }
    if (!uri.startsWith(preUri)) {
        uri = preUri + uri;
    }
    showLoading();

    if (!!method && method == 'get') {
        axios.get(uri, {
            params: params
        }).then(function (response) {
            handleRequestSuccess(response, callback, errorCallback);
        }).catch(function (error) {
            handleRequestError(error);
        });
    } else {
        //Post请求
        axios.post(uri, params).then(function (response) {
            handleRequestSuccess(response, callback, errorCallback);
        }).catch(function (error) {
            handleRequestError(error);
        });
    }
}

function handleRequestSuccess(response, callback, errorCallback) {
    closeLoading();
    let data = checkAndGetData(response, errorCallback);
    if (data === 0 || data) {
        //结果返回是0（注意要用严格===判断）或者对象或者true，则表示成功了
        typeof callback === 'function' && callback(data);
    }


}

function handleRequestError(error, errorCallback) {
    closeLoading();
    console.log(error);
    if (typeof errorCallback === 'function') {
        errorCallback(error);
        return;
    }
    showDialog(error)

}

/**
 * 获取业务数据，只有到达服务器才有这个处理，如果像是404的，不会到达这里
 * @param data
 * @param errorCallback
 * @returns {*}
 */
function checkAndGetData(data, errorCallback) {
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
            window.location.href = "login.html";
            return;
        }
        if (code == 403) {
            //权限不足
            showDialog("权限不足");

            return;
        }
        let msg = ret.msg ? ret.msg : "业务请求失败";
        showDialog(msg);
        return false;
    }
    return ret.data == 0 ? 0 : ret.data || true;
}

function showLoading() {
    try {
        //不一定所有页面都有
        vm.showLoading();
    } catch (e) {
        console.warn(e);
    }
}

function closeLoading(){
    try {
        //不一定所有页面都有
        vm.closeLoading();
    } catch (e) {
        console.warn(e);
    }
}

function showDialog(msg) {
    try {
        //不一定所有页面都有
        vm.showDialog(msg);
    } catch (e) {
        console.warn(e);
    }
}

//写入cookie
function setCookie(name, value) {
    //判断value是否为对象
    let isObj = Object.prototype.toString.call(value) === "[object Object]"
    let obj = value;
    if (isObj) {
        obj = JSON.stringify(value);
    }
    //一天
    let days = 1 * 24 * 3600 * 1000;
    let exp = new Date();
    exp.setTime(exp.getTime() + days);
    document.cookie = name + "=" + escape(obj) + ";expires=" + exp.toGMTString();
}

//获取cookie
function getCookie(name) {
    let arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg)) {
        let data = unescape(arr[2]);
        //判断是否为对象
        try {
            let ret = JSON.parse(data);
            return ret;
        } catch (e) {
            //不是对象
        }
        return data;
    } else {
        return null;
    }

}

//删除cookie
function delCookie(name) {
    let exp = new Date();
    exp.setTime(exp.getTime() - 1);
    let cval = getCookie(name);
    if (cval != null)
        document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
}

function formatDate(date) {
    var date = new Date(date);
    var YY = date.getFullYear() + '-';
    var MM = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    var DD = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate());
    var hh = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    var mm = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    var ss = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
    return YY + MM + DD + " " + hh + mm + ss;
}