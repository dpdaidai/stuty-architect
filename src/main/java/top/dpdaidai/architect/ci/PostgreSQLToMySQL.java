package top.dpdaidai.architect.ci;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CREATE TABLE IF NOT EXISTS `local_head` (
 *   `HEAD_ID` int(11) NOT NULL COMMENT '号头标识（唯一标识无实际意义）',
 *   `HEAD` varchar(20) NOT NULL COMMENT '号头',
 *   `AREA_CODE` varchar(20) NOT NULL COMMENT '号头所在地区区号',
 *   `HEAD_LENGTH` int(11) DEFAULT NULL COMMENT '号头长度',
 *   `COMMENTS` varchar(60) DEFAULT NULL COMMENT '说明信息',
 *   `EFF_DATE` datetime DEFAULT NULL,
 *   `EXP_DATE` datetime DEFAULT NULL,
 *   `STATE_DATE` datetime DEFAULT NULL,
 *   PRIMARY KEY (`HEAD_ID`),
 *   UNIQUE KEY `UK_LOCAL_HEAD` (`HEAD`,`AREA_CODE`,`EFF_DATE`,`EXP_DATE`),
 *   KEY `IND_LOCAL_HEAD_HEAD` (`HEAD`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='A.1.9.3　归属局/拜访局代码表';
 *
 * 1    表名和字段的`需要转化为"
 * 2    表名切换为大写
 * 3    bigint(d)/int(d) -> int
 * 4    datetime -> timestamp
 * 4    删除存储引擎和默认字符集
 * 5    字段注释 删除后 , 额外增加一行sql
 * 6    表注释删除 , 额外增加一行sql
 * 7    修改索引 : 删除建表时的普通索引 , 移动建表后再建立 , 额外增加一行sql
 *
 *
 * 待处理 :
 * 1    表名和字段名未确认是否全部大写 . 有时候建表字段用的小写 , 索引又用的大写
 * 2    在同一个schema下, pg不能有同名索引 , 即使索引在不同的表上
 *        --> 导致uni_card_alone_month(15) , uni_card_end2end_year , uni_card_core_month(4) 暂时不知道如何导入
 * 3    uni_card_alone 数据库打开乱码 , 我的系统下没有找到对应的编码格式
 *
 * 参考链接 : https://www.itmuch.com/work/mysql-ddl-2-pgsql-ddl/
 *
 * @Author chenpantao
 * @Date 7/29/21 3:52 PM
 * @Version 1.0
 */
public class PostgreSQLToMySQL {

    public static void main(String[] args) throws IOException, JSQLParserException {
        // 你的MySQL DDL路径
        String mysqlDDLPath = "/Users/chenpantao/Downloads/建表语句/uni_card_core_month(4).sql";
        String dDLs = FileUtils.readFileToString(new File(mysqlDDLPath));

        System.out.println(dDLs);
        System.out.println("++++++++++开始转换SQL语句+++++++++++++");

        Statements statements = CCJSqlParserUtil.parseStatements(dDLs);

        statements.getStatements()
                .stream()
                .map(statement -> (CreateTable) statement).forEach(ct -> {
            Table table = ct.getTable();

            //表名设置大写
//            table.setName(table.getName().toUpperCase());

            //获取字段注释
            List<ColumnDefinition> columnDefinitions = ct.getColumnDefinitions();
            List<String> comments = new ArrayList<>();
            List<ColumnDefinition> collect = columnDefinitions.stream()
                    .peek(columnDefinition -> {
                        List<String> columnSpecStrings = columnDefinition.getColumnSpecStrings();

                        int commentIndex = getCommentIndex(columnSpecStrings);

                        if (commentIndex != -1) {
                            int commentStringIndex = commentIndex + 1;
                            String commentString = columnSpecStrings.get(commentStringIndex);

                            String commentSql = genCommentSql(table.toString(), columnDefinition.getColumnName(), commentString);
                            comments.add(commentSql);
                            columnSpecStrings.remove(commentStringIndex);
                            columnSpecStrings.remove(commentIndex);
                        }
                        columnDefinition.setColumnSpecStrings(columnSpecStrings);
                    }).collect(Collectors.toList());
            ct.setColumnDefinitions(collect);


            //删除建表时的普通索引 , 移动建表后再建立
            List<Index> indexList = ct.getIndexes();
            List<Index> removeList = new ArrayList<>();
            List<String> newIndex = new ArrayList<>();
            for (Index index : indexList) {
                if (index.getType().equals("KEY")) {
                    removeList.add(index);
                    newIndex.add(genIndex(table.toString(), index));
                }
                if (index.getType().equals("UNIQUE KEY")) {
                    removeList.add(index);
                    newIndex.add(genUniqueIndex(table.toString(), index));
                }

                //如果某个字段为自增 , 那么它一定是主键 , pg设置自增主键为 BIGSERIAL/SERIAL PRIMARY KEY
                //并且不需要在建表最后声明 primary key
                if (index.getType().equals("PRIMARY KEY")) {
                    for (ColumnDefinition columnDefinition : columnDefinitions) {
                        if (columnDefinition.getColumnSpecStrings().contains("AUTO_INCREMENT")) {
                            removeList.add(index);
                            continue;
                        }
                    }
                }
            }
            indexList.removeAll(removeList);


            //修改关键字
            String createSQL = ct.toString()
                    .replaceAll("`", "\"")
                    .replaceAll("int \\((\\d*)\\)", "int")
                    .replaceAll("bigint UNIQUE NOT NULL AUTO_INCREMENT", "BIGSERIAL PRIMARY KEY")
                    .replaceAll("bigint NOT NULL AUTO_INCREMENT", "BIGSERIAL PRIMARY KEY")
                    .replaceAll("bigint NULL AUTO_INCREMENT", "BIGSERIAL PRIMARY KEY")
                    .replaceAll("int UNIQUE NOT NULL AUTO_INCREMENT", "SERIAL PRIMARY KEY")
                    .replaceAll("int NOT NULL AUTO_INCREMENT", "SERIAL PRIMARY KEY")
                    .replaceAll("int NULL AUTO_INCREMENT", "SERIAL PRIMARY KEY")
//                    .replaceAll("IF NOT EXISTS", "")
                    .replaceAll("tinyint", "smallint")
                    .replaceAll("datetime", "timestamp")
//                    .replaceAll(", PRIMARY KEY \\(\"id\"\\)", "")
                    .replaceAll("ENGINE = InnoDB DEFAULT CHARSET = utf8mb4", "")
                    .replaceAll("ENGINE = InnoDB DEFAULT CHARSET = utf8 COLLATE = utf8_bin", "")
                    .replaceAll("COLLATE utf8mb4_bin", "")
                    .replaceAll("COLLATE utf8_bin", "")
                    .replaceAll("COLLATE = utf8mb4_bin", "");

            // 如果存在表注释
            if (createSQL.contains("COMMENT =")) {
                String comment = createSQL.substring(createSQL.indexOf("COMMENT =") + 9);
                comments.add(genTableCommentSql(table.toString(), comment));
                createSQL = createSQL.substring(0, createSQL.indexOf("COMMENT ="));
            }

            System.out.println(createSQL + ";");

            newIndex.forEach(t -> System.out.println(t.replaceAll("`", "\"") + ";"));
            comments.forEach(t -> System.out.println(t.replaceAll("`", "\"") + ";"));

        });
    }

    /**
     * 获得注释的下标
     *
     * @param columnSpecStrings columnSpecStrings
     * @return 下标
     */
    private static int getCommentIndex(List<String> columnSpecStrings) {
        for (int i = 0; i < columnSpecStrings.size(); i++) {
            if ("COMMENT".equalsIgnoreCase(columnSpecStrings.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 生成字段COMMENT语句
     *
     * @param table        表名
     * @param column       字段名
     * @param commentValue 描述文字
     * @return COMMENT语句
     */
    private static String genCommentSql(String table, String column, String commentValue) {
        return String.format("COMMENT ON COLUMN %s.%s IS %s", table, column, commentValue);
    }

    /**
     * 生成表COMMENT语句
     * @param table
     * @param commentValue
     * @return
     */
    private static String genTableCommentSql(String table, String commentValue) {
        return String.format("COMMENT ON table %s IS %s", table, commentValue);
    }

    /**
     * 唯一约束语法从mysql修改为PostgreSql
     * @param sql
     * @param indexList
     * @return
     */
//    private static String updateUniqueIndex(String sql, List<Index> indexList) {
//
//        for (Index index : indexList) {
//            if (index.getType().equals("UNIQUE KEY")) {
//
//                //UNIQUE KEY "INX_PROVINCE_NUM" ("PROVINCE_NUM_CODE")
//                String oldIndexSql = "UNIQUE KEY " + index.getName() + " (" + concat(index.getColumnsNames()) + ")";
//                oldIndexSql = oldIndexSql.replaceAll("`", "\"");
//
//                //CONSTRAINT "INX_PROVINCE_NUM" UNIQUE ("PROVINCE_NUM_CODE")
//                String newIndexSql = "CONSTRAINT " + index.getName() + " UNIQUE (" + concat(index.getColumnsNames()) + ")";
//                newIndexSql = newIndexSql.replaceAll("`", "\"");
//
//                sql = sql.replace(oldIndexSql, newIndexSql);
//            }
//        }
//
//        return sql;
//
//    }

    /**
     * 将mysql建表时的普通索引 , 移动到建表后建立
     *
     * @param table
     * @param index
     * @return
     */
    private static String genIndex(String table, Index index) {
        String sql = "CREATE INDEX %s ON %s (%s)";
        String format = String.format(sql, index.getName(), table, concat(index.getColumnsNames()));
        return format;
    }

    private static String genUniqueIndex(String table, Index index) {
        String sql = "CREATE UNIQUE INDEX %s ON %s (%s)";
        String format = String.format(sql, index.getName(), table, concat(index.getColumnsNames()));
        return format;
    }


    private static String concat(List<String> columnsNames) {
        String s = "";
        for (String columnsName : columnsNames) {
            s += columnsName + ", ";
        }

        s = s.substring(0, s.lastIndexOf(","));

        return s;
    }


}
