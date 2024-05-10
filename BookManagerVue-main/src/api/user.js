import request from '@/utils/request'

// 登錄
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 獲取用戶信息
export function getInfo(token) {
  return request({
    url: '/user/info',
    method: 'get',
    params: { token }
  })
}

// 登出
export function logout(token) {
  return request({
    url: '/user/logout',
    method: 'post',
    params: { token }
  })
}

// 註冊
export function register(params) {
  return request({
    url: '/user/register',
    method: 'post',
    params
  })
}

// 修改密碼
export function alterPassword(params) {
  return request({
    url: '/user/alterPassword',
    method: 'post',
    params
  })
}

// 獲取用戶數量
export function getCount() {
  return request({
    url: '/user/getCount',
    method: 'get'
  })
}

// 查詢所有用戶信息
export function queryUsers() {
  return request({
    url: '/user/queryUsers',
    method: 'get'
  })
}

// 分頁查詢用戶信息
export function queryUsersByPage(params) {
  return request({
    url: '/user/queryUsersByPage',
    method: 'get',
    params
  })
}

// 添加用戶信息
export function addUser(data) {
  return request({
    url: '/user/addUser',
    method: 'post',
    data
  })
}

// 刪除用戶信息
export function deleteUser(data) {
  return request({
    url: '/user/deleteUser',
    method: 'delete',
    data
  })
}

//  刪除一些用戶信息
export function deleteUsers(data) {
  return request({
    url: '/user/deleteUsers',
    method: 'delete',
    data
  })
}

//  更新用戶信息
export function updateUser(data) {
  return request({
    url: '/user/updateUser',
    method: 'put',
    data
  })
}
