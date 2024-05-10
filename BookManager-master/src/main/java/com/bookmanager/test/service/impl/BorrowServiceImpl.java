package com.bookmanager.test.service.impl;

import com.bookmanager.test.service.BorrowService;
import com.bookmanager.test.mapper.BorrowMapper;
import com.bookmanager.test.model.Borrow;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service
public class BorrowServiceImpl implements BorrowService {

    @Resource
    BorrowMapper borrowMapper;

    @Override
    public List<Borrow> queryBorrowsByPage(Integer page, Integer size) {
        List<Borrow> borrows = borrowMapper.selectAllByLimit((page - 1) * size, size);

        for(Borrow borrow : borrows) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(borrow.getBorrowtime() != null) borrow.setBorrowtimestr(simpleDateFormat.format(borrow.getBorrowtime()));
            if(borrow.getReturntime() != null) borrow.setReturntimestr(simpleDateFormat.format(borrow.getReturntime()));
        }
        return borrows;
    }

    @Override
    public Integer getCount() {
        return borrowMapper.selectCount();
    }

    @Override
    public Integer addBorrow(Borrow borrow) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            borrow.setBorrowtime(simpleDateFormat.parse(borrow.getBorrowtimestr()));
            borrow.setReturntime(simpleDateFormat.parse(borrow.getReturntimestr()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return borrowMapper.insertSelective(borrow);
    }


    @Override
    public Integer addBorrow2(Borrow borrow) {
        return borrowMapper.insertSelective(borrow);
    }

    @Override
    public Integer deleteBorrow(Borrow borrow) {

        Borrow borrow1 = borrowMapper.selectByPrimaryKey(borrow.getBorrowid());
        if(borrow1.getReturntime() == null) return 0;
        return borrowMapper.deleteByPrimaryKey(borrow.getBorrowid());
    }

    @Override
    public Integer deleteBorrows(List<Borrow> borrows) {
        int count = 0;
        for(Borrow borrow : borrows) {
            count += deleteBorrow(borrow);
        }
        return count;
    }

    @Override
    public Integer updateBorrow(Borrow borrow) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            borrow.setBorrowtime(simpleDateFormat.parse(borrow.getBorrowtimestr()));
            borrow.setReturntime(simpleDateFormat.parse(borrow.getReturntimestr()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return borrowMapper.updateByPrimaryKeySelective(borrow);
    }


    @Override
    public Integer updateBorrow2(Borrow borrow) {
        return borrowMapper.updateByPrimaryKeySelective(borrow);
    }

    @Override
    public int getSearchCount(Map<String, Object> searchParam) {
        return borrowMapper.selectCountBySearch(searchParam);
    }

    @Override
    public List<Borrow> searchBorrowsByPage(Integer page, Integer size, Map<String, Object> searchParam) {
        searchParam.put("begin", (page - 1) * size);
        searchParam.put("size", size);

        List<Borrow> borrows = borrowMapper.selectBySearch(searchParam);


        for(Borrow borrow : borrows) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(borrow.getBorrowtime() != null) borrow.setBorrowtimestr(simpleDateFormat.format(borrow.getBorrowtime()));
            if(borrow.getReturntime() != null) borrow.setReturntimestr(simpleDateFormat.format(borrow.getReturntime()));
        }
        return borrows;
    }

    @Override
    public Integer getCountByReader(Integer userid) {
        return borrowMapper.selectCountByReader(userid);
    }

    @Override
    public List<Borrow> queryBorrowsByPageByReader(Integer page, Integer size, Integer userid) {
        List<Borrow> borrows = borrowMapper.selectAllByLimitByReader((page - 1) * size, size, userid);

        for(Borrow borrow : borrows) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(borrow.getBorrowtime() != null) borrow.setBorrowtimestr(simpleDateFormat.format(borrow.getBorrowtime()));
            if(borrow.getReturntime() != null) borrow.setReturntimestr(simpleDateFormat.format(borrow.getReturntime()));
        }
        return borrows;
    }

    @Override
    public Borrow queryBorrowsById(Integer borrowid) {
        return borrowMapper.selectByPrimaryKey(borrowid);
    }

}
