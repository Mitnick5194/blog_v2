import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
    {
        path: "/login",
        name: "home",
        component: () => import("../view/LoginView")
    },
    {
        path: "/index",
        name: "/index",
        component: () => import("../view/BlogListView")
    },
    {
        path: "/",
        name: "index",
        component: () => import("../view/BlogListView")
    },
    {
        path: "/blog_detail",
        name: "blogDetail",
        component: () => import("../view/BlogDetailView")
    }, {
        path: "/add_blog",
        name: "addBlog",
        component: () => import("../view/AddBlog")
    },
];

const router = new VueRouter({
    mode: "history",
    routes,
});

export default router;
