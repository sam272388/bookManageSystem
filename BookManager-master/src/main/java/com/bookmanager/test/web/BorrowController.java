package com.bookmanager.test.web;

import com.bookmanager.test.model.BookInfo;
import com.bookmanager.test.model.Borrow;
import com.bookmanager.test.service.BookInfoService;
import com.bookmanager.test.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/borrow")
public class BorrowController {

    @Autowired
    BorrowService borrowService;
    @Autowired
    BookInfoService bookInfoService;

    // 分页查询借阅
    @RequestMapping(value = "/queryBorrowsByPage")
    public Map<String, Object> queryBorrowsByPage(Integer page, Integer limit){
        // 获取数量
        Integer count = borrowService.getCount();
        // 获取数据
        List<Borrow> borrows = borrowService.queryBorrowsByPage(page, limit);
        // 结果map
        Map<String,Object> res = new HashMap<String,Object>();
        res.put("code", 0);
        res.put("msg", "success");
        res.put("count", count);
        res.put("data", borrows);
        return res;
    }

    // 添加借阅
    @RequestMapping(value = "/addBorrow")
    public Integer addBorrow(@RequestBody Borrow borrow){
        return borrowService.addBorrow(borrow);
    }

    // 获得数量
    @RequestMapping(value = "/getCount")
    public Integer getCount(){
        return borrowService.getCount();
    }

    // 删除借阅
    @RequestMapping(value = "/deleteBorrow")
    public Integer deleteBorrow(@RequestBody Borrow borrow){
        return borrowService.deleteBorrow(borrow);
    }

    // 删除一些借阅
    @RequestMapping(value = "/deleteBorrows")
    public Integer deleteBorrows(@RequestBody List<Borrow> borrows){
        return borrowService.deleteBorrows(borrows);
    }

    // 更新借阅
    @RequestMapping(value = "/updateBorrow")
    public Integer updateBorrow(@RequestBody Borrow borrow){
        return borrowService.updateBorrow(borrow);
    }

    // 分页搜索借阅
    @RequestMapping("/searchBorrowsByPage")
    public Map<String,Object> searchBorrowsByPage(Integer page, Integer limit, String json){
        //获得搜索的参数
        Map<String, Object> searchParam = JsonUtil.parseMap(json, String.class, Object.class);
        //获取查询个数
        int count = borrowService.getSearchCount(searchParam);
        //查询数据
        List<Borrow> borrows = borrowService.searchBorrowsByPage(page, limit, searchParam);
        //结果map
        Map<String,Object> res = new HashMap<String,Object>();
        res.put("code", 0);
        res.put("msg", "success");
        res.put("count", count);
        res.put("data", borrows);
        return res;
    }

    // 用户分页搜索借阅
    @RequestMapping(value = "/reader/queryBorrowsByPageByReader")
    public Map<String, Object> queryBorrowsByPageByReader(Integer page, Integer limit, Integer userid){
        // 获取数量
        Integer count = borrowService.getCountByReader(userid);
        // 获取数据
        List<Borrow> borrows = borrowService.queryBorrowsByPageByReader(page, limit, userid);
        // 结果map
        Map<String,Object> res = new HashMap<String,Object>();
        res.put("code", 0);
        res.put("msg", "success");
        res.put("count", count);
        res.put("data", borrows);
        return res;
    }

    // 借书
    @RequestMapping(value = {"/borrowBook", "/reader/borrowBook"})
    @Transactional
    public Integer borrowBook(Integer userid, Integer bookid){
        try{
            // 查询该书的情况
            BookInfo theBook = bookInfoService.queryBookInfoById(bookid);

            if(theBook == null) {  // 書不存在
                throw new NullPointerException("圖書" + bookid + "不存在");
            } else if(theBook.getIsborrowed() == 1) {  // 已經被借走
                throw new NotEnoughException("圖書" + bookid + "庫存不足（已經被借走）");
            }

            // 更新图书表的isBorrowed
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookid(bookid);
            bookInfo.setIsborrowed((byte) 1);
            Integer res2 = bookInfoService.updateBookInfo(bookInfo);
            if(res2 == 0) throw new OperationFailureException("圖書" + bookid + "更新失敗");

            // 添加一条记录到borrow表
            Borrow borrow = new Borrow();
            borrow.setUserid(userid);
            borrow.setBookid(bookid);
            borrow.setBorrowtime(new Date(System.currentTimeMillis()));
            Integer res1 = borrowService.addBorrow2(borrow);
            if(res1 == 0) throw new OperationFailureException("圖書" + bookid + "添加借閱記錄失败");

        } catch (Exception e) {
            System.out.println("發生異常");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return 0;
        }

        return 1;
    }

    // 還書
    @RequestMapping(value = {"/returnBook", "/reader/returnBook"})
    @Transactional
    public Integer returnBook(Integer borrowid, Integer bookid){

        try {
            // 查詢該書的情況
            BookInfo theBook = bookInfoService.queryBookInfoById(bookid);
            // 查詢借書的情況
            Borrow theBorrow = borrowService.queryBorrowsById(borrowid);

            if(theBook == null) {  // 图书不存在
                throw new NullPointerException("圖書" + bookid + "不存在");
            } else if(theBorrow == null) {   //结束记录不存在
                throw new NullPointerException("借書記錄" + bookid + "不存在");
            } else if(theBook.getIsborrowed() == 0) {  // 已经还过书
                throw new NotEnoughException("圖書" + bookid + "已經還過了");
            }

            // 更新圖書表的isBorrowed
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookid(bookid);
            bookInfo.setIsborrowed((byte) 0);
            Integer res2 = bookInfoService.updateBookInfo(bookInfo);
            if(res2 == 0) throw new OperationFailureException("圖書" + bookid + "更新信息失败");

            // 更新Borrow表，更新結束時間
            Borrow borrow = new Borrow();
            borrow.setBorrowid(borrowid);
            borrow.setReturntime(new Date(System.currentTimeMillis()));
            Integer res1 = borrowService.updateBorrow2(borrow);
            if(res1 == 0) throw new OperationFailureException("圖書" + bookid + "更新借閱記錄失敗");

        } catch (Exception e) {
            System.out.println("異常");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return 0;
        }

        return 1;
    }

}
