package com.cong.service.impl;

import com.cong.mapper.StuMapper;
import com.cong.pojo.Stu;
import com.cong.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Stu getStuInfo(int id) {
        return stuMapper.selectByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveStu() {
        Stu stu = new Stu();
        stu.setAge(10);
        stu.setName("mike");
        stuMapper.insert(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateStu(int id) {
        Stu stu = new Stu();
        stu.setId(id);
        stu.setAge(12);
        stu.setName("melon");
        stuMapper.updateByPrimaryKey(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteStu(int id) {
        stuMapper.deleteByPrimaryKey(id);
    }

    public void saveParent() {
        Stu stu = new Stu();
        stu.setName("parent");
        stu.setAge(30);
        stuMapper.insert(stu);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveChildren() {
        saveChild1();
        int a = 1 / 0;
        saveChild2();
    }

    public void saveChild1() {
        Stu stu1 = new Stu();
        stu1.setName("child1");
        stu1.setAge(10);
        stuMapper.insert(stu1);
    }

    public void saveChild2() {
        Stu stu2 = new Stu();
        stu2.setName("child2");
        stu2.setAge(10);
        stuMapper.insert(stu2);
    }
}
