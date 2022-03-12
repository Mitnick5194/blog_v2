const storage = localStorage

//写入cookie
function saveLocalValue(name, value) {
    //判断value是否为对象
    let isObj = Object.prototype.toString.call(value) === "[object Object]"
    let obj = value;
    if (isObj) {
        obj = JSON.stringify(value);
    }
    storage.setItem(name, obj);
}

function getLocalValue(name) {
    let item = storage.getItem(name);
    //判断是否为对象
    try {
        let ret = JSON.parse(item);
        return ret;
    } catch (e) {
        //不是对象
    }
    return item;
}

function removeLocalValue(name) {
    localStorage.removeItem(name);
}

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

function gotoLoginPage() {
    let ref = location.href;
    ref = encodeURIComponent(ref);
    location.href = "login.html?ref=" + ref;
}

function getQueryString(name) {
    let url = window.location.href;
    let result = "";
    if (url.indexOf("?") >= 0) {
        let strs = url.split("?");
        let o = strs[1];
        let params = o.split("&");
        for (let i = 0; i < params.length; i++) {
            if (params[i].indexOf(name) >= 0) {
                let vals = params[i].split("=");
                result = vals[1];
            }
        }
    }
    return result;
}
