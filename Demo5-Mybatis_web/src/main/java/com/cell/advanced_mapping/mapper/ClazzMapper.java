package com.cell.advanced_mapping.mapper;

import com.cell.advanced_mapping.pojo.Clazz;

public interface ClazzMapper {
    Clazz selectByCid(Integer cid);

    Clazz selectClazzAndStusByCid(Integer cid);
}
