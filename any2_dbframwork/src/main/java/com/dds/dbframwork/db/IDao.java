package com.dds.dbframwork.db;

import java.util.List;

/**
 * Created by dds on 2019/7/12.
 * android_shuai@163.com
 * 数据库操作接口类
 */
public interface IDao<T> {

    /**
     * 插入
     *
     * @param entity 实体
     * @return 改动行数
     */
    long insert(T entity);

    /**
     * 更新
     *
     * @param entity 实体
     * @param where  条件
     * @return 更新条数
     */
    int update(T entity, T where);

    /**
     * 删除
     *
     * @param where 条件
     * @return 删除条数
     */
    int delete(T where);

    /**
     * 查询
     *
     * @param where 查询条件
     * @return 查询结果集
     */
    List<T> query(T where);


    List<T> query(T where,String orderBy,Integer startIndex,Integer limit);
}
