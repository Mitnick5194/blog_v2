package com.ajie.commons;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;

/**
 * po生成器
 */
public class PoGenerator {
    /**
     * class形式运行参数demo
     * <p>
     * -Deg.jdbc.driver=com.mysql.jdbc.Driver
     * -Deg.db.url=jdbc:mysql://192.168.33.11/fuli-realty-product
     * -Deg.db.user=fuli-realty
     * -Deg.db.passwd=密码
     * -Deg.db.tables=it_area%
     * -Deg.out.path=D:\mine\test
     */

    static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static String DB_URL = "jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";

    static String USER = "root";
    static final String PASS = System.getProperty("eg.db.passwd");
    /**
     * 表名，支持模糊匹配（%为模糊匹配符）
     */
    static String TABLE_NAME_PATTERN = "mb_%";
    /**
     * 生成的Java文件输出路径
     */
    static String OUT_PUT_PATH = "M:\\_idea_ws\\blog_v2\\generator-code";

    /**
     * 是否生成注释
     */
    static boolean isGenComment = true;
    /**
     * 是否输出dto
     */
    static boolean   isGenDto = true;

    /**
     * 是否输出das
     */
    static boolean isGenDas = false;

    /**
     * das抽象父类
     */
    static String abstractDas = "AbstractBaseDas";

    /**
     * 是否输出mapper
     */
    static boolean isGenMapper = true;

    static {
        String jdbc = System.getProperty("eg.jdbc.driver");
        String dbUrl = System.getProperty("eg.db.url");
        String user = System.getProperty("eg.db.user");
        String tables = System.getProperty("eg.db.tables");
        String path = System.getProperty("eg.out.path");
        if (StringUtils.isNotBlank(jdbc)) {
            JDBC_DRIVER = jdbc;
        }
        if (StringUtils.isNotBlank(dbUrl)) {
            DB_URL = dbUrl;
        }
        if (StringUtils.isNotBlank(user)) {
            USER = user;
        }
        if (StringUtils.isNotBlank(tables)) {
            TABLE_NAME_PATTERN = tables;
        }
        if (StringUtils.isNotBlank(path)) {
            OUT_PUT_PATH = path;
        }
    }


    static final String UNDERLINE = "_";

    static final String RF = "\r\n";

    static final String BRACE_L = "{";
    static final String BRACE_R = "}";


    /**
     * 数据库属性和Java属性映射
     */
    static final Map<String, String> TYPE_MAP = new HashMap<>();
    /**
     * 忽略的属性
     */
    static final List<String> IGNORE_PROPERTIES = new ArrayList<>();

    static {
        //TODO 没覆盖所有类型，按需添加
        TYPE_MAP.put("BIGINT", "Long");
        TYPE_MAP.put("TINYINT", "Integer");
        TYPE_MAP.put("SMALINT", "Integer");
        TYPE_MAP.put("MEDIUMINT", "Integer");
        TYPE_MAP.put("INT", "Integer");
        TYPE_MAP.put("FLOAT", "Float");
        TYPE_MAP.put("DOUBLE", "Double");
        TYPE_MAP.put("VARCHAR", "String");
        TYPE_MAP.put("CHAR", "String");
        TYPE_MAP.put("TEXT", "String");
        TYPE_MAP.put("DATETIME", "Date");
        TYPE_MAP.put("DATE", "Date");
        TYPE_MAP.put("DECIMAL", "BigDecimal");
        TYPE_MAP.put("BIT", "Integer");

        String s = System.getProperty("eg.is.gencomment");
        if (StringUtils.isNotBlank(s)) {
            isGenComment = Boolean.valueOf(s);
        }
        String genDto = System.getProperty("eg.is.gendto");
        if (StringUtils.isNotBlank(genDto)) {
            isGenDto = Boolean.valueOf(genDto);
        }
        IGNORE_PROPERTIES.add("id");
        IGNORE_PROPERTIES.add("del");
        IGNORE_PROPERTIES.add("create_time");
        IGNORE_PROPERTIES.add("update_time");
        IGNORE_PROPERTIES.add("create_person");
        IGNORE_PROPERTIES.add("update_person");

    }

    private static Connection ct;


    public static void main(String[] args) throws Exception {
        try {
            run();
        } finally {
            getConnection().close();
        }
    }

    private static void run() throws Exception {
        Connection ct = getConnection();
        DatabaseMetaData dbmd = ct.getMetaData();
        ResultSet tableRet = dbmd.getTables(null, "%", TABLE_NAME_PATTERN, new String[]{"TABLE"});
        Map<String, List<Table2JavaMeta>> map = new HashMap<>();
        while (tableRet.next()) {
            String table = tableRet.getString("TABLE_NAME");
            List<Table2JavaMeta> list = new ArrayList<>();
            map.put(table, list);
            ResultSet colunmRet = dbmd.getColumns(null, "%", table, "%");
            while (colunmRet.next()) {
                String columnName = colunmRet.getString("COLUMN_NAME");
                if (IGNORE_PROPERTIES.contains(columnName)) {
                    continue;
                }
                String type = colunmRet.getString("TYPE_NAME");
                String remark = colunmRet.getString("REMARKS");
                Table2JavaMeta metas = Table2JavaMeta.of(columnName, type);
                metas.setComment(remark);
                list.add(metas);
            }
        }
        Iterator<Map.Entry<String, List<Table2JavaMeta>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<Table2JavaMeta>> next = it.next();
            String tableName = next.getKey();
            String eoClassName = tableName2JavaClassName(tableName, "PO");
            String str = genJavaFileContent(tableName, eoClassName, next.getValue());
            output2File(eoClassName, str);
            if (isGenDto) {
                //生成dto
                String dtoClassName = tableName2JavaClassName(tableName, "Dto");
                String dtoContent = genDtojavaFileContent(dtoClassName, next.getValue());
                output2File(dtoClassName, dtoContent);
            }
            if (isGenDas) {
                //生成das
                String dasClassName = tableName2JavaClassName(tableName, "Das");
                String dasContent = genDasJavaFileContent(eoClassName, dasClassName);
                output2File(dasClassName, dasContent);
            }
            if (isGenMapper) {
                //生成mapper
                String mapperClassName = tableName2JavaClassName(tableName, "Mapper");
                String dasContent = genMapperJavaFileContent(eoClassName, mapperClassName);
                output2File(mapperClassName, dasContent);
            }
        }
    }

    /**
     * 生成PO Java文件内容
     *
     * @return
     */
    private static String genJavaFileContent(String tableName, String className, List<Table2JavaMeta> metas) {
        StringBuilder sb = new StringBuilder();
        sb.append("import java.util.Date;").append(RF).append(RF);
        sb.append("import java.math.BigDecimal;").append(RF).append(RF);
        sb.append("public class ").append(className).append(" extends BasePO ").append(BRACE_L).append(RF);
        for (Table2JavaMeta m : metas) {
            sb.append(m.out(false)).append(RF);
        }
        sb.append(BRACE_R);
        return sb.toString();
    }

    private static String genDtojavaFileContent(String dtoName, List<Table2JavaMeta> metas) {
        StringBuilder sb = new StringBuilder();
        sb.append("import java.io.Serializable;").append(RF);
        sb.append("import java.math.BigDecimal;").append(RF);
        sb.append("import java.util.Date;").append(RF).append(RF);
        sb.append("import io.swagger.annotations.ApiModel;").append(RF);
        sb.append("import io.swagger.annotations.ApiModelProperty;").append(RF);
        sb.append("@ApiModel(value = \"" + dtoName + "\", description = \"" + dtoName + "\")").append(RF);
        sb.append("public class ").append(dtoName).append(" implements Serializable ").append(BRACE_L).append(RF);
        for (Table2JavaMeta m : metas) {
            sb.append(m.out(true)).append(RF);
        }
        sb.append(BRACE_R);
        return sb.toString();
    }

    private static String genDasJavaFileContent(String eoClassName, String dasClassName) {
        StringBuilder sb = new StringBuilder();
        sb.append("import com.dtyunxi.realty.center.trade.das.base.AbstractBaseDas;").append(RF);
        sb.append("import org.springframework.stereotype.Repository;").append(RF).append(RF);
        sb.append("@Repository").append(RF);
        sb.append("public class ").append(dasClassName).append(" extends ").append(abstractDas).append("<").append(eoClassName).append(", ").append("Long> ").append(BRACE_L).append(RF);
        sb.append(BRACE_R);
        return sb.toString();
    }

    private static String genMapperJavaFileContent(String eoClassName, String mapperClassName) {
        StringBuilder sb = new StringBuilder();
        sb.append(" import com.baomidou.mybatisplus.core.mapper.BaseMapper;").append(RF);
        sb.append("import org.apache.ibatis.annotations.Mapper;").append(RF).append(RF);
        sb.append("@Mapper").append(RF);
        sb.append("public interface ").append(mapperClassName).append(" extends BaseMapper").append("<").append(eoClassName).append("> ").append(BRACE_L).append(RF);
        sb.append(BRACE_R);
        return sb.toString();
    }


    /**
     * 将内容输出到文件
     *
     * @param content
     */
    private static void output2File(String fileName, String content) throws Exception {
        OutputStream out = null;
        try {
            String fixFileName = OUT_PUT_PATH + File.separator + fileName + ".java";
            //System.out.println("正在生成文件：" + fixFileName);
            out = new FileOutputStream(new File(fixFileName));
            out.write(content.getBytes("utf-8"));
            out.flush();
            System.out.println("成功生成文件：" + fixFileName);
        } finally {
            if (null != out) {
                out.close();
            }
        }

    }

    /**
     * @param tableName 表名
     * @param suffix    后缀
     * @return
     */
    private static String tableName2JavaClassName(String tableName, String suffix) {
        if (null == tableName) {
            throw new NullPointerException();
        }
        if (null == suffix) {
            suffix = "";
        }
        //it_xxx
        int idx = tableName.indexOf("_");
        tableName = tableName.substring(idx + 1, idx + 2).toUpperCase() + tableName.substring(idx + 2) + suffix;
        return convertName(tableName);

    }

    private static Connection getConnection() throws Exception {
        if (null != ct) {
            return ct;
        }
        Class.forName(JDBC_DRIVER);
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        ct = conn;
        return conn;
    }

    /**
     * 将下划线转换成驼峰
     *
     * @param property
     * @return
     */
    private static String convertName(String property) {
        int idx = property.indexOf(UNDERLINE);
        if (-1 == idx) {
            return property;
        }
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(property.substring(0, idx));
            property = property.substring(idx + 1, idx + 2).toUpperCase() + property.substring(idx + 2);
            idx = property.indexOf(UNDERLINE);
        } while (idx > 0);
        sb.append(property);
        return sb.toString();
    }

    public static class Table2JavaMeta {
        private String tableProperty;
        private String type;
        private String comment;

        private Table2JavaMeta() {
        }

        public String genJavaPropertyName() {
            return convertName(tableProperty);
        }

        public String genAnnotation() {
            return "@Column(name = \"" + tableProperty + "\")";
        }

        public String genDtoAnnotation() {
            return " @ApiModelProperty(name = \"" + genJavaPropertyName() + "\", value = \"" + comment + "\")";
        }

        public String tableType2JavaType() {
            Optional.ofNullable(TYPE_MAP.get(type)).orElseThrow(() -> new IllegalArgumentException("找不到类型映射：" + type));
            return TYPE_MAP.get(type);
        }

        /**
         * @param isDto 是否输出dto
         * @return
         */
        public String out(boolean isDto) {
            StringBuilder sb = new StringBuilder();
            if (isGenComment && StringUtils.isNotBlank(comment)) {
                sb.append("/**").append(comment).append("*/").append(RF);
            }
            if (isDto) {
                sb.append(genDtoAnnotation());
            }/* else {
                sb.append(genAnnotation());
            }*/
            sb.append(RF);
            sb.append("private ").append(tableType2JavaType()).append(" ").append(genJavaPropertyName()).append(";");
            return sb.toString();
        }

        public static Table2JavaMeta of(String tableProperty, String type) {
            if (StringUtils.isBlank(tableProperty) || StringUtils.isBlank(type)) {
                throw new NullPointerException();
            }
            Table2JavaMeta t = new Table2JavaMeta();
            t.tableProperty = tableProperty;
            t.type = type;
            return t;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
