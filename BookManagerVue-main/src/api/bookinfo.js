import request from '@/utils/request'

// 獲取圖書數量
export function getCount() {
  return request({
    url: '/bookInfo/getCount',
    method: 'get'
  })
}

// 查詢所有圖書信息
export function queryBookInfos() {
  return request({
    url: '/bookInfo/queryBookInfos',
    method: 'get'
  })
}

// 分頁查詢圖書信息
export function queryBookInfosByPage(params) {
  return request({
    url: '/bookInfo/queryBookInfosByPage',
    method: 'get',
    params
  })
}

// 添加圖書信息
export function addBookInfo(data) {
  return request({
    url: '/bookInfo/addBookInfo',
    method: 'post',
    data
  })
}

// 刪除圖書信息
export function deleteBookInfo(data) {
  return request({
    url: '/bookInfo/deleteBookInfo',
    method: 'delete',
    data
  })
}

//  刪除一些圖書信息
export function deleteBookInfos(data) {
  return request({
    url: '/bookInfo/deleteBookInfos',
    method: 'post', // 用delete無法批量刪除
    data
  })
}

//  更新圖書信息
export function updateBookInfo(data) {
  return request({
    url: '/bookInfo/updateBookInfo',
    method: 'put',
    data
  })
}
