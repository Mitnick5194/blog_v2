const PAGE_SIZE = 1000;
const DEFAULT_IMG_URL = "images/default_img.png";

function ContentBlock(key, label, model) {
    return {
        key: key,
        label: label,
        model: model
    };
}

ContentBlock.of = function (key, label, model) {
    return new ContentBlock(key, label, model);
}

function getBlockByKey(blocks, key) {
    if (!blocks || !key) {
        return null;
    }
    for (let i = 0; i < blocks.length; i++) {
        let b = blocks[i];
        if (b.key == key) {
            return b;
        }
    }
    return null;
}

/**
 * 为各项赋值
 * @param data
 */
function asignData(blocks, data) {
    for (let i = 0; i < blocks.length; i++) {
        let b = blocks[i];
        if (!b) {
            continue;
        }
        let key = b.key;
        let d = data[key];
        if (key == 'imgUrl' && !d) {
            d = DEFAULT_IMG_URL;
        }
        if (!d) {
            d = null;
        }
        b.model = d;
    }
    return blocks;
}

/**
 * 清除值
 * @param blocks
 */
function clearData(blocks) {
    for (let i = 0; i < blocks.length; i++) {
        let b = blocks[i];
        if (!b) {
            continue;
        }
        b.model = "";
    }
}

/**
 * 将参数转换成提交参数
 * @param blocks
 * @param ignoreProperty 忽略提交的属性
 */
function buildSubmitObj(blocks, ignoreProperty) {
    if (!blocks) {
        blocks = [];
    }
    if (!ignoreProperty) {
        ignoreProperty = [];
    }
    let obj = {};
    for (let i = 0; i < blocks.length; i++) {
        let b = blocks[i];
        let key = b.key;
        if (key == 'imgUrl') {
            //判断是否为默认头像
            if (DEFAULT_IMG_URL == b.model) {
                b.model = null;//默认头像要置空
            }

        }
        if (ignoreProperty.indexOf(key) > -1) {
            continue;
        }
        obj[key] = b.model;
    }
    return obj;
}

function Content(idx, type, contentBlocks) {
    return {
        idx: idx,
        type, type,
        contentBlocks: contentBlocks
    };
}

//ID
const id = ContentBlock.of("id", "ID", "");
const imgUrl = ContentBlock.of("imgUrl", "imgUrl", "");
//编号
const serial = ContentBlock.of("serial", "编号", "");
const name = ContentBlock.of("name", "姓名", "");
const introduction = ContentBlock.of("introduction", "简介", "");
const nursingRecord = ContentBlock.of("nursingRecord", "护理记录", "");
const testRecord = ContentBlock.of("testRecord", "体检记录", "");
const guardian = ContentBlock.of("guardian", "监护人", "");
const dormitory = ContentBlock.of("dormitory", "宿舍地址", "");
/*//志愿者信息
const voluteerSerial = ContentBlock.of("voluteerSerial", "团队编号", "");
const voluteerName = ContentBlock.of("voluteerName", "团队名", "");
const voluteerIntroduction = ContentBlock.of("voluteerIntroduction", "团队简介", "");
*/
const serviceRecord = ContentBlock.of("serviceRecord", "服务记录", "");


Content.of = function (idx, type, contentBlocks) {
    return new Content(idx, type, contentBlocks);
}

//约定id第一，头像第二
const editContents = [
    Content.of(0, "community", [id, imgUrl]),
    Content.of(2, "elder", [id, imgUrl, serial, name, introduction, nursingRecord, testRecord, guardian, dormitory]),
    Content.of(3, "nurse", [id, imgUrl, serial, name, introduction, nursingRecord]),
    Content.of(4, "doctor", [id, imgUrl, serial, name, introduction, nursingRecord]),
    Content.of(5, "volunteers", [id, imgUrl, serial, name, introduction, serviceRecord])
];

function getEditContentByType(type) {
    for (let i = 0; i < editContents.length; i++) {
        let c = editContents[i];
        if (c.type === type) {
            return c;
        }
    }
    return null;
}

function Menu(type, value, uri) {
    return {
        type: type,
        value: value,
        uri: uri
    }
}

Menu.of = function (type, value, uri) {
    return new Menu(type, value, uri);
}

const menus = [
    Menu.of("community", "社区信息", "/community/get"),
    Menu.of("elder", "老人信息", "/elder/query-by-page"),
    Menu.of("nurse", "护理员信息", "/nurse/query-by-page"),
    Menu.of("doctor", "医生信息", "/doctor/query-by-page"),
    Menu.of("volunteers", "志愿团队信息", "/volunteers/query-by-page")
];

function getMenuIdx(menu) {
    if (!menu) {
        return 0;
    }
    for (let i = 0; i < menus.length; i++) {
        let m = menus[i];
        if (m.type == menu.type) {
            return i;
        }
    }
    return 0;
}


let vm = new Vue({
    el: '#app',
    data: {
        user: null,
        menus: menus,
        menu: menus[0],
        infos: [],//页面内容
        currentPage: 1,
        isEdit: false,
        isDetail: false,
        imgSelected: false,
        coverImgSrc: "",
        file: null, //保存待上传图片
        // content: {}, //新增时的输入
        updateBlock: [], //查看详情或修改时的数据
        update: false, //更新数据标识
        detailIdx: 0,//查看详情时的数据下标，用于编辑返回使用
        isShowDialog: false,// 弹窗
        dialogMsg: null,
        isShowCancel: true,//是否显示取消按钮
        isShowLoading: false,
        commentContent: null,//用户输入的评论内容
        comments: [],//加载回来的内容
        loadFlag: false,
        isCommunity: true, //菜单是否为社区信息
        isEditCommunity: false,
        isDelete: false, //删除标志

    },
    created: function () {
        let user = getCookie("user") || {};
        this.user = user;
    },
    mounted: function () {
        this.queryInfo(null, 0);
        this.loadFlag = true;

    },
    methods: {
        toggleEdit: function () {
            this.isEdit = !this.isEdit;
            this.updateBlock = [];
            //清空数据
            //this.content = {};
            this.file = null;
            this.imgSelected = false;
            if (this.$refs.imgInputClear) {
                this.$refs.imgInputClear.value = ''
            }
            if (this.$refs.imgClear) {
                this.$refs.imgClear.src = ''
            }
            this.imgSelected = false;
            this.isDelete = false;
        }
        ,
        queryInfo: function (e, index) {
            let idx = (index === 0 || index) ? index : e.srcElement.dataset.idx;
            if (idx == 0) {
                this.isCommunity = true;
            } else {
                this.isCommunity = false;
            }
            let menu = this.menus[idx];
            if (!menu) {
                return;
            }
            let page = this.currentPage;
            this.menu = menu;
            this.isEdit = false;
            this.isDetail = false;
            this.update = false;
            this.isDelete = false;
            this.isEditCommunity = false;
            let that = this;
            doRequest(menu.uri, {
                currentPage: page,
                pageSize: PAGE_SIZE
            }, function (data) {
                if (!data) {
                    that.infos = [];
                    return;
                }
                if (data.currentPage === 0 || data.currentPage) {
                    //分页返回的结果
                    let ret = data.data || [];
                    that.infos = ret;
                    return;
                }
                //返回对象
                let ret = [];
                ret.push(data);
                that.infos = ret;
            }, idx == 0 ? "get" : null);
        }
        ,
        gotoEdit: function () {
            //看看是不是修改
            let isDetail = this.isDetail;
            if (isDetail) {
                let blocks = this.updateBlock;
                //分析头像
                let imgBlock = getBlockByKey(blocks, "imgUrl");
                if (imgBlock) {
                    let imgUrl = imgBlock.model;
                    if (imgUrl == DEFAULT_IMG_URL) {
                        //使用的是默认头像，把他清空
                        this.imgSelected = false;
                    } else if (imgUrl) {
                        //有头像
                        this.imgSelected = true;
                        this.coverImgSrc = imgUrl;
                    }
                }

                this.isDetail = false;
                this.update = true;
                return;
            }
            this.$options.methods.toggleEdit.call(this);
            //找到查看类型
            let type = this.menu.type;
            //根据类型匹配显示的内容
            let content = getEditContentByType(type) || {};
            let blocks = content.contentBlocks || [];
            clearData(blocks);
            this.updateBlock = blocks;
        }
        ,
        cancelEdit: function () {
            this.$options.methods.toggleEdit.call(this);
            //判断是否为update
            if (this.update === true || this.isEditCommunity) {
                //返回detail
                this.$options.methods.gotoDetail.call(this, null, true);
            }
            this.update = false;
        }
        ,
        gotoDetail: function (e, isEditReturn) { //isEditReturn标识是否为修改页面返回
            //注意，查看详情isEdit是为true的（因为用到isEdit的页面）
            let idx = isEditReturn ? this.detailIdx : e.srcElement.dataset.idx;
            this.detailIdx = idx;
            let data = this.infos[idx];
            this.$options.methods.toggleEdit.call(this);
            this.isDetail = true;
            if (!data) {
                return;
            }
            //找到查看类型
            let type = this.menu.type;
            //根据类型匹配显示的内容
            let content = getEditContentByType(type);
            if (null == content) {
                return null;
            }
            let blocks = content.contentBlocks;
            if (!blocks || blocks.length == 0) {
                return;
            }
            //为内容的各个项赋值
            let newBlocks = asignData(blocks, data);
            this.updateBlock = newBlocks;
            //加载评论
            let idBlock = getBlockByKey(blocks, "id");
            if (idBlock) {
                this.$options.methods.loadComment.call(this, idBlock.model, true);
            }

        }
        ,
        submit: function () {
            let file = this.file;
            let that = this;
            let submitFn = function (imgUrl) {
                let blocks = that.updateBlock
                let obj;
                if (that.isCommunity) {
                    //提交的是社区信息
                    obj = blocks[0];
                } else {
                    obj = buildSubmitObj(blocks, []);
                }
                if (imgUrl) {
                    obj.imgUrl = imgUrl;
                }
                let menu = that.menu;
                let method = "/create";
                if (that.update || (that.isEditCommunity && obj.id)) {
                    //更新
                    method = "/update";
                }
                if (that.isDelete) {
                    method = "/delete";
                }
                doRequest("/" + menu.type + method, obj, function (data) {
                    console.log(data);
                    let index = getMenuIdx(that.menu) || 0;
                    that.$options.methods.queryInfo.call(that, that, index);

                })
            }
            //检查是否有图片上传
            if (file) {
                uploadImg("/api/upload", file, submitFn);
            } else {
                submitFn();
            }
        }
        ,
        coverImg: function (e) {
            //选择图片后回显
            let file = e.target.files[0];
            this.file = file;
            let reader = new FileReader();
            let that = this;
            reader.readAsDataURL(file);
            reader.onload = function (e) {
                that.coverImgSrc = this.result;
                that.imgSelected = true;
            }
        }
        ,
        clearImg: function () {
            //删除图片
            let isUpdate = this.update;
            if (isUpdate) {
                //更新
                //吧数据的头像清了
                let blocks = this.updateBlock;
                //分析头像
                let imgBlock = getBlockByKey(blocks, "imgUrl");
                if (imgBlock) {
                    imgBlock.model = null;
                }
                this.updateBlock = blocks;
            }
            if (this.$refs.imgClear) {
                this.$refs.imgClear.src = '';
            }

            if (this.$refs.bannerClear) {
                this.$refs.bannerClear.src = '';
            }
            if (this.$refs.imgInputClear) {
                this.$refs.imgInputClear.value = '';
            }
            if (this.$refs.bannerInputClear) {
                this.$refs.bannerInputClear.value = '';
            }
            this.imgSelected = false;
        }
        ,
        logout: function () {
            doRequest("/account/logout", {}, function () {
                window.location.href = "login.html";
            }, "get")
        },
        showDialog: function (msg) {
            this.dialogMsg = msg;
            this.isShowDialog = true;
        },
        closeDialog: function () {
            this.isShowDialog = false;
        },
        showLoading: function () {
            this.isShowLoading = true;
        },
        closeLoading: function () {
            this.isShowLoading = false;
        },
        submitComment: function () {
            //获取当前详情
            let blocks = this.updateBlock;
            let idBlock = getBlockByKey(blocks, "id");
            if (!idBlock) {
                this.$options.methods.showDialog.call(this, "找不到评论对象");
            }
            let id = idBlock.model;
            if (!id) {
                this.$options.methods.showDialog.call(this, "找不到评论对象");
            }
            //评论内容
            let content = this.commentContent;
            if (!content) {
                this.$options.methods.showDialog.call(this, "评论内容为空");
            }
            let menu = this.menu;
            let param = {
                refId: id,
                refType: menu.type,
                content: content
            }
            let that = this;
            doRequest("/comment/create", param, function () {
                that.$options.methods.loadComment.call(that, id);
            })
        },
        loadComment: function (refId) {
            if (!refId) {
                return;
            }
            let menu = this.menu;
            if (null == menu) {
                return;
            }
            let type = menu.type;
            let page = this.currentPage;
            let param = {
                currentPage: page,
                pageSize: PAGE_SIZE,
                refId: refId,
                refType: type
            }
            let that = this;
            doRequest("/comment/query-by-page", param, function (data) {
                that.commentContent = null;
                let ds = data.data;
                if (ds) {
                    for (let i = 0; i < ds.length; i++) {
                        let d = ds[i];
                        d.createTime = formatDate(d.createTime);
                    }
                    that.comments = data.data;
                } else {
                    that.comments = [];
                }
            })
        },
        gotoEditCommunity: function () {
            //修改社区信息
            this.isEditCommunity = true;
            this.imgSelected = false;
            this.$options.methods.toggleEdit.call(this);
            let data = this.infos;
            let obj = {
                id: null,
                imgUrl: '',
                content: null
            };
            //构造updateBlock
            let blocks = [];
            blocks.push(obj);
            this.updateBlock = blocks;
            if (!data || !data.length) {
                //新增
                return;
            }
            //社区信息只能有一条哦
            let info = this.infos[0];
            obj.id = info.id;
            obj.imgUrl = info.imgUrl;
            obj.content = info.content;
            this.updateBlock = blocks;
            if (obj.imgUrl && obj.imgUrl.length > 0) {
                this.imgSelected = true;
            }
        },
        deleteRecord: function () {
            if (confirm("是否确认删除")) {
                this.isDelete = true;
                this.$options.methods.submit.call(this);
            }

        },
    }
})

/**
 * 图片上传
 * @param uri 上传路径
 * @param file 图片文件
 * param callback 回调函数
 */
function uploadImg(uri, file, callback) {
    var formData = new FormData();// 创建form对象
    formData.append('file', file, file.name);// 通过append向form对象添加数据,可以通过append继续添加数据
    let config = {
        headers: {'Content-Type': 'multipart/form-data'}
    };  //添加请求头
    axios.post(uri, formData, config).then(function (resp) {
        if (resp == "fail") {
            vm.showDialog("图片上传失败");
            return;
        }
        typeof callback === 'function' && callback(resp.data);
    })
}




