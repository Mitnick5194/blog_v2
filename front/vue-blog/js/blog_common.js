
const preUri = "";
/**
 * 请求
 * @param uri 请求uri
 * @param params 请求参数
 * @param callback 回调函数
 * @param method 请求类型，不传则默认post，非必传
 * @param errorCallback 返回非200状态码回调，非必传
 */
function request(uri, params, method, succCallback, errorCallback) {
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