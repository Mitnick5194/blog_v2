const host = "http://blog.qyun.nzjie.cn";

/**
 * 请求
 * @param uri 请求uri
 * @param params 请求参数
 * @param callback 回调函数
 * @param method 请求类型，不传则默认post，非必传
 * @param errorCallback 返回非200状态码回调，非必传
 */
function doRequest(uri, params, method, succCallback, errorCallback) {
    if (!uri) {
        console.error("uri无效");
        return;
    }
    if (!uri.startsWith("/")) {
        uri = "/" + uri;
    }
    if (preUri && preUri.length && !uri.startsWith(preUri)) {
        uri = preUri + uri;
    }
    if (!!method && method == 'get') {
        doGet(uri, params, succCallback, errorCallback);
    } else {
        //Post请求
        doPost(uri, params, succCallback, errorCallback);
    }
}

/**
 * get请求
 * @param uri
 * @param params
 * @param succCallback
 * @param errorCallback
 */
function doGet(uri, params, succCallback, errorCallback) {
    let url = host + uri;
    axios.get(url, {
        params: params
    }).then(function (response) {
        handleRequestSuccess(response, succCallback, errorCallback);
    }).catch(function (error) {
        handleRequestError(error);
    });
}

/**
 * post请求
 * @param uri
 * @param params
 * @param succCallback
 * @param errorCallback
 */
function doPost(uri, params, succCallback, errorCallback) {
    let url = host + uri;
    //Post请求
    axios.post(url, params).then(function (response) {
        handleRequestSuccess(response, succCallback, errorCallback);
    }).catch(function (error) {
        handleRequestError(error);
    });
}

function handleRequestSuccess(response, callback, errorCallback) {
    let data = checkAndGetData(response, errorCallback);
    if (data === 0 || data) {
        //结果返回是0（注意要用严格===判断）或者对象或者true，则表示成功了
        typeof callback === 'function' && callback(data);
    }


}

function handleRequestError(error, errorCallback) {
    console.error(error);
    if (typeof errorCallback === 'function') {
        errorCallback(error);
        return;
    }

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
            alert("无权限")
            return;
        }
        let msg = ret.msg ? ret.msg : "业务请求失败";
        return false;
    }
    return ret.data == 0 ? 0 : ret.data || true;
}
