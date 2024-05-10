import request from '@/utils/request'

// 獲取借閱數量
export function getCount() {
  return request({
    url: '/borrow/getCount',
    method: 'get'
  })
}

// 查詢所有借閱信息
export function queryBorrows() {
  return request({
    url: '/borrow/queryBorrows',
    method: 'get'
  })
}

// 分頁查詢借閱信息
export function queryBorrowsByPage(params) {
  return request({
    url: '/borrow/queryBorrowsByPage',
    method: 'get',
    params
  })
}

// 添加借閱信息
export function addBorrow(data) {
  return request({
    url: '/borrow/addBorrow',
    method: 'post',
    data
  })
}

// 刪除借閱信息
export function deleteBorrow(data) {
  return request({
    url: '/borrow/deleteBorrow',
    method: 'delete',
    data
  })
}

//  刪除一些借閱信息
export function deleteBorrows(data) {
  return request({
    url: '/borrow/deleteBorrows',
    method: 'post',
    data
  })
}

//  更新借閱信息
export function updateBorrow(data) {
  return request({
    url: '/borrow/updateBorrow',
    method: 'put',
    data
  })
}

// 借書
export function borrowBook(userid, bookid) {
  return request({
    url: '/borrow/borrowBook',
    method: 'post',
    params: {
      userid,
      bookid
    }
  })
}

// 還書
export function returnBook(borrowid, bookid) {
  return request({
    url: '/borrow/returnBook',
    method: 'post',
    params: {
      borrowid,
      bookid
    }
  })
}
