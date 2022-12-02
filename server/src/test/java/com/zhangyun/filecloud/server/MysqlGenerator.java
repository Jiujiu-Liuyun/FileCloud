package com.zhangyun.filecloud.server;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MysqlGenerator {
    @Test
    void generator(){
        // 代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir("/Users/zhangyun/Documents/javaProject/FileCloud/server/src/main/java");
        gc.setAuthor("zy");
        gc.setOpen(false);
        // 是否覆盖文件
        gc.setFileOverride(true);
        //是否开启Swagger2配置
        gc.setSwagger2(true);
        gc.setServiceName("%sService"); //避免生成 IEntityService
        // 指定Java中的日期格式
        gc.setDateType(DateType.ONLY_DATE);
        // 指定ID生成策略
        // ASSIGN_ID雪花算法，生成一串纯数字的Long类型（对应bigint）或者String类型（对应varchar）的ID
        // ASSIGN_UUID雪花算法，生成一串纯字符的String类型（对应varchar）的ID
        gc.setIdType(IdType.AUTO);
        autoGenerator.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/file_cloud?servertimezone=Asia?Shanghai&useUnicode=true&charaterEncoding=utf-8&useSSL=false");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("H2r9P4_f*21L9");
        dsc.setDbType(DbType.MYSQL);
        autoGenerator.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        // 设置模块名称
        // pc.setModuleName("模块名称");
        pc.setParent("com.zhangyun.filecloud.server.database");
        pc.setEntity("entity");
        pc.setMapper("mapper");
        pc.setService("service");
        pc.setController("controller");
        autoGenerator.setPackageInfo(pc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 开启数据库字段下划线转JAVA中的驼峰命名
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // 生成lombok注解
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setControllerMappingHyphenStyle(true);

        // 设置Entity实体继承的公共父类，没有则不需要配置
//        strategy.setSuperEntityClass("cn.ecut.mybatisplusspringboot.commons.BaseEntity");
        // 设置公共父类中的公共字段
//        strategy.setSuperEntityColumns("version", "deleted", "create_time", "modified_time");

        // 如果没有设置父类实体，则可以设置该配置，表示是将该字段设置成逻辑删除字段
         strategy.setLogicDeleteFieldName("deleted");

        // 如果没有设置父类实体，则可以设置该配置，表示是将该字段设置成乐观锁字段
        // strategy.setVersionFieldName("version");

        // 如果没有设置父类实体，则可以设置该配置，表示自动填充字段
         TableFill createTime = new TableFill("create_time", FieldFill.INSERT);
         TableFill modifiedTime = new TableFill("modified_time", FieldFill.INSERT_UPDATE);
         ArrayList<TableFill> tableFills = new ArrayList<>();
         tableFills.add(createTime);
         tableFills.add(modifiedTime);
         strategy.setTableFillList(tableFills);

        // 设置需要操作的表名
        strategy.setInclude("file");
        // strategy.setTablePrefix(pc.getModuleName() + "_");
        autoGenerator.setStrategy(strategy);

        // 执行
        autoGenerator.execute();
    }

}