import request from '@/utils/request'

// 獲取圖書類型數量
export function getCount() {
  return request({
    url: '/bookType/getCount',
    method: 'get'
  })
}

// 查詢所有圖書類型
export function queryBookTypes() {
  return request({
    url: '/bookType/queryBookTypes',
    method: 'get'
  })
}

// 分頁查詢圖書類型
export function queryBookTypesByPage(params) {
  return request({
    url: '/bookType/queryBookTypesByPage',
    method: 'get',
    params
  })
}

// 添加圖書類型
export function addBookType(data) {
  return request({
    url: '/bookType/addBookType',
    method: 'post',
    data
  })
}

// 刪除圖書類型
export function deleteBookType(data) {
  return request({
    url: '/bookType/deleteBookType',
    method: 'delete',
    data
  })
}

//  刪除一些圖書類型
export function deleteBookTypes(data) {
  return request({
    url: '/bookType/deleteBookTypes',
    method: 'post',
    data
  })
}

//  更新圖書類型
export function updateBookType(data) {
  return request({
    url: '/bookType/updateBookType',
    method: 'put',
    data
  })
}
