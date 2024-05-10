package com.bookmanager.test.web;


import com.bookmanager.test.model.BookType;
import com.bookmanager.test.service.BookTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/bookType")
public class BookTypeController {

    @Autowired
    BookTypeService bookTypeService;

    // 分頁查詢類型
    @GetMapping(value = "/queryBookTypesByPage")
    public Map<String, Object> queryBookTypesByPage(Integer page, Integer limit){
        // 取得數量
        Integer count = bookTypeService.getCount();

        // 取得數據
        List<BookType> bookTypes = bookTypeService.queryBookTypesByPage(page, limit);

        // 結果map
        Map<String,Object> res = new HashMap<String,Object>();
        res.put("code", 0);
        res.put("msg", "success");
        res.put("count", count);
        res.put("data", bookTypes);
        return res;
    }

    // 查詢所有類型
    @GetMapping(value = {"/queryBookTypes", "/reader/queryBookTypes"})
    public List<BookType> queryBookTypes(){
        return bookTypeService.queryBookTypes();
    }

    // 添加類型
    @PostMapping(value = "/addBookType")
    public Integer addBookType(@RequestBody BookType bookType){
        return bookTypeService.addBookType(bookType);
    }

    // 取得數量
    @GetMapping(value = "/getCount")
    public Integer getCount(){
        return bookTypeService.getCount();
    }

    // 刪除類型
    @DeleteMapping(value = "/deleteBookType")
    public Integer deleteBookType(@RequestBody BookType bookType){
        return bookTypeService.deleteBookType(bookType);
    }

    // 刪除部分類型
    @DeleteMapping(value = "/deleteBookTypes")
    public Integer deleteBookTypes(@RequestBody List<BookType> bookTypes){
        return bookTypeService.deleteBookTypes(bookTypes);
    }

    // 更新類型
    @PutMapping(value = "/updateBookType")
    public Integer updateBookType(@RequestBody BookType bookType){
        return bookTypeService.updateBookType(bookType);
    }

    // 分頁搜索類型
    @GetMapping("/searchBookTypesByPage")
    public Map<String,Object> searchBookTypesByPage(Integer page, Integer limit, String json){
        //獲得搜索的參數
        Map<String, Object> searchParam = JsonUtil.parseMap(json, String.class, Object.class);
        //取得查詢個數
        int count = bookTypeService.getSearchCount(searchParam);
        //查詢數據
        List<BookType> bookTypes = bookTypeService.searchBookTypesByPage(page, limit, searchParam);
        //結果map
        Map<String,Object> res = new HashMap<String,Object>();
        res.put("code", 0);
        res.put("msg", "success");
        res.put("count", count);
        res.put("data", bookTypes);
        return res;
    }
}
