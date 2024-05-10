package com.bookmanager.test.web;



import com.bookmanager.test.service.BookInfoService;
import com.bookmanager.test.model.BookInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/bookInfo")
public class BookInfoController {

    @Autowired
    BookInfoService bookInfoService;

    // 分頁查詢圖書信息
    @GetMapping(value = "/queryBookInfosByPage")
    public Map<String, Object> queryBookInfosByPage(Integer page, Integer limit){
        // 取得數量
        Integer count = bookInfoService.getCount();
        // 取得數據
        List<BookInfo> bookInfos = bookInfoService.queryBookInfosByPage(page, limit);
        // 結果map
        return getStringObjectMap(count, bookInfos);
    }

    private Map<String, Object> getStringObjectMap(Integer count, List<BookInfo> bookInfos) {
        Map<String,Object> res = new HashMap<String,Object>();
        res.put("code", 0);
        res.put("msg", "success");
        res.put("count", count);
        res.put("data", bookInfos);
        return res;
    }

    // 查詢所有圖書信息
    @GetMapping(value = "/queryBookInfos")
    public List<BookInfo> queryBookInfos(){
        return bookInfoService.queryBookInfos();
    }

    // 添加圖書信息
    @PostMapping(value = "/addBookInfo")
    public Integer addBookInfo(@RequestBody BookInfo bookInfo){
        return bookInfoService.addBookInfo(bookInfo);
    }

    // 取得圖書數量
    @GetMapping(value = "/getCount")
    public Integer getCount(){
        return bookInfoService.getCount();
    }

    // 刪除圖書信息
    @DeleteMapping(value = "/deleteBookInfo")
    public Integer deleteBookInfo(@RequestBody BookInfo bookInfo){
        return bookInfoService.deleteBookInfo(bookInfo);
    }

    // 刪除部分圖書信息
    @DeleteMapping(value = "/deleteBookInfos")
    public Integer deleteBookInfos(@RequestBody List<BookInfo> bookInfos){
        return bookInfoService.deleteBookInfos(bookInfos);
    }

    // 更新圖書信息
    @PutMapping(value = "/updateBookInfo")
    public Integer updateBookInfo(@RequestBody BookInfo bookInfo){
        return bookInfoService.updateBookInfo(bookInfo);
    }

    // 查詢圖書信息通過分頁
    @GetMapping("/searchBookInfosByPage")
    public Map<String,Object> searchBookInfosByPage(Integer page, Integer limit, String json){
        //取得搜索的參數
        Map<String, Object> searchParam = JsonUtil.parseMap(json, String.class, Object.class);
        //取得查詢個數
        int count = bookInfoService.getSearchCount(searchParam);
        //查詢數據
        List<BookInfo> bookInfos = bookInfoService.searchBookInfosByPage(page, limit, searchParam);
        //結果map
        return getStringObjectMap(count, bookInfos);
    }

    // 讀者的查詢數量
    @GetMapping(value = "/reader/getSearchCount")
    public Integer getSearchCountByReader(String bookname){
        bookname = bookname.trim();
        if(bookname.equals("") || bookname.equals("null")) {
            return bookInfoService.getCount();
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("bookname", bookname);
            return bookInfoService.getSearchCount(map);
        }
    }

    // 讀者的搜索圖書
    @GetMapping("/reader/searchBookInfosByPage")
    public Map<String,Object> searchBookInfosByPageByReader(Integer page, Integer limit, String bookname){
        bookname = bookname.trim();
        Map<String,Object> res;
        if (bookname.equals("") || bookname.equals("null")) {
            res = queryBookInfosByPage(page, limit);
        } else {
            String searchJson = "{\"bookname\":\"" + bookname + "\",\"bookauthor\":\"\",\"booktypeid\":\"\"}";
            res = searchBookInfosByPage(page, limit, searchJson);
        }
        return res;
    }

    // 讀者的取得類型
    @GetMapping(value = "/reader/getCountByType")
    public Integer getCountByType(Integer booktypeid){
        if(booktypeid == null) return 0;
        Map<String, Object> map = new HashMap<>();
        map.put("booktypeid", booktypeid);
        return bookInfoService.getCountByType(map);
    }

    // 讀者的通過分類查詢圖書信息
    @GetMapping("/reader/queryBookInfosByPageByType")
    public Map<String,Object> queryBookInfosByPageByType(Integer page, Integer limit, Integer booktypeid){
        if(booktypeid == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("booktypeid", booktypeid);
        //取得查詢個數
        int count = bookInfoService.getCountByType(map);
        //查詢數據
        List<BookInfo> bookInfos = bookInfoService.queryBookInfosByPageByType(page, limit, map);
        //結果map
        return getStringObjectMap(count, bookInfos);
    }
}
