const DEVELOPMENT_CONFIG = {
    "api": "http://blog.qyun.nzjie.cn",
    "basePath": ""
};

const PRODUCTION_CONFIG = {
    "api": "http://blog.qyun.nzjie.cn",
    "basePath": ""
};

const getConfig = () => {
    const mode = process.env.VUE_APP_CONFIG_MODE;
    if (mode === "production") {
        return PRODUCTION_CONFIG;
    } else if (mode === "development") {
        return DEVELOPMENT_CONFIG;
    }
}

export default getConfig;
